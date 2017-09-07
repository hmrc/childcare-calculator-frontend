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
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{Format, Reads}
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.test.Helpers._
import scala.concurrent.Future

/**
 * Created by user on 01/09/17.
 */
class WhoGetsVouchersControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhoGetsVouchersController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(whoGetsVouchersPath)

  def buildPageObjects(youOrPartner: Option[YouPartnerBothEnum] = None): PageObjects = PageObjects(
    whoGetsVouchers = youOrPartner,
    getVouchers = Some(YesNoUnsureEnum.YES),
    expectChildcareCosts = Some(true),
    livingWithPartner = Some(true),
    paidOrSelfEmployed = Some(true),
    whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
    household = Household(
      location = LocationEnum.ENGLAND,
      parent = Claimant(),
      partner = Some(Claimant())
    )
  )

  "WhoGetsVouchersController" when {

    "onPageLoad is called" should {

      "load template successfully if there is no data in keystore for current page object" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(buildPageObjects(youOrPartner = None))
            )
          )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(buildPageObjects(youOrPartner = Some(YouPartnerBothEnum.YOU)))
            )
          )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "redirect to error page if can't connect with keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.failed(new RuntimeException)
          )
        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if there is no pageObject in keystore" in {
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
    }

    "onSubmit is called" when {

      "there are errors" should {
        "load same template and return BAD_REQUEST" in {

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
              Future.successful(
                Some(buildPageObjects(youOrPartner = None))
              )
            )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsVouchersKey -> "")
                .withSession(validSession)
            )
          )
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }

        "redirect to error page if there is no pageObject in keystore" in {
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
                .withFormUrlEncodedBody(whoGetsVouchersKey -> YouPartnerBothEnum.YOU.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "saving in keystore is successful" should {
        s"go to ${whoGetsBenefitsPath}" when {
          "user selects 'YOU'" should {
            "keep vouchers for parent but delete partner's" in {
              val initialObject: PageObjects = buildPageObjects(youOrPartner = Some(YouPartnerBothEnum.BOTH))
              val keystoreObject: PageObjects = initialObject.copy(
                household = initialObject.household.copy(
                  parent = initialObject.household.parent.copy(
                    escVouchers = Some(YesNoUnsureEnum.YES)
                  ),
                  partner = Some(
                    initialObject.household.partner.get.copy(
                      escVouchers = None
                    )
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                  Future.successful(
                    Some(keystoreObject)
                  )
                )

              val modifiedObject = keystoreObject.copy(
                household = keystoreObject.household.copy(
                  partner = Some(
                    keystoreObject.household.partner.get.copy(
                      escVouchers = None
                    )
                  )
                ),
                whoGetsVouchers = Some(YouPartnerBothEnum.YOU)
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
                    .withFormUrlEncodedBody(whoGetsVouchersKey -> YouPartnerBothEnum.YOU.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe getBenefitsPath
            }
          }
        }
        s"go to ${whoGetsBenefitsPath}" when {
          "user selects 'PARTNER'" should {
            "keep vouchers for partner but delete parents's" in {
              val initialObject: PageObjects = buildPageObjects(youOrPartner = Some(YouPartnerBothEnum.BOTH))
              val keystoreObject: PageObjects = initialObject.copy(
                household = initialObject.household.copy(
                  parent = initialObject.household.parent.copy(
                    escVouchers = None
                  ),
                  partner = Some(
                    initialObject.household.partner.get.copy(
                      escVouchers = Some(YesNoUnsureEnum.YES)
                    )
                  )
                )
              )

              when(
                sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
              ).thenReturn(
                  Future.successful(
                    Some(keystoreObject)
                  )
                )

              val modifiedObject = keystoreObject.copy(
                household = keystoreObject.household.copy(
                  parent = keystoreObject.household.parent.copy(
                    escVouchers = None
                  )
                ),
                whoGetsVouchers = Some(YouPartnerBothEnum.PARTNER)
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
                    .withFormUrlEncodedBody(whoGetsVouchersKey -> YouPartnerBothEnum.PARTNER.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe getBenefitsPath
            }
          }

          "user selects 'BOTH'" should {
            "keep vouchers for parent and partner" in {
              val initialObject: PageObjects = buildPageObjects(youOrPartner = Some(YouPartnerBothEnum.BOTH))
              val keystoreObject: PageObjects = initialObject.copy(
                household = initialObject.household.copy(
                  parent = initialObject.household.parent.copy(
                    escVouchers = Some(YesNoUnsureEnum.YES)
                  ),
                  partner = Some(
                    initialObject.household.partner.get.copy(
                      escVouchers = Some(YesNoUnsureEnum.YES)
                    )
                  )
                )
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
                    .withFormUrlEncodedBody(whoGetsVouchersKey -> YouPartnerBothEnum.BOTH.toString)
                    .withSession(validSession)
                )
              )
              status(result) shouldBe SEE_OTHER
              result.header.headers("Location") shouldBe getBenefitsPath
            }
          }
        }
      }

      "connecting with keystore fails" should {
        s"redirect to ${technicalDifficultiesPath}" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
              Future.successful(
                Some(buildPageObjects(youOrPartner = Some(YouPartnerBothEnum.YOU)))
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
                .withFormUrlEncodedBody(whoGetsVouchersKey -> YouPartnerBothEnum.YOU.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

    }
  }
}