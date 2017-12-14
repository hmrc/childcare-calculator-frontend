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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class StatutoryCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      YourStatutoryPayBeforeTaxId.toString -> ((v, cm) => storeYourStatutoryPayBeforeTax(v, cm)),
      YourStatutoryPayTypeId.toString -> ((v, cm) => storeYourStatutoryPayType(v, cm)),
      YouStatutoryPayId.toString -> ((v, cm) => storeYouStatutoryPay(v, cm))
    )

  private def storeYourStatutoryPayBeforeTax(value: JsValue, cacheMap: CacheMap): CacheMap  = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourStatutoryPayPerWeekId.toString)
      case _ => cacheMap
    }

    store(YourStatutoryPayBeforeTaxId.toString, value, mapToStore)
  }

  private def storeYourStatutoryPayType(value: JsValue, cacheMap: CacheMap): CacheMap  = {

    val existingStatPayType: Option[JsValue] = cacheMap.data.get(YourStatutoryPayTypeId.toString)

    val mapToStore: CacheMap = existingStatPayType.fold(cacheMap){
      case `value` => cacheMap
      case _ => cacheMap copy (data = cacheMap.data - YourStatutoryPayPerWeekId.toString - YourStatutoryWeeksId.toString -
        YourStatutoryStartDateId.toString)
    }

    store(YourStatutoryPayTypeId.toString, value, mapToStore)
  }

  private def storeYouStatutoryPay(value: JsValue, cacheMap: CacheMap): CacheMap  = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourStatutoryPayTypeId.toString - YourStatutoryPayPerWeekId.toString -
        YourStatutoryWeeksId.toString - YourStatutoryStartDateId.toString - YourStatutoryPayBeforeTaxId.toString)
      case _ => cacheMap
    }

    store(YouStatutoryPayId.toString, value, mapToStore)
  }

}
