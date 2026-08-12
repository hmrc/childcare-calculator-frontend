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
import play.api.libs.json.*
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheKey, CacheMap}

@Singleton
class ChildrenCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      NoOfChildrenId.cacheKey               -> ((v, cm) => storeNoOfChildren(v, cm)),
      AboutYourChildId.cacheKey             -> ((v, cm) => storeAboutYourChild(v, cm)),
      ChildrenDisabilityBenefitsId.cacheKey -> ((v, cm) => storeChildrenDisabilityBenefits(v, cm)),
      ChildDisabilityBenefitsId.cacheKey    -> ((v, cm) => storeChildDisabilityBenefits(v, cm)),
      WhichChildrenDisabilityId.cacheKey    -> ((v, cm) => storeWhichChildrenDisability(v, cm)),
      RegisteredBlindId.cacheKey            -> ((v, cm) => storeRegisteredBlind(v, cm)),
      WhoHasChildcareCostsId.cacheKey       -> ((v, cm) => storeWhoHasChildcareCosts(v, cm))
    )

  private def storeNoOfChildren(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("noOfChildren")
    val mapToStore = value match {
      case JsNumber(_) if !originalDataSet.contains(value) =>
        cacheMap.removedAll(
          AboutYourChildId,
          ChildrenDisabilityBenefitsId,
          WhichChildrenDisabilityId,
          WhichDisabilityBenefitsId,
          ChildRegisteredBlindId,
          RegisteredBlindId,
          WhichChildrenBlindId,
          WhoHasChildcareCostsId,
          ChildcarePayFrequencyId,
          ExpectedChildcareCostsId
        )
      case _ => cacheMap
    }
    mapToStore.updated(NoOfChildrenId, value)
  }

  private def storeAboutYourChild(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("aboutYourChild")
    val mapToStore = value match {
      case JsObject(_) if !originalDataSet.contains(value) =>
        cacheMap.copy(data = cacheMap.data)

      case _ => cacheMap
    }
    mapToStore.updated(AboutYourChildId, value)
  }

  private def storeChildrenDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removedAll(WhichChildrenDisabilityId, WhichDisabilityBenefitsId)
      case _ => cacheMap
    }
    mapToStore.updated(ChildrenDisabilityBenefitsId, value)
  }

  private def storeChildDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removed(WhichDisabilityBenefitsId)
      case _ => cacheMap
    }
    mapToStore.updated(ChildDisabilityBenefitsId, value)
  }

  private def storeRegisteredBlind(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removed(WhichChildrenBlindId)
      case _ => cacheMap
    }
    mapToStore.updated(RegisteredBlindId, value)
  }

  private def storeWhichChildrenDisability(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = removeChildcareDependencies(
      value,
      cacheMap,
      WhichChildrenDisabilityId.cacheKey,
      WhichDisabilityBenefitsId
    )

    mapToStore.updated(WhichChildrenDisabilityId, value)
  }

  private def storeWhoHasChildcareCosts(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val updatedChildcarePayFrequency =
      removeChildcareDependencies(value, cacheMap, WhoHasChildcareCostsId.cacheKey, ChildcarePayFrequencyId)
    val updatedExpectedChildCareCosts = removeChildcareDependencies(
      value,
      updatedChildcarePayFrequency,
      WhoHasChildcareCostsId.cacheKey,
      ExpectedChildcareCostsId
    )

    updatedExpectedChildCareCosts.updated(WhoHasChildcareCostsId, value)
  }

  private def removeChildcareDependencies(
      value: JsValue,
      cacheMap: CacheMap,
      parentKey: String,
      elementToDeleteKey: CacheKey[?]
  ): CacheMap =
    value
      .validate[Set[Int]]
      .fold(
        _ => cacheMap,
        newData =>
          cacheMap.data.get(parentKey) match {
            case Some(originalValues) =>
              cacheMap.data.get(elementToDeleteKey.cacheKey).fold(cacheMap) { elementToDelete =>
                val valuesToDelete = originalValues.as[Set[Int]].filterNot(newData)
                val updatedValues = valuesToDelete
                  .foldLeft(elementToDelete)((dataObject, element) => dataObject.as[JsObject] - element.toString)
                cacheMap.updated(elementToDeleteKey, updatedValues)
              }
            case _ => cacheMap
          }
      )

}
