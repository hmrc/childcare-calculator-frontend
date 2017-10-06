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
import play.api.i18n.Messages.Implicits.applicationMessagesApi
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howManyChildren
import uk.gov.hmrc.childcarecalculatorfrontend.{ControllersValidator, ObjectBuilder}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.childcarecalculatorfrontend.MockBuilder._
import scala.concurrent.Future

class HowManyChildrenControllerSpec extends ControllersValidator with BeforeAndAfterEach with ObjectBuilder{


  val howManyChildrenController = new HowManyChildrenController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(howManyChildrenController.keystore)
  }

  "HowManyChildrenController" when {

    "onPageLoad is called" should {
      "show technical difficulties page if there is no data in keystore" in {

        setupMocks(howManyChildrenController.keystore)
        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))

      }

      "load template successfully if there is data in keystore" in {

        setupMocks(howManyChildrenController.keystore, modelToFetch = Some(buildPageObjects))

        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        setupMocksForException(howManyChildrenController.keystore)

        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "onSubmit is called" when {

        "there are errors" should {
          "load same template and return BAD_REQUEST" when {
            "invalid data is submitted" in {


              setupMocks(howManyChildrenController.keystore, modelToFetch = Some(buildPageObjects))

              val result = await(
                howManyChildrenController.onSubmit()(
                  request
                    .withFormUrlEncodedBody(howManyChildrenKey -> "22")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe BAD_REQUEST
              result.body.contentType.get shouldBe "text/html; charset=utf-8"
            }
          }
        }
        "connecting with keystore fails" should {
          s"redirect to ${technicalDifficultiesPath}" in {

            setupMocks(howManyChildrenController.keystore)

            setupMocksForException(howManyChildrenController.keystore, cacheException = true)

            val result = await(
              howManyChildrenController.onSubmit(
                request
                  .withFormUrlEncodedBody(howManyChildrenKey -> "false")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }
        }

        "there is no data in keystore for PageObjects object" should {
          s"redirect to ${technicalDifficultiesPath}" in {

            setupMocksForException(howManyChildrenController.keystore)

            val result = await(
              howManyChildrenController.onSubmit(
                request
                  .withFormUrlEncodedBody(howManyChildrenKey -> "false")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }
        }
      }

      s"redirect successfully" when {
        "to next page when valid data is submitted" in {

          setupMocks(howManyChildrenController.keystore,
            modelToFetch = Some(buildPageObjects),
            modelToStore = Some(buildPageObjects.copy(howManyChildren = Some(4))),
            storePageObjects = true
          )


          val result = await(
            howManyChildrenController.onSubmit()(
              request
                .withFormUrlEncodedBody(howManyChildrenKey -> "4")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(routes.ChildCareBaseController.underConstruction().url))
        }


      s"to previous page ${maxFreeHoursInfoPath}" in {

        val parent = buildPageObjects.household.parent.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true))), maximumEarnings = Some(false))
        val model = Some(buildPageObjects.copy(
          household = buildPageObjects.household.copy(childAgedThreeOrFour = Some(true), parent = parent), whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU)
        ))

        setupMocks(howManyChildrenController.keystore,
          modelToFetch = model)

        val result = await(howManyChildrenController.onPageLoad()(request.withSession(validSession)))
        status(result) shouldBe OK

        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe maxFreeHoursInfoPath
      }

        s"to previous page $creditsPath}" in {

        setupMocks(howManyChildrenController.keystore,
          modelToFetch = Some(buildPageObjects))

        val result = await(howManyChildrenController.onPageLoad()(request.withSession(validSession)))
        status(result) shouldBe OK

        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe creditsPath
      }

     }
    }
  }
}


