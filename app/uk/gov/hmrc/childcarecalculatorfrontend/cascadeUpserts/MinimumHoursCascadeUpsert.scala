/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class MinimumHoursCascadeUpsert @Inject()() extends SubCascadeUpsert {
  lazy val No: String = YesNoNotYetEnum.NO.toString

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      LocationId.toString -> ((v, cm) => storeLocation(v, cm)),
      ChildcareCostsId.toString -> ((v,cm) => storeChildcareCosts(v,cm)),
      ApprovedProviderId.toString -> ((v,cm) => storeApprovedProvider(v,cm))
    )

  private def storeLocation(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(northernIreland)) {
      cacheMap copy (data = cacheMap.data - ChildAgedTwoId.toString)
    } else {
      cacheMap
    }

    store(LocationId.toString, value, mapToStore)
  }

  private def storeChildcareCosts(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val locationValue = cacheMap.data.getOrElse(LocationId.toString, JsString(England))
    val childAgedTwoValue = cacheMap.data.getOrElse(ChildAgedTwoId.toString, JsBoolean(false))
    val childAgedThreeOrFourValue = cacheMap.data.getOrElse(ChildAgedThreeOrFourId.toString, JsBoolean(false))

    val existingChildCareCostValue = cacheMap.data.get(ChildcareCostsId.toString)

    val mapToStore = value match {
      case JsString(No) if !existingChildCareCostValue.contains(JsString(NO)) => {
        cacheMap copy (data = Map(LocationId.toString -> locationValue,
          ChildAgedTwoId.toString -> childAgedTwoValue,
          ChildAgedThreeOrFourId.toString -> childAgedThreeOrFourValue))
      }
      case _ => {
        cacheMap
      }
    }

    store(ChildcareCostsId.toString, value, mapToStore)
  }

  private def storeApprovedProvider(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val locationValue = cacheMap.data.getOrElse(LocationId.toString, JsString(England))
    val childAgedTwoValue = cacheMap.data.getOrElse(ChildAgedTwoId.toString, JsBoolean(false))
    val childAgedThreeOrFourValue = cacheMap.data.getOrElse(ChildAgedThreeOrFourId.toString, JsBoolean(false))
    val childCareCostValue = cacheMap.data.getOrElse(ChildcareCostsId.toString, JsString(No))

    val existingApprovedProviderValue = cacheMap.data.get(ApprovedProviderId.toString)

    val NO: String = YesNoUnsureEnum.NO.toString

    val mapToStore = value match {
      case JsString(NO) if !existingApprovedProviderValue.contains(JsString(NO)) => {
        cacheMap copy (data = Map(LocationId.toString -> locationValue,
          ChildAgedTwoId.toString -> childAgedTwoValue,
          ChildAgedThreeOrFourId.toString -> childAgedThreeOrFourValue,
          ChildcareCostsId.toString -> childCareCostValue))
      }
      case _ => {
        cacheMap
      }
    }

    store(ApprovedProviderId.toString, value, mapToStore)
  }

}
