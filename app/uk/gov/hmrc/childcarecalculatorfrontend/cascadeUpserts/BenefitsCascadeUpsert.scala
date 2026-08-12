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

import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

import javax.inject.{Inject, Singleton}

@Singleton
class BenefitsCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      WhosHadBenefitsId.cacheKey        -> ((v, cm) => storeWhosHadBenefits(v, cm)),
      YouAnyTheseBenefitsCYId.cacheKey  -> ((v, cm) => storeYouAnyTheseBenefits(v, cm)),
      BothAnyTheseBenefitsCYId.cacheKey -> ((v, cm) => storeBothAnyTheseBenefitsCY(v, cm))
    )

  private def storeWhosHadBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(YouPartnerBoth.You.toString) =>
        cacheMap.removedAll(PartnerBenefitsIncomeCYId, BenefitsIncomeCYId)
      case JsString(YouPartnerBoth.Partner.toString) =>
        cacheMap.removedAll(YouBenefitsIncomeCYId, BenefitsIncomeCYId)
      case JsString(YouPartnerBoth.Both.toString) =>
        cacheMap.removedAll(YouBenefitsIncomeCYId, PartnerBenefitsIncomeCYId)
      case _ => cacheMap
    }

    mapToStore.updated(WhosHadBenefitsId, value)
  }

  private def storeYouAnyTheseBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.removed(YouBenefitsIncomeCYId)
      case _                => cacheMap
    }

    mapToStore.updated(YouAnyTheseBenefitsCYId, value)
  }

  private def storeBothAnyTheseBenefitsCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removedAll(WhosHadBenefitsId, YouBenefitsIncomeCYId, PartnerBenefitsIncomeCYId, BenefitsIncomeCYId)
      case _ => cacheMap
    }

    mapToStore.updated(BothAnyTheseBenefitsCYId, value)
  }

}
