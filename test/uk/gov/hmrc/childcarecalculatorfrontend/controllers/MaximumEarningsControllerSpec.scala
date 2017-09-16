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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.{ControllersValidator, ObjectBuilder}
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

      "redirect to technical difficulty page if there is some runtime exception" in {

        val modelToFetch = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = Some(true),
          partnerEarnMoreThanNMW = Some(true),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)
        )

        setupMocks(modelToFetch = Some(modelToFetch))
        setupMocksForException()

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
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsParentPath

        }

        "have back url of parent Minimum Earnings page when parent earns more than NMW and " +
          "maximum earnings page selection exists" in {
          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true))),
            maximumEarnings = Some(true))

          val modelToFetch = buildPageObjectsModel(isPartner = false,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))))

          val result = maximumEarningsController.onPageLoad("YOU")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsParentPath

        }
      }

      "load template successfully for partner only " when  {
        "have back url as partner Minimum Earnings page when partner earns more than NMW" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true))),
            maximumEarnings = Some(true))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner)),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER))))

          val result = maximumEarningsController.onPageLoad("PARTNER")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsPartnerPath

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
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsPartnerPath
        }


      }

      "load template successfully when both are in paid employment " when  {
        "have back url partner Minimum Earnings page when both earns more than NMW" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad("BOTH")(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsPartnerPath

        }

        "have back url as partner Selfemployed or Apprentice  page when parent earns more than NMW and " +
          "partner doesn't and partner is not selfemployed" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticePartnerPath
        }

        "have back url as partner Selfemployed 12 months page when parent earns more than NMW and " +
          "partner doesn't, partner is selfemployed and been selfemployed for less than 12 months" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED),
            selfEmployedIn12Months = Some(true))))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedPartnerPath

        }

        "have back url as to partner Selfemployed 12 months page when parent earns more than NMW and " +
          "partner doesn't, partner is selfemployed and not been selfemployed for less than 12 months" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED),
            selfEmployedIn12Months = Some(true))))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedPartnerPath

        }

        "have back url as parent Selfemployed or Apprentice  page when partner earns more than NMW and " +
          "parent doesn't and parent is not selfemployed" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None,
            selfEmployedIn12Months = Some(true))))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedOrApprenticeParentPath

        }

        "have back url as parent Selfemployed 12 months page when partner earns more than NMW and " +
          "parent doesn't and parent is selfemployed" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None,
            selfEmployedIn12Months = Some(true))))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(false),
            employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe selfEmployedParentPath

        }

        "have back url partner Minimum Earnings page when partner earns more than NMW" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsPartnerPath

        }

        "have back url partner Minimum Earnings page when parent earns more than NMW" in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings))

          val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val modelToFetch = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          setupMocks(modelToFetch = Some(modelToFetch.copy(household = modelToFetch.household.copy(partner = Some(partner), parent = parent),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))))

          val result = maximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)(request.withSession(validSession))
          status(result) shouldBe OK

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe minimumEarningsPartnerPath

        }
      }

     }
 }

  "onSubmit is called" when {

    "there are errors" should {
      "load same template and return BAD_REQUEST as a partner" in {
        val model = buildPageObjectsModel(true)
        setupMocks(modelToFetch = Some(model))

        val result = maximumEarningsController.onSubmit("PARTNER")(
            request
              .withFormUrlEncodedBody(maximumEarningsKey -> "")
              .withSession(validSession)
          )

        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load same template and return BAD_REQUEST as a parent" in {

        val model = buildPageObjectsModel(false)
        setupMocks(modelToFetch = Some(model))

        val result = maximumEarningsController.onSubmit("YOU")(
            request
              .withFormUrlEncodedBody(maximumEarningsKey -> "")
              .withSession(validSession)
          )

        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "save the data in keystore successfully for parent" should {
      "redirect to tc/uc page" in {

        val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val model = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = None)

        val modelToFetch = model.copy(household = model.household.copy(parent = parent),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))

        val modelToStore = modelToFetch.copy(household = modelToFetch.household.copy(
          parent = parent.copy(maximumEarnings = Some(true))))

        setupMocks(modelToFetch = Some(modelToFetch), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.YOU.toString)(
          request
            .withFormUrlEncodedBody(maximumEarningsKey -> "true")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        //TODO: To be replaced by TC/UC page
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.underConstruction().url))

      }
    }

    "save the data in keystore successfully for partner" should {
        "redirect to tc/uc page " in {

          val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
            employmentStatus = None)))

          val model = buildPageObjectsModel(isPartner = true,
            parentEarnMoreThanNMW = None)

          val modelToFetch = model.copy(household = model.household.copy(partner = Some(partner)),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER))

          val modelToStore = modelToFetch.copy(household = modelToFetch.household.copy(
            partner = Some(partner.copy(maximumEarnings = Some(true)))))

          setupMocks(modelToFetch = Some(modelToFetch), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.PARTNER.toString)(
            request
              .withFormUrlEncodedBody(maximumEarningsKey -> "true")
              .withSession(validSession)
          )

          status(result) shouldBe SEE_OTHER
          //TODO: To be replaced by TC/UC page
          redirectLocation(result) should be(Some(routes.ChildCareBaseController.underConstruction().url))
        }

    }

    "save the data in keystore successfully for both parent and partner" should {
      "redirect to tc/uc page" in {

        val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val model = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = None)

        val modelToFetch = model.copy(household = model.household.copy(partner = Some(partner), parent = parent),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))

        val modelToStore = modelToFetch.copy(household = modelToFetch.household.copy(parent = parent.copy(maximumEarnings = Some(true)),
          partner = Some(partner.copy(maximumEarnings = Some(true)))))

        setupMocks(modelToFetch = Some(modelToFetch), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.BOTH.toString)(
          request
            .withFormUrlEncodedBody(maximumEarningsKey -> "true")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        //TODO: To be replaced by TC/UC page
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.underConstruction().url))
      }
    }

    "data store in keystore fails" should {
      "redirect to technical difficulties page" in {

        val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val model = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = None)

        val modelToFetch = model.copy(household = model.household.copy(partner = Some(partner), parent = parent),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))

        setupMocks(modelToFetch = Some(modelToFetch), modelToStore = None, storePageObjects = true)

        val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.BOTH.toString)(
          request
            .withFormUrlEncodedBody(maximumEarningsKey -> "true")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }
    }

    "page objects not found in keystore" should {
      "redirect to technical difficulties page" in {

        setupMocks(modelToFetch = None)
        val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.BOTH.toString)(
          request
            .withFormUrlEncodedBody(maximumEarningsKey -> "true")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }
    }

    "runtime exception occurs" should {
      "redirect to technical difficulties page" in {

        val partner = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val parent = buildClaimant.copy(minimumEarnings = Some(buildMinimumEarnings.copy(earnMoreThanNMW = Some(true),
          employmentStatus = None)))

        val model = buildPageObjectsModel(isPartner = true,
          parentEarnMoreThanNMW = None)

        val modelToFetch = model.copy(household = model.household.copy(partner = Some(partner), parent = parent),
          whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH))

        setupMocks(modelToFetch = Some(modelToFetch), modelToStore = None, storePageObjects = true)
        setupMocksForException()

        val result = maximumEarningsController.onSubmit(YouPartnerBothEnum.BOTH.toString)(
          request
            .withFormUrlEncodedBody(maximumEarningsKey -> "true")
            .withSession(validSession)
        )

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
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
