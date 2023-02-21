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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class BenefitsCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      WhosHadBenefitsPYId.toString -> ((v, cm) => storeWhosHadBenefitsPY(v, cm)),
      YouAnyTheseBenefitsPYId.toString -> ((v, cm) => storeYouAnyTheseBenefitsPY(v, cm)),
      PartnerAnyTheseBenefitsPYId.toString -> ((v, cm) => storePartnerAnyTheseBenefitsPY(v, cm)),
      BothAnyTheseBenefitsPYId.toString -> ((v, cm) => storeBothAnyTheseBenefitsPY(v, cm)),
      WhosHadBenefitsId.toString->((v,cm) => storeWhosHadBenefits(v,cm)),
      YouAnyTheseBenefitsIdCY.toString ->((v,cm) => storeYouAnyTheseBenefits(v,cm)),
      PartnerAnyTheseBenefitsCYId.toString ->((v,cm) => storePartnerAnyTheseBenefitsCY(v,cm)),
      BothAnyTheseBenefitsCYId.toString ->((v,cm) => storeBothAnyTheseBenefitsCY(v,cm))
    )

  private def storeWhosHadBenefitsPY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - PartnerBenefitsIncomePYId.toString -
        BothBenefitsIncomePYId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomePYId.toString -
        BothBenefitsIncomePYId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomePYId.toString -
        PartnerBenefitsIncomePYId.toString)
      case _ => cacheMap
    }

    store(WhosHadBenefitsPYId.toString, value, mapToStore)
  }

  private def storeYouAnyTheseBenefitsPY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YouBenefitsIncomePYId.toString)
      case _ => cacheMap
    }

    store(YouAnyTheseBenefitsPYId.toString, value, mapToStore)
  }

  private def storePartnerAnyTheseBenefitsPY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - PartnerBenefitsIncomePYId.toString)
      case _ => cacheMap
    }

    store(PartnerAnyTheseBenefitsPYId.toString, value, mapToStore)
  }

  private def storeBothAnyTheseBenefitsPY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - WhosHadBenefitsPYId.toString -
        YouBenefitsIncomePYId.toString - PartnerBenefitsIncomePYId.toString - BothBenefitsIncomePYId.toString)
      case _ => cacheMap
    }

    store(BothAnyTheseBenefitsPYId.toString, value, mapToStore)
  }

  private def storeWhosHadBenefits(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - PartnerBenefitsIncomeCYId.toString -
        BenefitsIncomeCYId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomeCYId.toString -
        BenefitsIncomeCYId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - YouBenefitsIncomeCYId.toString -
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



