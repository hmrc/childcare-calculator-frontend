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
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, SubCascadeUpsert}

class StatutoryCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      YouStatutoryPayId.toString -> ((v, cm) => storeYouStatutoryPay(v, cm)),
      YourStatutoryPayTypeId.toString -> ((v, cm) => storeYourStatutoryPayType(v, cm)),
      YourStatutoryPayBeforeTaxId.toString -> ((v, cm) => storeYourStatutoryPayBeforeTax(v, cm)),
      BothStatutoryPayId.toString -> ((v, cm) => storeBothStatutoryPay(v, cm)),
      PartnerStatutoryPayTypeId.toString -> ((v, cm) => storePartnerStatutoryPayType(v, cm)),
      PartnerStatutoryPayBeforeTaxId.toString -> ((v, cm) => storePartnerStatutoryPayBeforeTax(v, cm)),
      WhoGotStatutoryPayId.toString -> ((v, cm) => storeWhoGotStatutoryPay(v, cm))
    )

  private def storeYouStatutoryPay(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourStatutoryPayTypeId.toString -
        YourStatutoryPayPerWeekId.toString - YourStatutoryWeeksId.toString -
        YourStatutoryStartDateId.toString - YourStatutoryPayBeforeTaxId.toString)
      case _ => cacheMap
    }

    store(YouStatutoryPayId.toString, value, mapToStore)
  }

  private def storeYourStatutoryPayType(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val existingStatPayType: Option[JsValue] = cacheMap.data.get(YourStatutoryPayTypeId.toString)

    val mapToStore: CacheMap = existingStatPayType.fold(cacheMap) {
      case `value` => cacheMap
      case _ => cacheMap copy (data = cacheMap.data
        - YourStatutoryPayPerWeekId.toString
        - YourStatutoryWeeksId.toString
        - YourStatutoryPayBeforeTaxId.toString
        - YourStatutoryStartDateId.toString)
    }

    store(YourStatutoryPayTypeId.toString, value, mapToStore)
  }

  private def storeYourStatutoryPayBeforeTax(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString("false") => cacheMap copy (data = cacheMap.data - YourStatutoryPayPerWeekId.toString)
      case _ => cacheMap
    }

    store(YourStatutoryPayBeforeTaxId.toString, value, mapToStore)
  }

  private def storeBothStatutoryPay(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - PartnerStatutoryPayTypeId.toString -
        PartnerStatutoryPayPerWeekId.toString - PartnerStatutoryWeeksId.toString -
        PartnerStatutoryStartDateId.toString - PartnerStatutoryPayBeforeTaxId.toString)
      case _ => cacheMap
    }

    store(BothStatutoryPayId.toString, value, mapToStore)
  }


  private def storePartnerStatutoryPayType(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val existingStatPayType: Option[JsValue] = cacheMap.data.get(PartnerStatutoryPayTypeId.toString)

    val mapToStore: CacheMap = existingStatPayType.fold(cacheMap) {
      case `value` => cacheMap
      case _ => cacheMap copy (data = cacheMap.data
        - PartnerStatutoryPayPerWeekId.toString
        - PartnerStatutoryWeeksId.toString
        - PartnerStatutoryPayBeforeTaxId.toString
        - PartnerStatutoryStartDateId.toString)
    }

    store(PartnerStatutoryPayTypeId.toString, value, mapToStore)
  }

  private def storePartnerStatutoryPayBeforeTax(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString("false") => cacheMap copy (data = cacheMap.data - PartnerStatutoryPayPerWeekId.toString)
      case _ => cacheMap
    }

    store(PartnerStatutoryPayBeforeTaxId.toString, value, mapToStore)
  }

  private def storeWhoGotStatutoryPay(value: JsValue, cacheMap: CacheMap): CacheMap = {

     val mapToStore = value match {
      case JsString("partner") => cacheMap copy (data = cacheMap.data
        - YourStatutoryPayTypeId.toString
        - YourStatutoryPayPerWeekId.toString
        - YourStatutoryWeeksId.toString
        - YourStatutoryStartDateId.toString
        - YourStatutoryPayBeforeTaxId.toString)
      case JsString("you") => cacheMap copy (data = cacheMap.data
        - PartnerStatutoryPayTypeId.toString
        - PartnerStatutoryPayPerWeekId.toString
        - PartnerStatutoryWeeksId.toString
        - PartnerStatutoryStartDateId.toString
        - PartnerStatutoryPayBeforeTaxId.toString)
      case _ => cacheMap
    }
    store(WhoGotStatutoryPayId.toString, value, mapToStore)
  }
}

