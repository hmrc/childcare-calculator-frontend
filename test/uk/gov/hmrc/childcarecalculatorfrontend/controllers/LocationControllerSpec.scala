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

import org.scalatest.BeforeAndAfterEach
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.i18n.Messages.Implicits.applicationMessagesApi
import play.api.libs.json.{Format, Reads}
import play.api.test.FakeRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import scala.concurrent.Future

class LocationControllerSpec extends UnitSpec with FakeCCApplication with BeforeAndAfterEach {

  val sut = new LocationController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  s"${locationPath} url" should {

    "be available" when {

      "GET request is made" in {
        val req = FakeRequest(GET, locationPath).withSession(validSession)
        val result = route(app, req)

        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }

      "POST request is made" in {
        val req = FakeRequest(POST, locationPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }
    }
  }

  "LocationController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any[HeaderCarrier], any[Reads[String]])
        ).thenReturn(
          Future.successful(None)
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any[HeaderCarrier], any[Reads[String]])
        ).thenReturn(
          Future.successful(Some(LocationEnum.ENGLAND.toString))
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any[HeaderCarrier], any[Reads[String]])
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
          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(locationKey -> "")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "saving in keystore is successful" should {
        s"go to ${childAgedTwoPath}" when {
          val childAgeTwoLocations = List(
            LocationEnum.ENGLAND.toString,
            LocationEnum.SCOTLAND.toString,
            LocationEnum.WALES.toString
          )
          childAgeTwoLocations.foreach { loc =>
            s"${loc} is selected" in {
              when(
                sut.keystore.cacheEntryForSession[String](refEq(locationKey), anyString)(any[HeaderCarrier], any[Format[String]])
              ).thenReturn(
                Future.successful(Some(loc))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe childAgedTwoPath
            }
          }
        }

        s"go to '3 or 4 years old page' ${childAgedThreeOrFourPath}" when {
          val childAgeTwoLocations = List(
            LocationEnum.NORTHERNIRELAND.toString
          )
          childAgeTwoLocations.foreach { loc =>
            s"${loc} is selected" in {
              when(
                sut.keystore.cacheEntryForSession[String](refEq(locationKey), anyString)(any[HeaderCarrier], any[Format[String]])
              ).thenReturn(
                Future.successful(Some(loc))
              )

              when(
                sut.keystore.removeFromSession(anyString)(any[HeaderCarrier])
              ).thenReturn(
                Future.successful(true)
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe childAgedThreeOrFourPath
            }
          }
        }
      }

      "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            sut.keystore.cacheEntryForSession[String](refEq(locationKey), anyString)(any[HeaderCarrier], any[Format[String]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(locationKey -> LocationEnum.ENGLAND.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      s"redirect to ${technicalDifficultiesPath}" when {
        val childAgeTwoLocations = List(
          LocationEnum.NORTHERNIRELAND.toString
        )
        childAgeTwoLocations.foreach { loc =>
          s"${loc} is selected but deleting old data from session fails" in {
            when(
              sut.keystore.cacheEntryForSession[String](refEq(locationKey), anyString)(any[HeaderCarrier], any[Format[String]])
            ).thenReturn(
              Future.successful(Some(loc))
            )

            when(
              sut.keystore.removeFromSession(anyString)(any[HeaderCarrier])
            ).thenReturn(
              Future.successful(false)
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(locationKey -> loc)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          s"${loc} is selected but deleting old data from session throws exception" in {
            when(
              sut.keystore.cacheEntryForSession[String](refEq(locationKey), anyString)(any[HeaderCarrier], any[Format[String]])
            ).thenReturn(
              Future.successful(Some(loc))
            )

            when(
              sut.keystore.removeFromSession(anyString)(any[HeaderCarrier])
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(locationKey -> loc)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }
        }
      }

    }
  }
}
