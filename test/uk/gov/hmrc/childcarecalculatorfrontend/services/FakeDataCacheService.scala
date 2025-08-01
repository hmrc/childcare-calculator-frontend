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

import play.api.libs.json.Format
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

import scala.concurrent.{ExecutionContext, Future}

object FakeDataCacheService extends DataCacheConnector {

  implicit val ec: ExecutionContext = ExecutionContext.global

  override def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = Future(
    CacheMap(cacheId, Map())
  )

  override def remove(cacheId: String, key: String): Future[Boolean] = ???

  override def fetch(cacheId: String): Future[Option[CacheMap]] = Future(Some(CacheMap(cacheId, Map())))

  override def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] = Future(
    CacheMap(cacheId, Map()).getEntry(key)
  )

  override def addToCollection[A](cacheId: String, collectionKey: String, value: A)(
      implicit fmt: Format[A]
  ): Future[CacheMap] = Future(CacheMap(cacheId, Map()))

  override def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(
      implicit fmt: Format[A]
  ): Future[CacheMap] = Future(CacheMap(cacheId, Map()))

  override def replaceInSeq[A](cacheId: String, collectionKey: String, index: Int, item: A)(
      implicit fmt: Format[A]
  ): Future[CacheMap] = Future(CacheMap(cacheId, Map()))

  override def saveInMap[K, V](cacheId: String, collectionKey: String, key: K, value: V)(
      implicit fmt: Format[Map[K, V]]
  ) = Future(CacheMap(cacheId, Map()))

  override def updateMap(data: CacheMap): Future[Boolean] = Future(true)
}
