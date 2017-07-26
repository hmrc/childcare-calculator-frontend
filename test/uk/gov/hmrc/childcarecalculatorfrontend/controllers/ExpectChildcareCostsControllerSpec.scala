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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.Future

class ExpectChildcareCostsControllerSpec extends UnitSpec with FakeCCApplication {

  val sut = new ExpectChildcareCostsController(applicationMessagesApi) {
    override val keystore = mock[KeystoreService]
  }

  s"${expectChildcareCostsPath} url" should {
    "be available" when {
      "GET request is made" in {
        val req = FakeRequest(GET, expectChildcareCostsPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }

      "POST request is made" in {
        val req = FakeRequest(POST, expectChildcareCostsPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }
    }
  }

  "ExpectChildcareCostsController" should {
    "load successfully the ExpectChildcareCosts page" when {
      "onPageLoad is called with some session data" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "onPageLoad is called with none session data" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.successful(None)
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "onPageLoad is called with an exception from keystore service" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "submit successfully from the ExpectChildcareCosts page" when {
      "onSubmit is called with valid form and successful cacheEntry" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )
        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe whatYouNeedPath
      }

      "onSubmit is called with valid form and an exception from cacheEntry" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "onSubmit is called with invalid form bind request" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }
  }
}
