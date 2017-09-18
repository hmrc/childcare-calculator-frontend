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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.test.Helpers._
import scala.concurrent.Future
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

class WhoGetsBenefitsControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhoGetsBenefitsController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(whoGetsBenefitsPath)

  "calling onPageLoad" should {

    "load template successfully" when {
      "valid session is used and there is no data for benefits in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(),
                  partner = Some(Claimant())
                )
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }

      "valid session is used and there is some data for benefits in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(benefits = Some(Benefits())),
                  partner = Some(Claimant(benefits = Some(Benefits())))
                )
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    s"redirect to error page ${technicalDifficultiesPath}" when {
      "partner object doesn't exist in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(),
                  partner = None
                )
              )
            )
          )
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(sut.onPageLoad(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }
  }

  "calling onSubmit" should {

    "load template with status BAD_REQUEST" when {
      "user submits invalid data" in {
        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(whoGetsBeneftsKey -> "")
              .withSession(validSession)
          )
        )
        status(result) shouldBe BAD_REQUEST
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
      }
    }

    "redirect to correct next page" when {
      val testCases = Table(
        ("Selected", "Next page"),
        (YouPartnerBothEnum.YOU.toString, benefitsParentPath),
        (YouPartnerBothEnum.PARTNER.toString, benefitsPartnerPath),
        (YouPartnerBothEnum.BOTH.toString, benefitsParentPath)
      )

      forAll(testCases) { case (selection, nextPage) =>
        s"user selects '${selection}' should go to '${nextPage}'" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(),
                    partner = Some(Claimant())
                  )
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(mock[PageObjects]))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> selection)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe nextPage
        }
      }
    }

    "modify keystore object correctly" when {

      "user selects 'YOU'" should {

        "keep parent's benefits if only they're set" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = Some(
                Claimant(benefits = None)
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
            Future.successful(Some(keystoreObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "clear partner's benefits and create parent's ones if only partner's exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(),
                    partner = Some(
                      Claimant(benefits = Some(Benefits()))
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = Some(Benefits())),
              partner = Some(
                Claimant(benefits = None)
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "clear partner's benefits and keep parent's ones if both exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(benefits = Some(Benefits(true, false, false, false))),
                    partner = Some(
                      Claimant(benefits = Some(Benefits()))
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = Some(
                Claimant(benefits = None)
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }
      }

      "user selects 'PARTNER'" should {

        "keep partner's benefits if only they're set" in {
          val keystoreObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = None),
              partner = Some(
                Claimant(benefits = Some(Benefits(true, false, false, false)))
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
            Future.successful(Some(keystoreObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.PARTNER.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "clear parent's benefits and create partner's ones if only parent's exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(benefits = Some(Benefits())),
                    partner = Some(
                      Claimant()
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = None),
              partner = Some(
                Claimant(benefits = Some(Benefits()))
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.PARTNER.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "clear parent's benefits and keep partner's ones if both exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(benefits = Some(Benefits())),
                    partner = Some(
                      Claimant(benefits = Some(Benefits(true, false, false, false)))
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = None),
              partner = Some(
                Claimant(benefits = Some(Benefits(true, false, false, false)))
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.PARTNER.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }
      }

      "user selects 'BOTH'" should {

        "create parent's and partner's benefits if none exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(benefits = None),
                    partner = Some(
                      Claimant(benefits = None)
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = Some(Benefits())),
              partner = Some(
                Claimant(benefits = Some(Benefits()))
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.BOTH.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "keep parent's and partner's benefits if both exists" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent = Claimant(benefits = Some(Benefits(true, false, false, false))),
                    partner = Some(
                      Claimant(benefits = Some(Benefits(true, false, false, false)))
                    )
                  )
                )
              )
            )
          )

          val modifiedPageObject = PageObjects(
            household = Household(
              location = LocationEnum.ENGLAND,
              parent = Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = Some(
                Claimant(benefits = Some(Benefits(true, false, false, false)))
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedPageObject))(any[HeaderCarrier], any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(Some(modifiedPageObject))
          )

          val result = await(
            sut.onSubmit(
              request
                .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.BOTH.toString)
                .withSession(validSession)
            )
          )
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }
      }
    }

    s"redirects to error page (${technicalDifficultiesPath})" when {
      "partner object doesn't exist in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(),
                  partner = None
                )
              )
            )
          )
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore while fetching data" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.failed(new RuntimeException)
        )

        val result = await(
          sut.onSubmit(
            request
              .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "can't connect to keystore while caching data" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(
                  location = LocationEnum.ENGLAND,
                  parent = Claimant(),
                  partner = Some(Claimant())
                )
              )
            )
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
              .withFormUrlEncodedBody(whoGetsBeneftsKey -> YouPartnerBothEnum.YOU.toString)
              .withSession(validSession)
          )
        )
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }
    }
  }

}
