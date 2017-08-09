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
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{childAgedThreeOrFour, childAgedTwo, location}

import scala.concurrent.Future

class ChildAgedThreeOrFourControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new ChildAgedThreeOrFourController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(childAgedThreeOrFourPath)

  def buildPageObjects(childAgedTwo: Option[Boolean] = None,
                       childAgedThreeOrFour: Option[Boolean] = None): PageObjects = PageObjects(household = Household(
    location = LocationEnum.ENGLAND),
    childAgedTwo = childAgedTwo,
    childAgedThreeOrFour = childAgedThreeOrFour
  )

  "onPageLoad" should {
    "load successfully ChildAgedThreeOrFour template" when {

      "there is no data in keystore about child aged 3 or 4" should {
        s"contain back url to ${locationPath} if there is no data about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = None,
                  childAgedThreeOrFour = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe locationPath
        }

        s"contain back url to ${childAgedTwoPath} if there is data about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = Some(true),
                  childAgedThreeOrFour = None
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe childAgedTwoPath
        }
      }

      "there is data in keystore about child aged 3 or 4" should {
        s"contain back url to ${locationPath} if there is no data about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = None,
                  childAgedThreeOrFour = Some(true)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe locationPath
        }

        s"contain back url to ${childAgedTwoPath} if there is data about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = Some(true),
                  childAgedThreeOrFour = Some(true)
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe childAgedTwoPath
        }
      }
    }

    s"redirect to technical difficulties page (${technicalDifficultiesPath})" when {
      "there is no data for household in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }
  }

  "onSubmit" should {
    "load template with status BAD_REQUEST" when {
      "invalid data is submitted" should {
        s"cantain back url to ${locationPath} if there is no data in keystore about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = None,
                  childAgedThreeOrFour = None
                )
              )
            )
          )

          val result = await(sut.onSubmit(request.withSession(validSession)))
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe locationPath
        }

        s"contain back url to ${childAgedTwoPath} if there is data in keystore about child aged 2" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(),any())
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObjects(
                  childAgedTwo = Some(true),
                  childAgedThreeOrFour = None
                )
              )
            )
          )

          val result = await(sut.onSubmit(request.withSession(validSession)))
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe childAgedTwoPath
        }
      }
    }

    s"redirect to error page (${technicalDifficultiesPath})" when {
      "there is no data in keystore for Household object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore loading Household object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "valid data is submitted and saving in keystore fails" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObjects(
                childAgedTwo = None,
                childAgedThreeOrFour = None
              )
            )
          )
        )

        when(
          sut.keystore.cache(any[PageObjects]())(any(),any())
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
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObjects(
                childAgedTwo = None,
                childAgedThreeOrFour = None
              )
            )
          )
        )

        when(
          sut.keystore.cache(any[PageObjects]())(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObjects(
                childAgedTwo = None,
                childAgedThreeOrFour = Some(true)
              )
            )
          )
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

}