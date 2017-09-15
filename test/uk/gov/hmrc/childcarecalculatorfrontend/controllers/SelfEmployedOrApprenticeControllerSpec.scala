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
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class SelfEmployedOrApprenticeControllerSpec extends ControllersValidator with BeforeAndAfterEach {

 val selfEmployedOrApprenticeController = new SelfEmployedOrApprenticeController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  /**
    * To be run before each of this suite's tests.
    */
  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(selfEmployedOrApprenticeController.keystore)
  }

  "SelfEmployedOrApprenticeController" when {

    "onPageLoad is called" should {
      "show technical difficulties page if there is no data in keystore for parent" in {

        setupMocks(modelToFetch = None)

        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(technicalDifficultiesPath))
      }

      "redirect to error page if can't connect with keystore for parent" in {
        setupMocksForException()

        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "load template successfully if there is data in keystore for parent" in {

        setupMocks(modelToFetch = Some(buildPageObjects(false)))

        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      s"redirect to ${parentMinimumEarningsPath} when back button is pressed as parent" in {

        val modifiedObject = buildPageObjects(false)
        setupMocks(modelToFetch = Some(modifiedObject.copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU))))

        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe parentMinimumEarningsPath
      }

      "load template successfully if there is data in keystore for partner" in {

        setupMocks(modelToFetch = Some(buildPageObjects(true)))

        val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if partner is empty" in {

        val modified = buildPageObjects(true)
        setupMocks(modelToFetch = Some(modified.copy(household = modified.household.copy(partner = None))))

        val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to correct page when back button is pressed as partner" when {
        val modifiedObject = buildPageObjects(true)

        s"redirect to ${partnerMinimumEarningsPath}" in {

          setupMocks(modelToFetch = Some(modifiedObject.copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER))))

          val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        s"redirect to ${partnerMinimumEarningsPath} if both are in paid employment and parent satisfy minimum earnings" in {

          setupMocks(modelToFetch = Some(modifiedObject.copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            household = modifiedObject.household.copy(
              parent = modifiedObject.household.parent.copy(
                minimumEarnings = modifiedObject.household.parent.minimumEarnings.map(x => x.copy(earnMoreThanNMW = Some(true)))
              )
            )
          )))

          val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        s"redirect to ${parentSelfEmployedPath} if both are in paid employment and parent not satisfy minimum earnings & selected as self employed" in {

          setupMocks(modelToFetch = Some(modifiedObject.copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            household = modifiedObject.household.copy(
              parent = modifiedObject.household.parent.copy(
                minimumEarnings = modifiedObject.household.parent.minimumEarnings.map(x => x.copy(
                  employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED)))
              )
            )
          )))

          val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentSelfEmployedPath
        }

        s"redirect to ${parentSelfEmployedOrApprenticePath} if both are in paid employment & parent not satisfy minimum earnings & selected as apprentice" in {

          setupMocks(modelToFetch = Some(modifiedObject.copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            household = modifiedObject.household.copy(
              parent = modifiedObject.household.parent.copy(
                minimumEarnings = modifiedObject.household.parent.minimumEarnings.map(x => x.copy(
                  employmentStatus = Some(EmploymentStatusEnum.APPRENTICE)))
              )
            )
          )))

          val result = selfEmployedOrApprenticeController.onPageLoad(true)(request.withSession(validSession))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentSelfEmployedOrApprenticePath
        }

      }


    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST as parent" in {

          setupMocks(modelToFetch = Some(buildPageObjects(false)))

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
                 request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "")
                .withSession(validSession)
              )

          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }

        "load same template and return BAD_REQUEST as partner" in {
          setupMocks(modelToFetch = Some(buildPageObjects(true)))

          val result = selfEmployedOrApprenticeController.onSubmit(true)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "")
              .withSession(validSession)
          )

          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      //================================ Parent Mode Starts =======================================
      //TODO: Redirection location to be changed for both parent and partner
      s"saving in keystore is successful as parent, only paid employment and selfemployed redirect to ${parentSelfEmployedPath}" in {

        val model = buildPageObjects(false, YouPartnerBothEnum.YOU)

        val modelToStore = model.copy(household = model.household.copy(
          parent = model.household.parent.copy(
            minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
              employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))))

        setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(false)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(parentSelfEmployedPath))
      }

      s"saving in keystore is successful as parent, only paid employment and apprentice redirect to (tc/uc)${underConstructionPath}" in {

        val model = buildPageObjects(false, YouPartnerBothEnum.YOU)

        val modelToStore = model.copy(household = model.household.copy(
          parent = model.household.parent.copy(
            minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
              employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))))

        setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(false)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(underConstructionPath))
      }

      s"saving in keystore is successful as parent, only paid employment and neither redirect to ${underConstructionPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.YOU)
          val modelToStore = model.copy(household = model.household.copy(
            parent = model.household.parent.copy(
              minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.NEITHER))))))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(underConstructionPath))
      }

      s"saving in keystore is successful as parent, both in paid employment and is self employed redirect to ${parentSelfEmployedPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)
          val modelToStore = model.copy(household = model.household.copy(
            parent = model.household.parent.copy(
              minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(parentSelfEmployedPath))
      }

      s"saving in keystore is successful as parent, both in paid employment and neither redirect to ${partnerSelfEmployedOrApprenticePath}" in {
          val minimumEarning = MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))
          val claimant = Claimant(minimumEarnings = Some(minimumEarning))

          val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)

          val modelToMock = model.copy(household = model.household.copy(
            parent = model.household.parent.copy(
              minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.NEITHER)))),
            partner = Some(claimant)))

          setupMocks(modelToFetch = Some(modelToMock), modelToStore = Some(modelToMock), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(partnerSelfEmployedOrApprenticePath))
      }

      s"saving in keystore is successful as parent, both in paid employment and apprentice redirect to ${partnerSelfEmployedOrApprenticePath}" in {
        val minimumEarning = MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))
        val claimant = Claimant(minimumEarnings = Some(minimumEarning))

        val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)

        val modelToMock = model.copy(household = model.household.copy(
          parent = model.household.parent.copy(
            minimumEarnings = None
          ),
          partner = Some(claimant)))

        setupMocks(modelToFetch = Some(modelToMock), modelToStore = Some(modelToMock), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(false)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(partnerSelfEmployedOrApprenticePath))
      }

      s"saving in keystore is successful as parent, both in paid employment with partner satisfy min earnings, " +
        s"redirect to ${partnerMaximumEarningsPath}" in {

        val minimumEarning = MinimumEarnings(earnMoreThanNMW = Some(true), employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))
        val claimant = Claimant(minimumEarnings = Some(minimumEarning))
        val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)

        val modelToMock = model.copy(household = model.household.copy(
          parent = model.household.parent.copy(
            minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
              employmentStatus = Some(EmploymentStatusEnum.NEITHER)))),
          partner = Some(claimant)))

        setupMocks(modelToFetch = Some(modelToMock), modelToStore = Some(modelToMock), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(false)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(partnerMaximumEarningsPath))
      }

      //================================ Parent Mode Ends ==========================================

      //================================ Partner Mode Starts =======================================
      s"saving in keystore is successful as partner, only paid employment and selfemployed, redirect to ${partnerSelfEmployedPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.PARTNER)
          val modelToStore = model.copy(household = model.household.copy(
            partner = model.household.partner.map(_.copy(
              minimumEarnings = model.household.partner.get.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED)))))))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(partnerSelfEmployedPath))
      }

      s"saving in keystore is successful as partner, only paid employment and apprentice, redirect to (tc/uc)${underConstructionPath}" in {

        val model = buildPageObjects(true, YouPartnerBothEnum.PARTNER)
        val modelToStore = model.copy(household = model.household.copy(
          partner = model.household.partner.map(_.copy(
            minimumEarnings = model.household.partner.get.minimumEarnings.map(x => x.copy(
              employmentStatus = Some(EmploymentStatusEnum.APPRENTICE)))))))

        setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(true)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(underConstructionPath))
      }

      s"saving in keystore is successful as partner, both are in paid employment and selfemployed, redirect to ${partnerSelfEmployedPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)
          val modelToStore = model.copy(household = model.household.copy(
            partner = model.household.partner.map(_.copy(
              minimumEarnings = model.household.partner.get.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED)))))))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(partnerSelfEmployedPath))
      }

      s"saving in keystore is successful as partner, both are in paid employment and parent satisfy min earnings, " +
        s"redirect to ${parentMaximumEarningsPath}" in {

        val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)
        val modelToStore = model.copy(household = model.household.copy(
          parent = model.household.parent.copy(
            minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
              earnMoreThanNMW = Some(true)))
          ),
          partner = model.household.partner.map(_.copy(
            minimumEarnings = None
          ))
        ))

        setupMocks(modelToFetch = Some(modelToStore), modelToStore = Some(modelToStore), storePageObjects = true)

        val result = await(
          selfEmployedOrApprenticeController.onSubmit(true)(
            request
              .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(parentMaximumEarningsPath))
      }

      s"saving in keystore is successful as partner, both are in paid employment and apprentice, redirect to ${underConstructionPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)
          val modelToStore = model.copy(household = model.household.copy(
            partner = model.household.partner.map(_.copy(
              minimumEarnings = model.household.partner.get.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.APPRENTICE)))))))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(underConstructionPath))
      }

      s"saving in keystore is successful as partner, both are in paid employment and neither, redirect to ${underConstructionPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)
          val modelToStore = model.copy(household = model.household.copy(
            parent = model.household.parent.copy(
              minimumEarnings = None
            ),
            partner = model.household.partner.map(_.copy(
              minimumEarnings = model.household.partner.get.minimumEarnings.map(x => x.copy(
                employmentStatus = Some(EmploymentStatusEnum.NEITHER)))
            ))
          ))

          setupMocks(modelToFetch = Some(model), modelToStore = Some(modelToStore), storePageObjects = true)

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(underConstructionPath))
      }
      //================================ Partner Mode Ends   =======================================

     "connecting with keystore fails" should {
        s"while fetching, redirect to ${technicalDifficultiesPath}" in {

          setupMocksForException()
          setupMocks(modelToFetch = Some(buildPageObjects(false)))

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> EmploymentStatusEnum.SELFEMPLOYED.toString)
                .withSession(validSession)
          )

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(technicalDifficultiesPath))
        }

       s"while caching, redirect to ${technicalDifficultiesPath}" in {

         setupMocksForException()
         setupMocks(modelToStore = Some(buildPageObjects(true)))

         val result = selfEmployedOrApprenticeController.onSubmit(true)(
           request
             .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> EmploymentStatusEnum.SELFEMPLOYED.toString)
             .withSession(validSession)
         )

         status(result) shouldBe SEE_OTHER
         redirectLocation(result) should be (Some(technicalDifficultiesPath))
       }

      }

    }
  }

  /**
    * Builds the model
    * @param isPartner
    * @param inPaidEmployment
    * @return
    */
  private def buildPageObjects(isPartner: Boolean, inPaidEmployment: YouPartnerBothEnum = YouPartnerBothEnum.BOTH): PageObjects = {
    val minimumEarning = MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))
    val claimant = Claimant(minimumEarnings = Some(minimumEarning))
    if (isPartner) {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant, partner = Some(claimant)),
        whichOfYouInPaidEmployment = Some(inPaidEmployment))
    } else {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant),
        whichOfYouInPaidEmployment = Some(inPaidEmployment))
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
        selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
      ).thenReturn(
        Future.successful(modelToFetch)
      )
    }

    if (storePageObjects) {
      when(
        selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
      ).thenReturn(
        Future.successful(modelToStore)
      )

    }

  }

  /**
    * setup the mock with runtimeException
    * @return
    */
  private def setupMocksForException() = {
    when(
      selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
    ).thenReturn(
      Future.failed(new RuntimeException)
    )
  }
}
