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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class EmploymentIncomeCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      BothPaidWorkPYId.toString -> storeBothPaidWorkPY
    )

  private def storeBothPaidWorkPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - WhoWasInPaidWorkPYId.toString -
        EmploymentIncomePYId.toString - ParentEmploymentIncomePYId.toString - PartnerEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - PartnerPaidPensionPYId.toString - BothPaidPensionPYId.toString -
        WhoPaidIntoPensionPYId.toString - HowMuchYouPayPensionPYId.toString - HowMuchPartnerPayPensionPYId.toString -
        HowMuchBothPayPensionPYId.toString)

      case _ => cacheMap
    }

    store(BothPaidWorkPYId.toString, value, mapToStore)
  }

  private def storeWhoGetsOtherIncomeCY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(You) => cacheMap copy (data = cacheMap.data  - PartnerOtherIncomeAmountCYId.toString -
        OtherIncomeAmountCYId.toString)
      case JsString(Partner) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountCYId.toString -
        OtherIncomeAmountCYId.toString)
      case JsString(Both) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountCYId.toString -
        PartnerOtherIncomeAmountCYId.toString)
      case _ => cacheMap
    }

    store(WhoGetsOtherIncomeCYId.toString, value, mapToStore)
  }

}
