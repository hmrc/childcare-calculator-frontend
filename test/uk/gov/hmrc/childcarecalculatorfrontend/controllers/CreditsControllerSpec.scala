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
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.{ControllersValidator, MockBuilder, ObjectBuilder}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class CreditsControllerSpec extends ControllersValidator with BeforeAndAfterEach with MockBuilder with ObjectBuilder {

  val sut = new CreditsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(creditsPath)

  "CreditsController" when {

    "onPageLoad is called" should {

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(sut.onPageLoad()(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to technical difficulties page if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.onPageLoad()(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "load template when user visiting the page first time" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjectsModel(isPartner = false, parentEarnMoreThanNMW = None))
          )
        )
        val result = await(sut.onPageLoad()(request.withSession(validSession)))
        status(result) shouldBe OK
      }

      "load template successfully if there is data in keystore and define correct backURL" when {

        s"redirect to ${maximumEarningsParentPath} page when single parent satisfy minimum earnings" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = false, parentEarnMoreThanNMW = Some(true)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe maximumEarningsParentPath
        }

        s"redirect to ${selfEmployedOrApprenticeParentPath} page when single parent not satisfy minimum earnings and apprentice or neither is selected on " +
          "self employed/apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = false, parentEarnMoreThanNMW = Some(false), parentEmploymentStatus =
                Some(EmploymentStatusEnum.APPRENTICE)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticeParentPath
        }

        s"redirect to ${selfEmployedParentPath} page when single parent not satisfy minimum earnings & select self employed on " +
          s"self employed/apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = false, parentEarnMoreThanNMW = Some(false), parentEmploymentStatus =
                Some(EmploymentStatusEnum.SELFEMPLOYED)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedParentPath
        }

        s"redirect to ${maximumEarningsPartnerPath} page when only partner is in paid employment & satisfy minimum earnings" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(true), whichOfYouInPaidEmployment =
                Some(YouPartnerBothEnum.PARTNER)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe maximumEarningsPartnerPath
        }

        s"redirect to ${selfEmployedOrApprenticePartnerPath} page when only partner in paid employment & not satisfy minimum earnings & apprentice or " +
          "neither is selected on self employed/apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(false), partnerEmploymentStatus =
                Some(EmploymentStatusEnum.NEITHER), whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticePartnerPath
        }

        s"redirect to ${selfEmployedPartnerPath} page when only parent in paid employment & not satisfy minimum earnings & select self employed on self " +
          "employed/ apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(false), partnerEmploymentStatus =
                Some(EmploymentStatusEnum.SELFEMPLOYED), whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedPartnerPath
        }

        s"redirect to ${maximumEarningsPath} page when both are in paid employment & satisfy minimum earnings" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(true), parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe maximumEarningsPath
        }

        s"redirect to ${maximumEarningsPartnerPath} page when both are in paid employment & partner satisfy & parent not satisfy min earnings" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(true), parentEarnMoreThanNMW = Some(false),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe maximumEarningsPartnerPath
        }

        s"redirect to ${maximumEarningsParentPath} page when both are in paid employment & parent satisfy & partner not satisfy min earnings" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(false), parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe maximumEarningsParentPath
        }

        s"redirect to ${selfEmployedOrApprenticePartnerPath} page when both are in paid employment & not satisfy minimum earnings & apprentice or " +
          "neither is selected on partner self employed/apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(false), parentEarnMoreThanNMW = Some(false),
                partnerEmploymentStatus = Some(EmploymentStatusEnum.APPRENTICE), whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticePartnerPath
        }

        s"redirect to ${selfEmployedPartnerPath} page when both are in paid employment & not satisfy minimum earnings & selects self employed on partner " +
          "self employed/ apprentice page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true, partnerEarnMoreThanNMW = Some(false), parentEarnMoreThanNMW = Some(false),
                partnerEmploymentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED), whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )
          val result = await(sut.onPageLoad()(request.withSession(validSession)))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedPartnerPath
        }

      }

    }

    "onSubmit is called" should {

      s"redirect to ${technicalDifficultiesPath} if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = await(sut.onSubmit()(
          request
            .withFormUrlEncodedBody(creditsKey -> CreditsEnum.UNIVERSALCREDIT.toString)
            .withSession(validSession)
        ))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      s"redirect to ${technicalDifficultiesPath} if keystore is unable to cache" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            Some(buildPageObjectsModel(true))
          )
        )

        when(
          sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(
          sut.onSubmit()(
            request
              .withFormUrlEncodedBody(creditsKey -> CreditsEnum.UNIVERSALCREDIT.toString)
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "load template when there is incorrect input, return BAD_REQUEST" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjectsModel(true))
          )
        )
        val result = await(
          sut.onSubmit()(
            request
              .withFormUrlEncodedBody(creditsKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      s"redirect to ${technicalDifficultiesPath} page when there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )

        val result = await(
          sut.onSubmit()(
            request
              .withFormUrlEncodedBody(creditsKey -> "123")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      s"successful submission with location is England, has child of 3 or 4 years, satisfy minimum earnings and earning less than £100,000, " +
        s"redirect to ${maxFreeHoursInfoPath} info page" in {
        val model = buildPageObjectsModel(isPartner = false, whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))

        val modelToFetch = model.copy(household = model.household.copy(credits = None))

        val modelToStore = modelToFetch.copy(household = modelToFetch.household.copy(credits = Some(CreditsEnum.NONE)))

        setupMocks(sut.keystore, modelToFetch = Some(modelToFetch), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = sut.onSubmit()(
          request
            .withFormUrlEncodedBody(creditsKey -> "NONE")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(maxFreeHoursInfoPath))
      }

      s"successful submission with location=England, no child of 3 or 4 years, satisfy minimum earnings and earnings less than £100,000, " +
        s"redirect to ${howManyChildrenPath} page" in {
        val model = buildPageObjectsModel(isPartner = false, whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))

        val modelToFetch = model.copy(household = model.household.copy(credits = None))

        val modelToStore = modelToFetch.copy(household = modelToFetch.household.copy(credits = Some(CreditsEnum.NONE)))

        setupMocks(sut.keystore, modelToFetch = Some(modelToFetch), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = sut.onSubmit()(
          request
            .withFormUrlEncodedBody(creditsKey -> "NONE")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(howManyChildrenPath))
      }

      s"successful submission with location=England, no child of 3 or 4 years, satisfy minimum earnings and earnings greater than £100,000, " +
        "redirect to how many children page" in {
      }

      s"successful submission with location=England, no child of 3 or 4 years, not satisfy minimum earnings and not self employed or neither, " +
        "redirect to how many children page" in {
      }

      s"successful submission with location=England, has child of 3 or 4 years, not satisfy minimum earnings and is apprentice, " +
        "redirect to maximum free hours info page" in {
      }

      s"successful submission with location=England, has only child of 3 or 4 years, satisfy minimum earnings and earnings greater than £100,000, " +
        "redirect to result page" in {
      }

    }
  }

  private def buildPageObjectsModel(isPartner: Boolean,
                                    parentEarnMoreThanNMW: Option[Boolean] = None,
                                    partnerEarnMoreThanNMW: Option[Boolean] = None,
                                    parentEmploymentStatus: Option[EmploymentStatusEnum] = None,
                                    partnerEmploymentStatus: Option[EmploymentStatusEnum] = None,
                                    whichOfYouInPaidEmployment: Option[YouPartnerBothEnum] = None
                                   ): PageObjects = {

    val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = parentEarnMoreThanNMW,
      employmentStatus = parentEmploymentStatus)))
    val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = partnerEarnMoreThanNMW,
      employmentStatus = partnerEmploymentStatus)))

    if (isPartner) {
      buildPageObjects.copy(whichOfYouInPaidEmployment = whichOfYouInPaidEmployment,
        household = buildHousehold.copy(location = LocationEnum.ENGLAND, parent = parent, partner = Some(partner)))
    } else {
      buildPageObjects.copy(whichOfYouInPaidEmployment = whichOfYouInPaidEmployment,
        household = buildHousehold.copy(location = LocationEnum.ENGLAND, parent = parent))
    }
  }


}