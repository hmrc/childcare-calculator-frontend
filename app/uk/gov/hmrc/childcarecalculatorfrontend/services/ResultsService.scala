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
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeChildcareWorkingParents, FreeHours, MaxFreeHours, TaxFreeChildcare}
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
                               maxFreeHours: MaxFreeHours,
                               firstParagraphBuilder: FirstParagraphBuilder,
                               tcSchemeInEligibilityMsgBuilder: TCSchemeInEligibilityMsgBuilder,
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

    def getEarnings(moreThanMinimum: Option[Boolean], moreThanMaximum: Option[Boolean]): Option[EarningsEnum] =
      (moreThanMinimum, moreThanMaximum) match {
        case (Some(true), Some(true)) => Some(EarningsEnum.GreaterThanMaximum)
        case (Some(true), _) => Some(EarningsEnum.BetweenMinimumAndMaximum)
        case (Some(false), _) => Some(EarningsEnum.LessThanMinimum)
        case _ => None
      }

    val yourEarnings = getEarnings(answers.yourMinimumEarnings, answers.yourMaximumEarnings)
    val partnerEarnings = getEarnings(answers.partnerMinimumEarnings, answers.partnerMaximumEarnings)

    def tfcEligibilityMessage: Option[String] = {
      if(!getFreeChildcareWorkingParentsEligibility(answers)) {
        if(answers.doYouLiveWithPartner.getOrElse(false)) {
          if (!answers.whoIsInPaidEmployment.contains("both")) Some(messages("result.tfc.not.eligible.partner.paidEmployment"))
          else if (!(answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false))) {
            val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
            val earningsForPartnerAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourPartnersAge)
            if (earningsForAge == earningsForPartnerAge) Some(messages("result.tfc.not.eligible.partner.minimumEarning.sameAge", earningsForAge))
            else Some(messages("result.tfc.not.eligible.partner.minimumEarning.differentAge", earningsForAge, earningsForPartnerAge))
          }
          else if (answers.eitherOfYouMaximumEarnings.getOrElse(false)) Some(messages("result.tfc.not.eligible.partner.maximumEarning"))
          else if(!answers.hasChildEligibleForTfc) Some(messages("result.tfc.not.eligible.age"))
          else None
        } else {
          if (answers.childcareCosts.contains("no")) Some(messages("result.tfc.not.eligible.noCosts"))
          else if(answers.approvedProvider.contains("NO")) Some(messages("result.tfc.not.eligible.approvedProvider"))
          else if (!answers.areYouInPaidWork.getOrElse(false)) Some(messages("result.tfc.not.eligible.paidEmployment"))
          else if (!answers.yourMinimumEarnings.getOrElse(false)) {
            val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
            Some(messages("result.tfc.not.eligible.minimumEarning", earningsForAge))
          }
          else if (answers.yourMaximumEarnings.getOrElse(false)) Some(messages("result.tfc.not.eligible.maximumEarning"))
          else if(!answers.hasChildEligibleForTfc) Some(messages("result.tfc.not.eligible.age"))
          else None
        }
      } else None
    }

    def freeChildcareWorkingParentsEligibilityMessage: Option[String] = {
      if(!getFreeChildcareWorkingParentsEligibility(answers)) {
        if (answers.isChildAgedNineTo23Months.getOrElse(false) || answers.isChildAgedTwo.getOrElse(false) || answers.isChildAgedThreeOrFour.getOrElse(false)) {
          if (answers.doYouLiveWithPartner.getOrElse(false)) {
            if (!answers.whoIsInPaidEmployment.contains("both")) Some(messages("result.free.childcare.working.parents.not.eligible.partner.paidEmployment"))
            else if (!(answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false))) {
              val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
              val earningsForPartnerAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourPartnersAge)
              if (earningsForAge == earningsForPartnerAge) Some(messages("result.free.childcare.working.parents.not.eligible.partner.minimumEarning.sameAge", earningsForAge))
              else Some(messages("result.free.childcare.working.parents.not.eligible.partner.minimumEarning.differentAge", earningsForAge, earningsForPartnerAge))
            }
            else if (answers.eitherOfYouMaximumEarnings.getOrElse(false)) Some(messages(s"result.free.childcare.working.parents.not.eligible.partner.maximumEarning"))
            else None
          } else {
            if(answers.childcareCosts.contains("no")) Some(messages("result.free.childcare.working.parents.not.eligible.noCosts"))
            else if(answers.approvedProvider.contains("NO")) Some(messages("result.free.childcare.working.parents.not.eligible.approvedProvider"))
            else if (!answers.areYouInPaidWork.getOrElse(false)) Some(messages("result.free.childcare.working.parents.not.eligible.paidEmployment"))
            else if (!answers.yourMinimumEarnings.getOrElse(false)) {
              val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, answers.yourAge)
              Some(messages("result.free.childcare.working.parents.not.eligible.minimumEarning", earningsForAge))
            }
            else if (answers.yourMaximumEarnings.getOrElse(false)) Some(messages("result.free.childcare.working.parents.not.eligible.maximumEarning"))
            else None
          }
        } else None
      } else None
    }

    val resultViewModel = ResultsViewModel(
      firstParagraph = firstParagraphBuilder.buildFirstParagraph(answers),
      freeChildcareWorkingParents = getFreeChildcareWorkingParentsEligibility(answers),
      location = location,
      childrenAgeGroups = answers.childrenAgeGroups.getOrElse(Set(NoneOfThese)),
      tcSchemeInEligibilityMsg = tcSchemeInEligibilityMsgBuilder.getMessage(answers),
      hasChildcareCosts = childcareCost,
      hasCostsWithApprovedProvider = approvedProvider,
      isAnyoneInPaidEmployment = paidEmployment,
      livesWithPartner = livingWithPartner,
      yourEarnings = yourEarnings,
      partnerEarnings = partnerEarnings,
      freeChildcareWorkingParentsEligibilityMsg = freeChildcareWorkingParentsEligibilityMessage,
      taxFreeChildcareEligibilityMsg = tfcEligibilityMessage
    )

    val schemeResults: Future[SchemeResults] = eligibilityService.eligibility(answers)

    schemeResults.map(results => {
      val result = results.schemes.foldLeft(resultViewModel)((result, scheme) => getViewModelWithFreeHours(answers, setSchemeInViewModel(scheme, result, answers.taxOrUniversalCredits, answers.eligibleForTaxCredits)))
      if (result.tfc.isDefined && result.taxCreditsOrUC.contains("tc")) {
        result.copy(showTFCWarning = true, tfcWarningMessage = messages("result.schemes.tfc.tc.warning"))
      } else {
        if (result.taxCreditsOrUC.contains("uc") && result.tfc.isDefined)
          result.copy(showTFCWarning = true, tfcWarningMessage = messages("result.schemes.tfc.uc.warning"))
        else
          result
      }
    })
  }

  private def getFreeChildcareWorkingParentsEligibility(userAnswers: UserAnswers): Boolean = {
    freeChildcareWorkingParents.eligibility(userAnswers) match {
      case Eligible => true
      case _ => false
    }
  }

  private def setSchemeInViewModel(scheme: Scheme, resultViewModel: ResultsViewModel, taxCreditsOrUC: Option[String], eligibleForTaxCredits: Boolean) = {
    if (scheme.amount > 0) {
      scheme.name match {
        case TCELIGIBILITY =>
            if (taxCreditsOrUC.contains(universalCredits)) {
              resultViewModel.copy(taxCreditsOrUC = taxCreditsOrUC)
            } else {
              if(eligibleForTaxCredits) resultViewModel.copy(tc = Some(scheme.amount), taxCreditsOrUC = taxCreditsOrUC) else resultViewModel
            }
        case TFCELIGIBILITY => resultViewModel.copy(tfc = Some(scheme.amount))
        case ESCELIGIBILITY => resultViewModel.copy(esc = Some(scheme.amount))
      }
    }
    else {
      resultViewModel.copy(taxCreditsOrUC = taxCreditsOrUC)
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
    val maxFreeHoursEligibility = maxFreeHours.eligibility(answers)
    val location: Option[Location.Value] = answers.location

    freeHoursEligibility match {
      case Eligible if maxFreeHoursEligibility == Eligible => resultViewModel.copy(freeHours = Some(eligibleMaxFreeHours))
      case _ if freeChildcareWorkingParentsEligibility == Eligible => resultViewModel.copy(freeHours = Some(freeHoursForEngland))
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
}
