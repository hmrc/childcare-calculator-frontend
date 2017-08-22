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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class LivingWithPartnerControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new LivingWithPartnerController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(livingWithPartnerPath, List(GET))

  def buildPageObjects(livingWithPartner: Option[Boolean],
                     childAgedThreeOrFour: Option[Boolean] = None): PageObjects = PageObjects(household = Household(
    location = LocationEnum.ENGLAND),
    childAgedThreeOrFour = childAgedThreeOrFour,
    livingWithPartner = livingWithPartner
  )

  "LivingWithPartnerController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(None))
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(Some(true)))
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if there is no data keystore for household object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(None)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      s"contain back url to ${expectChildcareCostsPath} if there is no data about child aged 3 or 4" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObjects(
                livingWithPartner = None,
                childAgedThreeOrFour = None
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe expectChildcareCostsPath
      }

      s"contain back url to ${freeHoursInfoPath} if there is data about child aged 3 or 4" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(),any())
        ).thenReturn(
          Future.successful(
            Some(
              buildPageObjects(
                livingWithPartner = None,
                childAgedThreeOrFour = Some(true)
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe freeHoursInfoPath
      }
    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST" in {

          when(
            sut.keystore.fetch[PageObjects]()(any(), any())
          ).thenReturn(
            Future.successful(
              Some(buildPageObjects(None))
            )
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(childAgedTwoKey -> "")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "saving in keystore is successful" should {
        s"redirect to next page ${paidEmploymentPath}" when {
          "user selects 'YES'" in {
            val keystoreObject: PageObjects = buildPageObjects(livingWithPartner = None)

            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(keystoreObject)
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(keystoreObject)
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(livingWithPartnerKey -> "true")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe paidEmploymentPath
          }

          "user selects 'NO'" in {
            val keystoreObject: PageObjects = buildPageObjects(livingWithPartner = None)

            when(
              sut.keystore.fetch[PageObjects]()(any(), any())
            ).thenReturn(
              Future.successful(
                Some(keystoreObject)
              )
            )

            when(
              sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
            ).thenReturn(
              Future.successful(
                Some(keystoreObject)
              )
            )

            val result = await(
              sut.onSubmit(
                request
                  .withFormUrlEncodedBody(livingWithPartnerKey -> "false")
                  .withSession(validSession)
              )
            )
            status(result) shouldBe SEE_OTHER
            result.header.headers("Location") shouldBe paidEmploymentPath
          }

        }

        "modify correctly data in keystore" when {
          "user selects YES" when {
            "previous selection was same (don't modify data in keystore)" in {
              val keystoreObject: PageObjects = PageObjects(
                livingWithPartner = Some(true),
                whichOfYouInPaidEmployment = None,
                paidOrSelfEmployed = Some(true),
                getVouchers = Some(YesNoUnsureEnum.YES),
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = Some(YesNoUnsureEnum.YES)
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any(), any())
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
                    .withFormUrlEncodedBody(livingWithPartnerKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") should not be technicalDifficultiesPath
            }

            "previous selection was 'No' (create parent, delete information for following pages)" in {
              val keystoreObject: PageObjects = PageObjects(
                livingWithPartner = Some(false),
                whichOfYouInPaidEmployment = None,
                paidOrSelfEmployed = Some(true),
                getVouchers = Some(YesNoUnsureEnum.YES),
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = Some(YesNoUnsureEnum.YES)
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any(), any())
              ).thenReturn(
                Future.successful(
                  Some(keystoreObject)
                )
              )

              val modifiedObject: PageObjects = PageObjects(
                livingWithPartner = Some(true),
                whichOfYouInPaidEmployment = None,
                paidOrSelfEmployed = None,
                getVouchers = None,
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = None
                  ),
                  partner = Some(Claimant())
                )
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
                    .withFormUrlEncodedBody(livingWithPartnerKey -> "true")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") should not be technicalDifficultiesPath
            }
          }

          "user selects NO" when {
            "previous selection was same (don't modify data in keystore)" in {
              val keystoreObject: PageObjects = PageObjects(
                livingWithPartner = Some(false),
                whichOfYouInPaidEmployment = None,
                paidOrSelfEmployed = Some(true),
                getVouchers = Some(YesNoUnsureEnum.YES),
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = Some(YesNoUnsureEnum.YES)
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any(), any())
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
                    .withFormUrlEncodedBody(livingWithPartnerKey -> "false")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") should not be technicalDifficultiesPath
            }

            "previous selection was 'Yes' (create parent, delete information for following pages)" in {
              val keystoreObject: PageObjects = PageObjects(
                livingWithPartner = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
                paidOrSelfEmployed = Some(true),
                getVouchers = Some(YesNoUnsureEnum.NOTSURE),
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = Some(YesNoUnsureEnum.NOTSURE)
                  ),
                  partner = Some(
                    Claimant(
                      hours = Some(37.5),
                      escVouchers = Some(YesNoUnsureEnum.NOTSURE)
                    )
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any(), any())
              ).thenReturn(
                Future.successful(
                  Some(keystoreObject)
                )
              )

              val modifiedObject: PageObjects = PageObjects(
                livingWithPartner = Some(false),
                whichOfYouInPaidEmployment = None,
                paidOrSelfEmployed = None,
                getVouchers = None,
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(
                    hours = Some(37.5),
                    escVouchers = None
                  ),
                  partner = None
                )
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
                    .withFormUrlEncodedBody(livingWithPartnerKey -> "false")
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") should not be technicalDifficultiesPath
            }
          }
        }
      }
    }

    "connecting with keystore fails" should {
      s"redirect to ${technicalDifficultiesPath}" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            Some(buildPageObjects(Some(true), None))
          )
        )

        when(
          sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(livingWithPartnerKey -> "false")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

    "there is no data in keystore for PageObjects object" should {
      s"redirect to ${technicalDifficultiesPath}" in {
        when(
          sut.keystore.fetch[PageObjects]()(any(), any())
        ).thenReturn(
          Future.successful(
            None
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(livingWithPartnerKey -> "false")
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }

  }

}
