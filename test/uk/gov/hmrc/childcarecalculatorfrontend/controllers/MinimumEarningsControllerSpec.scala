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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class MinimumEarningsControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new MinimumEarningsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(parentMinimumEarningsPath)
  validateUrl(partnerMinimumEarningsPath)

  def buildPageObjects(isPartner: Boolean,
                       parentAgeRange: Option[AgeRangeEnum] = None,
                       partnerAgeRange: Option[AgeRangeEnum] = None,
                       parentEarnBelowNMW: Option[Boolean] = None,
                       partnerEarnBelowNMW: Option[Boolean] = None
                      ): PageObjects = {
    val parent = Claimant(ageRange = parentAgeRange, minimumEarnings = Some(MinimumEarnings(earnMoreThanNMW = parentEarnBelowNMW)))
    val partner = Claimant(ageRange = partnerAgeRange, minimumEarnings = Some(MinimumEarnings(earnMoreThanNMW = partnerEarnBelowNMW)))

    if (isPartner) {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = parent, partner = Some(partner)))
    } else {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = parent))
    }
  }

  val ageRanges = List(
    AgeRangeEnum.UNDER18,
    AgeRangeEnum.EIGHTEENTOTWENTY,
    AgeRangeEnum.TWENTYONETOTWENTYFOUR,
    AgeRangeEnum.OVERTWENTYFOUR
  )

  "MinimumEarningsController" when {

    "onPageLoad is called" should {

      "redirect successfully if there is no data in keystore if parent" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect successfully if there is no data in keystore if partner" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            None
          )
        )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "load template successfully if there is data in keystore for parent and define correctly backURL" when {
        "parent is 21 to 24" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false, parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR), parentEarnBelowNMW = Some(true)))
            )
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe whatsYourAgePath + "/parent"
        }

        "redirect to error page if can't connect with keystore if parent" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "load template successfully if there is data in keystore for partner and display correct backurl" when {
        "partner is 21 to 24" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true,
                parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
                partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
                parentEarnBelowNMW = Some(true),
                partnerEarnBelowNMW = Some(true)))
            )
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe whatsYourAgePath + "/partner"
        }

        "redirect to error page if can't connect with keystore if partner" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(true, None))
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )

          val result = await(
            sut.onSubmit(true)(
              request
                .withFormUrlEncodedBody(whatsYourAgeKey -> AgeRangeEnum.OVERTWENTYFOUR.toString)
                .withSession(validSession)
            )
          )
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
            Some(buildPageObjects(true, parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR), partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR)))
          )
        )
        val result = await(
          sut.onSubmit(true)(
            request
              .withFormUrlEncodedBody(minimumEarningKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load same template and return BAD_REQUEST with a parent" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(false, parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR), partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR)))
          )
        )
        val result = await(
          sut.onSubmit(false)(
            request
              .withFormUrlEncodedBody(minimumEarningKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "saving in keystore is successful as a partner" should {
      s"for each age range for a partner" when {

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
              sut.onSubmit(true)(
                request
                  .withFormUrlEncodedBody(minimumEarningKey -> "123")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

//          s"${range.toString} has been previously selected and there is data in keystore for PageObjects.partner.minimumEarnings.earnMoreThanNMW object for partner" in {
//            when(
//              sut.keystore.fetch[PageObjects]()(any(), any())
//            ).thenReturn(
//              Future.successful(
//
//                Some(buildPageObjects(isPartner = true,
//                  parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
//                  partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
//                  parentEarnBelowNMW = Some(true),
//                  partnerEarnBelowNMW = Some(true)))
//              )
//            )
//
//            when(
//              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
//            ).thenReturn(
//              Future.successful(
//                Some(buildPageObjects(isPartner = true,
//                  parentAgeRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR),
//                  partnerAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
//                  parentEarnBelowNMW = Some(true),
//                  partnerEarnBelowNMW = Some(true)))
//              )
//            )
//
//            val result = await(
//              sut.onSubmit(true)(
//                request
//                  .withFormUrlEncodedBody(minimumEarningKey -> "true")
//                  .withSession(validSession)
//              )
//            )
//            status(result) shouldBe SEE_OTHER
//            result.header.headers("Location") shouldBe underConstrctionPath
//          }
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
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(minimumEarningKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          s"${range.toString} has been previously selected and there is data in keystore for PageObjects.parent.minimumEarnings.earnMoreThanNMW object for parent" in {
            val po = buildPageObjects(isPartner = false,
              parentAgeRange = Some(range),
              parentEarnBelowNMW = Some(true))
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
                  parentEarnBelowNMW = Some(true)))
              )
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(minimumEarningKey -> "false")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }
        }
      }
    }

    "saving in keystore is successful as a parent and earnOverNMW = false" should {
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
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(minimumEarningKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          s"${range.toString} has been previously selected and there is data in keystore for PageObjects.parent.minimumEarnings.earnMoreThanNMW object for parent" in {
            val po = buildPageObjects(isPartner = false,
              parentAgeRange = Some(range),
              parentEarnBelowNMW = Some(false))
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
                  parentEarnBelowNMW = Some(false)))
              )
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(minimumEarningKey -> "false")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe underConstrctionPath
          }
        }
      }
    }

//
//    "saving in keystore is successful as a parent" should {
//      s"go to ${whatsYourAgePath}/parent" when {
//        val ageRanges = List(
//          AgeRangeEnum.UNDER18,
//          AgeRangeEnum.EIGHTEENTOTWENTY,
//          AgeRangeEnum.TWENTYONETOTWENTYFOUR,
//          AgeRangeEnum.OVERTWENTYFOUR
//        )
//        ageRanges.foreach { range =>
//          s"${range.toString} is selected if there is no data in keystore for PageObjects object for parent" in {
//            when(
//              sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
//            ).thenReturn(
//              Future.successful(
//                None
//              )
//            )
//
//            val result = await(
//              sut.onSubmit(false)(
//                request
//                  .withFormUrlEncodedBody(minimumEarningKey -> range.toString)
//                  .withSession(validSession)
//              )
//            )
//            status(result) shouldBe SEE_OTHER
//            result.header.headers("Location") shouldBe technicalDifficultiesPath
//          }
//
//          s"${range.toString} is selected if there is data in keystore for PageObjects object for parent" in {
//            when(
//              sut.keystore.fetch[PageObjects]()(any(), any())
//            ).thenReturn(
//              Future.successful(
//                Some(buildPageObjects(false, Some(range)))
//              )
//            )
//
//            when(
//              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
//            ).thenReturn(
//              Future.successful(
//                Some(buildPageObjects(false, Some(range)))
//              )
//            )
//
//            val result = await(
//              sut.onSubmit(false)(
//                request
//                  .withFormUrlEncodedBody(minimumEarningKey -> range.toString)
//                  .withSession(validSession)
//              )
//            )
//            status(result) shouldBe SEE_OTHER
//            result.header.headers("Location") shouldBe underConstrctionPath
//          }
//
//          s"${range.toString} is selected if there is data in keystore for PageObjects and both are in paid employment object for parent" in {
//            when(
//              sut.keystore.fetch[PageObjects]()(any(), any())
//            ).thenReturn(
//              Future.successful(
//                Some(buildPageObjects(false, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
//              )
//            )
//
//            when(
//              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
//            ).thenReturn(
//              Future.successful(
//                Some(buildPageObjects(false, Some(range)))
//              )
//            )
//
//            val result = await(
//              sut.onSubmit(false)(
//                request
//                  .withFormUrlEncodedBody(minimumEarningKey -> range.toString)
//                  .withSession(validSession)
//              )
//            )
//            status(result) shouldBe SEE_OTHER
//            result.header.headers("Location") shouldBe partnerMinimumEarningsPath
//          }
//        }
//      }
//    }
  }
}
