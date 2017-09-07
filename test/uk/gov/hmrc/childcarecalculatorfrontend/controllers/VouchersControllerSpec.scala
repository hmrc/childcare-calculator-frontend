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
      val nextPageTest = Table(
        ("User in paid employment", "Partner", "Selected vouchers", "Next page"),
        (None, None, YesNoUnsureEnum.YES, getBenefitsPath),
        (None, None, YesNoUnsureEnum.NO, getBenefitsPath),
        (None, None, YesNoUnsureEnum.NOTSURE, getBenefitsPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.YES, getBenefitsPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.NO, getBenefitsPath),
        (Some(YouPartnerBothEnum.YOU), Some(Claimant()), YesNoUnsureEnum.NOTSURE, getBenefitsPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.YES, getBenefitsPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.NO, getBenefitsPath),
        (Some(YouPartnerBothEnum.PARTNER), Some(Claimant()), YesNoUnsureEnum.NOTSURE, getBenefitsPath),
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.YES, whoGetsVouchersPath),
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.NO, getBenefitsPath),
        (Some(YouPartnerBothEnum.BOTH), Some(Claimant()), YesNoUnsureEnum.NOTSURE, getBenefitsPath)
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

    "modify data in keystore corectly" when {
      "value for getVouchers is unchanged, keystore object shouldn't change" in {
        val keystoreObject = buildPageObject(
          getVouchers = Some(YesNoUnsureEnum.NO),
          paidOrSelfEmployed = Some(true),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
          partner = Some(Claimant(hours = Some(37.5), escVouchers = Some(YesNoUnsureEnum.NO)))
        )

        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(keystoreObject)
          )
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
              .withFormUrlEncodedBody(vouchersKey -> YesNoUnsureEnum.NO.toString)
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") should not be technicalDifficultiesPath
      }

      "value for getVouchers is changed" should {
        "set value for parent vouchrs when only parent is in paid employment" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.YES)),
              partner = Some(Claimant())
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU),
            getVouchers = Some(YesNoUnsureEnum.YES)
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(keystoreObject)
            )
          )

          val modifiedObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.NO)),
              partner = Some(Claimant())
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU),
            getVouchers = Some(YesNoUnsureEnum.NO)
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
                .withFormUrlEncodedBody(vouchersKey -> YesNoUnsureEnum.NO.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "set value for partner vouchrs when only partner is in paid employment" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(),
              partner = Some(Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.YES)))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
            getVouchers = Some(YesNoUnsureEnum.YES)
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(keystoreObject)
            )
          )

          val modifiedObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(),
              partner = Some(Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.NO)))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
            getVouchers = Some(YesNoUnsureEnum.NO)
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
                .withFormUrlEncodedBody(vouchersKey -> YesNoUnsureEnum.NO.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "set value for parent and partner vouchers when both are in paid employment" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.YES)),
              partner = Some(Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.YES)))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = Some(YesNoUnsureEnum.YES)
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(keystoreObject)
            )
          )

          val modifiedObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.NO)),
              partner = Some(Claimant(hours = Some(15), escVouchers = Some(YesNoUnsureEnum.NO)))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = Some(YesNoUnsureEnum.NO)
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
                .withFormUrlEncodedBody(vouchersKey -> YesNoUnsureEnum.NO.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "not set value for parent and partner vouchers when both are in paid employment if user selects 'YES'" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = None),
              partner = Some(Claimant(hours = Some(15), escVouchers = None))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = None
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(keystoreObject)
            )
          )

          val modifiedObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(hours = Some(15), escVouchers = None),
              partner = Some(Claimant(hours = Some(15), escVouchers = None))
            ),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = Some(YesNoUnsureEnum.YES)
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
                .withFormUrlEncodedBody(vouchersKey -> YesNoUnsureEnum.YES.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
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
