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

class ChildrenCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      NoOfChildrenId.toString -> ((v, cm) => storeNoOfChildren(v, cm))

    )

  private def storeNoOfChildren(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val originalDataSet = cacheMap.data.get("noOfChildren")

    val mapToStore= value match {
      case JsString(_) if !originalDataSet.contains(value) => cacheMap copy (data = cacheMap.data - AboutYourChildId.toString - ChildApprovedEducationId.toString -
        ChildStartEducationId.toString - ChildrenDisabilityBenefitsId.toString - WhichChildrenDisabilityId.toString - WhichDisabilityBenefitsId.toString -
        ChildRegisteredBlindId.toString - WhichChildrenBlindId.toString - ChildRegisteredBlindId.toString - WhichBenefitsYouGetId.toString -
        ChildcarePayFrequencyId.toString - ExpectedChildcareCostsId.toString
        )
      case _ => cacheMap
    }
    store(NoOfChildrenId.toString, value, mapToStore)
  }

}
