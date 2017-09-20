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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.models.CreditsEnum
import uk.gov.hmrc.play.test.UnitSpec

class CreditsFormSpec extends UnitSpec with FakeCCApplication {

  "CreditsForm" should {

    "accept valid value" when {
      CreditsEnum.values.foreach { credits => {
        val creditsValue = credits.toString
        s"${creditsValue} is selected" in {
          val result = new CreditsForm(applicationMessagesApi).form.bind(Map(
            creditsKey -> creditsValue
          ))
          result.hasErrors shouldBe false
          result.value.get.get shouldBe creditsValue
        }
      }}
    }

    "throw error" when {
      val invalidValues = List("", "abcd", "123")

      invalidValues.foreach { invalidValue =>
        s"'${invalidValue}' is selected" in {
          val result = new CreditsForm(applicationMessagesApi).form.bind(Map(
            creditsKey -> invalidValue
          ))
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages("credits.radio.not.selected.error")
          result.errors.head.message should not be "credits.radio.not.selected.error"
          result.value shouldBe None
        }
      }
    }

  }

}
