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

import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.Future

class ExpectChildcareCostsControllerSpec extends UnitSpec with FakeCCApplication {

  val sut = new ExpectChildcareCostsController(applicationMessagesApi) {
    override val keystore = mock[KeystoreService]
  }

  s"${expectChildcareCostsPath} url" should {
    "be available" when {
      "GET request is made" in {
        val req = FakeRequest(GET, expectChildcareCostsPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }

      "POST request is made" in {
        val req = FakeRequest(POST, expectChildcareCostsPath).withSession(validSession)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) should not be NOT_FOUND
      }
    }
  }

  "calling onPageLoad" should {
    "load successfully the ExpectChildcareCosts page" when {
      "there is data in session" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.successful(Some(true))
        )

        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
        ).thenReturn(
          Future.successful(Some("england"))
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "there is no data in session" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
        ).thenReturn(
          Future.successful(Some("england"))
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    s"redirect to technical difficulties page (${technicalDifficultiesPath})" when {
      "an exception is thrown from keystore service" in {
        when(
          sut.keystore.fetchEntryForSession[Boolean](refEq(expectChildcareCostsKey))(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
        ).thenReturn(
          Future.successful(Some("england"))
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }
  }

  "calling onSubmit" should {
    "load ExpectChildcareCosts and display errors" when {
      "invalid data is submitted" in {
        when(
          sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        when(
          sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
        ).thenReturn(
          Future.successful(Some("england"))
        )

        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "redirect correctly to next page" when {
      "valid data is submitted and saved successfully in keystore" should {

        s"go to free hours results page (${freeHoursResultsPath})" when {
          "user doesn't expect to have childcare cost" when {
            "has a child aged 2, 3 or 4" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "false")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe freeHoursResultsPath
            }
            "doesn't have a child aged 2, 3 or 4" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "false")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe freeHoursResultsPath
            }
          }
          "user expects to have childcare cost" when {
            "has a child aged 2" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe freeHoursResultsPath
            }
            "has a child aged 3 or 4" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe freeHoursResultsPath
            }
          }
        }

        s"go to do you live with partner page (${livingWithPartnerPath})" when {
          "user expects to have childcare cost" when {
            "has no children aged 2, 3 or 4" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(false))
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe livingWithPartnerPath
            }
            "there is no data for children aged 2, 3 or 4 in keystore" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(None)
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.successful(None)
              )

              when(
                sut.keystore.fetchEntryForSession[String](refEq(locationKey))(any(),any())
              ).thenReturn(
                Future.successful(Some("england"))
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe livingWithPartnerPath
            }
          }
        }

        s"go to technical difficulties page (${technicalDifficultiesPath})" when {
          "an exception is thrown while saving data" in {
            when(
              sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
            ).thenReturn(
              Future.failed(new RuntimeException)
            )
            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          "an exception is thrown while looking for data for 2 years old" in {
            when(
              sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
            ).thenReturn(
              Future.successful(Some(true))
            )

            when(
              sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
            ).thenReturn(
              Future.failed(new RuntimeException)
            )

            when(
              sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
            ).thenReturn(
              Future.successful(None)
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          "an exception is thrown while looking for data for 3 or 4 years old" in {
              when(
                sut.keystore.cacheEntryForSession[Boolean](refEq(expectChildcareCostsKey), any())(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedTwoKey))(any(),any())
              ).thenReturn(
                Future.successful(Some(true))
              )

              when(
                sut.keystore.fetchEntryForSession[Boolean](refEq(childAgedThreeOrFourKey))(any(),any())
              ).thenReturn(
                Future.failed(new RuntimeException)
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> "true")
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
