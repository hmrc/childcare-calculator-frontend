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

import play.api.libs.json.{JsArray, JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Location, YesNoNotSure, YesNoNotYet}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

import javax.inject.{Inject, Singleton}

@Singleton
class MinimumHoursCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      LocationId.cacheKey         -> ((v, cm) => storeLocation(v, cm)),
      ChildcareCostsId.cacheKey   -> ((v, cm) => storeChildcareCosts(v, cm)),
      ApprovedProviderId.cacheKey -> ((v, cm) => storeApprovedProvider(v, cm))
    )

  private def storeLocation(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(Location.England.toString) =>
          cacheMap.removedAll(ChildAgedTwoId, ChildAgedThreeOrFourId)
        case JsString(Location.NorthernIreland.toString) | JsString(Location.Wales.toString) =>
          cacheMap.removedAll(ChildAgedTwoId, ChildrenAgeGroupsId)
        case _ =>
          cacheMap.removed(ChildrenAgeGroupsId)
      }

    mapToStore.updated(LocationId, value)
  }

  private def storeChildcareCosts(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val locationValue             = cacheMap.data.getOrElse(LocationId.cacheKey, JsString(Location.England.toString))
    val childAgedTwoValue         = cacheMap.data.getOrElse(ChildAgedTwoId.cacheKey, JsBoolean(false))
    val childAgedThreeOrFourValue = cacheMap.data.getOrElse(ChildAgedThreeOrFourId.cacheKey, JsBoolean(false))
    val childrenAgeGroupsValue =
      cacheMap.data.getOrElse(ChildrenAgeGroupsId.cacheKey, JsArray(Seq(JsString(ChildAgeGroup.NoneOfThese.toString))))

    val existingChildCareCostValue = cacheMap.data.get(ChildcareCostsId.cacheKey)

    val mapToStore =
      value match {
        case JsString(YesNoNotYet.No.toString)
            if !existingChildCareCostValue
              .contains(JsString(YesNoNotYet.No.toString)) && locationValue == JsString(Location.England.toString) =>
          cacheMap.copy(data =
            Map(LocationId.cacheKey -> locationValue, ChildrenAgeGroupsId.cacheKey -> childrenAgeGroupsValue)
          )
        case JsString(YesNoNotYet.No.toString)
            if !existingChildCareCostValue.contains(JsString(YesNoNotYet.No.toString)) =>
          cacheMap.copy(data =
            Map(
              LocationId.cacheKey             -> locationValue,
              ChildAgedTwoId.cacheKey         -> childAgedTwoValue,
              ChildAgedThreeOrFourId.cacheKey -> childAgedThreeOrFourValue
            )
          )
        case _ =>
          cacheMap
      }

    mapToStore.updated(ChildcareCostsId, value)
  }

  private def storeApprovedProvider(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val locationValue             = cacheMap.data.getOrElse(LocationId.cacheKey, JsString(Location.England.toString))
    val childAgedTwoValue         = cacheMap.data.getOrElse(ChildAgedTwoId.cacheKey, JsBoolean(false))
    val childAgedThreeOrFourValue = cacheMap.data.getOrElse(ChildAgedThreeOrFourId.cacheKey, JsBoolean(false))
    val childrenAgeGroupsValue =
      cacheMap.data.getOrElse(ChildrenAgeGroupsId.cacheKey, JsArray(Seq(JsString(ChildAgeGroup.NoneOfThese.toString))))
    val childCareCostValue = cacheMap.data.getOrElse(ChildcareCostsId.cacheKey, JsString(YesNoNotYet.No.toString))

    val existingApprovedProviderValue = cacheMap.data.get(ApprovedProviderId.cacheKey)

    val mapToStore = value match {
      case JsString(YesNoNotSure.No.toString)
          if !existingApprovedProviderValue
            .contains(JsString(YesNoNotSure.No.toString)) && locationValue == JsString(Location.England.toString) =>
        cacheMap.copy(data =
          Map(
            LocationId.cacheKey          -> locationValue,
            ChildrenAgeGroupsId.cacheKey -> childrenAgeGroupsValue,
            ChildcareCostsId.cacheKey    -> childCareCostValue
          )
        )
      case JsString(YesNoNotSure.No.toString)
          if !existingApprovedProviderValue.contains(JsString(YesNoNotSure.No.toString)) =>
        cacheMap.copy(data =
          Map(
            LocationId.cacheKey             -> locationValue,
            ChildAgedTwoId.cacheKey         -> childAgedTwoValue,
            ChildAgedThreeOrFourId.cacheKey -> childAgedThreeOrFourValue,
            ChildcareCostsId.cacheKey       -> childCareCostValue
          )
        )
      case _ =>
        cacheMap
    }

    mapToStore.updated(ApprovedProviderId, value)
  }

}
