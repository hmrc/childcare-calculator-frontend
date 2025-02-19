/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.services

import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.EarningsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeChildcareWorkingParents, FreeHours, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, _}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils._
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

//scalastyle:off
class ResultsService @Inject()(appConfig: FrontendAppConfig,
                               eligibilityService: EligibilityService,
                               freeHours: FreeHours,
                               freeChildcareWorkingParents: FreeChildcareWorkingParents,
                               taxFreeChildcare: TaxFreeChildcare,
                               firstParagraphBuilder: FirstParagraphBuilder,
                               utils: Utils)(implicit ec: ExecutionContext) {

  def getResultsViewModel(answers: UserAnswers, location: Location)
                         (implicit req: play.api.mvc.Request[_], hc: HeaderCarrier, messages: Messages): Future[ResultsViewModel] = {

    val childcareCost = answers.childcareCosts.fold(false) {
      case ChildcareConstants.no => false
      case _ => true
    }

    val approvedProvider = answers.approvedProvider.fold(false) {
      case ChildcareConstants.NO => false
      case _ => true
    }

    val livingWithPartner = answers.doYouLiveWithPartner.fold(false)(identity)

    val paidEmployment = checkIfInEmployment(answers)

    val yourEarnings = getEarnings(answers.yourMinimumEarnings, answers.yourMaximumEarnings)
    val partnerEarnings = getEarnings(answers.partnerMinimumEarnings, answers.partnerMaximumEarnings)

    val resultViewModel = ResultsViewModel(
      firstParagraph = firstParagraphBuilder.buildFirstParagraph(answers),
      freeChildcareWorkingParents = getFreeChildcareWorkingParentsEligibility(answers),
      location = location,
      childrenAgeGroups = answers.childrenAgeGroups.getOrElse(Set(NoneOfThese)),
      hasChildcareCosts = childcareCost,
      hasCostsWithApprovedProvider = approvedProvider,
      isAnyoneInPaidEmployment = paidEmployment,
      livesWithPartner = livingWithPartner,
      yourEarnings = yourEarnings,
      partnerEarnings = partnerEarnings,
      freeChildcareWorkingParentsEligibilityMsg = freeChildcareWorkingParentsEligibilityMessage(answers),
      taxFreeChildcareEligibilityMsg = tfcEligibilityMessage(answers)
    )

    val schemeResults: Future[SchemeResults] = eligibilityService.eligibility(answers)

    schemeResults.map(results => {
      val result = results.schemes.foldLeft(resultViewModel)((result, scheme) =>
        getViewModelWithFreeHours(
          answers,
          setSchemeInViewModel(scheme, result, answers.taxOrUniversalCredits)
        )
      )

      if (result.tfc.isDefined && answers.taxOrUniversalCredits.contains("uc")) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.uc")))
      } else if (result.tfc.isDefined && result.tc.isDefined && result.esc.isDefined) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.tc.esc")))
      } else if (result.tfc.isDefined && result.tc.isDefined) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.tc")))
      } else if (result.tfc.isDefined && result.esc.isDefined) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.esc")))
      } else {
        result
      }
    })
  }

  private def getEarnings(moreThanMinimum: Option[Boolean], moreThanMaximum: Option[Boolean]): Option[EarningsEnum] =
    (moreThanMinimum, moreThanMaximum) match {
      case (Some(true), Some(true)) => Some(EarningsEnum.GreaterThanMaximum)
      case (Some(true), _) => Some(EarningsEnum.BetweenMinimumAndMaximum)
      case (Some(false), _) => Some(EarningsEnum.LessThanMinimum)
      case _ => None
    }

  private def getFreeChildcareWorkingParentsEligibility(userAnswers: UserAnswers): Boolean = {
    freeChildcareWorkingParents.eligibility(userAnswers) match {
      case Eligible => true
      case _ => false
    }
  }

  private def setSchemeInViewModel(scheme: SingleSchemeResult, resultViewModel: ResultsViewModel, taxCreditsOrUC: Option[String]) = {
    if (scheme.amount > 0) {
      scheme.name match {
        case TCELIGIBILITY =>
          //Backend calculator returns TC calculation independently of whether user gets TC or not,
          //since tc can longer be applied to this is hiding the scheme if user doesn't already get it.
          //TODO: Update cc-eligibility to instead require a tc flag (similar to esc) so frontend doesn't need to undo the tc calc
          if (taxCreditsOrUC.contains(taxCredits)) {
            resultViewModel.copy(tc = Some(scheme.amount))
          } else {
            resultViewModel
          }
        case TFCELIGIBILITY => resultViewModel.copy(tfc = Some(scheme.amount))
        case ESCELIGIBILITY => resultViewModel.copy(esc = Some(scheme.amount))
      }
    }
    else {
      resultViewModel
    }
  }

  private def checkIfInEmployment(userAnswers: UserAnswers) = {
    if (userAnswers.areYouInPaidWork.isDefined) {
      userAnswers.areYouInPaidWork.getOrElse(false)
    } else {
      userAnswers.whoIsInPaidEmployment.fold(false) {
        case ChildcareConstants.neither => false
        case _ => true
      }
    }
  }

  private def getViewModelWithFreeHours(answers: UserAnswers, resultViewModel: ResultsViewModel) = {
    val freeHoursEligibility = freeHours.eligibility(answers)
    val freeChildcareWorkingParentsEligibility = freeChildcareWorkingParents.eligibility(answers)
    val location: Option[Location.Value] = answers.location

    freeHoursEligibility match {
      case _ if freeChildcareWorkingParentsEligibility == Eligible && answers.isChildAgedThreeOrFour.getOrElse(true) => resultViewModel.copy(freeHours = Some(eligibleMaxFreeHours))
      case _ if freeChildcareWorkingParentsEligibility == Eligible => resultViewModel.copy(freeHours = Some(appConfig.maxFreeHoursAmount))
      case Eligible => getFreeHoursForLocation(location, resultViewModel)
      case _ => resultViewModel
    }
  }

  private def getFreeHoursForLocation(optionLocation: Option[Location.Value], resultViewModel: ResultsViewModel) =
    optionLocation.fold(resultViewModel) {
      case ENGLAND => resultViewModel.copy(freeHours = Some(freeHoursForEngland))
      case SCOTLAND => resultViewModel.copy(freeHours = Some(freeHoursForScotland))
      case WALES => resultViewModel.copy(freeHours = Some(freeHoursForWales))
      case NORTHERN_IRELAND => resultViewModel.copy(freeHours = Some(freeHoursForNI))
    }

  private def tfcEligibilityMessage(answers: UserAnswers)(implicit messages: Messages): Option[String] = {
    lazy val hasEligibileChildren = answers.hasChildEligibleForTfc
    lazy val youInPaidWork = answers.areYouInPaidWork.getOrElse(false)
    lazy val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
    lazy val youEligibleMinEarnings = answers.yourMinimumEarnings.getOrElse(false)
    lazy val youEligibleMaxEarnings = !answers.yourMaximumEarnings.getOrElse(false)
    lazy val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    lazy val bothInPaidWork = answers.whoIsInPaidEmployment.contains(both)
    lazy val earningsForPartnerAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourPartnersAge)
    lazy val bothEligibleMinEarnings = answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false)
    lazy val bothEligibleMaxEarnings = !answers.eitherOfYouMaximumEarnings.getOrElse(false)

    lazy val msgKey = "result.tfc.ineligible"

    taxFreeChildcare.eligibility(answers) match {
      case Eligible => None
      case _ if answers.childcareCosts.contains(no) || answers.approvedProvider.contains(NO) =>
        Some(messages(s"$msgKey.noCostsWithApprovedProvider"))
      case _ if hasPartner && !bothInPaidWork =>
        Some(messages(s"$msgKey.partner.paidEmployment"))
      case _ if hasPartner && !bothEligibleMinEarnings =>
        if (earningsForAge == earningsForPartnerAge) Some(messages(s"$msgKey.partner.minimumEarning.sameAge", earningsForAge))
        else Some(messages(s"$msgKey.partner.minimumEarning.differentAge", earningsForAge, earningsForPartnerAge))
      case _ if hasPartner && !bothEligibleMaxEarnings =>
        Some(messages(s"$msgKey.partner.maximumEarning"))
      case _ if !hasPartner && !youInPaidWork =>
        Some(messages(s"$msgKey.paidEmployment"))
      case _ if !hasPartner && !youEligibleMinEarnings =>
        Some(messages(s"$msgKey.minimumEarning", earningsForAge))
      case _ if !hasPartner && !youEligibleMaxEarnings =>
        Some(messages(s"$msgKey.maximumEarning"))
      case _ if !hasEligibileChildren =>
        Some(messages(s"$msgKey.noEligibleChild"))
      case _ => None
    }
  }

  private def freeChildcareWorkingParentsEligibilityMessage(answers: UserAnswers)(implicit messages: Messages): Option[String] = {
    lazy val inEngland = answers.location.contains(ENGLAND)
    lazy val hasEligibileChildren = answers.childrenAgeGroups.exists(!_.contains(NoneOfThese))
    lazy val youInPaidWork = answers.areYouInPaidWork.getOrElse(false)
    lazy val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
    lazy val youEligibleMinEarnings = answers.yourMinimumEarnings.getOrElse(false)
    lazy val youEligibleMaxEarnings = !answers.yourMaximumEarnings.getOrElse(false)
    lazy val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    lazy val bothInPaidWork = answers.whoIsInPaidEmployment.contains(both)
    lazy val earningsForPartnerAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourPartnersAge)
    lazy val bothEligibleMinEarnings = answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false)
    lazy val bothEligibleMaxEarnings = !answers.eitherOfYouMaximumEarnings.getOrElse(false)

    lazy val msgKey = "result.free.childcare.working.parents.ineligible"
    freeChildcareWorkingParents.eligibility(answers) match {
      case Eligible => None
      case _ if !inEngland => None
      case _ if !hasEligibileChildren =>
        Some(messages(s"$msgKey.noChildrenInAgeRange"))
      case _ if hasPartner && !bothInPaidWork =>
        Some(messages(s"$msgKey.partner.paidEmployment"))
      case _ if hasPartner && !bothEligibleMinEarnings =>
        if (earningsForAge == earningsForPartnerAge) Some(messages(s"$msgKey.partner.minimumEarning.sameAge", earningsForAge))
        else Some(messages(s"$msgKey.partner.minimumEarning.differentAge", earningsForAge, earningsForPartnerAge))
      case _ if hasPartner && !bothEligibleMaxEarnings =>
        Some(messages(s"$msgKey.partner.maximumEarning"))
      case _ if !hasPartner && !youInPaidWork =>
        Some(messages(s"$msgKey.paidEmployment"))
      case _ if !hasPartner && !youEligibleMinEarnings =>
        Some(messages(s"$msgKey.minimumEarning", earningsForAge))
      case _ if !hasPartner && !youEligibleMaxEarnings =>
        Some(messages(s"$msgKey.maximumEarning"))
      case _ => None
    }
  }
}
