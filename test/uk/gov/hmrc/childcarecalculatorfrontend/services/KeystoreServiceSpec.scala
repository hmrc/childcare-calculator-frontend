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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import play.api.libs.json.{JsString, Writes, Reads}
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.config.CCSessionCache
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import uk.gov.hmrc.play.http.{HttpResponse, HeaderCarrier}
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import scala.concurrent.Future

class KeystoreServiceSpec extends UnitSpec with MockitoSugar with FakeCCApplication with BeforeAndAfterEach {

  val sut = new KeystoreService {
    override val sessionCache: SessionCache = mock[SessionCache]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.sessionCache)
  }

  "KeystoreService" should {
    "use CCSessionCache" in {
      KeystoreService.sessionCache shouldBe CCSessionCache
    }

    "succeed saving data in cache" when {
      "returns data given for saving" in {
        when(
          sut.sessionCache.cache[String](anyString, anyString)(any[Writes[String]], any[HeaderCarrier])
        ).thenReturn(
          Future.successful(
            CacheMap("id", Map("test_key" -> JsString("test_value")))
          )
        )
        val result: Option[String] = await(sut.cacheEntryForSession[String]("test_key", "test_value"))
        result.isDefined shouldBe true
        result.get shouldBe "test_value"
      }
    }

    "fail saving data in cache" when {
      "returns None" in {
        when(
          sut.sessionCache.cache[String](anyString, anyString)(any[Writes[String]], any[HeaderCarrier])
        ).thenReturn(
          Future.successful(
            CacheMap("id", Map())
          )
        )
        val result: Option[String] = await(sut.cacheEntryForSession[String]("test_key", "test_value"))
        result.isDefined shouldBe false
      }
    }

    "return value from cache" when {
      "there is some data for given key" in {
        when(
          sut.sessionCache.fetchAndGetEntry[String](anyString)(any[HeaderCarrier], any[Reads[String]])
        ).thenReturn(
          Future.successful(
            Some("test_value")
          )
        )

        val result: Option[String] = await(sut.fetchEntryForSession[String]("test_key"))
        result.isDefined shouldBe true
        result.get shouldBe "test_value"
      }

      "there is no data for given key" in {
        when(
          sut.sessionCache.fetchAndGetEntry[String](anyString)(any[HeaderCarrier], any[Reads[String]])
        ).thenReturn(
          Future.successful(
            None
          )
        )

        val result: Option[String] = await(sut.fetchEntryForSession[String]("test_key"))
        result.isDefined shouldBe false
      }
    }

    "remove data related to given key" when {
      "there is no data in keystore" in {
        when(
          sut.sessionCache.fetch()(any[HeaderCarrier])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.removeFromSession("test_key"))
        result shouldBe true
      }

      "the key doesn't exists in data returned from keystore" in {
        when(
          sut.sessionCache.fetch()(any[HeaderCarrier])
        ).thenReturn(
          Future.successful(
            Some(CacheMap("id", Map()))
          )
        )
        val result = await(sut.removeFromSession("test_key"))
        result shouldBe true
      }

//      "the key exists in data returned from keystore" in {
//        when(
//          sut.sessionCache.fetch()(any[HeaderCarrier])
//        ).thenReturn(
//          Future.successful(
//            Some(CacheMap("id", Map("test_key1" -> JsString("test_value1"), "test_key2" -> JsString("test_value2"))))
//          )
//        )
//
//        when(
//          sut.sessionCache.remove()(any[HeaderCarrier])
//        ).thenReturn(
//          Future.successful(
//            HttpResponse(OK)
//          )
//        )
//
//        when(
//          sut.sessionCache.cache[String](anyString, anyString)(any[Writes[String]], any[HeaderCarrier])
//        ).thenReturn(
//          Future.successful(
//            CacheMap("id", Map("test_key2" -> JsString("test_value2")))
//          )
//        )
//
//        val result = await(sut.removeFromSession("test_key1"))
//        result shouldBe true
//      }
    }
  }

}
