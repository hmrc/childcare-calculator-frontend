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
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.test.Helpers._
import scala.concurrent.Future

class LocationControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new LocationController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(locationPath)

  def buildHousehold(location: LocationEnum = LocationEnum.ENGLAND): Household = Household(
    location = location
  )

  "LocationController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
        ).thenReturn(
          Future.successful(
            None
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
            Some(buildHousehold(location = LocationEnum.ENGLAND))
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
            LocationEnum.ENGLAND,
            LocationEnum.SCOTLAND,
            LocationEnum.WALES
          )
          childAgeTwoLocations.foreach { loc =>
            s"${loc.toString} is selected if there is no data in keystore for Household object" in {
              when(
                sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
              ).thenReturn(
                Future.successful(
                  None
                )
              )

              when(
                sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = loc))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe childAgedTwoPath
            }

            s"${loc.toString} is selected if there is data in keystore for Household object" in {
              when(
                sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = LocationEnum.ENGLAND))
                )
              )

              when(
                sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = loc))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc.toString)
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
            LocationEnum.NORTHERNIRELAND
          )
          childAgeTwoLocations.foreach { loc =>
            s"${loc.toString} is selected if there is no data in keystore for Househild object" in {
              when(
                sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
              ).thenReturn(
                Future.successful(
                  None
                )
              )

              when(
                sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = loc))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe childAgedThreeOrFourPath
            }

            s"${loc.toString} is selected if there is data in keystore for Household object" in {
              when(
                sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = LocationEnum.ENGLAND))
                )
              )

              when(
                sut.keystore.cache[Household](any[Household])(any[HeaderCarrier], any[Format[Household]])
              ).thenReturn(
                Future.successful(
                  Some(buildHousehold(location = loc))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(locationKey -> loc.toString)
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
            sut.keystore.fetch[Household]()(any[HeaderCarrier], any[Reads[Household]])
          ).thenReturn(
            Future.successful(
              Some(buildHousehold(location = LocationEnum.ENGLAND))
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
                .withFormUrlEncodedBody(locationKey -> LocationEnum.ENGLAND.toString)
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
