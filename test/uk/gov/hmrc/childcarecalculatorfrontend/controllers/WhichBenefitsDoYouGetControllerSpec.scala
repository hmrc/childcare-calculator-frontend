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
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsEnum.BenefitsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsEnum.BenefitsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

/**
 * Created by user on 24/08/17.
 */
class WhichBenefitsDoYouGetControllerSpec  extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhichBenefitsDoYouGetController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(parentBenefitsPath)
  validateUrl(partnerBenefitsPath)

  "Benefits Controller" when {

    "onPageLoad is called for parent" should {
      "load template successfully if there is some data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    partner = Some(Claimant())
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
                    partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(None)
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.failed(new RuntimeException)
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }


    "onPageLoad is called for partner" should {
      "load template successfully if there is some data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    partner = Some(Claimant())
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
                    partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(None)
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.failed(new RuntimeException)
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "onSubmit is called for parent" should {

      "go to technical difficulties page" when {

        "unable to connect to keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onSubmit(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "unable to find data from the keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )
          val result = await(sut.onSubmit(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }


        "unable to save data in keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
              Future.successful(
                Some(
                  PageObjects(
                    livingWithPartner = Some(true),
                    household = Household(
                      location = LocationEnum.ENGLAND,
                      parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
                      partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
                    )
                  )
                )
              )
            )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
              Future.failed(new RuntimeException)
            )

          val result = await(sut.onSubmit(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "there are errors" when {
//        "load the same template with status bad request" in {
//        }
      }
    }
  }
}
