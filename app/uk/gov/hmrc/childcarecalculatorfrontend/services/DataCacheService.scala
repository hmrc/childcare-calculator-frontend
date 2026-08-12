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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import com.google.inject.{ImplementedBy, Inject}
import play.api.libs.json.{Format, Json, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts.CascadeUpsert
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.SessionIdProvider
import uk.gov.hmrc.childcarecalculatorfrontend.repositories.SessionRepository
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheKey, CacheMap}

import scala.concurrent.{ExecutionContext, Future}

class DataCacheServiceImpl @Inject() (val sessionRepository: SessionRepository, val cascadeUpsert: CascadeUpsert)(
    using ec: ExecutionContext
) extends DataCacheService {

  def save[A](
      cacheKey: CacheKey[A],
      value: A
  )(
      using writes: Writes[A],
      request: SessionIdProvider
  ): Future[CacheMap] = {
    val cacheId = request.sessionId

    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap =
        cascadeUpsert(cacheKey, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map(_ => updatedCacheMap)
    }
  }

  def fetch()(using request: SessionIdProvider): Future[Option[CacheMap]] =
    sessionRepository().get(request.sessionId)

  def getEntry[A](
      key: CacheKey[A]
  )(using reads: Reads[A], request: SessionIdProvider): Future[Option[A]] =
    fetch().map(optionalCacheMap => optionalCacheMap.flatMap(cacheMap => cacheMap.getEntry(key)))

  def saveInMap[K, V](cacheKey: CacheKey[Map[K, V]], key: K, value: V)(
      using fmt: Format[Map[K, V]],
      request: SessionIdProvider
  ): Future[CacheMap] = {
    val cacheId = request.sessionId

    sessionRepository().get(cacheId).flatMap {
      _.map { cacheMap =>
        val map: Map[K, V]  = cacheMap.getEntry(cacheKey).getOrElse(Map.empty)
        val updatedMap      = map + (key -> value)
        val updatedCacheMap = cacheMap.copy(data = cacheMap.data + (cacheKey.cacheKey -> Json.toJson(updatedMap)))
        sessionRepository().upsert(updatedCacheMap).map(_ => updatedCacheMap)
      }.getOrElse(throw new RuntimeException(s"Couldn't find document with key $cacheId"))
    }
  }

}

@ImplementedBy(classOf[DataCacheServiceImpl])
trait DataCacheService {

  def save[A](key: CacheKey[A], value: A)(
      using Writes[A],
      SessionIdProvider
  ): Future[CacheMap]

  def fetch()(using SessionIdProvider): Future[Option[CacheMap]]

  def getEntry[A](
      key: CacheKey[A]
  )(using Reads[A], SessionIdProvider): Future[Option[A]]

  def saveInMap[K, V](
      cacheKey: CacheKey[Map[K, V]],
      key: K,
      value: V
  )(
      using Format[Map[K, V]],
      SessionIdProvider
  ): Future[CacheMap]

}
