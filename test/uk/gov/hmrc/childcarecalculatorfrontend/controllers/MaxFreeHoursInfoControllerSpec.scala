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
import play.api.test.Helpers._
import scala.concurrent.Future
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, LocationEnum, Household, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService


/**
 * Created by user on 18/09/17.
 */
class MaxFreeHoursInfoControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new FreeHoursInfoController(applicationMessagesApi) {
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(maxFreeHoursInfoPath)

  "MaxFreeHoursInfoController" should {
    "load successfully template when data in keystore" in {
      when(
        sut.keystore.fetch[PageObjects]()(any(),any())
      ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                expectChildcareCosts = Some(true),
                childAgedThreeOrFour = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
              )
            )
          )
        )
      val result = await(sut.onPageLoad(request.withSession(validSession)))
      status(result) shouldBe OK
      result.body.contentType.get shouldBe "text/html; charset=utf-8"
    }

    "load successfully template when no data in keystore" in {
      when(
        sut.keystore.fetch[PageObjects]()(any(),any())
      ).thenReturn(
          Future.successful(
            Some(
              PageObjects(
                household = Household(location = LocationEnum.ENGLAND),
                expectChildcareCosts = Some(true),
                childAgedThreeOrFour = Some(true),
                whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
              )
            )
          )
        )
      val result = await(sut.onPageLoad(request.withSession(validSession)))
      status(result) shouldBe OK
      result.body.contentType.get shouldBe "text/html; charset=utf-8"
    }

    "redirect to error page if there is no data keystore for pageObjects object" in {
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

    "redirect successfully to next and back page" when {
      "next button is clicked then go to how many children page" in {
        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe livingWithPartnerPath
      }

      "back link is clicked then go to tc/us page" in {
        val result = await(sut.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe creditsPath
      }
    }
  }

}
