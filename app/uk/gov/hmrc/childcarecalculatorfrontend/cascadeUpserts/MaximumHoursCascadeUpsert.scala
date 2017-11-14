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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import javax.inject.Inject

import play.api.libs.json.{JsString, JsValue,JsBoolean}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{SelfEmployedOrApprenticeOrNeitherEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class MaximumHoursCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      DoYouLiveWithPartnerId.toString() -> ((v, cm) => storeDoYouLiveWithPartner(v, cm)),
      PaidEmploymentId.toString -> ((v, cm) => storePaidEmployment(v, cm)),
      WhoIsInPaidEmploymentId.toString -> ((v, cm) => storeWhoIsInPaidEmployment(v, cm)),
      AreYouInPaidWorkId.toString -> ((v, cm) => storeAreYouInPaidWork(v, cm)),
     HasYourTaxCodeBeenAdjustedId.toString -> ((v, cm) => storeHasYourTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourAdjustedTaxCodeId.toString -> ((v, cm) => storeDoYouKnowYourAdjustedTaxCode(v, cm)),
      HasYourPartnersTaxCodeBeenAdjustedId.toString -> ((v, cm) => storeHasYourPartnersTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> ((v, cm) => storeDoYouKnowYourPartnersAdjustedTaxCode(v, cm)),
      EitherGetsVouchersId.toString -> ((v, cm) => storeEitherGetsVoucher(v, cm)),
      DoYouOrYourPartnerGetAnyBenefitsId.toString -> ((v, cm) => storeYouOrYourPartnerGetAnyBenefits(v, cm)),
      AreYouSelfEmployedOrApprenticeId.toString -> ((v, cm) => AreYouSelfEmployedOrApprentice(v, cm)),
      PartnerSelfEmployedOrApprenticeId.toString -> ((v, cm) => PartnerSelfEmployedOrApprentice(v, cm)),
      YourMinimumEarningsId.toString -> ((v, cm) => storeMinimumEarnings(v, cm)),
      PartnerMinimumEarningsId.toString -> ((v, cm) => storePartnerMinimumEarnings(v, cm))
    )

  private def storeDoYouLiveWithPartner(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - PaidEmploymentId.toString - WhoIsInPaidEmploymentId.toString - PartnerWorkHoursId.toString -
        HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString -
        EitherGetsVouchersId.toString - WhoGetsVouchersId.toString - PartnerChildcareVouchersId.toString - DoYouOrYourPartnerGetAnyBenefitsId.toString -
        WhoGetsBenefitsId.toString - YourPartnersAgeId.toString - PartnerSelfEmployedOrApprenticeId.toString -
        PartnerMinimumEarningsId.toString - PartnerMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString)
    } else if (value == JsBoolean(true))
      cacheMap copy (data = cacheMap.data - AreYouInPaidWorkId.toString - DoYouGetAnyBenefitsId.toString)
    else cacheMap

    store(DoYouLiveWithPartnerId.toString, value, mapToStore)
  }

  private def storeAreYouInPaidWork(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString -
        HasYourTaxCodeBeenAdjustedId.toString - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString -
        YourChildcareVouchersId.toString - DoYouGetAnyBenefitsId.toString - YourAgeId.toString -
        YourMinimumEarningsId.toString - YourMaximumEarningsId.toString - TaxOrUniversalCreditsId.toString -
        PartnerPaidWorkCYId.toString - ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString -
        HowMuchYouPayPensionId.toString - YourOtherIncomeThisYearId.toString - YouAnyTheseBenefitsIdCY.toString -
        YouBenefitsIncomeCYId.toString - PartnerPaidWorkPYId.toString - ParentEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString - YourOtherIncomeLYId.toString -
        YouAnyTheseBenefitsPYId.toString - YouBenefitsIncomePYId.toString)
    } else cacheMap

    store(AreYouInPaidWorkId.toString, value, mapToStore)
  }

  private def storePaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhoIsInPaidEmploymentId.toString - ParentWorkHoursId.toString - PartnerWorkHoursId.toString -
        HasYourTaxCodeBeenAdjustedId.toString - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString -
        HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString -
        EitherGetsVouchersId.toString - WhoGetsVouchersId.toString - YourChildcareVouchersId.toString - PartnerChildcareVouchersId.toString -
        DoYouOrYourPartnerGetAnyBenefitsId.toString - WhoGetsBenefitsId.toString - DoYouGetAnyBenefitsId.toString - YourAgeId.toString -
        YourMinimumEarningsId.toString - PartnerMinimumEarningsId.toString - YourPartnersAgeId.toString - AreYouSelfEmployedOrApprenticeId.toString -
        PartnerSelfEmployedOrApprenticeId.toString - YourMaximumEarningsId.toString - PartnerMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString -
        TaxOrUniversalCreditsId.toString -
        //Current Year
        PartnerPaidWorkCYId.toString - ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString -
        HowMuchYouPayPensionId.toString - YourOtherIncomeThisYearId.toString - YouAnyTheseBenefitsIdCY.toString -
        YouBenefitsIncomeCYId.toString - ParentPaidWorkCYId.toString - PartnerEmploymentIncomeCYId.toString -
        PartnerPaidPensionCYId.toString - HowMuchPartnerPayPensionId.toString - PartnerAnyOtherIncomeThisYearId.toString -
        PartnerAnyTheseBenefitsCYId.toString - PartnerBenefitsIncomeCYId.toString - EmploymentIncomeCYId.toString -
        BothPaidPensionCYId.toString - WhoPaysIntoPensionId.toString - HowMuchBothPayPensionId.toString -
        BothOtherIncomeThisYearId.toString - WhoGetsOtherIncomeCYId.toString - OtherIncomeAmountCYId.toString -
        BothAnyTheseBenefitsCYId.toString - WhosHadBenefitsId.toString - BenefitsIncomeCYId.toString -
        //Previous Year
        PartnerPaidWorkPYId.toString - ParentEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString - YourOtherIncomeLYId.toString -
        YouAnyTheseBenefitsPYId.toString - YouBenefitsIncomePYId.toString - ParentPaidWorkPYId.toString -
        PartnerEmploymentIncomePYId.toString - PartnerPaidPensionPYId.toString - HowMuchPartnerPayPensionPYId.toString -
        PartnerAnyOtherIncomeLYId.toString - PartnerAnyTheseBenefitsPYId.toString - PartnerBenefitsIncomePYId.toString -
        EmploymentIncomePYId.toString - BothPaidPensionPYId.toString - WhoPaidIntoPensionPYId.toString -
        HowMuchBothPayPensionPYId.toString - BothOtherIncomeLYId.toString - WhoOtherIncomePYId.toString -
        OtherIncomeAmountPYId.toString - BothAnyTheseBenefitsPYId.toString - WhosHadBenefitsPYId.toString -
        BothBenefitsIncomePYId.toString)

    } else cacheMap

    store(PaidEmploymentId.toString, value, mapToStore)
  }

  private def storeWhoIsInPaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(You) =>
          cacheMap copy (data = cacheMap.data - PartnerWorkHoursId.toString - HasYourPartnersTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString - PartnerChildcareVouchersId.toString - EitherGetsVouchersId.toString -
            WhoGetsVouchersId.toString - YourPartnersAgeId.toString - PartnerMinimumEarningsId.toString - PartnerSelfEmployedOrApprenticeId.toString -
            PartnerMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString - ParentPaidWorkCYId.toString - PartnerEmploymentIncomeCYId.toString -
            PartnerPaidPensionCYId.toString - HowMuchPartnerPayPensionId.toString - PartnerAnyOtherIncomeThisYearId.toString - PartnerAnyTheseBenefitsCYId.toString -
            PartnerBenefitsIncomeCYId.toString - ParentPaidWorkPYId.toString - PartnerEmploymentIncomePYId.toString - PartnerPaidPensionPYId.toString -
            HowMuchPartnerPayPensionPYId.toString - PartnerAnyOtherIncomeLYId.toString - PartnerAnyTheseBenefitsPYId.toString - PartnerBenefitsIncomePYId.toString -
            EmploymentIncomeCYId.toString - BothPaidPensionCYId.toString - WhoPaysIntoPensionId.toString - HowMuchBothPayPensionId.toString -
            BothOtherIncomeThisYearId.toString - WhoGetsOtherIncomeCYId.toString - OtherIncomeAmountCYId.toString - BothAnyTheseBenefitsCYId.toString -
            WhosHadBenefitsId.toString - BenefitsIncomeCYId.toString - EmploymentIncomePYId.toString - BothPaidPensionPYId.toString -
            WhoPaidIntoPensionPYId.toString - HowMuchBothPayPensionPYId.toString - BothOtherIncomeLYId.toString - WhoOtherIncomePYId.toString -
            OtherIncomeAmountPYId.toString - BothAnyTheseBenefitsPYId.toString - WhosHadBenefitsPYId.toString - BothBenefitsIncomePYId.toString)
        case JsString(Partner) =>
          cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString - HasYourTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString - YourChildcareVouchersId.toString - EitherGetsVouchersId.toString - WhoGetsVouchersId.toString -
            YourAgeId.toString - YourMinimumEarningsId.toString - AreYouSelfEmployedOrApprenticeId.toString - YourMaximumEarningsId.toString -
            EitherOfYouMaximumEarningsId.toString - PartnerPaidWorkCYId.toString - ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString -
            HowMuchYouPayPensionId.toString - YourOtherIncomeThisYearId.toString - YouAnyTheseBenefitsIdCY.toString - YouBenefitsIncomeCYId.toString -
            PartnerPaidWorkPYId .toString - ParentEmploymentIncomePYId.toString - YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString -
            YourOtherIncomeLYId.toString - YouAnyTheseBenefitsPYId.toString - YouBenefitsIncomePYId.toString - EmploymentIncomeCYId.toString -
            BothPaidPensionCYId.toString - WhoPaysIntoPensionId.toString -
            HowMuchBothPayPensionId.toString - BothOtherIncomeThisYearId.toString - WhoGetsOtherIncomeCYId.toString - OtherIncomeAmountCYId.toString -
            BothAnyTheseBenefitsCYId.toString - WhosHadBenefitsId.toString - BenefitsIncomeCYId.toString - EmploymentIncomePYId.toString -
            BothPaidPensionPYId.toString - WhoPaidIntoPensionPYId.toString - HowMuchBothPayPensionPYId.toString - BothOtherIncomeLYId.toString -
            WhoOtherIncomePYId.toString - OtherIncomeAmountPYId.toString - BothAnyTheseBenefitsPYId.toString - WhosHadBenefitsPYId.toString -
            BothBenefitsIncomePYId.toString )

        case JsString(Both) => cacheMap copy (data = cacheMap.data - YourChildcareVouchersId.toString - PartnerChildcareVouchersId.toString -
          PartnerPaidWorkCYId.toString - ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString - HowMuchYouPayPensionId.toString -
          YourOtherIncomeThisYearId.toString - YouAnyTheseBenefitsIdCY.toString - YouBenefitsIncomeCYId.toString - PartnerPaidWorkPYId .toString -
          ParentEmploymentIncomePYId.toString - YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString - YourOtherIncomeLYId.toString -
          YouAnyTheseBenefitsPYId.toString - YouBenefitsIncomePYId.toString - ParentPaidWorkCYId.toString - PartnerEmploymentIncomeCYId.toString -
          PartnerPaidPensionCYId.toString - HowMuchPartnerPayPensionId.toString - PartnerAnyOtherIncomeThisYearId.toString - PartnerAnyTheseBenefitsCYId.toString -
          PartnerBenefitsIncomeCYId.toString - ParentPaidWorkPYId.toString - PartnerEmploymentIncomePYId.toString - PartnerPaidPensionPYId.toString -
          HowMuchPartnerPayPensionPYId.toString - PartnerAnyOtherIncomeLYId.toString - PartnerAnyTheseBenefitsPYId.toString - PartnerBenefitsIncomePYId.toString )
        case _ => cacheMap
      }

    store(WhoIsInPaidEmploymentId.toString, value, mapToStore)
  }

  private def storeHasYourTaxCodeBeenAdjusted(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(YesNoUnsureEnum.NO.toString)) {
      cacheMap copy (data = cacheMap.data - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(HasYourTaxCodeBeenAdjustedId.toString, value, mapToStore)
  }

  private def storeDoYouKnowYourAdjustedTaxCode(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhatIsYourTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(DoYouKnowYourAdjustedTaxCodeId.toString, value, mapToStore)
  }

  private def storeDoYouKnowYourPartnersAdjustedTaxCode(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhatIsYourPartnersTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(DoYouKnowYourPartnersAdjustedTaxCodeId.toString, value, mapToStore)
  }

  private def storeHasYourPartnersTaxCodeBeenAdjusted(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(HasYourPartnersTaxCodeBeenAdjustedId.toString, value, mapToStore)
  }

  private def storeEitherGetsVoucher(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val No = YesNoUnsureEnum.NO.toString

    val mapToStore = if (value == JsString(No)) {
      cacheMap copy (data = cacheMap.data - WhoGetsVouchersId.toString)
    } else {
      cacheMap
    }
    store(EitherGetsVouchersId.toString, value, mapToStore)
  }

  private def storeYouOrYourPartnerGetAnyBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhoGetsBenefitsId.toString)
    } else {
      cacheMap
    }
    store(DoYouOrYourPartnerGetAnyBenefitsId.toString, value, mapToStore)
  }

  private def AreYouSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
      || (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))) {
      cacheMap copy (data = cacheMap.data - YourSelfEmployedId.toString)
    } else cacheMap

    store(AreYouSelfEmployedOrApprenticeId.toString, value, mapToStore)
  }

  private def PartnerSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
      || (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))) {
      cacheMap copy (data = cacheMap.data - PartnerSelfEmployedId.toString)
    } else cacheMap

    store(PartnerSelfEmployedOrApprenticeId.toString, value, mapToStore)
  }

  private def storeMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(true)) {
      cacheMap copy (data = cacheMap.data - AreYouSelfEmployedOrApprenticeId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - YourMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString)
    else cacheMap

    store(YourMinimumEarningsId.toString, value, mapToStore)
  }

  private def storePartnerMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(true)) {
      cacheMap copy (data = cacheMap.data - PartnerSelfEmployedOrApprenticeId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - PartnerMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString)
    else cacheMap

    store(PartnerMinimumEarningsId.toString, value, mapToStore)
  }

}
