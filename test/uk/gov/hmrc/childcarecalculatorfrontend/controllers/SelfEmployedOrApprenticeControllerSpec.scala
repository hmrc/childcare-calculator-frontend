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

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits.applicationMessagesApi
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class SelfEmployedOrApprenticeControllerSpec extends ControllersValidator with BeforeAndAfterEach {

 val selfEmployedOrApprenticeController = new SelfEmployedOrApprenticeController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }



  "SelfEmployedOrApprenticeController" when {

    "onPageLoad is called" should {
      "show technical difficulties page if there is no data in keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }

      "load template successfully if there is data in keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(false))
          )
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
      "redirect to error page if can't connect with keystore" in {
        when(
          selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )
        val result = selfEmployedOrApprenticeController.onPageLoad(false)(request.withSession(validSession))

        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST as parent" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false))
            )
          )

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
                 request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "")
                .withSession(validSession)
              )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }

        "load same template and return BAD_REQUEST as partner" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true))
            )
          )

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
      "saving in keystore is successful as parent, only paid employment and selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.YOU)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false, YouPartnerBothEnum.YOU))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                parent = model.household.parent.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as parent, only paid employment and apprentice" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.YOU)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false, YouPartnerBothEnum.YOU))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                parent = model.household.parent.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as parent, only paid employment and neither apprentice nor selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.YOU)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false, YouPartnerBothEnum.YOU))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                parent = model.household.parent.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.NEITHER))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as parent, both are in paid employment and selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false, YouPartnerBothEnum.BOTH))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                parent = model.household.parent.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as parent, both are in paid employment and neither apprentice nor selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(false, YouPartnerBothEnum.BOTH)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false, YouPartnerBothEnum.BOTH))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                parent = model.household.parent.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.NEITHER))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      //================================ Parent Mode Ends ==========================================

      //================================ Partner Mode Starts =======================================
      "saving in keystore is successful as partner, only paid employment and selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.PARTNER)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true, YouPartnerBothEnum.PARTNER))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                partner = model.household.partner.map(_.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED)))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as partner, both are in paid employment and selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true, YouPartnerBothEnum.BOTH))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                partner = model.household.partner.map(_.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED)))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "SELFEMPLOYED")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as partner, both are in paid employment and apprentice" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true, YouPartnerBothEnum.BOTH))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                partner = model.household.partner.map(_.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.APPRENTICE)))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "APPRENTICE")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }

      "saving in keystore is successful as partner, both are in paid employment and neither apprentice nor selfemployed" should {
        s"go to ${selfEmployedTimescaleParentPath}" in {

          val model = buildPageObjects(true, YouPartnerBothEnum.BOTH)

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true, YouPartnerBothEnum.BOTH))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(model.copy(household = model.household.copy(
                partner = model.household.partner.map(_.copy(
                  minimumEarnings = model.household.parent.minimumEarnings.map(x => x.copy(
                    employmentStatus = Some(EmploymentStatusEnum.NEITHER)))))))
              )
            )
          )

          val result = await(
            selfEmployedOrApprenticeController.onSubmit(true)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> "NEITHER")
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          // redirectLocation(result) should be (Some(.url))
        }
      }
      //================================ Partner Mode Ends   =======================================

     "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            selfEmployedOrApprenticeController.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(false))
            )
          )

          when(
            selfEmployedOrApprenticeController.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = selfEmployedOrApprenticeController.onSubmit(false)(
              request
                .withFormUrlEncodedBody(selfEmployedOrApprenticeKey -> EmploymentStatusEnum.SELFEMPLOYED.toString)
                .withSession(validSession)
          )

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
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
}
