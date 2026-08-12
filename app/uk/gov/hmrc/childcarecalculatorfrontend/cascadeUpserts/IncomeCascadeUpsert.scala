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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

@Singleton
class IncomeCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      YourOtherIncomeThisYearId.cacheKey -> ((v, cm) => storeYourOtherIncomeThisYear(v, cm)),
      BothOtherIncomeThisYearId.cacheKey -> ((v, cm) => storeBothOtherIncomeThisYear(v, cm)),
      WhoGetsOtherIncomeCYId.cacheKey    -> ((v, cm) => storeWhoGetsOtherIncomeCY(v, cm)),
      ParentPaidWorkCYId.cacheKey        -> ((v, cm) => storeParentPaidWorkCY(v, cm)),
      PartnerPaidWorkCYId.cacheKey       -> ((v, cm) => storePartnerPaidWorkCY(v, cm))
    )

  private def storeYourOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.removed(YourOtherIncomeAmountCYId)
      case _                => cacheMap
    }

    mapToStore.updated(YourOtherIncomeThisYearId, value)
  }

  private def storeBothOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removedAll(
          WhoGetsOtherIncomeCYId,
          YourOtherIncomeAmountCYId,
          PartnerOtherIncomeAmountCYId,
          OtherIncomeAmountCYId
        )
      case _ => cacheMap
    }

    mapToStore.updated(BothOtherIncomeThisYearId, value)
  }

  private def storeWhoGetsOtherIncomeCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(YouPartnerBoth.You.toString) =>
        cacheMap.removedAll(PartnerOtherIncomeAmountCYId, OtherIncomeAmountCYId)
      case JsString(YouPartnerBoth.Partner.toString) =>
        cacheMap.removedAll(YourOtherIncomeAmountCYId, OtherIncomeAmountCYId)
      case JsString(YouPartnerBoth.Both.toString) =>
        cacheMap.removedAll(YourOtherIncomeAmountCYId, PartnerOtherIncomeAmountCYId)
      case _ => cacheMap
    }

    mapToStore.updated(WhoGetsOtherIncomeCYId, value)
  }

  private def storePartnerPaidWorkCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val existingValue = cacheMap.data.get(PartnerPaidWorkCYId.cacheKey)

    val mapToStore = value match {
      case JsBoolean(false) if existingValue.contains(JsBoolean(true)) =>
        cacheMap.removedAll(
          EmploymentIncomeCYId,
          BothPaidPensionCYId,
          WhoPaysIntoPensionId,
          HowMuchPartnerPayPensionId,
          HowMuchBothPayPensionId
        )

      case JsBoolean(true) if existingValue.contains(JsBoolean(false)) =>
        cacheMap.removedAll(ParentEmploymentIncomeCYId, YouPaidPensionCYId)
      case _ => cacheMap
    }

    mapToStore.updated(PartnerPaidWorkCYId, value)
  }

  private def storeParentPaidWorkCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val existingValue = cacheMap.data.get(ParentPaidWorkCYId.cacheKey)

    val mapToStore = value match {
      case JsBoolean(false) if existingValue.contains(JsBoolean(true)) =>
        cacheMap.removedAll(
          EmploymentIncomeCYId,
          PartnerPaidPensionCYId,
          HowMuchYouPayPensionId,
          HowMuchBothPayPensionId
        )
      case JsBoolean(true) if existingValue.contains(JsBoolean(false)) =>
        cacheMap.removedAll(PartnerEmploymentIncomeCYId, BothPaidPensionCYId, WhoPaysIntoPensionId)
      case _ => cacheMap
    }

    mapToStore.updated(ParentPaidWorkCYId, value)
  }

}
