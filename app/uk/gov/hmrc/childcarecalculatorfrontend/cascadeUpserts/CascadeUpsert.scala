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

import play.api.libs.json.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheKey, CacheMap}

import javax.inject.{Inject, Singleton}

@Singleton
class CascadeUpsert @Inject() (
    pensions: PensionsCascadeUpsert,
    income: IncomeCascadeUpsert,
    benefits: BenefitsCascadeUpsert,
    maxHours: MaximumHoursCascadeUpsert,
    minHours: MinimumHoursCascadeUpsert,
    children: ChildrenCascadeUpsert
) {

  private val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] = pensions.funcMap ++ income.funcMap ++
    benefits.funcMap ++ maxHours.funcMap ++ minHours.funcMap ++ children.funcMap

  def apply[A](key: CacheKey[A], value: A, originalCacheMap: CacheMap)(using fmt: Writes[A]): CacheMap =
    funcMap
      .get(key.cacheKey)
      .fold {
        originalCacheMap.updated(key, Json.toJson(value))
      }(fn => fn(Json.toJson(value), originalCacheMap))

}
