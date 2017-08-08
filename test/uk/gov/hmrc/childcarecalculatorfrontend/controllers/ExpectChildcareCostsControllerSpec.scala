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
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.concurrent.Future
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

class ExpectChildcareCostsControllerSpec extends ControllersValidator {

  val sut = new ExpectChildcareCostsController(applicationMessagesApi) {
    override val keystore = mock[KeystoreService]
  }

  validateUrl(expectChildcareCostsPath)

  def buildHousehold(location: LocationEnum =  LocationEnum.ENGLAND,
                     childAgedTwo: Option[Boolean] = None,
                     childAgedThreeOrFour: Option[Boolean] = None,
                     expectChildcareCosts: Option[Boolean] =  None): Household = Household(
    location = location,
    childAgedTwo = childAgedTwo,
    childAgedThreeOrFour = childAgedThreeOrFour,
    expectChildcareCosts =  expectChildcareCosts
  )

  "calling onPageLoad" should {
    "load successfully the ExpectChildcareCosts page" when {
      "there is data in session" in {
        when(
          sut.keystore.fetch[Household]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildHousehold(
                location = LocationEnum.ENGLAND,
                expectChildcareCosts = Some(true)
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "there is no data in session" in {
        when(
          sut.keystore.fetch[Household]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildHousehold(
                location = LocationEnum.ENGLAND,
                expectChildcareCosts = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    s"redirect to technical difficulties page (${technicalDifficultiesPath})" when {
      "there is no data in keystore for Household object" in {
        when(
          sut.keystore.fetch[Household]()(any(),any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "an exception is thrown from keystore service" in {
        when(
          sut.keystore.fetch[Household]()(any(),any())
        ).thenReturn(
          Future.failed(new RuntimeException)
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
          sut.keystore.fetch[Household]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildHousehold(
                location = LocationEnum.ENGLAND,
                expectChildcareCosts = Some(true)
              )
            )
          )
        )

        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "redirect correctly to next page" when {
      "valid data is submitted and saved successfully in keystore" should {

        val resultsTable = Table(
          ("Location", "Has child aged 2", "Has child aged 3 or 4", "Has costs", "Next page"),
          (LocationEnum.ENGLAND, false, false, false, freeHoursResultsPath),
          (LocationEnum.SCOTLAND, false, false, false, freeHoursResultsPath),
          (LocationEnum.WALES, false, false, false, freeHoursResultsPath),
          (LocationEnum.NORTHERNIRELAND, false, false, false, freeHoursResultsPath),

          (LocationEnum.ENGLAND, false, false, true, livingWithPartnerPath),
          (LocationEnum.SCOTLAND, false, false, true, livingWithPartnerPath),
          (LocationEnum.WALES, false, false, true, livingWithPartnerPath),
          (LocationEnum.NORTHERNIRELAND, false, false, true, livingWithPartnerPath),

          (LocationEnum.ENGLAND, false, true, false, freeHoursInfoPath),
          (LocationEnum.SCOTLAND, false, true, false, freeHoursResultsPath),
          (LocationEnum.WALES, false, true, false, freeHoursResultsPath),
          (LocationEnum.NORTHERNIRELAND, false, true, false, freeHoursResultsPath),

          (LocationEnum.ENGLAND, false, true, true, freeHoursInfoPath),
          (LocationEnum.SCOTLAND, false, true, true, freeHoursInfoPath),
          (LocationEnum.WALES, false, true, true, freeHoursInfoPath),
          (LocationEnum.NORTHERNIRELAND, false, true, true, freeHoursInfoPath),

          (LocationEnum.ENGLAND, true, false, false, livingWithPartnerPath),
          (LocationEnum.SCOTLAND, true, false, false, livingWithPartnerPath),
          (LocationEnum.WALES, true, false, false, livingWithPartnerPath),
          (LocationEnum.NORTHERNIRELAND, true, false, false, livingWithPartnerPath),

          (LocationEnum.ENGLAND, true, false, true, livingWithPartnerPath),
          (LocationEnum.SCOTLAND, true, false, true, livingWithPartnerPath),
          (LocationEnum.WALES, true, false, true, livingWithPartnerPath),
          (LocationEnum.NORTHERNIRELAND, true, false, true, livingWithPartnerPath),

          (LocationEnum.ENGLAND, true, true, false, freeHoursInfoPath),
          (LocationEnum.SCOTLAND, true, true, false, freeHoursInfoPath),
          (LocationEnum.WALES, true, true, false, freeHoursInfoPath),
          (LocationEnum.NORTHERNIRELAND, true, true, false, freeHoursInfoPath),

          (LocationEnum.ENGLAND, true, true, true, freeHoursInfoPath),
          (LocationEnum.SCOTLAND, true, true, true, freeHoursInfoPath),
          (LocationEnum.WALES, true, true, true, freeHoursInfoPath),
          (LocationEnum.NORTHERNIRELAND, true, true, true, freeHoursInfoPath)
        )

        forAll(resultsTable) { case (location, hasChildAged2, hasChildAges3Or4, hasCost, resultPage) =>
          s"go to page (${resultPage})" when {

            s"user lives in ${location.toString}, has child aged 2 = ${hasChildAged2}, has child aged 3 or 4 = ${hasChildAges3Or4} and has cost = ${hasCost}" in {

              when(
                sut.keystore.fetch[Household]()(any(),any())
              ).thenReturn(
                Future.successful(
                  Some(
                    buildHousehold(
                      location = location,
                      childAgedTwo = Some(hasChildAged2),
                      childAgedThreeOrFour = Some(hasChildAges3Or4),
                      expectChildcareCosts = None
                    )
                  )
                )
              )

              when(
                sut.keystore.cache[Household](any[Household]())(any(), any())
              ).thenReturn(
                Future.successful(
                  Some(
                    buildHousehold(
                      location = location,
                      childAgedTwo = Some(hasChildAged2),
                      childAgedThreeOrFour = Some(hasChildAges3Or4),
                      expectChildcareCosts = Some(hasCost)
                    )
                  )
                )
              )

              val result = await(
                sut.onSubmit(
                  request
                    .withFormUrlEncodedBody(expectChildcareCostsKey -> hasCost.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe resultPage
            }


          }
        }

        s"go to technical difficulties page (${technicalDifficultiesPath})" when {
          "an exception is thrown while saving data" in {

            when(
              sut.keystore.fetch[Household]()(any(),any())
            ).thenReturn(
              Future.successful(
                Some(
                  buildHousehold(
                    location = LocationEnum.ENGLAND,
                    childAgedTwo = Some(true),
                    childAgedThreeOrFour = Some(true),
                    expectChildcareCosts = None
                  )
                )
              )
            )

            when(
              sut.keystore.cache[Household](any[Household]())(any(), any())
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

          "an exception is thrown while looking for data" in {
            when(
              sut.keystore.fetch[Household]()(any(),any())
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

          "there is no data in keystore for Household object" in {
            when(
              sut.keystore.fetch[Household]()(any(),any())
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
        }
      }
    }


  }
}
