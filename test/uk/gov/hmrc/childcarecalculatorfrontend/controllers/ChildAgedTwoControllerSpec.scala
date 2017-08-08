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
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{LocationEnum, Household}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.Future

class ChildAgedTwoControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new ChildAgedTwoController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(childAgedTwoPath)

  def buildHousehold(childAgedTwo: Option[Boolean] = None): Household = Household(
    location = LocationEnum.ENGLAND,
    childAgedTwo = childAgedTwo
  )
  "ChildAgedTwoController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.successful(
            Some(buildHousehold(childAgedTwo = None))
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.successful(
            Some(buildHousehold(childAgedTwo = Some(true)))
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST" in {

          when(
            sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
          ).thenReturn(
            Future.successful(
              Some(buildHousehold(childAgedTwo = None))
            )
          )
          
          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(childAgedTwoKey -> "")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "saving in keystore is successful" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.successful(
            Some(buildHousehold(childAgedTwo = None))
          )
        )

        when(
          sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
        ).thenReturn(
          Future.successful(
            Some(buildHousehold(childAgedTwo = Some(true)))
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(childAgedTwoKey -> "true")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe childAgedThreeOrFourPath
      }
    }

    "connecting with keystore fails" should {
      s"redirect to ${technicalDifficultiesPath}" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.successful(
            Some(buildHousehold(childAgedTwo = None))
          )
        )

        when(
          sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(childAgedTwoKey -> "false")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }
  }
}
