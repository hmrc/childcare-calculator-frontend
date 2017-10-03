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

class ParentWorkHoursFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyInvalid = "invalid character"

  "ParentWorkHours Form" must {

    "bind zero" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "0"), expectedError)
    }

    "bind positive whole number" in {
      val form = ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "12"))
      form.get shouldBe 12
    }

    "bind decimal numbers with one decimal place" in {
      val form = ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "12.6"))
      form.get shouldBe 12.6
    }

    "fail to bind decimal numbers more than 99.5" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "99.6"), expectedError)
    }

    "fail to bind decimal numbers with two decimal places" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "12.67"), expectedError)
    }

    "fail to bind decimal numbers with more than 2 digits with one decimal places" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "124.6"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(ParentWorkHoursForm(errorKeyBlank, errorKeyInvalid), emptyForm, expectedError)
    }
  }
}
