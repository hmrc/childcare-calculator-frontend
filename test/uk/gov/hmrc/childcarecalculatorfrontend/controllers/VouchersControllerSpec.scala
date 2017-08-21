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
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.test.Helpers._
import scala.concurrent.Future
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

class VouchersControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new VouchersController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  def buildPageObject(
                       getVouchers: Option[YesNoUnsureEnum] = None,
                       paidOrSelfEmployed: Option[Boolean] = None,
                       whichOfYouInPaidEmployment: Option[YouPartnerBothEnum] = None,
                       partner: Option[Claimant] = None
                       ): PageObjects = PageObjects(
    household = Household(
      location = LocationEnum.ENGLAND,
      partner = partner
    ),
    paidOrSelfEmployed = paidOrSelfEmployed,
    whichOfYouInPaidEmployment = whichOfYouInPaidEmployment,
    getVouchers = getVouchers
  )

  validateUrl(vouchersPath)

  "calling onPageLoad" should {

    val testCases = Table(
      ("Family status", "Who is in paid employment?", "Back url"),
      ("single user", None, hoursParentPath),
      ("couple", Some(YouPartnerBothEnum.YOU), hoursParentPath),
      ("couple", Some(YouPartnerBothEnum.PARTNER), hoursPartnerPath),
      ("couple", Some(YouPartnerBothEnum.BOTH), hoursParentPath)
    )

    forAll(testCases) { case (familyStatus, whoIsInPaidEmployment, backUrl) =>
      s"load template successfully with back url = ${backUrl} for ${familyStatus} if in employment is ${whoIsInPaidEmployment}" when {
        "there is no data in keystore about vouchers" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  getVouchers = None,
                  paidOrSelfEmployed = Some(true),
                  whichOfYouInPaidEmployment = whoIsInPaidEmployment,
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe backUrl
        }

        "there is some data in keystore about vouchers" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                buildPageObject(
                  getVouchers = Some(YesNoUnsureEnum.YES),
                  paidOrSelfEmployed = Some(true),
                  partner = Some(Claimant())
                )
              )
            )
          )

          val result = await(sut.onPageLoad(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }
    }

    s"reditect to error page (${technicalDifficultiesPath})" when {
      "there is no information about partner if whichOfYouInPaidEmployment is 'PARTNER' in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObject(
                getVouchers = None,
                paidOrSelfEmployed = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                partner = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "there is no information about partner if whichOfYouInPaidEmployment is 'BOTH' in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObject(
                getVouchers = None,
                paidOrSelfEmployed = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                partner = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "there is no information about paidOrSelfEmployed in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObject(
                getVouchers = None,
                paidOrSelfEmployed = None,
                whichOfYouInPaidEmployment = None,
                partner = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "there is noone is in paid employment" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObject(
                getVouchers = None,
                paidOrSelfEmployed = Some(false),
                whichOfYouInPaidEmployment = None,
                partner = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "there is no PageObject in keystore" in {
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

      "can't connect to keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
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

    "load template and return BAD_REQUEST" when {

      "invalid data is submitted" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObject(paidOrSelfEmployed = Some(true), whichOfYouInPaidEmployment = None))
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(vouchersKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

    }

    "redirect to correct next page" when {
      // TODO: Update all urls to go to 'Benefits' page
      val nextPageTest = Table(
        ("User in paid employment", "Partner", "Selected vouchers", "Next page"),
        (None, None, YesNoUnsureEnum.YES, underConstrctionPath),
        (None, None, YesNoUnsureEnum.NO, underConstrctionPath),
        (None, None, YesNoUnsureEnum.NOTSURE, underConstrctionPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.YES, underConstrctionPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.NO, underConstrctionPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.NOTSURE, underConstrctionPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.YES, underConstrctionPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.NO, underConstrctionPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.NOTSURE, underConstrctionPath),
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.YES, underConstrctionPath), // TODO: Only this ine should go to 'Which of you is offered vouchers'
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.NO, underConstrctionPath),
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.NOTSURE, underConstrctionPath)
      )

      forAll(nextPageTest) { case (inPaidEmployment, partner, selectedVouchers, nextPage) =>
        s"in paid employment is ${inPaidEmployment} and user select ${selectedVouchers}" should {
          s"go to ${nextPage}" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObject(
                  paidOrSelfEmployed = Some(true),
                  whichOfYouInPaidEmployment = inPaidEmployment,
                  partner = partner
                ))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(mock[PageObjects])
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(vouchersKey -> selectedVouchers.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe nextPage
          }
        }
      }
    }

    s"redrect to error page (${technicalDifficultiesPath})" when {

      "noone is in paid employment" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObject(paidOrSelfEmployed = Some(false)))
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(vouchersKey -> "yes")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "there is no information about paid employment" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObject(paidOrSelfEmployed = None))
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(vouchersKey -> "yes")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "pageObjects doesn't exists in keystore" in {
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
              .withFormUrlEncodedBody(vouchersKey -> "yes")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore while loading data" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(vouchersKey -> "yes")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

    }

  }

}
