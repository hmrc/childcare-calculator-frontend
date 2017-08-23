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

import org.jsoup.Jsoup
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class HoursControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new HoursController(applicationMessagesApi){
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  def buildPageObject(
                       livingWithPartner: Option[Boolean] = None,
                       whichOfYouInPaidEmployment: Option[YouPartnerBothEnum] = None,
                       partner: Option[Claimant] = None
                       ): PageObjects = PageObjects(
    household = Household(
      location = LocationEnum.ENGLAND,
      partner = partner
    ),
    livingWithPartner = livingWithPartner,
    whichOfYouInPaidEmployment = whichOfYouInPaidEmployment
  )


  validateUrl(hoursParentPath)
  validateUrl(hoursPartnerPath)

  "HoursController for parent" when {

    "onPageLoad is called" should {
      "load template successfully with correct backUrl" when {

        s"there is a single user - (${paidEmploymentPath})" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(false)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe paidEmploymentPath
        }

        s"there is a couple and only parent is in paid employment - (${whoIsInPaidEmploymentPath})" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe whoIsInPaidEmploymentPath
        }

        s"there is a couple and both are in paid employment - (${hoursPartnerPath})" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe hoursPartnerPath
        }
      }

      s"redirect to error page (${technicalDifficultiesPath})" when {
        "can't connect to keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for PageObjects" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for livingWithPartner" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for whichOfYouInPaidEmployment" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }
    }

    "onSubmit is called" should {
      "load same template with status BAD_REQUEST" when {
        "invalid data is submitted" in {

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(false)
                )
              )
            )
          )

          val result = await(
            sut.onSubmit(false)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.55")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      s"redirect successfully to next page ($vouchersPath)" when {
        "valid data is submitted" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(false)
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(false)
                )
              )
            )
          )

          val result = await(
            sut.onSubmit(false)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.5")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe vouchersPath
        }
      }

      s"redirect to error page (${technicalDifficultiesPath})" when {
        "data in keystore is missing" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )

          val result = await(
            sut.onSubmit(false)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.5")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "can't connect to keystore" when {

          "loading data" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(hoursKey -> "37.5")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          "saving data" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(
                  buildPageObject(
                    livingWithPartner = Some(false)
                  )
                )
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(hoursKey -> "37.5")
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

  "HoursController for partner" when {

    "onPageLoad is called" should {
      "load template successfully with correct backUrl" when {

        s"only partner is in paid employment - (${whoIsInPaidEmploymentPath})" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe whoIsInPaidEmploymentPath
        }

        s"both are in paid employment - (${whoIsInPaidEmploymentPath})" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe whoIsInPaidEmploymentPath
        }

      }

      s"redirect to error page (${technicalDifficultiesPath})" when {
        "can't connect to keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for PageObjects" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for livingWithPartner" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for whichOfYouInPaidEmployment" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "there is no data for partner" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }
    }

    "onSubmit is called" should {
      "load same template with status BAD_REQUEST" when {
        "invalid data is submitted" in {

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(
            sut.onSubmit(true)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.55")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      // TODO: redirect to correct vouchers page
      "redirect successfully to next page " when {
        s"valid data is submitted and both are in paid employment ($hoursParentPath)" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  partner = Some(Claimant())
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(
            sut.onSubmit(true)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.5")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe hoursParentPath
        }

        s"valid data is submitted and only partner is in paid employment ($vouchersPath)" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                  partner = Some(Claimant())
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(
            sut.onSubmit(true)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.5")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe vouchersPath
        }
      }

      s"redirect to error page (${technicalDifficultiesPath})" when {
        "data in keystore is missing" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )

          val result = await(
            sut.onSubmit(true)(
              request
                .withFormUrlEncodedBody(hoursKey -> "37.5")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "can't connect to keystore" when {

          "loading data" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            val result = await(
              sut.onSubmit(true)(
                request
                  .withFormUrlEncodedBody(hoursKey -> "37.5")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          "saving data" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(
                  buildPageObject(
                    livingWithPartner = Some(true),
                    whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                    partner = Some(Claimant())
                  )
                )
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            val result = await(
              sut.onSubmit(true)(
                request
                  .withFormUrlEncodedBody(hoursKey -> "37.5")
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
