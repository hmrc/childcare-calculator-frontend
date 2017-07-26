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

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.test.UnitSpec
import scala.concurrent.Future

class ChildAgedThreeOrFourControllerSpec extends UnitSpec with FakeCCApplication with BeforeAndAfterEach {

  val sut = new ChildAgedThreeOrFourController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
    when(
      sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
    ).thenReturn(
      Future.successful(None)
    )
  }

  s"calling ${childAgedThreeOrFourPath}" should {
    "be available" when {
      "GET request is made" in {
        val req = FakeRequest(GET, childAgedThreeOrFourPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }

      "POST request is made" in {
        val req = FakeRequest(POST, childAgedThreeOrFourPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }
    }
  }

  "onPageLoad" should {
    "load successfully ChildAgedThreeOrFour template" when {
      "there is no data in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "there is data in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "redirect to technical difficulties page" when {
      "can't connect to keystore" when {

        "loading data for childAged3or4" in {
          when(
            sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "can't construct backUrl" in {
          when(
            sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
          ).thenReturn(
            Future.successful(None)
          )
          when(
            sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

      }
    }
  }

  "onSubmit" should {
    "load template with status BAD_REQUEST" when {
      "invalid data is submitted and constructing back url is successful" in {
        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    s"redirect to error page (${technicalDifficultiesPath})" when {
      "invalid data is submitted and constructing back url fails" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "valid data is submitted and saving in keystore fails" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(childAgedThreeOrFourKey), anyBoolean())(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(childAgedThreeOrFourKey -> "true")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    s"redirect to next page (${expectChildcareCostsPath})" when {
      "valid data is submitted and saving in keystore is successful" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(childAgedThreeOrFourKey), anyBoolean())(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )
        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(childAgedThreeOrFourKey -> "true")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe expectChildcareCostsPath
      }
    }
  }

  "getBackUrl" should {
    s"return Location page url (${locationPath})" when {
      "there is no data for childAded2 in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.getBackUrl())
        result.url shouldBe locationPath
      }
    }

    s"return ChildAged2 page url (${childAgedTwoPath})" when {
      "there is data for childAded2 in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )

        val result = await(sut.getBackUrl())
        result.url shouldBe childAgedTwoPath
      }
    }

    s"throw exception" when {
      "can't connect to keystore" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException("test message"))
        )

        val result = intercept[Exception] (
          await(sut.getBackUrl())
        )
        result.getMessage shouldBe "test message"
      }
    }
  }
}