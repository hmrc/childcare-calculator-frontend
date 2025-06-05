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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ParentsBenefits, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}

import javax.inject.Inject

// scalastyle:off number.of.methods
class MaximumHoursNavigator @Inject() (
    schemes: Schemes,
    freeChildcareWorkingParents: FreeChildcareWorkingParents,
    tfc: TaxFreeChildcare,
    esc: EmploymentSupportedChildcare
) extends SubNavigator {

  override protected def routeMap: Map[Identifier, UserAnswers => Call] = Map(
    DoYouLiveWithPartnerId            -> doYouLiveRoute,
    AreYouInPaidWorkId                -> areYouInPaidWorkRoute,
    WhoIsInPaidEmploymentId           -> whoIsInPaidWorkRoute,
    YourChildcareVouchersId           -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    PartnerChildcareVouchersId        -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    WhoGetsVouchersId                 -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId             -> doYouGetAnyBenefitsRoute,
    DoesYourPartnerGetAnyBenefitsId   -> doesYourPartnerGetAnyBenefitsRoute,
    YourAgeId                         -> yourAgeRoute,
    YourPartnersAgeId                 -> yourPartnerAgeRoute,
    YourMinimumEarningsId             -> yourMinimumEarningsRoute,
    PartnerMinimumEarningsId          -> partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId  -> areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId                -> yourSelfEmployedRoute,
    PartnerSelfEmployedId             -> partnerSelfEmployedRoute,
    YourMaximumEarningsId             -> yourMaximumEarningsRoute,
    PartnerMaximumEarningsId          -> partnerMaximumEarningsRoute,
    EitherOfYouMaximumEarningsId      -> eitherMaximumEarningsRoute,
    UniversalCreditId                 -> universalCreditRoutes
  )

  private val SelfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString

  private def doYouLiveRoute(answers: UserAnswers): Call =
    if (answers.doYouLiveWithPartner.contains(true)) {
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }

  private def areYouInPaidWorkRoute(answers: UserAnswers): Call =
    if (answers.areYouInPaidWork.contains(true)) {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.ResultController.onPageLoad()
    }

  private def whoIsInPaidWorkRoute(answers: UserAnswers): Call =
    answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment) match {
      case `you`     => routes.YourChildcareVouchersController.onPageLoad(NormalMode)
      case `partner` => routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
      case `both`    => routes.WhoGetsVouchersController.onPageLoad(NormalMode)
      case `neither` => routes.ResultController.onPageLoad()
      case _         => SessionExpiredRouter.route(getClass.getName, "whoIsInPaidWorkRoute", Some(answers))
    }

  private def doYouGetAnyBenefitsRoute(answers: UserAnswers): Call = {
    def shouldRedirectToResults: Boolean =
      answers.whoIsInPaidEmployment.contains(`partner`) &&
        answers.partnerChildcareVouchers.contains(false) &&
        answers.doYouGetAnyBenefits.contains(Set(ParentsBenefits.NoneOfThese))

    answers.doYouLiveWithPartner match {
      case Some(true) if shouldRedirectToResults => routes.ResultController.onPageLoad()
      case Some(true)  => routes.DoesYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
      case Some(false) => routes.YourAgeController.onPageLoad(NormalMode)

      case None => SessionExpiredRouter.route(getClass.getName, "doYouGetAnyBenefitsRoute", Some(answers))
    }
  }

  private def doesYourPartnerGetAnyBenefitsRoute(answers: UserAnswers): Call = {
    def shouldRedirectToResults: Boolean =
      answers.yourChildcareVouchers.contains(false) &&
        answers.doesYourPartnerGetAnyBenefits.contains(Set(ParentsBenefits.NoneOfThese))

    answers.whoIsInPaidEmployment match {
      case Some(`you`) if shouldRedirectToResults => routes.ResultController.onPageLoad()
      case Some(`you` | `both`)                   => routes.YourAgeController.onPageLoad(NormalMode)
      case Some(`partner`)                        => routes.YourPartnersAgeController.onPageLoad(NormalMode)

      case _ => SessionExpiredRouter.route(getClass.getName, "doesYourPartnerGetAnyBenefitsRoute", Some(answers))
    }
  }

  private def yourAgeRoute(answers: UserAnswers): Call =
    if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
    } else {
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }

  private def yourPartnerAgeRoute(answers: UserAnswers): Call =
    routes.AverageWeeklyEarningController.onPageLoad(NormalMode)

  private def yourMinimumEarningsRoute(answers: UserAnswers): Call =
    if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(both)) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else if (answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }

  private def partnerMinimumEarningsRoute(answers: UserAnswers): Call =
    if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(partner)) {
      if (answers.partnerMinimumEarnings.contains(true)) {
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    } else if (answers.yourMinimumEarnings.contains(false)) {
      routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    } else {
      if (answers.partnerMinimumEarnings.contains(true)) {
        routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    }

  private def areYouSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call =
    if (answers.areYouSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.YourSelfEmployedController.onPageLoad(NormalMode)
    } else if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.UniversalCreditController.onPageLoad(NormalMode)
    } else if (answers.partnerMinimumEarnings.contains(false)) {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    } else {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }

  private def partnerSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call =
    if (answers.partnerSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    } else if (answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.UniversalCreditController.onPageLoad(NormalMode)
    }

  private def yourSelfEmployedRoute(answers: UserAnswers): Call =
    if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.UniversalCreditController.onPageLoad(NormalMode)
    } else if (answers.partnerMinimumEarnings.contains(true)) {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }

  private def partnerSelfEmployedRoute(answers: UserAnswers): Call =
    if (answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.UniversalCreditController.onPageLoad(NormalMode)
    }

  private def yourMaximumEarningsRoute(answers: UserAnswers): Call = {
    val yourMaxEarnings = answers.yourMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, yourMaxEarnings)
  }

  private def partnerMaximumEarningsRoute(answers: UserAnswers): Call = {
    val partnerMaxEarnings = answers.partnerMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, partnerMaxEarnings)
  }

  private def eitherMaximumEarningsRoute(answers: UserAnswers): Call = {
    val eitherMaxEarnings = answers.eitherOfYouMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, eitherMaxEarnings)
  }

  private def maximumEarningsRedirection(answers: UserAnswers, maxEarnings: Boolean): Call = {

    def getCallForVoucherValue(voucherValue: Boolean): Call =
      if (!voucherValue && maxEarnings) {
        routes.ResultController.onPageLoad()
      } else {
        routes.UniversalCreditController.onPageLoad(NormalMode)
      }

    (answers.yourChildcareVouchers, answers.partnerChildcareVouchers) match {
      case (Some(parentVoucher), _)  => getCallForVoucherValue(parentVoucher)
      case (_, Some(partnerVoucher)) => getCallForVoucherValue(partnerVoucher)
      case _ =>
        answers.whoGetsVouchers.fold(
          SessionExpiredRouter.route(getClass.getName, "maximumEarningsRedirection", Some(answers))
        )(_ => routes.UniversalCreditController.onPageLoad(NormalMode))
    }
  }

  private def universalCreditRoutes(answers: UserAnswers): Call =
    if (schemes.allSchemesDetermined(answers)) {

      val isNotEligibleForTfc           = tfc.eligibility(answers) == NotEligible
      val isNotEligibleForEsc           = esc.eligibility(answers) == NotEligible
      val isNotEligibleForBothTfcAndEsc = !(isNotEligibleForTfc && isNotEligibleForEsc)
      val isFreeChildcareEligible       = freeChildcareWorkingParents.eligibility(answers) == Eligible

      (isNotEligibleForBothTfcAndEsc, answers.hasApprovedCosts, isFreeChildcareEligible) match {
        case (true, Some(true), true)  => routes.MaxFreeHoursInfoController.onPageLoad()
        case (true, Some(true), false) => routes.NoOfChildrenController.onPageLoad(NormalMode)
        case _                         => routes.ResultController.onPageLoad()
      }

    } else {
      SessionExpiredRouter.route(getClass.getName, "universalCreditRoutes", Some(answers))
    }

}
