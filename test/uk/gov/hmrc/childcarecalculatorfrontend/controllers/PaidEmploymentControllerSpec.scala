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

import java.lang

import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Format
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class PaidEmploymentControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new PaidEmploymentController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(paidEmploymentPath)

  "PaidEmploymentController" when {

    "onPageLoad is called" should {

      "load successfully template when data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                livingWithPartner = Some(false)
              )
            )
          )
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load successfully template when no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                livingWithPartner = None
              )
            )
          )
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if there is no data keystore for pageObjects object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "onSubmit is called" should {

      "load same page with status BAD_REQUEST" when {
        "invalid data is submitted" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true))))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(paidEmploymentKey -> "")
                .withSession(validSession)
            )
          )

          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "redirect to correct next page" when {

        "single user selects 'no'" should {
          s"go to results page ${hoursPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false), paidOrSelfEmployed = Some(false)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(paidEmploymentKey -> "false")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe hoursPath
          }
        }

        "user with partner selects 'no'" should {
          s"go to results page ${hoursPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true), paidOrSelfEmployed = Some(false)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(paidEmploymentKey -> "false")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe hoursPath
          }
        }

        "single user selects 'yes'" should {
          s"go to hours page ${hoursPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false), paidOrSelfEmployed = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(paidEmploymentKey -> "true")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe hoursPath
          }
        }

        "user with partner selects 'yes'" should {
          s"go to 'Which of you is in paid employment' page ${whoIsInPaidEmploymentPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true), paidOrSelfEmployed = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(paidEmploymentKey -> "true")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe whoIsInPaidEmploymentPath
          }
        }
      }

      s"redirect technichal difficulties page (${technicalDifficultiesPath})" when {


        "can't connect to keystore while fetching data" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(paidEmploymentKey -> "true")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data in keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.successful(None)
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(paidEmploymentKey -> "true")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "can't save data in keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(true))))
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(paidEmploymentKey -> "true")
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
