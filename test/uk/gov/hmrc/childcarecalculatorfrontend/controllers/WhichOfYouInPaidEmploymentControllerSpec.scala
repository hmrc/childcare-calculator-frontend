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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class WhichOfYouInPaidEmploymentControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhichOfYouInPaidEmploymentController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(whoIsInPaidEmploymentPath)

  def buildPageObjects(youOrPartner: Option[String] = None): PageObjects = PageObjects(
    whichOfYouInPaidEmployment = youOrPartner,
    household = Household(
      location = LocationEnum.ENGLAND)
  )

  "WhichOfYouInPaidEmploymentController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore for current page object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(youOrPartner = None))
          )
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(youOrPartner = Some("you")))
          )
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if there is no pageObject in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
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
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(youOrPartner = None))
            )
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whichOfYouInPaidEmploymentKey -> "")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
        
        "redirect to error page if there is no pageObject in keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              None
            )
          )
          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whichOfYouInPaidEmploymentKey -> "YOU")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "saving in keystore is successful" should {
        s"go to ${whoIsInPaidEmploymentPath}" when {

          YouPartnerBothEnum.values.foreach { each =>
            val who = each.toString
            s"${who} is selected if there is no data in keystore for whichOfYouInPaidEmployment object" in {
              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects())
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(youOrPartner = Some(who)))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(whichOfYouInPaidEmploymentKey -> who)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe whatYouNeedPath
            }

            s"${who} is selected if there is data in keystore for PageObjects object" in {
              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(youOrPartner = Some(who)))
                )
              )

              when(
                sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
              ).thenReturn(
                Future.successful(
                  Some(buildPageObjects(youOrPartner = Some(who)))
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(whichOfYouInPaidEmploymentKey -> who)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe whatYouNeedPath
            }
          }
        }

      }

      "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(youOrPartner = Some("YOU")))
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whichOfYouInPaidEmploymentKey -> "YOU")
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
