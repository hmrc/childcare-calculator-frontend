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

import play.api.libs.json.{Format, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.SessionIdProvider
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheKey, CacheMap}

import scala.concurrent.{ExecutionContext, Future}

object FakeDataCacheService extends DataCacheService {

  given ec: ExecutionContext = ExecutionContext.global

  override def save[A](
      key: CacheKey[A],
      value: A
  )(using writes: Writes[A], sessionIdProvider: SessionIdProvider): Future[CacheMap] = Future(
    CacheMap(sessionIdProvider.sessionId, Map())
  )

  override def fetch()(using sessionIdProvider: SessionIdProvider): Future[Option[CacheMap]] = Future(
    Some(CacheMap(sessionIdProvider.sessionId, Map()))
  )

  override def getEntry[A](
      key: CacheKey[A]
  )(using reads: Reads[A], sessionIdProvider: SessionIdProvider): Future[Option[A]] = Future(
    CacheMap(sessionIdProvider.sessionId, Map()).getEntry(key)
  )

  override def saveInMap[K, V](collectionKey: CacheKey[Map[K, V]], key: K, value: V)(
      using fmt: Format[Map[K, V]],
      sessionIdProvider: SessionIdProvider
  ) = Future(CacheMap(sessionIdProvider.sessionId, Map()))

}
