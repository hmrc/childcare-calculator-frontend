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

package uk.gov.hmrc.childcarecalculatorfrontend.repositories


import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Indexes._
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.libs.json.{Format, JsValue, Json}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.SECONDS
import scala.concurrent.{ExecutionContext, Future}

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: Instant = Instant.now())

object DatedCacheMap {
  implicit val dateFormat: Format[Instant] = MongoJavatimeFormats.Implicits.jatInstantFormat
  implicit val formats: Format[DatedCacheMap] = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DatedCacheMap](
    collectionName = config.get[String]("appName"),
    mongoComponent = mongo,
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(
      IndexModel(ascending("lastUpdated"), IndexOptions()
        .name("userAnswersExpiry")
        .expireAfter(config.get[Long]("mongodb.timeToLiveInSeconds"), SECONDS))
    )
    , extraCodecs = Seq(Codecs.playFormatCodec(CacheMap.formats))
  ) with Logging {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val dcm = DatedCacheMap(cm)
    collection.replaceOne(
      filter = equal("id", dcm.id),
      replacement = dcm,
      ReplaceOptions().upsert(true)
    ).toFuture().map(_.wasAcknowledged())
  }

  def get(id: String): Future[Option[CacheMap]] =
    collection.find[CacheMap](and(equal("id", id))).headOption()
}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: MongoComponent)(implicit ec: ExecutionContext) {

  private lazy val sessionRepository = new ReactiveMongoRepository(config, mongoComponent)

  def apply(): ReactiveMongoRepository = sessionRepository
}
