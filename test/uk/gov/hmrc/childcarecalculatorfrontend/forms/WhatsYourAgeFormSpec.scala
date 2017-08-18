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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AgeRangeEnum, LocationEnum}
import uk.gov.hmrc.play.test.UnitSpec

class WhatsYourAgeFormSpec extends UnitSpec with FakeCCApplication {

  "WhatsYourAgeForm" should {
    "accept valid value" when {
      AgeRangeEnum.values.foreach { ageRange => {
        val ageRangeValue = ageRange.toString
        s"${ageRangeValue} is selected" in {
          val result = new WhatsYourAgeForm(applicationMessagesApi).form.bind(Map(
            whatsYourAgeKey -> ageRangeValue
          ))
          result.hasErrors shouldBe false
          result.value.get.get shouldBe ageRangeValue
        }
      }
      }
    }

    "throw error" when {
      val invalidValues = List("", "abcd", "123")
//TODO sort for partner
      invalidValues.foreach { invalidValue =>
        s"'${invalidValue}' is selected" in {
          val result = new WhatsYourAgeForm(applicationMessagesApi).form.bind(Map(
            whatsYourAgeKey -> invalidValue
          ))
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages("whats.your.age.radio.not.selected.error.parent")
          result.errors.head.message should not be "whats.your.age.radio.not.selected.error.parent"
          result.value shouldBe None
        }
      }
    }

  }

}
