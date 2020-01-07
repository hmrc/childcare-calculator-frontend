/*
 * Copyright 2020 HM Revenue & Customs
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

class ChildrenCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      NoOfChildrenId.toString -> ((v, cm) => storeNoOfChildren(v, cm)),
      AboutYourChildId.toString -> ((v, cm) => storeAboutYourChild(v, cm)),
      ChildApprovedEducationId.toString -> ((v, cm) => storeChildApprovedEducation(v, cm)),
      ChildrenDisabilityBenefitsId.toString -> ((v, cm) => storeChildrenDisabilityBenefits(v, cm)),
      ChildDisabilityBenefitsId.toString -> ((v, cm) => storeChildDisabilityBenefits(v, cm)),
      WhichChildrenDisabilityId.toString -> ((v, cm) => storeWhichChildrenDisability(v, cm)),
      RegisteredBlindId.toString -> ((v, cm) => storeRegisteredBlind(v, cm)),
      WhoHasChildcareCostsId.toString -> ((v, cm) => storeWhoHasChildcareCosts(v, cm))

    )

  private def storeNoOfChildren(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("noOfChildren")
    val mapToStore = value match {
      case JsNumber(_) if !originalDataSet.contains(value) => {
        cacheMap copy (data = cacheMap.data - AboutYourChildId.toString - ChildApprovedEducationId.toString -
          ChildStartEducationId.toString - ChildrenDisabilityBenefitsId.toString - WhichChildrenDisabilityId.toString - WhichDisabilityBenefitsId.toString -
          ChildRegisteredBlindId.toString - RegisteredBlindId.toString - WhichChildrenBlindId.toString -
          WhoHasChildcareCostsId.toString - ChildcarePayFrequencyId.toString - ExpectedChildcareCostsId.toString)
      }
      case _ => cacheMap
    }
    store(NoOfChildrenId.toString, value, mapToStore)
  }

  private def storeAboutYourChild(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("aboutYourChild")
    val mapToStore = value match {
      case JsObject(_) if !originalDataSet.contains(value) =>
        cacheMap copy (data = cacheMap.data - ChildApprovedEducationId.toString.toString - ChildStartEducationId.toString)

      case _ => cacheMap
    }
    store(AboutYourChildId.toString, value, mapToStore)
  }

  private def storeChildApprovedEducation(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value.validate[Map[String, Boolean]].fold(_ => cacheMap, newValues => {
      cacheMap.data.get(ChildStartEducationId.toString).fold(cacheMap)(elementToDelete => {
        val updatedValues = newValues.filter(!_._2).foldLeft(elementToDelete)((dataObject, element) => dataObject.as[JsObject] - element._1)
        store(ChildStartEducationId.toString, updatedValues, cacheMap)
      })
    })

    store(ChildApprovedEducationId.toString, value, mapToStore)
  }

  private def storeChildrenDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap copy (data = cacheMap.data - WhichChildrenDisabilityId.toString - WhichDisabilityBenefitsId.toString)
      case _ => cacheMap
    }
    store(ChildrenDisabilityBenefitsId.toString, value, mapToStore)
  }

  private def storeChildDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap copy (data = cacheMap.data - WhichDisabilityBenefitsId.toString)
      case _ => cacheMap
    }
    store(ChildDisabilityBenefitsId.toString, value, mapToStore)
  }

  private def storeRegisteredBlind(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap copy (data = cacheMap.data - WhichChildrenBlindId.toString)
      case _ => cacheMap
    }
    store(RegisteredBlindId.toString, value, mapToStore)
  }

  private def storeWhichChildrenDisability(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = removeChildcareDependencies(value, cacheMap, WhichChildrenDisabilityId.toString, WhichDisabilityBenefitsId.toString)

    store(WhichChildrenDisabilityId.toString, value, mapToStore)
  }

  private def storeWhoHasChildcareCosts(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val updatedChildcarePayFrequency = removeChildcareDependencies(value, cacheMap, WhoHasChildcareCostsId.toString, ChildcarePayFrequencyId.toString)
    val updatedExpectedChildCareCosts = removeChildcareDependencies(value, updatedChildcarePayFrequency, WhoHasChildcareCostsId.toString, ExpectedChildcareCostsId.toString)

    store(WhoHasChildcareCostsId.toString, value, updatedExpectedChildCareCosts)
  }

  def removeChildcareDependencies(value: JsValue, cacheMap: CacheMap, parentKey: String, elementToDeleteKey: String) = {
    value.validate[Set[Int]].fold(_ => cacheMap, newData => {
      cacheMap.data.get(parentKey) match {
        case Some(originalValues) => {
          cacheMap.data.get(elementToDeleteKey).fold(cacheMap)(elementToDelete => {
            val valuesToDelete = originalValues.as[Set[Int]].filterNot(newData)
            val updatedValues = valuesToDelete.foldLeft(elementToDelete)((dataObject, element) => dataObject.as[JsObject] - element.toString)
            store(elementToDeleteKey, updatedValues, cacheMap)
          })
        }
        case _ => cacheMap
      }
    })
  }
}