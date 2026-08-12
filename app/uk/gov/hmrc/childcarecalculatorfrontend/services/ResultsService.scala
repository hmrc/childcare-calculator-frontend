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
import uk.gov.hmrc.childcarecalculatorfrontend.config.{FrontendAppConfig, NmwConfig}
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeChildcareWorkingParents, FreeHours, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

//scalastyle:off
@Singleton
class ResultsService @Inject() (
    appConfig: FrontendAppConfig,
    nmwConfig: NmwConfig,
    eligibilityService: EligibilityService,
    freeHours: FreeHours,
    freeChildcareWorkingParents: FreeChildcareWorkingParents,
    taxFreeChildcare: TaxFreeChildcare,
    firstParagraphBuilder: FirstParagraphBuilder
)(using ec: ExecutionContext) {

  def getResultsViewModel(
      answers: UserAnswers,
      location: Location
  )(using req: play.api.mvc.Request[?], hc: HeaderCarrier, messages: Messages): Future[ResultsViewModel] = {

    val childcareCost = answers.childcareCosts.fold(false) {
      case YesNoNotYet.No => false
      case _              => true
    }

    val approvedProvider = answers.approvedProvider.fold(false) {
      case YesNoNotSure.No => false
      case _               => true
    }

    val livingWithPartner = answers.doYouLiveWithPartner.fold(false)(identity)

    val paidEmployment = checkIfInEmployment(answers)

    val yourEarnings    = getEarnings(answers.yourMinimumEarnings, answers.yourMaximumEarnings)
    val partnerEarnings = getEarnings(answers.partnerMinimumEarnings, answers.partnerMaximumEarnings)

    val resultViewModel = ResultsViewModel(
      firstParagraph = firstParagraphBuilder.buildFirstParagraph(answers),
      freeChildcareWorkingParents = getFreeChildcareWorkingParentsEligibility(answers),
      location = location,
      childrenAgeGroups = answers.childrenAgeGroups.getOrElse(Set(ChildAgeGroup.NoneOfThese)),
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

    schemeResults.map { results =>
      val result = results.schemes.foldLeft(resultViewModel)((result, scheme) =>
        getViewModelWithFreeHours(
          answers,
          setSchemeInViewModel(scheme, result)
        )
      )

      if (result.tfc.isDefined && answers.universalCredit.contains(true)) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.uc")))
      } else if (result.tfc.isDefined && result.esc.isDefined) {
        result.copy(tfcWarningMessage = Some(messages("result.tfc.warning.esc")))
      } else {
        result
      }
    }
  }

  private def getEarnings(moreThanMinimum: Option[Boolean], moreThanMaximum: Option[Boolean]): Option[Earnings] =
    (moreThanMinimum, moreThanMaximum) match {
      case (Some(true), Some(true)) => Some(Earnings.GreaterThanMaximum)
      case (Some(true), _)          => Some(Earnings.BetweenMinimumAndMaximum)
      case (Some(false), _)         => Some(Earnings.LessThanMinimum)
      case _                        => None
    }

  private def getFreeChildcareWorkingParentsEligibility(userAnswers: UserAnswers): Boolean =
    freeChildcareWorkingParents.eligibility(userAnswers) match {
      case Eligibility.Eligible => true
      case _                    => false
    }

  private def setSchemeInViewModel(scheme: SingleSchemeResult, resultViewModel: ResultsViewModel) =
    if (scheme.amount > 0) {
      scheme.name match {
        case Scheme.TcEligibility  => resultViewModel
        case Scheme.TfcEligibility => resultViewModel.copy(tfc = Some(scheme.amount))
        case Scheme.EscEligibility => resultViewModel.copy(esc = Some(scheme.amount))
      }
    } else {
      resultViewModel
    }

  private def checkIfInEmployment(userAnswers: UserAnswers) =
    if (userAnswers.areYouInPaidWork.isDefined) {
      userAnswers.areYouInPaidWork.getOrElse(false)
    } else {
      userAnswers.whoIsInPaidEmployment.fold(false) {
        case YouPartnerBothNeither.Neither => false
        case _                             => true
      }
    }

  private def getViewModelWithFreeHours(answers: UserAnswers, resultViewModel: ResultsViewModel) = {
    val freeHoursEligibility                   = freeHours.eligibility(answers)
    val freeChildcareWorkingParentsEligibility = freeChildcareWorkingParents.eligibility(answers)
    val location: Option[Location]             = answers.location

    freeHoursEligibility match {
      case _
          if freeChildcareWorkingParentsEligibility == Eligibility.Eligible && answers.isChildAgedThreeOrFour.getOrElse(
            true
          ) =>
        resultViewModel.copy(freeHours = Some(eligibleMaxFreeHours))
      case _ if freeChildcareWorkingParentsEligibility == Eligibility.Eligible =>
        resultViewModel.copy(freeHours = Some(appConfig.maxFreeHoursAmount))
      case Eligibility.Eligible => getFreeHoursForLocation(location, resultViewModel)
      case _                    => resultViewModel
    }
  }

  private def getFreeHoursForLocation(optionLocation: Option[Location], resultViewModel: ResultsViewModel) =
    optionLocation.fold(resultViewModel) {
      case Location.England         => resultViewModel.copy(freeHours = Some(freeHoursForEngland))
      case Location.Scotland        => resultViewModel.copy(freeHours = Some(freeHoursForScotland))
      case Location.Wales           => resultViewModel.copy(freeHours = Some(freeHoursForWales))
      case Location.NorthernIreland => resultViewModel.copy(freeHours = Some(freeHoursForNI))
    }

  private def tfcEligibilityMessage(answers: UserAnswers)(using messages: Messages): Option[String] = {
    lazy val hasEligibleChildren    = answers.hasChildEligibleForTfc
    lazy val youInPaidWork          = answers.areYouInPaidWork.getOrElse(false)
    lazy val earningsForAge         = nmwConfig.getEarningsForAgeRange(LocalDate.now, answers.yourAge)
    lazy val youEligibleMinEarnings = answers.yourMinimumEarnings.getOrElse(false)
    lazy val youEligibleMaxEarnings = !answers.yourMaximumEarnings.getOrElse(false)
    lazy val hasPartner             = answers.doYouLiveWithPartner.getOrElse(false)
    lazy val bothInPaidWork         = answers.whoIsInPaidEmployment.contains(YouPartnerBothNeither.Both)
    lazy val earningsForPartnerAge =
      nmwConfig.getEarningsForAgeRange(LocalDate.now, answers.yourPartnersAge)
    lazy val bothEligibleMinEarnings =
      answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false)
    lazy val bothEligibleMaxEarnings = !answers.eitherOfYouMaximumEarnings.getOrElse(false)

    lazy val msgKey = "result.tfc.ineligible"

    taxFreeChildcare.eligibility(answers) match {
      case Eligibility.Eligible => None
      case _ if answers.childcareCosts.contains(YesNoNotYet.No) || answers.approvedProvider.contains(YesNoNotSure.No) =>
        Some(messages(s"$msgKey.noCostsWithApprovedProvider"))
      case _ if hasPartner && !bothInPaidWork =>
        Some(messages(s"$msgKey.partner.paidEmployment"))
      case _ if hasPartner && !bothEligibleMinEarnings =>
        if (earningsForAge == earningsForPartnerAge)
          Some(messages(s"$msgKey.partner.minimumEarning.sameAge", earningsForAge))
        else Some(messages(s"$msgKey.partner.minimumEarning.differentAge", earningsForAge, earningsForPartnerAge))
      case _ if hasPartner && !bothEligibleMaxEarnings =>
        Some(messages(s"$msgKey.partner.maximumEarning"))
      case _ if !hasPartner && !youInPaidWork =>
        Some(messages(s"$msgKey.paidEmployment"))
      case _ if !hasPartner && !youEligibleMinEarnings =>
        Some(messages(s"$msgKey.minimumEarning", earningsForAge))
      case _ if !hasPartner && !youEligibleMaxEarnings =>
        Some(messages(s"$msgKey.maximumEarning"))
      case _ if !hasEligibleChildren =>
        Some(messages(s"$msgKey.noEligibleChild"))
      case _ => None
    }
  }

  private def freeChildcareWorkingParentsEligibilityMessage(
      answers: UserAnswers
  )(using messages: Messages): Option[String] = {
    lazy val inEngland              = answers.location.contains(Location.England)
    lazy val hasEligibleChildren    = answers.childrenAgeGroups.exists(!_.contains(ChildAgeGroup.NoneOfThese))
    lazy val youInPaidWork          = answers.areYouInPaidWork.getOrElse(false)
    lazy val earningsForAge         = nmwConfig.getEarningsForAgeRange(LocalDate.now, answers.yourAge)
    lazy val youEligibleMinEarnings = answers.yourMinimumEarnings.getOrElse(false)
    lazy val youEligibleMaxEarnings = !answers.yourMaximumEarnings.getOrElse(false)
    lazy val hasPartner             = answers.doYouLiveWithPartner.getOrElse(false)
    lazy val bothInPaidWork         = answers.whoIsInPaidEmployment.contains(YouPartnerBothNeither.Both)
    lazy val earningsForPartnerAge =
      nmwConfig.getEarningsForAgeRange(LocalDate.now, answers.yourPartnersAge)
    lazy val bothEligibleMinEarnings =
      answers.partnerMinimumEarnings.getOrElse(false) && answers.yourMinimumEarnings.getOrElse(false)
    lazy val bothEligibleMaxEarnings = !answers.eitherOfYouMaximumEarnings.getOrElse(false)

    lazy val msgKey = "result.free.childcare.working.parents.ineligible"
    freeChildcareWorkingParents.eligibility(answers) match {
      case Eligibility.Eligible => None
      case _ if !inEngland      => None
      case _ if !hasEligibleChildren =>
        Some(messages(s"$msgKey.noChildrenInAgeRange"))
      case _ if hasPartner && !bothInPaidWork =>
        Some(messages(s"$msgKey.partner.paidEmployment"))
      case _ if hasPartner && !bothEligibleMinEarnings =>
        if (earningsForAge == earningsForPartnerAge)
          Some(messages(s"$msgKey.partner.minimumEarning.sameAge", earningsForAge))
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
