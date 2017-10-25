/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ParentEmploymentIncomeCYId, PartnerEmploymentIncomeCYId, PartnerPaidPensionCYId, PartnerPaidWorkCYId, YouPaidPensionCYId, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.navigation._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.currentYear
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject()(schemes: Schemes,
                          maxEarningsNav: MaximumEarningsNavigation = new MaximumEarningsNavigation(),
                          selfEmpOrApprNav: SelfEmployedOrApprenticeNavigation = new SelfEmployedOrApprenticeNavigation(),
                          currentYearIncomeNav: CurrentYearIncomeNavigation = new CurrentYearIncomeNavigation(),
                          pensionNav: PensionNavigation = new PensionNavigation()) {

  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString


  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> costRoute,
    ApprovedProviderId -> approvedChildCareRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> doYouLiveRoute,
    AreYouInPaidWorkId -> areYouInPaidWorkRoute,
    PaidEmploymentId -> paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> workHoursRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    PartnerWorkHoursId -> partnerWorkHoursRoute,
    HasYourTaxCodeBeenAdjustedId -> taxCodeAdjustedRoute,
    DoYouKnowYourAdjustedTaxCodeId -> DoYouKnowYourAdjustedTaxCodeRoute,
    WhatIsYourTaxCodeId -> whatIsYourTaxCodeRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> partnerTaxCodeAdjustedRoute,
    DoYouKnowYourPartnersAdjustedTaxCodeId -> doYouKnowPartnersTaxCodeRoute,
    WhatIsYourPartnersTaxCodeId -> whatIsYourPartnersTaxCodeRoute,
    YourChildcareVouchersId -> parentsVouchersRoute,
    PartnerChildcareVouchersId -> partnersVouchersRoute,
    EitherGetsVouchersId -> vouchersRoute,
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> doYouGetAnyBenefitsRoute,
    DoYouOrYourPartnerGetAnyBenefitsId -> doYouOrYourPartnerGetAnyBenefitsRoute,
    WhoGetsBenefitsId -> whoGetsBenefitsRoute,
    WhichBenefitsYouGetId -> whichBenefitsYouGetRoute,
    WhichBenefitsPartnerGetId -> whichBenefitsPartnerGetRoute,
    YourAgeId -> yourAgeRoute,
    YourPartnersAgeId -> yourPartnerAgeRoute,
    YourMinimumEarningsId -> yourMinimumEarningsRoute,
    PartnerMinimumEarningsId -> partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId -> selfEmpOrApprNav.areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> selfEmpOrApprNav.partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> yourSelfEmployedRoute,
    PartnerSelfEmployedId -> partnerSelfEmployedRoute,
    YourMaximumEarningsId -> maxEarningsNav.yourMaximumEarningsRoute,
    PartnerMaximumEarningsId -> maxEarningsNav.partnerMaximumEarningsRoute,
    EitherOfYouMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    PartnerPaidWorkCYId -> (_ =>  currentYearIncomeNav.partnerPaidWorkCYRoute),
    ParentPaidWorkCYId -> (_ => currentYearIncomeNav.parentPaidWorkCYRoute),
    ParentEmploymentIncomeCYId -> (_ => currentYearIncomeNav.parentEmploymentIncomeCYRoute),
    PartnerEmploymentIncomeCYId -> (_ =>currentYearIncomeNav.partnerEmploymentIncomeCYRoute),
    EmploymentIncomeCYId -> (_ => currentYearIncomeNav.employmentIncomeCYRoute),
    YouPaidPensionCYId -> (answers => pensionNav.yourPensionRoute(answers, currentYear)),
    PartnerPaidPensionCYId -> (answers => pensionNav.partnerPensionRoute(answers, currentYear)),
    BothPaidPensionCYId -> (answers => pensionNav.bothPensionRoute(answers, currentYear))
  )

  private def defineWhoGetsBenefits(whoGetsBenefits: Option[String]): String = {
    whoGetsBenefits match {
      case Some(You) => You
      case Some(Partner) => Partner
      case Some(Both) => Both
      case _ => You
    }
  }

  private def whichBenefitsYouGetRoute(answers: UserAnswers) = {
    defineWhoGetsBenefits(answers.whoGetsBenefits) match {
      case You => routes.YourAgeController.onPageLoad(NormalMode)
      case Both => routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def whichBenefitsPartnerGetRoute(answers: UserAnswers) = {
    defineWhoGetsBenefits(answers.whoGetsBenefits) match {
      case Partner => routes.YourPartnersAgeController.onPageLoad(NormalMode)
      case Both => routes.YourAgeController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def locationRoute(answers: UserAnswers) = {
    val Ni = LocationEnum.NORTHERNIRELAND.toString
    answers.location match {
      case Some(Ni) => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      case Some(_) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def doYouLiveRoute(answers: UserAnswers) = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      routes.PaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }
  }

  private def areYouInPaidWorkRoute(answers: UserAnswers) = {
    if (answers.areYouInPaidWork.contains(true)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def paidEmploymentRoute(answers: UserAnswers) = {
    if (answers.paidEmployment.contains(true)) {
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def workHoursRoute(answers: UserAnswers) = {
    answers.whoIsInPaidEmployment match {
      case Some(You) => routes.ParentWorkHoursController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case Some(Both) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerWorkHoursRoute(answers: UserAnswers) = {
    if (answers.whoIsInPaidEmployment.contains(Both)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  private def costRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString
    answers.childcareCosts match {
      case Some(No) =>
        if (answers.isEligibleForMaxFreeHours == Eligible) {
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      case Some(_) => routes.ApprovedProviderController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def approvedChildCareRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString

    answers.approvedProvider match {
      case Some(No) =>
        if (answers.isEligibleForMaxFreeHours == Eligible) {
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      case Some(_) => if (answers.isEligibleForFreeHours == Eligible) routes.FreeHoursInfoController.onPageLoad()
      else routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def taxCodeAdjustedRoute(answers: UserAnswers): Call = {
    answers.hasYourTaxCodeBeenAdjusted match {
      case Some(true) => routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
      case Some(false) =>
        if (answers.hasBothInPaidWork) {
          routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        } else {
          routes.YourChildcareVouchersController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def DoYouKnowYourAdjustedTaxCodeRoute(answers: UserAnswers): Call = {
    answers.doYouKnowYourAdjustedTaxCode match {
      case Some(true) => routes.WhatIsYourTaxCodeController.onPageLoad(NormalMode)
      case Some(false) =>
        if (answers.hasPartnerInPaidWork | answers.hasBothInPaidWork) {
          routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        } else {
          routes.YourChildcareVouchersController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def whatIsYourTaxCodeRoute(answers: UserAnswers): Call = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      answers.whoIsInPaidEmployment match {
        case Some(Both) => routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        case Some(You) => routes.YourChildcareVouchersController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    } else {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }
  }

  private def partnerTaxCodeAdjustedRoute(answers: UserAnswers): Call = {
    answers.hasYourPartnersTaxCodeBeenAdjusted match {
      case Some(true) => routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
      case Some(false) =>
        if (answers.hasBothInPaidWork) {
          routes.EitherGetsVouchersController.onPageLoad(NormalMode)
        } else {
          routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def doYouKnowPartnersTaxCodeRoute(answers: UserAnswers): Call =
    answers.doYouKnowYourPartnersAdjustedTaxCode match {
      case Some(true) => routes.WhatIsYourPartnersTaxCodeController.onPageLoad(NormalMode)
      case Some(false) =>
        if (answers.hasPartnerInPaidWork) {
          routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
        } else {
          routes.EitherGetsVouchersController.onPageLoad(NormalMode)
        }
      case None => routes.SessionExpiredController.onPageLoad()
    }

  private def whatIsYourPartnersTaxCodeRoute(answers: UserAnswers): Call = {
    if (answers.hasBothInPaidWork) {
      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
    } else if (answers.hasPartnerInPaidWork) {
      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private def parentsVouchersRoute(answers: UserAnswers) = {
    answers.yourChildcareVouchers match {
      case Some(_) =>
        if (answers.doYouLiveWithPartner.contains(true)) {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        } else {
          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnersVouchersRoute(answers: UserAnswers) = {
    answers.partnerChildcareVouchers match {
      case Some(_) =>
        if (answers.doYouLiveWithPartner.contains(true)) {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        } else {
          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def vouchersRoute(answers: UserAnswers): Call = {
    val Yes = YesNoUnsureEnum.YES.toString

    answers.eitherGetsVouchers match {
      case Some(Yes) => if (answers.doYouLiveWithPartner.contains(true)) {
        if (answers.whoIsInPaidEmployment.contains(Both)) {
          routes.WhoGetsVouchersController.onPageLoad(NormalMode)
        } else {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      } else {
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
      }
      case Some(_) =>
        if (answers.doYouLiveWithPartner.contains(true)) {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        } else {
          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def doYouGetAnyBenefitsRoute(answers: UserAnswers) = {
    if (answers.doYouGetAnyBenefits.contains(false)) {
      routes.YourAgeController.onPageLoad(NormalMode)
    } else {
      routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
    }
  }

  private def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers) = {
    answers.doYouOrYourPartnerGetAnyBenefits match {
      case Some(false) =>
        if (answers.hasPartnerInPaidWork) {
          routes.YourPartnersAgeController.onPageLoad(NormalMode)
        } else {
          routes.YourAgeController.onPageLoad(NormalMode)
        }
      case Some(true) => routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def whoGetsBenefitsRoute(answers: UserAnswers) = {
    answers.whoGetsBenefits match {
      case Some(You) | Some(Both) => routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
      case Some(Partner) => routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def yourAgeRoute(answers: UserAnswers) = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      answers.whoIsInPaidEmployment match {
        case Some(Both) => routes.YourPartnersAgeController.onPageLoad(NormalMode)
        case Some(You) => routes.YourMinimumEarningsController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    } else {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }
  }

  private def yourPartnerAgeRoute(answers: UserAnswers) = {
    if (answers.hasBothInPaidWork) {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    } else if (answers.hasPartnerInPaidWork) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private def yourMinimumEarningsRoute(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val areYouInPaidWork = answers.areYouInPaidWork.getOrElse(true)
    val whoIsInPaidEmp = answers.whoIsInPaidEmployment
    val hasMinimumEarnings = answers.yourMinimumEarnings

    (hasMinimumEarnings, hasPartner, areYouInPaidWork, whoIsInPaidEmp) match {
      case (Some(true), false, true, _) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
      case (Some(true), true, _, Some(You)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
      case (Some(true), true, _, Some(Both)) => routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
      case (Some(false), false, true, _) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case (Some(false), true, _, Some(You)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case (Some(false), true, _, Some(Both)) => routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerMinimumEarningsRoute(answers: UserAnswers) = {
    val yourMinEarnings = answers.yourMinimumEarnings
    val partnerMinEarnings = answers.partnerMinimumEarnings

    (yourMinEarnings, partnerMinEarnings) match {
      case (Some(true), Some(true)) => routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
      case (Some(false), Some(true)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case (Some(false), Some(false)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case (Some(true), Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case (_, Some(true)) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      case (_, Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def yourSelfEmployedRoute(answers: UserAnswers) = {
    val yourMinEarnings = answers.yourMinimumEarnings
    val partnerMinEarnings = answers.partnerMinimumEarnings

    if (answers.doYouLiveWithPartner.contains(true)) {
      if (answers.whoIsInPaidEmployment.contains(You) | answers.whoIsInPaidEmployment.contains(Partner)) {
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      } else {
        (yourMinEarnings, partnerMinEarnings) match {
          case (Some(false), Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
          case (Some(true), Some(false)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
          case (Some(false), Some(true)) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
          case _ => routes.SessionExpiredController.onPageLoad()
        }
      }
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  private def partnerSelfEmployedRoute(answers: UserAnswers) = {
    val yourMinEarnings = answers.yourMinimumEarnings
    val partnerMinEarnings = answers.partnerMinimumEarnings

    if (answers.hasPartnerInPaidWork) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if (answers.hasBothInPaidWork) {
      (yourMinEarnings, partnerMinEarnings) match {
        case (Some(false), Some(false)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case (Some(true), Some(false)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(
  )

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = {
    answers =>
      mode match {
        case NormalMode =>
          routeMap.getOrElse(id, (_: UserAnswers) => routes.WhatToTellTheCalculatorController.onPageLoad())(answers)
        case CheckMode =>
          editRouteMap.getOrElse(id, (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad())(answers)
      }
  }
}
