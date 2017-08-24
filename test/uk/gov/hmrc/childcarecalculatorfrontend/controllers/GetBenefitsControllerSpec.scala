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
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class GetBenefitsControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new GetBenefitsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(getBenefitsPath)

  "GetBenefitsController" when {

    "onPageLoad is called" should {

      "load template which of you get vouchers page when both are in paid employment" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                getVouchers = Some(YesNoUnsureEnum.YES),
                livingWithPartner = Some(true),
                paidOrSelfEmployed = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(hours = Some(24), escVouchers = Some(YesNoUnsureEnum.NOTSURE)),
                  partner = Some(Claimant(hours = Some(21), escVouchers = Some(YesNoUnsureEnum.NOTSURE)))
                )
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe underConstrctionPath
      }

      "load template do you get vouchers page when no or not sure selected on vouchers page" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                getVouchers = Some(YesNoUnsureEnum.NOTSURE),
                livingWithPartner = Some(true),
                paidOrSelfEmployed = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                household = Household(location = LocationEnum.ENGLAND)
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe vouchersPath
      }

      "load successfully template when data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                livingWithPartner = Some(false),
                getBenefits = Some(true)
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
                livingWithPartner = Some(false),
                getBenefits = None
              )
            )
          )
        )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if there is no data keystore for livingWithPartner object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                livingWithPartner = None,
                paidOrSelfEmployed = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
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
                .withFormUrlEncodedBody(getBenefitsKey -> "")
                .withSession(validSession)
            )
          )

          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "redirect to correct next page" when {

        "single user selects 'no'" should {
          // TODO: Redirect to which Benefits page when it's done
          s"go to which benefits page ${underConstrctionPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false), getBenefits = Some(false)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(getBenefitsKey -> "false")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }
        }

        "user with partner selects 'no'" should {
          // TODO: Redirect to which Benefits page when it's done
          s"go to which benefits page ${underConstrctionPath} and clear related data" in {
            val keystoreObject: PageObjects = PageObjects(
              household = Household(
                location = LocationEnum.ENGLAND,
                parent = Claimant(benefits = Some(Benefits(disabilityBenefits = false))),
                partner = Some(Claimant(benefits = None))
              ),
              livingWithPartner = Some(true)
            )

            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(keystoreObject))
            )

            val modifiedObject: PageObjects = PageObjects(
              household = Household(
                location = LocationEnum.ENGLAND,
                parent = Claimant(benefits = None),
                partner = Some(Claimant(benefits = None))
              ),
              livingWithPartner = Some(true),
              getBenefits = Some(false)
            )

            when(
              sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(modifiedObject)
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(getBenefitsKey -> "false")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }
        }

        "single user selects 'yes'" should {
          //TODO - redirect to what benefits with no partner
          s"go to what benefits do you get ${underConstrctionPath}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false))))
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(PageObjects(household = Household(location = LocationEnum.ENGLAND), livingWithPartner = Some(false), getBenefits = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(getBenefitsKey -> "true")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }
        }

        s"user with partner selects 'yes' - go to 'which of you get benefits page' page ${underConstrctionPath}" should {
          "shouldn't modify related data in keystore if value for paid employment is not changed" in {
            val keystoreObject: PageObjects = PageObjects(
              household = Household(
                location = LocationEnum.ENGLAND,
                parent = Claimant(hours = Some(15)),
                partner = Some(Claimant(hours = Some(37.5)))
              ),
              livingWithPartner = Some(true),
              getBenefits = Some(true)
            )

            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(keystoreObject))
            )

            when(
              sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(keystoreObject))(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(keystoreObject)
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(getBenefitsKey -> "true")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }

          "modify related data in keystore if selects new value for get benefits with disability and with partner" in {
            val keystoreObject: PageObjects = PageObjects(
              household = Household(
                location = LocationEnum.ENGLAND,
                parent = Claimant(hours = Some(15), benefits = Some(Benefits(highRateDisabilityBenefits = true))),
                partner = Some(Claimant(hours = Some(37.5)))
              ),
              livingWithPartner = Some(true),
              getBenefits = None
            )

            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(Some(keystoreObject))
            )

            val modifiedObject = PageObjects(
              household = Household(
                location = LocationEnum.ENGLAND,
                parent = Claimant(hours = Some(15), benefits = None),
                partner = Some(Claimant(hours = Some(37.5), benefits = None))
              ),
              livingWithPartner = Some(true),
              getBenefits = Some(true)
            )

            when(
              sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(modifiedObject)
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(getBenefitsKey -> "true")
                  .withSession(validSession)
              )
            )

            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
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
                .withFormUrlEncodedBody(getBenefitsKey -> "true")
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
                .withFormUrlEncodedBody(getBenefitsKey -> "true")
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
                .withFormUrlEncodedBody(getBenefitsKey -> "true")
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
