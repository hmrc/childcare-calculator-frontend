/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.helpers

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{DefaultReads, Format, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.repositories.ReactiveMongoRepository
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.MongoSpecSupport

import scala.concurrent.ExecutionContext.Implicits.global

trait SessionHelper extends MongoSpecSupport with BeforeAndAfterEach with DefaultReads {
  self: IntegrationSpecBase =>

  lazy val repo = new ReactiveMongoRepository(app.configuration, mongo)

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(repo.drop)
    await(repo.count) mustBe 0
    resetWiremock()
  }

  def verifySessionCacheData[T](id: String, key: String, data: Option[T])(implicit format: Format[T]): Unit ={
    val dataFromDb = await(repo.get(id)).flatMap(_.getEntry[T](key))
    if (data != dataFromDb) throw new Exception(s"Data in database doesn't match expected data:\n expected data $data was not equal to actual data $dataFromDb")
  }

  def cacheSessionData[T](id: String, key: String, data: T)(implicit format: Format[T]): Unit ={
    val initialCount = await(repo.count)
    val cacheMap = await(repo.get(id))
    val updatedCacheMap =
      cacheMap.fold(CacheMap(id, Map(key -> Json.toJson(data))))(map => map.copy(data = map.data + (key -> Json.toJson(data))))

    await(repo.upsert(updatedCacheMap))
  }
}
