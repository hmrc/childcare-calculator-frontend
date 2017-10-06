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

import play.api.libs.json.{Format, Reads}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.Future
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{LocationEnum, Household, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService


/**
 * Created by user on 18/09/17.
 */
class MaxFreeHoursInfoControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val maxFreeHoursInfoController = new MaxFreeHoursInfoController(applicationMessagesApi)


  "MaxFreeHoursInfoController" should {
    "load successfully template " in {

      val result = await(maxFreeHoursInfoController.onPageLoad(request.withSession(validSession)))
      status(result) shouldBe OK
      result.body.contentType.get shouldBe "text/html; charset=utf-8"
    }


    "redirect successfully to next page" when {
      "next button is clicked then go to how many children page" in {
        val result = await(maxFreeHoursInfoController.onSubmit(request.withSession(validSession)))
        status(result) shouldBe SEE_OTHER
        result.header.headers("Location") shouldBe howManyChildrenPath
      }
    }
  }

  validateUrl(maxFreeHoursInfoPath)

}
