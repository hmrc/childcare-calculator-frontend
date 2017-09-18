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
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsDoYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class WhichBenefitsDoYouGetControllerSpec  extends ControllersValidator with BeforeAndAfterEach {

  val sut = new WhichBenefitsDoYouGetController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(benefitsParentPath)
  validateUrl(benefitsPartnerPath)

  "Benefits Controller" when {

    "onPageLoad is called for parent" should {
      "load template successfully if there is some data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(false),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    partner = None
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe getBenefitsPath
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
                    partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe whoGetsBenefitsPath
      }

      "redirect to error page if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(None)
          )
        val result = await(sut.onPageLoad(false)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
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

    "onPageLoad is called for partner" should {
      "load template successfully if there is some data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    partner = Some(Claimant())
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe whoGetsBenefitsPath
      }

      "load template successfully if there is data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
                    partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
                  )
                )
              )
            )
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe OK
        result.body.contentType.get shouldBe "text/html; charset=utf-8"
        val content = Jsoup.parse(bodyOf(result))
        content.getElementById("back-button").attr("href") shouldBe benefitsParentPath
      }

      "load template successfully if there is no data in keystore" in {
        when(
          sut.keystore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
        ).thenReturn(
            Future.successful(None)
          )
        val result = await(sut.onPageLoad(true)(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe technicalDifficultiesPath
      }

      "redirect to error page if can't connect with keystore" in {
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

    "onSubmit is called for parent" should {

      "go to technical difficulties page" when {

        "unable to connect to keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onSubmit(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "unable to find data from the keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )
          val result = await(sut.onSubmit(false)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "unable to save data in keystore" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
              Future.successful(
                Some(
                  PageObjects(
                    livingWithPartner = Some(true),
                    household = Household(
                      location = LocationEnum.ENGLAND,
                      parent =  Claimant(benefits = Some(benefits)),
                      partner = Some(Claimant(benefits = Some(benefits)))
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

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "load the same template with status bad request" when {
        "there are errors" in {
          val benefits = Benefits(false, false, false, false)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = Some(benefits)))
                  )
                )
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "redirect to correct next page" when {
        s"parent has partner - go to ${benefitsPartnerPath}" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = Some(benefits)))
                  )
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                mock[PageObjects]
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe benefitsPartnerPath
        }

        s"parent has partner without benefits - go to ${whatsYourAgePath}/parent" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.YOU),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = None))
                  )
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                mock[PageObjects]
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe s"${whatsYourAgePath}/parent"
        }

        s"single parent - go to ${whatsYourAgePath}/parent" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = None
                  )
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                mock[PageObjects]
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe s"${whatsYourAgePath}/parent"
        }
      }

      "modify data correctly" when {
        "user has partner - shouldn't modify partner's benefits" in {
          val keystoreObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
              partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
            )
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                keystoreObject
              )
            )
          )

          val modifiedObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                modifiedObject
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(Benefits(true, false, false, false))
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "user has partner without benefits - shouldn't modify partner's benefits" in {
          val keystoreObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
              partner = Some(Claimant(benefits = None))
            )
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                keystoreObject
              )
            )
          )

          val modifiedObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = Some(Claimant(benefits = None))
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                modifiedObject
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(Benefits(true, false, false, false))
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "user without partner - shouldn't modify only parent benefits" in {
          val keystoreObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
              partner = None
            )
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                keystoreObject
              )
            )
          )

          val modifiedObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, false, false, false))),
              partner = None
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                modifiedObject
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(false, applicationMessagesApi).form.fill(Benefits(true, false, false, false))
          val result = await(sut.onSubmit(false)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }
      }
    }

    "onSubmit is called for partner" should {

      "go to technical difficulties page" when {

        "unable to connect to keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.failed(new RuntimeException)
          )
          val result = await(sut.onSubmit(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "unable to find data from the keystore" in {
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(None)
          )
          val result = await(sut.onSubmit(true)(request.withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }

        "unable to save data in keystore" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = Some(benefits)))
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

          val form = new WhichBenefitsDoYouGetForm(true, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(true)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe technicalDifficultiesPath
        }
      }

      "load the same template with status bad request" when {
        "there are errors" in {
          val benefits = Benefits(false, false, false, false)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = Some(benefits)))
                  )
                )
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(true, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(true)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe BAD_REQUEST
          result.body.contentType.get shouldBe "text/html; charset=utf-8"
        }
      }

      "redirect to correct next page" should {
        s"go to ${whatsYourAgePath}/partner" in {
          val benefits = Benefits(true, true, true, true)
          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                PageObjects(
                  livingWithPartner = Some(true),
                  whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.PARTNER),
                  household = Household(
                    location = LocationEnum.ENGLAND,
                    parent =  Claimant(benefits = Some(benefits)),
                    partner = Some(Claimant(benefits = Some(benefits)))
                  )
                )
              )
            )
          )

          when(
            sut.keystore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                mock[PageObjects]
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(true, applicationMessagesApi).form.fill(benefits)
          val result = await(sut.onSubmit(true)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") shouldBe s"${whatsYourAgePath}/partner"
        }
      }

      "modify data correctly" when {
        "couple - shouldn't modify parent's benefits" in {
          val keystoreObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
              partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
            )
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                keystoreObject
              )
            )
          )

          val modifiedObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = Some(Benefits(true, true, true, true))),
              partner = Some(Claimant(benefits = Some(Benefits(true, false, false, false))))
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                modifiedObject
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(true, applicationMessagesApi).form.fill(Benefits(true, false, false, false))
          val result = await(sut.onSubmit(true)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }

        "user has parent without benefits - shouldn't modify parent's benefits" in {
          val keystoreObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = None),
              partner = Some(Claimant(benefits = Some(Benefits(true, true, true, true))))
            )
          )

          when(
            sut.keystore.fetch[PageObjects]()(any[HeaderCarrier],any[Reads[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                keystoreObject
              )
            )
          )

          val modifiedObject = PageObjects(
            livingWithPartner = Some(true),
            household = Household(
              location = LocationEnum.ENGLAND,
              parent =  Claimant(benefits = None),
              partner = Some(Claimant(benefits = Some(Benefits(true, false, false, false))))
            )
          )

          when(
            sut.keystore.cache[PageObjects](org.mockito.Matchers.eq(modifiedObject))(any[HeaderCarrier],any[Format[PageObjects]])
          ).thenReturn(
            Future.successful(
              Some(
                modifiedObject
              )
            )
          )

          val form = new WhichBenefitsDoYouGetForm(true, applicationMessagesApi).form.fill(Benefits(true, false, false, false))
          val result = await(sut.onSubmit(true)(request.withFormUrlEncodedBody(form.data.toSeq: _*).withSession(validSession)))
          status(result) shouldBe SEE_OTHER
          result.header.headers("Location") should not be technicalDifficultiesPath
        }
      }
    }
  }
}
