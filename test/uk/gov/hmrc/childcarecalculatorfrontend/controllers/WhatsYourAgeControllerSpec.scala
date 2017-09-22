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
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum.AgeRangeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AgeRangeEnum, _}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class WhatsYourAgeControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhatsYourAgeController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(whatsYourAgePath + "/parent")
  validateUrl(whatsYourAgePath + "/partner")

  def buildPageObjects(isPartner: Boolean, ageRange: Option[AgeRangeEnum]): PageObjects = {
    val claimant = Claimant(ageRange = ageRange)
    if (isPartner) {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant, partner = Some(claimant)))
    } else {
      PageObjects(household = Household(location = LocationEnum.ENGLAND, parent = claimant))
    }
  }

  "WhatsYourAgeController" when {

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

      "load template successfully if there is data in keystore for parent and define correctly backURL" when {
        "parent gets benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(benefits = Some(Benefits()))
                )
              ))
            )
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe benefitsParentPath
        }

        "partner gets benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  partner = Some(Claimant(benefits = Some(Benefits())))
                ),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)
              ))
            )
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe benefitsPartnerPath
        }

        "both get benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  partner = Some(Claimant(benefits = Some(Benefits())))
                ),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
              ))
            )
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe benefitsPartnerPath
        }

        "none get benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = false, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                getBenefits = Some(false)
              ))
            )
          )
          val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe getBenefitsPath
        }
      }

      "load template successfully if there is data in keystore for partner and display correct backurl" when {
        "parent gets benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(benefits = Some(Benefits())),
                  partner = Some(Claimant())
                )
              ))
            )
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe benefitsParentPath
        }

        "both gets benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true, ageRange = None).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(benefits = Some(Benefits())),
                  partner = Some(Claimant(ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR), benefits = Some(Benefits())))
                )
              ))
            )
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe benefitsPartnerPath
        }

        "both are in paid employment" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(benefits = Some(Benefits())),
                  partner = Some(Claimant(benefits = Some(Benefits())))
                ),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                getBenefits = Some(false)
              ))
            )
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe s"${whatsYourAgePath}/parent"
        }

        "partner paid employed and gets no benefits" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(isPartner = true, ageRange = Some(AgeRangeEnum.TWENTYONETOTWENTYFOUR)).copy(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(),
                  partner = Some(Claimant())
                ),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                getBenefits = Some(false)
              ))
            )
          )
          val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
          status(result) shouldBe OK
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
          val content = Jsoup.parse(bodyOf(result))
          content.getElementById("back-button").attr("href") shouldBe getBenefitsPath
        }
      }

    }
  }

  "connecting with keystore fails" should {

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

  "onSubmit is called" when {

    "there are errors" should {
      "load same template and return BAD_REQUEST as a partner" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(true, ageRange = None))
          )
        )
        val result = await(
          sut.onSubmit(true)(
            request
              .withFormUrlEncodedBody(whatsYourAgeKey -> "")
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
            Some(buildPageObjects(false, ageRange = None))
          )
        )
        val result = await(
          sut.onSubmit(false)(
            request
              .withFormUrlEncodedBody(whatsYourAgeKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "saving in keystore is successful as a partner" should {
      s"go to ${whatsYourAgePath}/partner" when {
        val ageRanges = List(
          AgeRangeEnum.UNDER18,
          AgeRangeEnum.EIGHTEENTOTWENTY,
          AgeRangeEnum.TWENTYONETOTWENTYFOUR,
          AgeRangeEnum.OVERTWENTYFOUR
        )
        ageRanges.foreach { range =>
          s"${range.toString} is selected if there is no data in keystore for PageObjects object for partner" in {
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
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          s"${range.toString} is selected if there is data in keystore for PageObjects object for partner" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(true, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(true, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
              )
            )

            val result = await(
              sut.onSubmit(true)(
                request
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe minimumEarningsPartnerPath
          }

          s"${range.toString} is selected if there is data in keystore when both are in paid employment object for partner" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(true, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(true, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
              )
            )

            val result = await(
              sut.onSubmit(true)(
                request
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe minimumEarningsParentPath
          }

        }
      }
    }

    "saving in keystore is successful as a parent" should {
      s"go to ${whatsYourAgePath}/parent" when {
        val ageRanges = List(
          AgeRangeEnum.UNDER18,
          AgeRangeEnum.EIGHTEENTOTWENTY,
          AgeRangeEnum.TWENTYONETOTWENTYFOUR,
          AgeRangeEnum.OVERTWENTYFOUR
        )
        ageRanges.foreach { range =>
          s"${range.toString} is selected if there is no data in keystore for PageObjects object for parent" in {
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
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe technicalDifficultiesPath
          }

          s"${range.toString} is selected if there is data in keystore for PageObjects object for parent" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)))
              )
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe minimumEarningsParentPath
          }

          s"${range.toString} is selected if there is data in keystore when partner is in paid employment for parent" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER)))
              )
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe minimumEarningsPartnerPath
          }

          s"${range.toString} is selected if there is data in keystore for PageObjects and both are in paid employment object for parent" in {
            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)).copy(whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)))
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(buildPageObjects(false, Some(range)))
              )
            )

            val result = await(
              sut.onSubmit(false)(
                request
                  .withFormUrlEncodedBody(whatsYourAgeKey -> range.toString)
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe whatsYourAgePath + "/partner"
          }
        }
      }
    }
  }
}
