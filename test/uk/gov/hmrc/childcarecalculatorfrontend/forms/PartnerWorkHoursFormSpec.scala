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

import play.api.data.Form

class PartnerWorkHoursFormSpec extends FormSpec {

  val partnerWorkHoursForm: Form[BigDecimal] = new ParentWorkHoursForm(frontendAppConfig).apply()
  val errorKeyBlank = "workHours.blank"
  val errorKeyInvalid = "workHours.invalid"

  "ParentWorkHours Form" must {

    "bind positive whole number" in {
      val form = partnerWorkHoursForm.bind(Map("value" -> "12"))
      form.get shouldBe 12
    }

    "bind decimal numbers with one decimal place" in {
      val form = partnerWorkHoursForm.bind(Map("value" -> "12.6"))
      form.get shouldBe 12.6
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(partnerWorkHoursForm, emptyForm, expectedError)
    }

    Seq("0", "", "0.6", "99.6").foreach { value =>
      s"fail to bind value $value" in {
        val expectedError = error("value", errorKeyBlank)
        checkForError(partnerWorkHoursForm, Map("value" -> value), expectedError)
      }
    }

    Seq("12.67", "124.6", "-1", "not a number").foreach { value =>
      s"fail to bind decimal numbers with invalid value $value" in {
        val expectedError = error("value", errorKeyInvalid)
        checkForError(partnerWorkHoursForm, Map("value" -> value), expectedError)
      }
    }
  }
}
