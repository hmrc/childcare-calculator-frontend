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

class StatutoryCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      YourStatutoryPayBeforeTaxId.toString -> ((v, cm) => storeYourStatutoryPayBeforeTax(v, cm))
    )

  private def storeYourStatutoryPayBeforeTax(value: JsValue, cacheMap: CacheMap): CacheMap  = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourStatutoryPayPerWeekId.toString)
      case _ => cacheMap
    }

    store(YourStatutoryPayBeforeTaxId.toString, value, mapToStore)
  }

  private def storeWhosHadBenefits(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(You) => cacheMap copy (data = cacheMap.data  - PartnerBenefitsIncomeCYId.toString -
        BenefitsIncomeCYId.toString)
      case JsString(Partner) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomeCYId.toString -
        BenefitsIncomeCYId.toString)
      case JsString(Both) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomeCYId.toString -
        PartnerBenefitsIncomeCYId.toString)
      case _ => cacheMap
    }

    store(WhosHadBenefitsId.toString, value, mapToStore)
  }

  private def storeYouAnyTheseBenefits(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YouBenefitsIncomeCYId.toString)
      case _ => cacheMap
    }

    store(YouAnyTheseBenefitsIdCY.toString, value, mapToStore)
  }

  private def storePartnerAnyTheseBenefitsCY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - PartnerBenefitsIncomeCYId.toString)
      case _ => cacheMap
    }

    store(PartnerAnyTheseBenefitsCYId.toString, value, mapToStore)
  }


  private def storeBothAnyTheseBenefitsCY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - WhosHadBenefitsId.toString -
        YouBenefitsIncomeCYId.toString - PartnerBenefitsIncomeCYId.toString - BenefitsIncomeCYId.toString)
      case _ => cacheMap
    }

    store(BothAnyTheseBenefitsCYId.toString, value, mapToStore)
  }

}
