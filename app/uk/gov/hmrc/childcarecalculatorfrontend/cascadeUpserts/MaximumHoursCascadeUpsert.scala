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

import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AgeEnum, SelfEmployedOrApprenticeOrNeitherEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class MaximumHoursCascadeUpsert @Inject()() extends SubCascadeUpsert {

  lazy val no: String = YesNoUnsureEnum.NO.toString
  lazy val notSure: String = YesNoUnsureEnum.NOTSURE.toString

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      DoYouLiveWithPartnerId.toString() -> ((v, cm) => storeDoYouLiveWithPartner(v, cm)),
      WhoIsInPaidEmploymentId.toString -> ((v, cm) => storeWhoIsInPaidEmployment(v, cm)),
      AreYouInPaidWorkId.toString -> ((v, cm) => storeAreYouInPaidWork(v, cm)),
      HasYourTaxCodeBeenAdjustedId.toString -> ((v, cm) => storeHasYourTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourAdjustedTaxCodeId.toString -> ((v, cm) => storeDoYouKnowYourAdjustedTaxCode(v, cm)),
      HasYourPartnersTaxCodeBeenAdjustedId.toString -> ((v, cm) => storeHasYourPartnersTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> ((v, cm) => storeDoYouKnowYourPartnersAdjustedTaxCode(v, cm)),
      DoYouOrYourPartnerGetAnyBenefitsId.toString -> ((v, cm) => storeYouOrYourPartnerGetAnyBenefits(v, cm)),
      WhoGetsBenefitsId.toString -> ((v, cm) => storeWhoGetsBenefits(v, cm)),
      DoYouGetAnyBenefitsId.toString -> ((v, cm) => storeDoYouGetAnyBenefits(v, cm)),
      YourAgeId.toString -> ((v, cm) => storeYourAge(v, cm)),
      YourPartnersAgeId.toString -> ((v, cm) => storeYourPartnersAge(v, cm)),

      AreYouSelfEmployedOrApprenticeId.toString -> ((v, cm) => AreYouSelfEmployedOrApprentice(v, cm)),
      PartnerSelfEmployedOrApprenticeId.toString -> ((v, cm) => PartnerSelfEmployedOrApprentice(v, cm)),
      YourMinimumEarningsId.toString -> ((v, cm) => storeMinimumEarnings(v, cm)),
      PartnerMinimumEarningsId.toString -> ((v, cm) => storePartnerMinimumEarnings(v, cm))
    )

  private def storeDoYouLiveWithPartner(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = if (value.equals(JsBoolean(false))) {
      cacheMap copy (data = cacheMap.data - WhoIsInPaidEmploymentId.toString - PartnerWorkHoursId.toString -
        HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString - WhoGetsVouchersId.toString - PartnerChildcareVouchersId.toString - DoYouOrYourPartnerGetAnyBenefitsId.toString -
        WhoGetsBenefitsId.toString  - WhichBenefitsYouGetId.toString - WhichBenefitsPartnerGetId.toString - YourPartnersAgeId.toString -
        PartnerSelfEmployedOrApprenticeId.toString - PartnerMinimumEarningsId.toString - PartnerMaximumEarningsId.toString -
        EitherOfYouMaximumEarningsId.toString - BothStatutoryPayId.toString - WhoGotStatutoryPayId.toString - PartnerStatutoryPayTypeId.toString -
        PartnerStatutoryStartDateId.toString - PartnerStatutoryWeeksId.toString - PartnerStatutoryPayBeforeTaxId.toString -
        PartnerStatutoryPayPerWeekId.toString)
    } else if (value.equals(JsBoolean(true))) {
      cacheMap copy (data = cacheMap.data - AreYouInPaidWorkId.toString - DoYouGetAnyBenefitsId.toString)
    } else cacheMap

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

  private def storeWhoIsInPaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(`you`) =>
          cacheMap copy (data = cacheMap.data - PartnerWorkHoursId.toString - HasYourPartnersTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString - PartnerChildcareVouchersId.toString -
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
        case JsString(`partner`) =>
          cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString - HasYourTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString - YourChildcareVouchersId.toString - WhoGetsVouchersId.toString -
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

        case JsString(`both`) => cacheMap copy (data = cacheMap.data - YourChildcareVouchersId.toString - PartnerChildcareVouchersId.toString -
          PartnerPaidWorkCYId.toString - ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString - HowMuchYouPayPensionId.toString -
          YourOtherIncomeThisYearId.toString - YouAnyTheseBenefitsIdCY.toString - YouBenefitsIncomeCYId.toString - PartnerPaidWorkPYId .toString -
          ParentEmploymentIncomePYId.toString - YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString - YourOtherIncomeLYId.toString -
          YouAnyTheseBenefitsPYId.toString - YouBenefitsIncomePYId.toString - ParentPaidWorkCYId.toString - PartnerEmploymentIncomeCYId.toString -
          PartnerPaidPensionCYId.toString - HowMuchPartnerPayPensionId.toString - PartnerAnyOtherIncomeThisYearId.toString - PartnerAnyTheseBenefitsCYId.toString -
          PartnerBenefitsIncomeCYId.toString - ParentPaidWorkPYId.toString - PartnerEmploymentIncomePYId.toString - PartnerPaidPensionPYId.toString -
          HowMuchPartnerPayPensionPYId.toString - PartnerAnyOtherIncomeLYId.toString - PartnerAnyTheseBenefitsPYId.toString - PartnerBenefitsIncomePYId.toString )

        case JsString(`neither`) =>
          cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString - PartnerWorkHoursId.toString -
            HasYourTaxCodeBeenAdjustedId.toString - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString -
            HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString - WhoGetsVouchersId.toString - YourChildcareVouchersId.toString - PartnerChildcareVouchersId.toString -
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

        case _ => cacheMap
      }

    store(WhoIsInPaidEmploymentId.toString, value, mapToStore)
  }

  private def storeHasYourTaxCodeBeenAdjusted(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(no) || value == JsString(notSure)) {
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
    val mapToStore = if  (value == JsString(no) || value == JsString(notSure)) {
      cacheMap copy (data = cacheMap.data - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(HasYourPartnersTaxCodeBeenAdjustedId.toString, value, mapToStore)
  }

  private def storeYouOrYourPartnerGetAnyBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhoGetsBenefitsId.toString - WhichBenefitsYouGetId.toString - WhichBenefitsPartnerGetId.toString)
    } else {
      cacheMap
    }
    store(DoYouOrYourPartnerGetAnyBenefitsId.toString, value, mapToStore)
  }

  private def storeWhoGetsBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(you)) {
      cacheMap copy (data = cacheMap.data - WhichBenefitsPartnerGetId.toString)
    } else if (value == JsString(partner)) {
      cacheMap copy (data = cacheMap.data - WhichBenefitsYouGetId.toString)
    }else {
      cacheMap
    }
    store(WhoGetsBenefitsId.toString, value, mapToStore)
  }

  private def storeDoYouGetAnyBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap copy (data = cacheMap.data - WhichBenefitsYouGetId.toString)
    }else {
      cacheMap
    }
    store(DoYouGetAnyBenefitsId.toString, value, mapToStore)
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
      cacheMap copy (data = cacheMap.data - AreYouSelfEmployedOrApprenticeId.toString - YourSelfEmployedId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - YourMaximumEarningsId.toString )
    else cacheMap

    store(YourMinimumEarningsId.toString, value, mapToStore)
  }

  private def storePartnerMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(true)) {
      cacheMap copy (data = cacheMap.data - PartnerSelfEmployedOrApprenticeId.toString - PartnerSelfEmployedId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - PartnerMaximumEarningsId.toString - EitherOfYouMaximumEarningsId.toString)
    else cacheMap

    store(PartnerMinimumEarningsId.toString, value, mapToStore)
  }

  private def storeYourAge(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalValue = cacheMap.data.get("yourAge")
    val mapToStore = value match {
        case JsString(_) if !originalValue.contains(value)=> {
          cacheMap copy (data = cacheMap.data - YourMinimumEarningsId.toString -
            AreYouSelfEmployedOrApprenticeId.toString - YourSelfEmployedId.toString)
        }
        case _ => cacheMap
    }
    store(YourAgeId.toString, value, mapToStore)
  }

  private def storeYourPartnersAge(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalValue = cacheMap.data.get("yourPartnersAge")
    val mapToStore = value match {
      case JsString(_) if !originalValue.contains(value)=> {
        cacheMap copy (data = cacheMap.data - PartnerMinimumEarningsId.toString -
          PartnerSelfEmployedOrApprenticeId.toString - PartnerSelfEmployedId.toString)
      }
      case _ => cacheMap
    }
    store(YourPartnersAgeId.toString, value, mapToStore)
  }

}
