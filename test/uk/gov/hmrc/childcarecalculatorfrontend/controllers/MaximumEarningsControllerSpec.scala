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
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum.AgeRangeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class MaximumEarningsControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new MaximumEarningsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(maximumEarningsParentPath)
  validateUrl(maximumEarningsPartnerPath)
  validateUrl(maximumEarningsPath)

  def buildPageObjects(isPartner: Boolean,
                       parentAgeRange: Option[AgeRangeEnum] = None,
                       partnerAgeRange: Option[AgeRangeEnum] = None,
                       parentEarnMoreThanNMW: Option[Boolean] = None,
                       partnerEarnMoreThanNMW: Option[Boolean] = None,
                       whichOfYouInPaidEmployment: Option[YouPartnerBothEnum] = None
                      ): PageObjects = {
    val parent = Claimant(ageRange = parentAgeRange, minimumEarnings = Some(MinimumEarnings(earnMoreThanNMW = parentEarnMoreThanNMW)))
    val partner = Claimant(ageRange = partnerAgeRange, minimumEarnings = Some(MinimumEarnings(earnMoreThanNMW = partnerEarnMoreThanNMW)))

    if (isPartner) {
      PageObjects(whichOfYouInPaidEmployment=whichOfYouInPaidEmployment, household = Household(location = LocationEnum.ENGLAND, parent = parent,
        partner = Some(partner)))
    } else {
      PageObjects(whichOfYouInPaidEmployment=whichOfYouInPaidEmployment, household = Household(location = LocationEnum.ENGLAND, parent = parent))
    }
  }

  val ageRanges = List(
    AgeRangeEnum.UNDER18,
    AgeRangeEnum.EIGHTEENTOTWENTY,
    AgeRangeEnum.TWENTYONETOTWENTYFOUR,
    AgeRangeEnum.OVERTWENTYFOUR
  )

  "MaximumEarningsController" when {

    "onPageLoad is called" should {

      "redirect to technical difficulty page if there is no data in keystore for parent" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.onPageLoad("BOTH")(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }

      "redirect to technical difficulty page if there is no data in keystore for partner" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.onPageLoad("BOTH")(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) should be(Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
      }

      "load template when user visiting the page first time for partner" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(isPartner = true,
              parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
              parentEarnMoreThanNMW = Some(true),
              partnerAgeRange = Some(AgeRangeEnum.UNDER18),
              partnerEarnMoreThanNMW = Some(true),
              whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)
            ))
          )
        )

        val result = await(sut.onPageLoad("PARTNER")(request.withSession(validSession)))
        status(result) shouldBe OK
      }

      "load template when user visiting the page first time for parent" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(isPartner = false,
              parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
              parentEarnMoreThanNMW = None))
          )
        )
        val result = await(sut.onPageLoad("YOU")(request.withSession(validSession)))
        status(result) shouldBe OK
      }

      "load template successfully if there is data in keystore for parent and define correctly backURL" when {
        "redirect to parent's minimum earnings page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(
                isPartner = false,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                parentEarnMoreThanNMW = Some(true)))
            )
          )

          val result = await(sut.onPageLoad("YOU")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe parentMinimumEarningsPath
        }

        "redirect to partner's minimum page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )

          val result = await(sut.onPageLoad("PARTNER")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        "redirect to partner's minimum earnings page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
            )
          )

          val result = await(sut.onPageLoad("BOTH")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"

          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

        "redirect to error page if can't connect with keystore if parent" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(sut.onPageLoad("YOU")(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "load template successfully if there is data in keystore for partner and display correct backurl" when {
        "redirect to partner's minimum earnings page" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
                parentEarnMoreThanNMW = Some(true),
                partnerEarnMoreThanNMW = Some(true)))
            )
          )
          val result = await(sut.onPageLoad("BOTH")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }


       "redirect to partner's minimum earnings page when in paid partner " in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
                parentEarnMoreThanNMW = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                partnerEarnMoreThanNMW = Some(true)))
            )
          )
          val result = await(sut.onPageLoad("PARTNER")(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe partnerMinimumEarningsPath
        }

       "redirect to error page if can't connect with keystore if partner" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onPageLoad("PARTNER")(request.withSession(validSession)))
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
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(true,
              parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
              partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR)))
          )
        )
        val result = await(
          sut.onSubmit("PARTNER")(
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
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(false,
              parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
              partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR)))
          )
        )
        val result = await(
          sut.onSubmit("YOU")(
            request
              .withFormUrlEncodedBody(minimumEarningsKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "saving in keystore is successful as a partner" should {
      s"redirect to technical difficulties page" when {

        ageRanges.foreach { range =>
          s"${range.toString} has been previously selected and there is no data in keystore for PageObjects object for partner" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.successful(
                None
              )
            )

            val result = await(
              sut.onSubmit("BOTH")(
                request
                  .withFormUrlEncodedBody(minimumEarningsKey -> "123")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
          }

          /*s"redirect to tc/uc page" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(isPartner = true,
                  parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                  partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
                  parentEarnMoreThanNMW = Some(true),
                  partnerEarnMoreThanNMW = Some(true)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(isPartner = true,
                  parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                  partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
                  parentEarnMoreThanNMW = Some(true),
                  partnerEarnMoreThanNMW = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit("BOTH")(
                request
                  .withFormUrlEncodedBody(minimumEarningsKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
          }*/

        }
      }
    }

    "saving in keystore is successful as a parent and earnOverNMW = true" should {
      s"for each age range for a parent" when {

        ageRanges.foreach { range =>
          s"${range.toString} has been previously selected and there is no data in keystore for PageObjects object for parent" in {
            when(
              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
            ).thenReturn(
              Future.successful(
                None
              )
            )

            val result = await(
              sut.onSubmit("YOU")(
                request
                  .withFormUrlEncodedBody(maximumEarningsKey -> "YOU")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
          }

          // TODO: To be redone
         /* s"${range.toString} has been previously selected and there is data in keystore for parent" in {
            val po = buildPageObjects(isPartner = false,
              parentAgeRange = Some(range),
              parentEarnMoreThanNMW = Some(true))
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(po)
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(isPartner = false,
                  parentAgeRange = Some(range),
                  parentEarnMoreThanNMW = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit("YOU")(
                request
                  .withFormUrlEncodedBody(maximumEarningsKey -> "YOU")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            //redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
          }*/

          s"${range.toString} selected and there is data in keystore and both are in paid employment and getting min earnings as parent" in {
            val po = buildPageObjects(isPartner = true,
              parentAgeRange = Some(range),
              whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
              parentEarnMoreThanNMW = Some(true))
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(po)
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(isPartner = true,
                  parentAgeRange = Some(range),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                  parentEarnMoreThanNMW = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit("YOU")(
                request
                  .withFormUrlEncodedBody(minimumEarningsKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) should be (Some(routes.ChildCareBaseController.onTechnicalDifficulties().url))
          }

        }
      }
    }

  }
}
