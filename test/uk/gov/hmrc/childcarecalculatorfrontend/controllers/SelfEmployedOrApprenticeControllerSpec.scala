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
import play.api.i18n.Messages.Implicits.applicationMessagesApi
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class SelfEmployedOrApprenticeControllerSpec extends ControllersValidator with BeforeAndAfterEach {

 val selfEmployedOrApprenticeController = new SelfEmployedOrApprenticeController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  def buildPageObjects(isPartner: Boolean): PageObjects = {
    val minimumEarning = MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))
    val claimant = Claimant(minimumEarnings = Some(minimumEarning))
    if (isPartner) {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant, partner = Some(claimant)))
    } else {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant))
    }
  }

  "SelfEmployedOrApprenticeController" when {

    "onPageLoad is called" should {
      "load template successfully if there is no data in keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(false))
          )
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
      "redirect to error page if can't connect with keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST as parent" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false))
            )
          )

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
                 request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "")
                .withSession(validSession)
              )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }

        "load same template and return BAD_REQUEST as partner" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true))
            )
          )

          val result = selfEmployedOrApprenticeController.onSubmit(true)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "")
              .withSession(validSession)
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      /*"saving in keystore is successful" should {
        s"go to ${childAgedTwoPath}" when {
          val childAgeTwoLocations = List(
            LocationEnum.ENGLAND,
            LocationEnum.SCOTLAND,
            LocationEnum.WALES
          )
          childAgeTwoLocations.foreach { loc =>
            s"${loc.toString} is selected if there is no data in keystore for PageObjects object" in {
              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                Future.successful(
                  None
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = loc))
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

            s"${loc.toString} is selected if there is data in keystore for PageObjects object" in {
              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = LocationEnum.ENGLAND))
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = loc))
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
                sut.keystore.fetch[PageObjects]()(any(), any())
              ).thenReturn(
                Future.successful(
                  None
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = loc))
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

            s"${loc.toString} is selected if there is data in keystore for PageObjects object" in {
              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = LocationEnum.ENGLAND))
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(location = loc))
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
      }*/

     "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> EmploymentStatusEnum.SELFEMPLOYED.toString)
                .withSession(validSession)
          )

          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

    }
  }
}
