/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.connectors

import com.google.inject.{ImplementedBy, Inject}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.repositories.SessionRepository
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CascadeUpsert
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class DataCacheConnectorImpl @Inject()(val sessionRepository: SessionRepository, val cascadeUpsert: CascadeUpsert) extends DataCacheConnector {

  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = {
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap = cascadeUpsert(key, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map {_ => updatedCacheMap}
    }
  }

  def remove(cacheId: String, key: String): Future[Boolean] = {
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(Future(false)) { cacheMap =>
        val newCacheMap = cacheMap copy (data = cacheMap.data - key)
        sessionRepository().upsert(newCacheMap)
      }
    }
  }

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    sessionRepository().get(cacheId)

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] = {
    fetch(cacheId).map { optionalCacheMap =>
      optionalCacheMap.flatMap { cacheMap => cacheMap.getEntry(key)}
    }
  }

  def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = {
    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap = cascadeUpsert.addRepeatedValue(collectionKey, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository().upsert(updatedCacheMap).map {_ => updatedCacheMap}
    }
  }

  def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(implicit fmt: Format[A]): Future[CacheMap] = {

    import play.api.libs.json.JodaReads._
    import play.api.libs.json.JodaWrites._

    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(throw new Exception(s"Couldn't find document with key $cacheId")) {cacheMap =>
        val newSeq = cacheMap.data(collectionKey).as[Seq[A]].filterNot(x => x == item)
        val newCacheMap = if (newSeq.isEmpty) {
          cacheMap copy (data = cacheMap.data - collectionKey)
        } else {
          cacheMap copy (data = cacheMap.data + (collectionKey -> Json.toJson(newSeq)))
        }
        sessionRepository().upsert(newCacheMap).map {_ => newCacheMap}
      }
    }
  }

  def replaceInSeq[A](cacheId: String, collectionKey: String, index: Int, item: A)(implicit fmt: Format[A]): Future[CacheMap] = {
    import play.api.libs.json.JodaReads._
    import play.api.libs.json.JodaWrites._

    sessionRepository().get(cacheId).flatMap { optionalCacheMap =>
      optionalCacheMap.fold(throw new Exception(s"Couldn't find document with key $cacheId")) {cacheMap =>
        val oldSeq = cacheMap.data.lift(collectionKey).map(_.as[Seq[A]]).getOrElse(Seq.empty)
        val newSeq = if (index > oldSeq.length - 1) {
          oldSeq :+ item
        } else {
          oldSeq.updated(index, item)
        }
        val updatedCacheMap = cacheMap copy (data = cacheMap.data + (collectionKey -> Json.toJson(newSeq)))
        sessionRepository().upsert(updatedCacheMap).map {_ => updatedCacheMap}
      }
    }
  }

  def saveInMap[K, V](cacheId: String, collectionKey: String, key: K, value: V)
                        (implicit fmt: Format[Map[K, V]]): Future[CacheMap] = {
    import play.api.libs.json.JodaReads._
    import play.api.libs.json.JodaWrites._

    sessionRepository().get(cacheId).flatMap {
      _.map {
        cacheMap =>
          val map = cacheMap.data.get(collectionKey).map(_.as[Map[K, V]]).getOrElse(Map.empty)
          val updatedMap = map + (key -> value)
          val updatedCacheMap = cacheMap copy (data = cacheMap.data + (collectionKey -> Json.toJson(updatedMap)))
          sessionRepository().upsert(updatedCacheMap).map { _ => updatedCacheMap }
      }.getOrElse(throw new RuntimeException(s"Couldn't find document with key $cacheId"))
    }
  }

  def updateMap(data: CacheMap) : Future[Boolean] = {
    sessionRepository().upsert(data)
  }
}

@ImplementedBy(classOf[DataCacheConnectorImpl])
trait DataCacheConnector {
  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap]

  def updateMap(data: CacheMap) : Future[Boolean]

  def remove(cacheId: String, key: String): Future[Boolean]

  def fetch(cacheId: String): Future[Option[CacheMap]]

  def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]]

  def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap]

  def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(implicit fmt: Format[A]): Future[CacheMap]

  def replaceInSeq[A](cacheId: String, collectionKey: String, index: Int, item: A)(implicit fmt: Format[A]): Future[CacheMap]

  def saveInMap[K, V](cacheId: String, collectionKey: String, key: K, value: V)(implicit fmt: Format[Map[K, V]]): Future[CacheMap]
}
