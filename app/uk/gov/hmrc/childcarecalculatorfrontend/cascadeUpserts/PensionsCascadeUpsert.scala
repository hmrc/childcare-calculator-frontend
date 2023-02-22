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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.http.cache.client.CacheMap

class PensionsCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      YouPaidPensionCYId.toString -> ((v, cm) => storeYouPaidPensionCY(v, cm)),
      PartnerPaidPensionCYId.toString -> ((v, cm) => storePartnerPaidPensionCY(v, cm)),
      BothPaidPensionCYId.toString -> ((v, cm) => storeBothPaidPensionCY(v, cm)),
      WhoPaysIntoPensionId.toString -> ((v, cm) => storeWhoPaysIntoPension(v, cm)),
      YouPaidPensionPYId.toString -> ((v, cm) => storeYouPaidPensionPY(v, cm)),
      PartnerPaidPensionPYId.toString -> ((v, cm) => storePartnerPaidPensionPY(v, cm)),
      BothPaidPensionPYId.toString -> ((v, cm) => storeBothPaidPensionPY(v, cm)),
      WhoPaidIntoPensionPYId.toString -> ((v, cm) => storeWhoPaidPensionPY(v, cm))
    )

  private def storeYouPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchYouPayPensionId.toString)
      case _ => cacheMap
    }

    store(YouPaidPensionCYId.toString, value, mapToStore)
  }

  private def storePartnerPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchPartnerPayPensionId.toString)
      case _ => cacheMap
    }

    store(PartnerPaidPensionCYId.toString, value, mapToStore)
  }

  private def storeBothPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchYouPayPensionId.toString - HowMuchPartnerPayPensionId.toString
        - HowMuchBothPayPensionId.toString  - WhoPaysIntoPensionId.toString)
      case _ => cacheMap
    }

    store(BothPaidPensionCYId.toString, value, mapToStore)
  }

  private def storeWhoPaysIntoPension(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - HowMuchPartnerPayPensionId.toString -
        HowMuchBothPayPensionId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - HowMuchYouPayPensionId.toString -
        HowMuchBothPayPensionId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - HowMuchYouPayPensionId.toString -
        HowMuchPartnerPayPensionId.toString)
      case _ => cacheMap
    }

    store(WhoPaysIntoPensionId.toString, value, mapToStore)
  }

  private def storeYouPaidPensionPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchYouPayPensionPYId.toString)
      case _ => cacheMap
    }

    store(YouPaidPensionPYId.toString, value, mapToStore)
  }

  private def storePartnerPaidPensionPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchPartnerPayPensionPYId.toString)
      case _ => cacheMap
    }

    store(PartnerPaidPensionPYId.toString, value, mapToStore)
  }

  private def storeBothPaidPensionPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - HowMuchYouPayPensionPYId.toString -
        HowMuchPartnerPayPensionPYId.toString - HowMuchBothPayPensionPYId.toString  -
        WhoPaidIntoPensionPYId.toString)
      case _ => cacheMap
    }

    store(BothPaidPensionPYId.toString, value, mapToStore)
  }

  private def storeWhoPaidPensionPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - HowMuchPartnerPayPensionPYId.toString -
        HowMuchBothPayPensionPYId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - HowMuchYouPayPensionPYId.toString -
        HowMuchBothPayPensionPYId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - HowMuchYouPayPensionPYId.toString -
        HowMuchPartnerPayPensionPYId.toString)
      case _ => cacheMap
    }

    store(WhoPaidIntoPensionPYId.toString, value, mapToStore)
  }

}
