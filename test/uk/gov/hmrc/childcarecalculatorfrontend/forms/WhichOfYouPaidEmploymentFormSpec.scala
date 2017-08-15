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
import uk.gov.hmrc.play.test.UnitSpec

class WhichOfYouPaidEmploymentFormSpec extends UnitSpec with FakeCCApplication {

  "WhichOfYouPaidEmploymentForm" should {
    "accept valid value" when {
      val whichOfYouPaidEmployment = List("YOU", "PARTNER", "BOTH")
      whichOfYouPaidEmployment.foreach { who => {

        s"${who} is selected" in {
          val result = new WhichOfYouPaidEmploymentForm(applicationMessagesApi).form.bind(Map(
            whichOfYouInPaidEmploymentKey -> who
          ))
          result.hasErrors shouldBe false
          result.value.get.get shouldBe who
        }
      }
      }
    }

    "throw error" when {
      val invalidValues = List("", "abcd", "123")

      invalidValues.foreach { invalidValue =>
        s"'${invalidValue}' is selected" in {
          val result = new WhichOfYouPaidEmploymentForm(applicationMessagesApi).form.bind(Map(
            whichOfYouInPaidEmploymentKey -> invalidValue
          ))
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages("which.of.you.paid.employment.not.selected.error")
          result.errors.head.message should not be "which.of.you.paid.employment.not.selected.error"
          result.value shouldBe None
        }
      }
    }

  }

}
