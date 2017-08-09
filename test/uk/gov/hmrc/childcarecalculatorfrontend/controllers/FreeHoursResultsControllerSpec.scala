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
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.ControllersValidator
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService

import scala.concurrent.Future

class FreeHoursResultsControllerSpec extends ControllersValidator with BeforeAndAfterEach {

  val sut = new FreeHoursResultsController(applicationMessagesApi){
    override val keystore: KeystoreService = mock[KeystoreService]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(sut.keystore)
  }

  validateUrl(freeHoursResultsPath, List(GET))

  "load successfully template when data in keystore" in {
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

  "load successfully template when no data in keystore" in {
    when(
      sut.keystore.fetch[Household]()(any(),any())
    ).thenReturn(
      Future.successful(
        Some(
          Household(
            location = LocationEnum.ENGLAND,
            childAgedThreeOrFour = None
          )
        )
      )
    )
    val result = await(sut.onPageLoad(request.withSession(validSession)))
    status(result) shouldBe OK
    result.body.contentType.get shouldBe "text/html; charset=utf-8"
  }

  "redirect to error page if there is no data keystore for household object" in {
    when(
      sut.keystore.fetch[Household]()(any(), any())
    ).thenReturn(
      Future.successful(None)
    )

    val result = await(sut.onPageLoad(request.withSession(validSession)))
    status(result) shouldBe SEE_OTHER
    result.header.headers("Location") shouldBe technicalDifficultiesPath
  }

  "redirect to error page if can't connect with keystore" in {
    when(
      sut.keystore.fetch[Household]()(any(), any())
    ).thenReturn(
      Future.failed(new RuntimeException)
    )

    val result = await(sut.onPageLoad(request.withSession(validSession)))
    status(result) shouldBe SEE_OTHER
    result.header.headers("Location") shouldBe technicalDifficultiesPath
  }

}
