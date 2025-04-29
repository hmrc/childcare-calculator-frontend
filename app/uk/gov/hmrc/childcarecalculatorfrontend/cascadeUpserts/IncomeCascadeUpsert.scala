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

import javax.inject.Inject
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, SubCascadeUpsert}

class IncomeCascadeUpsert @Inject() () extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      YourOtherIncomeThisYearId.toString       -> ((v, cm) => storeYourOtherIncomeThisYear(v, cm)),
      PartnerAnyOtherIncomeThisYearId.toString -> ((v, cm) => storePartnerAnyOtherIncomeThisYear(v, cm)),
      BothOtherIncomeThisYearId.toString       -> ((v, cm) => storeBothOtherIncomeThisYear(v, cm)),
      WhoGetsOtherIncomeCYId.toString          -> ((v, cm) => storeWhoGetsOtherIncomeCY(v, cm)),
      ParentPaidWorkCYId.toString              -> ((v, cm) => storeParentPaidWorkCY(v, cm)),
      PartnerPaidWorkCYId.toString             -> ((v, cm) => storePartnerPaidWorkCY(v, cm))
    )

  private def storeYourOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.copy(data = cacheMap.data - YourOtherIncomeAmountCYId.toString)
      case _                => cacheMap
    }

    store(YourOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storePartnerAnyOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.copy(data = cacheMap.data - PartnerOtherIncomeAmountCYId.toString)
      case _                => cacheMap
    }

    store(PartnerAnyOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storeBothOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.copy(data =
          cacheMap.data - WhoGetsOtherIncomeCYId.toString - YourOtherIncomeAmountCYId.toString
            - PartnerOtherIncomeAmountCYId.toString - OtherIncomeAmountCYId.toString
        )
      case _ => cacheMap
    }

    store(BothOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storeWhoGetsOtherIncomeCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(`you`) =>
        cacheMap.copy(data =
          cacheMap.data - PartnerOtherIncomeAmountCYId.toString -
            OtherIncomeAmountCYId.toString
        )
      case JsString(`partner`) =>
        cacheMap.copy(data =
          cacheMap.data - YourOtherIncomeAmountCYId.toString -
            OtherIncomeAmountCYId.toString
        )
      case JsString(`both`) =>
        cacheMap.copy(data =
          cacheMap.data - YourOtherIncomeAmountCYId.toString -
            PartnerOtherIncomeAmountCYId.toString
        )
      case _ => cacheMap
    }

    store(WhoGetsOtherIncomeCYId.toString, value, mapToStore)
  }

  private def storePartnerPaidWorkCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val existingValue = cacheMap.data.get(PartnerPaidWorkCYId.toString)

    val mapToStore = value match {
      case JsBoolean(false) if existingValue.contains(JsBoolean(true)) =>
        cacheMap.copy(data =
          cacheMap.data - EmploymentIncomeCYId.toString -
            BothPaidPensionCYId.toString - WhoPaysIntoPensionId.toString - HowMuchPartnerPayPensionId.toString -
            HowMuchBothPayPensionId.toString
        )

      case JsBoolean(true) if existingValue.contains(JsBoolean(false)) =>
        cacheMap.copy(data =
          cacheMap.data -
            ParentEmploymentIncomeCYId.toString - YouPaidPensionCYId.toString
        )
      case _ => cacheMap
    }

    store(PartnerPaidWorkCYId.toString, value, mapToStore)
  }

  private def storeParentPaidWorkCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val existingValue = cacheMap.data.get(ParentPaidWorkCYId.toString)

    val mapToStore = value match {
      case JsBoolean(false) if existingValue.contains(JsBoolean(true)) =>
        cacheMap.copy(data =
          cacheMap.data - EmploymentIncomeCYId.toString -
            PartnerPaidPensionCYId.toString - HowMuchYouPayPensionId.toString - HowMuchBothPayPensionId.toString
        )
      case JsBoolean(true) if existingValue.contains(JsBoolean(false)) =>
        cacheMap.copy(data =
          cacheMap.data -
            PartnerEmploymentIncomeCYId.toString - BothPaidPensionCYId.toString - WhoPaysIntoPensionId.toString
        )
      case _ => cacheMap
    }

    store(ParentPaidWorkCYId.toString, value, mapToStore)
  }

}
