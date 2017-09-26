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
import uk.gov.hmrc.childcarecalculatorfrontend.{ControllersValidator, ObjectBuilder}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howManyChildren
import uk.gov.hmrc.play.http.HeaderCarrier

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

        setupMocks(modelToFetch = None)

       // val model = buildPageObjects

        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))


      }

      "load template successfully if there is data in keystore" in {

        setupMocks(modelToFetch = Some(buildPageObjects))

        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        setUpMocksForException()

        val result = howManyChildrenController.onPageLoad()(request.withSession(validSession))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "onSubmit is called" when {

        "there are errors" should {
          "load same template and return BAD_REQUEST" when {
            "invalid data is submitted" in {


              setupMocks(modelToFetch = Some(buildPageObjects))

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
      }

      s"redirect successfully to next page ($underConstructionPath)" when {
        "valid data is submitted" in {

          setupMocks(modelToFetch = Some(buildPageObjects),
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


      "there is no data for howManyChildren" ignore {

        setupMocks(modelToFetch = Some(buildPageObjects),
          modelToStore = Some(buildPageObjects.copy(howManyChildren = None)),
          storePageObjects = true)


          val result = await(
            howManyChildrenController.onSubmit()(
              request
                .withFormUrlEncodedBody(howManyChildrenKey -> " ")
                .withSession(validSession)
           )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

     }


    }
  }



  private def setupMocks (modelToFetch: Option[PageObjects] = None,
                          modelToStore: Option[PageObjects] = None,
                          fetchPageObjects: Boolean = true,
                          storePageObjects: Boolean = false) = {
    if (fetchPageObjects) {
      when(
         howManyChildrenController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])

      ).thenReturn(

        Future.successful(modelToFetch)
      )
    }

    if (storePageObjects){
      when(
        howManyChildrenController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
      ).thenReturn(
        Future.successful(modelToStore)
      )
    }
  }

  private def setUpMocksForException() = {
    when(
      howManyChildrenController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
    ).thenReturn(
      Future.failed(new RuntimeException)
    )
  }
}


