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
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.{ObjectBuilder, ControllersValidator}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum.AgeRangeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class MaximumEarningsControllerSpec extends ControllersValidator with BeforeAndAfterEach with ObjectBuilder{

  val maximumEarningsController = new MaximumEarningsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(maximumEarningsController.keystore)
  }

  validateUrl(maximumEarningsParentPath)
  validateUrl(maximumEarningsPartnerPath)
  validateUrl(maximumEarningsPath)


  "MaximumEarningsController" when {

    "onPageLoad is called" should {

      "redirect to technical difficulty page if there is no data in keystore for parent" in {
       setupMocks(modelToFetch = None)

        val result = maximumEarningsController.onPageLoad("BOTH")(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }

      "redirect to technical difficulty page if there is no data in keystore for partner" in {
        setupMocks(modelToFetch = None)

        val result = maximumEarningsController.onPageLoad("BOTH")(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }

      "load template when user visiting the page first time for partner" in {

        val modelToFetch = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = Some(true),
          partnerEarnMoreThanNMW = Some(true),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)
        )

        setupMocks(modelToFetch = Some(modelToFetch))

        val result = maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession))
        status(result) shouldBe OK
      }

      "load template when user visiting the page first time for parent" in {

       val modelToFetch = buildPageObjectsModel(isPartner = false,
          parentEarnMoreThanNMW = None)

        setupMocks(modelToFetch = Some(modelToFetch))

        val result = maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession))
        status(result) shouldBe OK
      }

      "load template successfully for parent only " when  {
        "have back url of parent Minimum Earnings page when parent earns more than NMW" in {

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true))))

          val modelToFetch = buildPageObjectsModel(isPartner = false,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))))

          val result = maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentMinimumEarningsPath

          //redirectLocation(result) should be(Some(routes.MinimumEarningsController.onPageLoad(false).url))

        }

        "have back url of parent Selfemployed or Apprentice  page when parent does not earn more than NMW and " +
          "is not selfemployed" in {

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))

          val modelToFetch = buildPageObjectsModel(isPartner = false,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))))

          val result = maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticeParentPath

        }

        "have back url as parent Selfemployed 12 months page when parent does not earn more than NMW and " +
          "is selfemployed" in {

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))

          val modelToFetch = buildPageObjectsModel(isPartner = false,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))))

          val result = maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentSelfEmployedPath
        }

      }

      "load template successfully for partner only " when  {
        "have back url as partner Minimum Earnings page when partner earns more than NMW" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true))))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner)),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER))))

          val result = maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath

        }

        "have back url as partner Selfemployed or Apprentice  page when partner does not earn more than NMW and " +
          "is not selfemployed" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner)),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER))))

          val result = maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        "should go back to partner Selfemployed 12 months page when partner does not earn more than NMW and " +
          "is selfemployed" in {

        }

      }

      "load template successfully when both are in paid employment " when  {
        "should go back to partner Minimum Earnings page when both earns more than NMW" in {

        }

        "should go back to partner Selfemployed or Apprentice  page when parent earns more than NMW and " +
          "partner doesn't and partner is not selfemployed" in {

        }

        "should go back to partner Selfemployed 12 months page when parent earns more than NMW and " +
          "partner doesn't and partner is selfemployed" in {

        }


        "should go back to parent Selfemployed or Apprentice  page when partner earns more than NMW and " +
          "parent doesn't and parent is not selfemployed" in {

        }

        "should go back to parent Selfemployed 12 months page when partner earns more than NMW and " +
          "parent doesn't and parent is selfemployed" in {

        }

      }


      //****************************************


      "load template successfully if there is data in keystore for parent and define correctly backURL" when {
        "redirect to parent's minimum earnings page" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(
                isPartner = false,
                parentEarnMoreThanNMW = Some(true)))
            )
          )

          val result = await(maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentMinimumEarningsPath
        }

        "redirect to partner's minimum page" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = false,
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )

          val result = await(maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        "redirect to partner's minimum earnings page" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = false,
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )

          val result = await(maximumEarningsController.onPageLoad("BOTH")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        "redirect to error page if can't connect with keystore if parent" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "load template successfully if there is data in keystore for partner and display correct backurl" when {
        "redirect to partner's minimum earnings page" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true,
                parentEarnMoreThanNMW = Some(true),
                partnerEarnMoreThanNMW = Some(true)))
            )
          )
          val result = await(maximumEarningsController.onPageLoad("BOTH")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }


       "redirect to partner's minimum earnings page when in paid partner " in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjectsModel(isPartner = true,
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                partnerEarnMoreThanNMW = Some(true)))
            )
          )
          val result = await(maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

       "redirect to error page if can't connect with keystore if partner" in {
          when(
            maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }
     }
 }

  "onSubmit is called" when {

    "there are errors" should {
      "load same template and return BAD_REQUEST as a partner" in {
        when(
          maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjectsModel(true))
          )
        )
        val result = await(
          maximumEarningsController.onSubmit("PARTNER")(
            request
              .withFormUrlEncodedBody(minimumEarningsKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load same template and return BAD_REQUEST as a parent" in {
        when(
          maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjectsModel(false))
          )
        )
        val result = await(
          maximumEarningsController.onSubmit("YOU")(
            request
              .withFormUrlEncodedBody(minimumEarningsKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "save the data in keystore successfully for parent" should {
      "redirect to tc/uc page" in {

      }
    }

    "save the data in keystore successfully for partner" should {
        "redirect to tc/uc pag " in {
        }

    }


    "save the data in keystore successfully for both parent and partner" should {
      "redirect to tc/uc page" in {

      }
    }

  }

  private def buildPageObjectsModel(isPartner: Boolean,
                       parentEarnMoreThanNMW: Option[Boolean] = None,
                       partnerEarnMoreThanNMW: Option[Boolean] = None,
                       whichOfYouInPaidEmployment: Option[YouPartnerBothEnum] = None
                      ): PageObjects = {

    val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = parentEarnMoreThanNMW)))
    val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = partnerEarnMoreThanNMW)))

    if (isPartner) {

      buildPageObjects.copy(whichOfYouInPaidEmployment = whichOfYouInPaidEmployment,
        household = buildHousehold.copy(location = LocationEnum.ENGLAND, parent = parent, partner = Some(partner)))

    } else {
      buildPageObjects.copy(whichOfYouInPaidEmployment = whichOfYouInPaidEmployment,
        household = buildHousehold.copy(location = LocationEnum.ENGLAND, parent = parent))
    }
  }

  /**
    * Setup the mocks
    *
    * @param modelToFetch
    * @param modelToStore
    * @param fetchPageObjects
    * @param storePageObjects
    * @return
    */
  private def setupMocks(modelToFetch: Option[PageObjects] = None,
                         modelToStore: Option[PageObjects] = None,
                         fetchPageObjects: Boolean = true,
                         storePageObjects: Boolean = false) = {
    if (fetchPageObjects) {
      when(
        maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
      ).thenReturn(
        Future.successful(modelToFetch)
      )
    }

    if (storePageObjects) {
      when(
        maximumEarningsController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
      ).thenReturn(
        Future.successful(modelToStore)
      )

    }

  }

  /**
    * setup the mock with runtimeException
    *
    * @return
    */
  private def setupMocksForException() = {
    when(
      maximumEarningsController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
    ).thenReturn(
      Future.failed(new RuntimeException)
    )
  }
}
