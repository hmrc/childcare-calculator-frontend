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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, SelfEmployedOrApprenticeOrNeitherEnum, YesNoUnsureEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MaximumHoursNavigator @Inject() () extends SubNavigator {

  override protected def routeMap: Map[Identifier, UserAnswers => Call] = Map(
    DoYouLiveWithPartnerId -> doYouLiveRoute,
    AreYouInPaidWorkId -> areYouInPaidWorkRoute,
    PaidEmploymentId -> paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> whoIsInPaidWorkRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    PartnerWorkHoursId -> partnerWorkHoursRoute,
    HasYourTaxCodeBeenAdjustedId -> hasYourTaxCodeBeenAdjusted,
    DoYouKnowYourAdjustedTaxCodeId -> doYouKnowYourAdjustedTaxCodeRoute,
    WhatIsYourTaxCodeId -> whatIsYourTaxCodeRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> hasYourPartnersTaxCodeBeenAdjusted,
    DoYouKnowYourPartnersAdjustedTaxCodeId -> doYouKnowPartnersTaxCodeRoute,
    WhatIsYourPartnersTaxCodeId -> whatIsYourPartnersTaxCodeRoute,
    YourChildcareVouchersId -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    PartnerChildcareVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    EitherGetsVouchersId -> eitherGetVouchersRoute,
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
    AreYouSelfEmployedOrApprenticeId -> areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> yourSelfEmployedRoute,
    PartnerSelfEmployedId -> partnerSelfEmployedRoute,
    YourMaximumEarningsId -> yourMaximumEarningsRoute,
    PartnerMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    EitherOfYouMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode))
  )

  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString
  val SelfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString

  def doYouLiveRoute(answers: UserAnswers): Call = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      routes.PaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }
  }

  def areYouInPaidWorkRoute(answers: UserAnswers): Call = {
    if (answers.areYouInPaidWork.contains(true)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  def paidEmploymentRoute(answers: UserAnswers): Call = {
    if(answers.paidEmployment.contains(true)){
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  def whoIsInPaidWorkRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.PartnerWorkHoursController.onPageLoad(NormalMode)
    }
  }

  def partnerWorkHoursRoute(answers: UserAnswers): Call = {
    if(answers.whoIsInPaidEmployment.contains(Both)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  def hasYourTaxCodeBeenAdjusted(answers: UserAnswers): Call = {
    if(answers.hasYourTaxCodeBeenAdjusted.contains(YesNoUnsureEnum.YES.toString)){
      routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  def doYouKnowYourAdjustedTaxCodeRoute(answers: UserAnswers): Call = {
    if(answers.doYouKnowYourAdjustedTaxCode.contains(true)) {
      routes.WhatIsYourTaxCodeController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  def doYouKnowPartnersTaxCodeRoute(answers: UserAnswers): Call = {
    if(answers.doYouKnowYourPartnersAdjustedTaxCode.contains(true)) {
      routes.WhatIsYourPartnersTaxCodeController.onPageLoad(NormalMode)
    } else if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Partner)) {
      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
    }
  }

  def whatIsYourTaxCodeRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Both)) {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    } else {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }
  }

  def hasYourPartnersTaxCodeBeenAdjusted (answers: UserAnswers): Call = {
    if(answers.hasYourPartnersTaxCodeBeenAdjusted.contains(YesNoUnsureEnum.YES.toString)){
      routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Partner)) {
      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    } else {
      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
    }
  }

  def whatIsYourPartnersTaxCodeRoute(answers: UserAnswers): Call = {
    if (answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Both)) {
      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
    } else {
      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    }
  }

  def eitherGetVouchersRoute(answers: UserAnswers): Call = {
    if(answers.eitherGetsVouchers.contains(YesNoUnsureEnum.YES.toString)) {
      routes.WhoGetsVouchersController.onPageLoad(NormalMode)
    } else {
      routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
    }
  }

  def doYouGetAnyBenefitsRoute(answers: UserAnswers): Call = {
    if(answers.doYouGetAnyBenefits.contains(false)) {
      routes.YourAgeController.onPageLoad(NormalMode)
    } else {
      routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
    }
  }

  def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers): Call = {
    if(answers.doYouOrYourPartnerGetAnyBenefits.contains(true)) {
      routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Partner)) {
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    } else {
      routes.YourAgeController.onPageLoad(NormalMode)
    }
  }

  def whoGetsBenefitsRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoGetsBenefits).contains(Partner)) {
      routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
    } else {
      routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
    }
  }

  def whichBenefitsYouGetRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoGetsBenefits).contains(Both)) {
      routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
    } else {
      routes.YourAgeController.onPageLoad(NormalMode)
    }
  }

  def whichBenefitsPartnerGetRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoGetsBenefits).contains(Both)) {
      routes.YourAgeController.onPageLoad(NormalMode)
    } else {
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }
  }

  def yourAgeRoute(answers: UserAnswers) = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }
  }

  def yourPartnerAgeRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Partner)) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }
  }

  def yourMinimumEarningsRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Both)) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }
  }

  def partnerMinimumEarningsRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(Partner)) {
      if (answers.partnerMinimumEarnings.contains(true)) {
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    } else if(answers.yourMinimumEarnings.contains(false)) {
        routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      } else {
      if(answers.partnerMinimumEarnings.contains(true)) {
        routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    }
  }

  def areYouSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call = {
    if(answers.areYouSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.YourSelfEmployedController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if(answers.partnerMinimumEarnings.contains(false)) {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    } else {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }
  }

  def partnerSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call = {
    if(answers.partnerSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    } else if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  def yourSelfEmployedRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(You)) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if(answers.partnerMinimumEarnings.contains(true)) {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }
  }

  def partnerSelfEmployedRoute(answers: UserAnswers): Call = {
    if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  def yourMaximumEarningsRoute(answers: UserAnswers): Call = {
    if(answers.partnerMinimumEarnings.contains(true)) {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }
}
