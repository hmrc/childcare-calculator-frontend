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


class ParentWorkHoursFormSpec extends FormSpec {

  val parentWorkHoursForm: Form[BigDecimal] = new ParentWorkHoursForm(frontendAppConfig).apply()

  val errorKeyBlank = "workHours.blank"
  val errorKeyInvalid = "workHours.invalid"

  "ParentWorkHours Form" must {

    Seq("12", "000012").foreach{ value =>
      s"bind positive whole number $value" in {
        val form = parentWorkHoursForm.bind(Map("value" -> value))
        form.get shouldBe BigDecimal(value)
      }
    }

    Seq("12.6", "00012.8").foreach { value =>
      s"bind decimal numbers with one decimal place $value" in {
        val form = parentWorkHoursForm.bind(Map("value" -> value))
        form.get shouldBe BigDecimal(value)
      }
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(parentWorkHoursForm, emptyForm, expectedError)
    }

    Seq("0", "", "0.6", "99.6").foreach { value =>
      s"fail to bind value $value" in {
        val expectedError = error("value", errorKeyBlank)
        checkForError(parentWorkHoursForm, Map("value" -> value), expectedError)
      }
    }

    Seq("12.67", "124.6", "-1", "not a number", "$&,").foreach { value =>
      s"fail to bind decimal numbers with invalid value $value" in {
        val expectedError = error("value", errorKeyInvalid)
        checkForError(parentWorkHoursForm, Map("value" -> value), expectedError)
      }
    }
  }
}
