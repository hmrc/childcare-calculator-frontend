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
class PensionsCascadeUpsert @Inject() {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      YouPaidPensionCYId.cacheKey     -> ((v, cm) => storeYouPaidPensionCY(v, cm)),
      PartnerPaidPensionCYId.cacheKey -> ((v, cm) => storePartnerPaidPensionCY(v, cm)),
      BothPaidPensionCYId.cacheKey    -> ((v, cm) => storeBothPaidPensionCY(v, cm)),
      WhoPaysIntoPensionId.cacheKey   -> ((v, cm) => storeWhoPaysIntoPension(v, cm))
    )

  private def storeYouPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.removed(HowMuchYouPayPensionId)
      case _                => cacheMap
    }

    mapToStore.updated(YouPaidPensionCYId, value)
  }

  private def storePartnerPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap.removed(HowMuchPartnerPayPensionId)
      case _                => cacheMap
    }

    mapToStore.updated(PartnerPaidPensionCYId, value)
  }

  private def storeBothPaidPensionCY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) =>
        cacheMap.removedAll(
          HowMuchYouPayPensionId,
          HowMuchPartnerPayPensionId,
          HowMuchBothPayPensionId,
          WhoPaysIntoPensionId
        )
      case _ => cacheMap
    }

    mapToStore.updated(BothPaidPensionCYId, value)
  }

  private def storeWhoPaysIntoPension(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(YouPartnerBoth.You.toString) =>
        cacheMap.removedAll(HowMuchPartnerPayPensionId, HowMuchBothPayPensionId)
      case JsString(YouPartnerBoth.Partner.toString) =>
        cacheMap.removedAll(HowMuchYouPayPensionId, HowMuchBothPayPensionId)
      case JsString(YouPartnerBoth.Both.toString) =>
        cacheMap.removedAll(HowMuchYouPayPensionId, HowMuchPartnerPayPensionId)
      case _ => cacheMap
    }

    mapToStore.updated(WhoPaysIntoPensionId, value)
  }

}
