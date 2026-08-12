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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{EmploymentStatus, YouPartnerBothNeither}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

import javax.inject.{Inject, Singleton}

@Singleton
class MaximumHoursCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      DoYouLiveWithPartnerId.cacheKey            -> ((v, cm) => storeDoYouLiveWithPartner(v, cm)),
      WhoIsInPaidEmploymentId.cacheKey           -> ((v, cm) => storeWhoIsInPaidEmployment(v, cm)),
      AreYouInPaidWorkId.cacheKey                -> ((v, cm) => storeAreYouInPaidWork(v, cm)),
      YourAgeId.cacheKey                         -> ((v, cm) => storeYourAge(v, cm)),
      YourPartnersAgeId.cacheKey                 -> ((v, cm) => storeYourPartnersAge(v, cm)),
      AreYouSelfEmployedOrApprenticeId.cacheKey  -> ((v, cm) => AreYouSelfEmployedOrApprentice(v, cm)),
      PartnerSelfEmployedOrApprenticeId.cacheKey -> ((v, cm) => PartnerSelfEmployedOrApprentice(v, cm)),
      YourMinimumEarningsId.cacheKey             -> ((v, cm) => storeMinimumEarnings(v, cm)),
      PartnerMinimumEarningsId.cacheKey          -> ((v, cm) => storePartnerMinimumEarnings(v, cm)),
      SessionDataClearId.cacheKey                -> ((v, cm) => clearSessionData(v, cm))
    )

  private def storeDoYouLiveWithPartner(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = if (value.equals(JsBoolean(false))) {
      cacheMap.removedAll(
        WhoIsInPaidEmploymentId,
        WhatIsYourPartnersTaxCodeId,
        WhoGetsVouchersId,
        PartnerChildcareVouchersId,
        DoYouGetAnyBenefitsId,
        DoesYourPartnerGetAnyBenefitsId,
        YourPartnersAgeId,
        PartnerSelfEmployedOrApprenticeId,
        PartnerMinimumEarningsId,
        PartnerMaximumEarningsId,
        EitherOfYouMaximumEarningsId
      )
    } else if (value.equals(JsBoolean(true))) {
      cacheMap.removedAll(AreYouInPaidWorkId, DoYouGetAnyBenefitsId)
    } else cacheMap

    mapToStore.updated(DoYouLiveWithPartnerId, value)
  }

  private def storeAreYouInPaidWork(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(false)) {
      cacheMap.removedAll(
        WhatIsYourTaxCodeId,
        WhatIsYourPartnersTaxCodeId,
        YourChildcareVouchersId,
        DoYouGetAnyBenefitsId,
        YourAgeId,
        YourMinimumEarningsId,
        YourMaximumEarningsId,
        UniversalCreditId,
        PartnerPaidWorkCYId,
        ParentEmploymentIncomeCYId,
        YouPaidPensionCYId,
        HowMuchYouPayPensionId,
        YourOtherIncomeThisYearId,
        YouAnyTheseBenefitsCYId,
        YouBenefitsIncomeCYId
      )
    } else cacheMap.removedAll(WhoGetsVouchersId, PartnerChildcareVouchersId)

    mapToStore.updated(AreYouInPaidWorkId, value)
  }

  private def storeWhoIsInPaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(YouPartnerBothNeither.You.toString) =>
          cacheMap.removedAll(
            WhatIsYourPartnersTaxCodeId,
            PartnerChildcareVouchersId,
            WhoGetsVouchersId,
            YourPartnersAgeId,
            PartnerMinimumEarningsId,
            PartnerSelfEmployedOrApprenticeId,
            PartnerMaximumEarningsId,
            EitherOfYouMaximumEarningsId,
            ParentPaidWorkCYId,
            PartnerEmploymentIncomeCYId,
            PartnerPaidPensionCYId,
            HowMuchPartnerPayPensionId,
            PartnerBenefitsIncomeCYId,
            EmploymentIncomeCYId,
            BothPaidPensionCYId,
            WhoPaysIntoPensionId,
            HowMuchBothPayPensionId,
            BothOtherIncomeThisYearId,
            WhoGetsOtherIncomeCYId,
            OtherIncomeAmountCYId,
            BothAnyTheseBenefitsCYId,
            WhosHadBenefitsId,
            BenefitsIncomeCYId
          )
        case JsString(YouPartnerBothNeither.Partner.toString) =>
          cacheMap.removedAll(
            WhatIsYourTaxCodeId,
            YourChildcareVouchersId,
            WhoGetsVouchersId,
            YourAgeId,
            YourMinimumEarningsId,
            AreYouSelfEmployedOrApprenticeId,
            YourMaximumEarningsId,
            EitherOfYouMaximumEarningsId,
            PartnerPaidWorkCYId,
            ParentEmploymentIncomeCYId,
            YouPaidPensionCYId,
            HowMuchYouPayPensionId,
            YourOtherIncomeThisYearId,
            YouAnyTheseBenefitsCYId,
            YouBenefitsIncomeCYId,
            EmploymentIncomeCYId,
            BothPaidPensionCYId,
            WhoPaysIntoPensionId,
            HowMuchBothPayPensionId,
            BothOtherIncomeThisYearId,
            WhoGetsOtherIncomeCYId,
            OtherIncomeAmountCYId,
            BothAnyTheseBenefitsCYId,
            WhosHadBenefitsId,
            BenefitsIncomeCYId
          )

        case JsString(YouPartnerBothNeither.Both.toString) =>
          cacheMap.removedAll(
            YourChildcareVouchersId,
            PartnerChildcareVouchersId,
            PartnerPaidWorkCYId,
            ParentEmploymentIncomeCYId,
            YouPaidPensionCYId,
            HowMuchYouPayPensionId,
            YourOtherIncomeThisYearId,
            YouAnyTheseBenefitsCYId,
            YouBenefitsIncomeCYId,
            ParentPaidWorkCYId,
            PartnerEmploymentIncomeCYId,
            PartnerPaidPensionCYId,
            HowMuchPartnerPayPensionId,
            PartnerBenefitsIncomeCYId
          )

        case JsString(YouPartnerBothNeither.Neither.toString) =>
          cacheMap.removedAll(
            WhatIsYourTaxCodeId,
            WhatIsYourPartnersTaxCodeId,
            WhoGetsVouchersId,
            YourChildcareVouchersId,
            PartnerChildcareVouchersId,
            DoYouGetAnyBenefitsId,
            YourAgeId,
            YourMinimumEarningsId,
            PartnerMinimumEarningsId,
            YourPartnersAgeId,
            AreYouSelfEmployedOrApprenticeId,
            PartnerSelfEmployedOrApprenticeId,
            YourMaximumEarningsId,
            PartnerMaximumEarningsId,
            EitherOfYouMaximumEarningsId,
            UniversalCreditId,
            // Current Year
            PartnerPaidWorkCYId,
            ParentEmploymentIncomeCYId,
            YouPaidPensionCYId,
            HowMuchYouPayPensionId,
            YourOtherIncomeThisYearId,
            YouAnyTheseBenefitsCYId,
            YouBenefitsIncomeCYId,
            ParentPaidWorkCYId,
            PartnerEmploymentIncomeCYId,
            PartnerPaidPensionCYId,
            HowMuchPartnerPayPensionId,
            PartnerBenefitsIncomeCYId,
            EmploymentIncomeCYId,
            BothPaidPensionCYId,
            WhoPaysIntoPensionId,
            HowMuchBothPayPensionId,
            BothOtherIncomeThisYearId,
            WhoGetsOtherIncomeCYId,
            OtherIncomeAmountCYId,
            BothAnyTheseBenefitsCYId,
            WhosHadBenefitsId,
            BenefitsIncomeCYId
          )

        case _ => cacheMap
      }

    mapToStore.updated(WhoIsInPaidEmploymentId, value)
  }

  private def AreYouSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(EmploymentStatus.Apprentice.toString) | JsString(EmploymentStatus.Neither.toString) =>
          cacheMap.removed(YourSelfEmployedId)
        case _ => cacheMap
      }

    mapToStore.updated(AreYouSelfEmployedOrApprenticeId, value)
  }

  private def PartnerSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(EmploymentStatus.Apprentice.toString) | JsString(EmploymentStatus.Neither.toString) =>
          cacheMap.removed(PartnerSelfEmployedId)
        case _ => cacheMap
      }

    mapToStore.updated(PartnerSelfEmployedOrApprenticeId, value)
  }

  private def storeMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsBoolean(true) =>
          cacheMap.removedAll(AreYouSelfEmployedOrApprenticeId, YourSelfEmployedId)
        case JsBoolean(false) =>
          cacheMap.removed(YourMaximumEarningsId)
        case _ => cacheMap
      }

    mapToStore.updated(YourMinimumEarningsId, value)
  }

  private def storePartnerMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsBoolean(true) =>
          cacheMap.removedAll(PartnerSelfEmployedOrApprenticeId, PartnerSelfEmployedId)
        case JsBoolean(false) =>
          cacheMap.removedAll(PartnerMaximumEarningsId, EitherOfYouMaximumEarningsId)
        case _ => cacheMap
      }

    mapToStore.updated(PartnerMinimumEarningsId, value)
  }

  private def storeYourAge(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalValue = cacheMap.data.get(YourAgeId.cacheKey)

    val mapToStore =
      value match {
        case JsString(_) if !originalValue.contains(value) =>
          cacheMap.removedAll(YourMinimumEarningsId, AreYouSelfEmployedOrApprenticeId, YourSelfEmployedId)
        case _ => cacheMap
      }

    mapToStore.updated(YourAgeId, value)
  }

  private def storeYourPartnersAge(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalValue = cacheMap.data.get(YourPartnersAgeId.cacheKey)

    val mapToStore =
      value match {
        case JsString(_) if !originalValue.contains(value) =>
          cacheMap.removedAll(PartnerMinimumEarningsId, PartnerSelfEmployedOrApprenticeId, PartnerSelfEmployedId)
        case _ => cacheMap
      }
    mapToStore.updated(YourPartnersAgeId, value)
  }

  private def clearSessionData(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = cacheMap.copy(data = Map())

    mapToStore.updated(SessionDataClearId, value)
  }

}
