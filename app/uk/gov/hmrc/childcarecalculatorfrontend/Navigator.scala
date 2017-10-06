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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> (ua => locationRoute(ua)),
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> (ua => costRoute(ua)),
    ApprovedProviderId -> (ua => approvedChildCareRoute(ua)),
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    HasYourTaxCodeBeenAdjustedId -> (ua => taxCodeAdjustedRoute(ua)),
    HasYourPartnersTaxCodeBeenAdjustedId -> (ua => partnerTaxCodeAdjustedRoute(ua)),
    DoYouLiveWithPartnerId -> (ua => doYouLiveRoute(ua)),
    AreYouInPaidWorkId -> (ua => areYouInPaidWorkRoute(ua)),
    PaidEmploymentId -> (ua => paidEmploymentRoute(ua)),
    WhoIsInPaidEmploymentId -> (ua => workHoursRoute(ua)),
    ParentWorkHoursId -> (ua => parentWorkHoursRoute(ua)),
    PartnerWorkHoursId -> (ua => partnerWorkHoursRoute(ua)),
	  VouchersId -> (vouchers => vouchersRoute(vouchers)),
    DoYouGetAnyBenefitsId -> (ua => doYouGetAnyBenefitsRoute(ua)),
    DoYouOrYourPartnerGetAnyBenefitsId -> (ua => doYouOrYourPartnerGetAnyBenefitsRoute(ua))
  )

 private def locationRoute(answers: UserAnswers) = answers.location match {
		case Some("northernIreland") => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
		case Some(_) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
		case _ => routes.SessionExpiredController.onPageLoad()
	  }

  private def doYouLiveRoute(answers: UserAnswers) = {
    if(answers.doYouLiveWithPartner.contains(true)){
      routes.PaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }
  }

  private def areYouInPaidWorkRoute(answers: UserAnswers) = {
    if(answers.areYouInPaidWork.contains(true)){
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def paidEmploymentRoute(answers: UserAnswers) = {
    if(answers.paidEmployment.contains(true)){
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def workHoursRoute(answers: UserAnswers) = {
    val You = YouPartnerBothEnum.YOU.toString
    val Partner = YouPartnerBothEnum.PARTNER.toString
    val Both = YouPartnerBothEnum.BOTH.toString

    answers.whoIsInPaidEmployment match {
      case Some(You) => routes.ParentWorkHoursController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case Some(Both) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerWorkHoursRoute(answers: UserAnswers) = {
    val Both = YouPartnerBothEnum.BOTH.toString

    if(answers.whoIsInPaidEmployment.contains(Both)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  private def parentWorkHoursRoute(answers: UserAnswers) = {
    val You = YouPartnerBothEnum.YOU.toString
    val Partner = YouPartnerBothEnum.PARTNER.toString
    val Both = YouPartnerBothEnum.BOTH.toString

    answers.whoIsInPaidEmployment match {
      case Some(You) => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      case Some(Both) => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def costRoute(answers: UserAnswers) = answers.childcareCosts match {
    case Some(ChildcareConstants.no) =>
      if(answers.isEligibleForFreeHours == Eligible && answers.location.contains("england") && answers.childAgedThreeOrFour.getOrElse(false)) {
        routes.FreeHoursInfoController.onPageLoad()
      } else if(answers.isEligibleForFreeHours == Eligible) {
        routes.FreeHoursResultController.onPageLoad()
      } else {
        routes.FreeHoursResultController.onPageLoad()
      }

    case Some(_) => routes.ApprovedProviderController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def taxCodeAdjustedRoute(answers: UserAnswers): Call =
    (answers.hasPartnerInPaidWork, answers.hasYourTaxCodeBeenAdjusted) match {
    case (true, Some(false)) => routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    case (_, Some(true)) => routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
    case (false, Some(false)) => routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def partnerTaxCodeAdjustedRoute(answers: UserAnswers): Call = {
      if (answers.hasYourPartnersTaxCodeBeenAdjusted.contains(true)) {
        routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
      } else if (answers.hasYourPartnersTaxCodeBeenAdjusted.contains(false)) {
        routes.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(NormalMode)
      } else {
        routes.SessionExpiredController.onPageLoad()
      }
    }

 private def approvedChildCareRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString

    answers.approvedProvider match {
      case Some(No) => {
        if(answers.isEligibleForFreeHours == Eligible && answers.location.contains(LocationEnum.ENGLAND.toString)){
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      }
      case Some(_) =>  if (answers.isEligibleForFreeHours == Eligible) routes.FreeHoursInfoController.onPageLoad()
                       else routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

 private def vouchersRoute(answers: UserAnswers) = answers.vouchers match {
    case Some(ChildcareConstants.yes) => routes.WhoGetsVouchersController.onPageLoad(NormalMode)
    case Some(_) => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def doYouGetAnyBenefitsRoute(answers: UserAnswers) = {
    if(answers.doYouGetAnyBenefits.contains(false)){
      routes.WhatsYourAgeController.onPageLoad(NormalMode)
    } else {
      //TODO: Go to new Which benefits do you get checkbox page
      routes.WhatToTellTheCalculatorController.onPageLoad()
    }
  }

  private def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers) = {
    if(answers.doYouOrYourPartnerGetAnyBenefits.contains(false)){
      routes.WhatsYourAgeController.onPageLoad(NormalMode)
    } else {
      routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
    }
  }


  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(
  )

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.WhatToTellTheCalculatorController.onPageLoad())
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
