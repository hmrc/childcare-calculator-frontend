/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts._
import uk.gov.hmrc.http.cache.client.CacheMap

@Singleton
class CascadeUpsert @Inject()(pensions: PensionsCascadeUpsert,
                              income: IncomeCascadeUpsert,
                              benefits: BenefitsCascadeUpsert,
                              maxHours: MaximumHoursCascadeUpsert,
                              minHours: MinimumHoursCascadeUpsert,
                              children: ChildrenCascadeUpsert,
                              statutory:StatutoryCascadeUpsert){

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] = pensions.funcMap ++ income.funcMap ++
                                                     benefits.funcMap ++ maxHours.funcMap ++ minHours.funcMap ++ children.funcMap ++
                                                     statutory.funcMap

  def apply[A](key: String, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap =
    funcMap.get(key).fold(store(key, value, originalCacheMap)) { fn => fn(Json.toJson(value), originalCacheMap)}

  def addRepeatedValue[A](key: String, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap = {
    val values = originalCacheMap.getEntry[Seq[A]](key).getOrElse(Seq()) :+ value
    originalCacheMap copy(data = originalCacheMap.data + (key -> Json.toJson(values)))
  }

  private def store[A](key:String, value: A, cacheMap: CacheMap)(implicit fmt: Format[A]) =
    cacheMap copy (data = cacheMap.data + (key -> Json.toJson(value)))

}

abstract class SubCascadeUpsert {
  def store[A](key:String, value: A, cacheMap: CacheMap)(implicit fmt: Format[A]) =
    cacheMap copy (data = cacheMap.data + (key -> Json.toJson(value)))
}
