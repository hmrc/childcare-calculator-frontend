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

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService

import scala.concurrent.Future

class FreeHoursResultsControllerSpec extends ControllersValidator {

  val sut = new FreeHoursResultsController(applicationMessagesApi){
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  validateUrl(freeHoursResultsPath, List(GET))

  "onPageLoad" should {
    "load successfully FreeHoursResults template" in {
      when(
        sut.keystore.fetch[Household]()(any(),any())
      ).thenReturn(
        Future.successful(
          Some(
            Household(
              location = LocationEnum.ENGLAND,
              childAgedThreeOrFour = Some(true)
            )
          )
        )
      )
      val result = await(sut.onPageLoad(request.withSession(validSession)))
      status(result) shouldBe OK
      result.body.contentType.get shouldBe "text/html; charset=utf-8"
    }
  }
}
