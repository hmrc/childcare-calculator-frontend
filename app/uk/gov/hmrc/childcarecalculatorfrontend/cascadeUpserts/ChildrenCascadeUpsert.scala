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

import play.api.libs.json.{Json, _}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class ChildrenCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      NoOfChildrenId.toString -> ((v, cm) => storeNoOfChildren(v, cm)) ,
        AboutYourChildId.toString -> ((v, cm) => storeAboutYourChild(v, cm)),
      ChildApprovedEducationId.toString -> ((v, cm) => storeChildApprovedEducation(v, cm)),
        ChildrenDisabilityBenefitsId.toString -> ((v, cm) => storeChildrenDisabilityBenefits(v, cm)),
          ChildDisabilityBenefitsId.toString -> ((v, cm) => storeChildDisabilityBenefits(v, cm)),
      WhichChildrenDisabilityId.toString -> ((v, cm) => storeWhichChildrenDisability(v, cm))


    )

  private def storeNoOfChildren(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("noOfChildren")
    val mapToStore= value match {
      case JsNumber(_) if !originalDataSet.contains(value) => {
        cacheMap copy (data = cacheMap.data - AboutYourChildId.toString - ChildApprovedEducationId.toString -
          ChildStartEducationId.toString - ChildrenDisabilityBenefitsId.toString - WhichChildrenDisabilityId.toString - WhichDisabilityBenefitsId.toString -
          ChildRegisteredBlindId.toString - RegisteredBlindId.toString - WhichChildrenBlindId.toString - WhichBenefitsYouGetId.toString -
          WhoHasChildcareCostsId.toString - ChildcarePayFrequencyId.toString - ExpectedChildcareCostsId.toString)
      }
      case _ => cacheMap
    }
    store(NoOfChildrenId.toString, value, mapToStore)
  }

  private def storeAboutYourChild(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("aboutYourChild")
    val mapToStore= value match {
     case JsObject(_) if !originalDataSet.contains(value)  =>
       cacheMap copy (data = cacheMap.data - ChildApprovedEducationId.toString.toString - ChildStartEducationId.toString)

      case _ => cacheMap
    }
    store(AboutYourChildId.toString, value, mapToStore)
  }

  private def storeChildApprovedEducation(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("childApprovedEducation")
    val mapToStore= value match {
      case JsObject(_)  if !originalDataSet.contains(value)  =>
        cacheMap copy (data = cacheMap.data - ChildStartEducationId.toString)
      case _ => cacheMap
    }
    store(ChildApprovedEducationId.toString, value, mapToStore)
  }


  private def storeChildrenDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore= value match {
      case JsBoolean(false)   =>
        cacheMap copy (data = cacheMap.data - WhichChildrenDisabilityId.toString -  WhichDisabilityBenefitsId.toString)
      case _ => cacheMap
    }
    store(ChildrenDisabilityBenefitsId.toString, value, mapToStore)
  }

  private def storeChildDisabilityBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val mapToStore= value match {
      case JsBoolean(false)   =>
        cacheMap copy (data = cacheMap.data  -  WhichDisabilityBenefitsId.toString)
      case _ => cacheMap
    }
    store(ChildDisabilityBenefitsId.toString, value, mapToStore)
  }

  private def storeWhichChildrenDisability(value: JsValue, cacheMap: CacheMap): CacheMap = {

    val originalDataSet = cacheMap.data.get("whichChildrenDisability")

    println("originalDataSet"+originalDataSet)

    println("value"+value)
    println("!originalDataSet.contains(value)"+originalDataSet.contains(value))

    val mapToStore= value match {
      case JsObject(_)  if !originalDataSet.contains(value)  => {
        println("in...................")
        cacheMap copy (data = cacheMap.data - WhichChildrenDisabilityId.toString)
      }
      case _ => cacheMap
    }
    store(WhichChildrenDisabilityId.toString, value, mapToStore)
  }

}
