/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours

import org.scalatest.{Assertion, OptionValues}
import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.FormSpec
import org.scalatest.Matchers.convertToAnyShouldWrapper

trait FormBehaviours extends FormSpec with OptionValues {

  val validData: Map[String, String]

  val maxValue: BigDecimal = 999999

  val minValue: BigDecimal = 1

  val form: Form[_]

  private def minimumValue(field: String): Assertion = {
    val data = validData + (field -> (minValue - 1).toString())
    val expectedError = error(field, s"$field.error.invalid")
    checkForError(form, data, expectedError)
  }

  def questionForm[A](expectedResult: A) = {
    "bind valid values correctly" in {
      val boundForm = form.bind(validData)
      boundForm.get shouldBe expectedResult
    }
  }

  def formWithOptionalTextFields(fields: String*) = {
    for (field <- fields) {
      s"bind when $field is omitted" in {
        val data = validData - field
        val boundForm = form.bind(data)
        boundForm.errors.isEmpty shouldBe true
      }
    }
  }

  def formWithMandatoryTextFields(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is omitted" in {
        val data = validData - field
        val expectedError = error(field, "error.required")
        checkForError(form, data, expectedError)
      }

      s"fail to bind when $field is blank" in {
        val data = validData + (field -> "")
        val expectedError = error(field, s"$field.error.required")
        checkForError(form, data, expectedError)
      }
    }
  }

  def formWithMandatoryTextFieldWithErrorMsgs(field: String,
                                              errorKeyRequired: String = "error.required",
                                              errorKeyBlank: String = "error.blank") = {

      s"fail to bind when $field is omitted" in {
        val data = validData - field
        val expectedError = error(field, errorKeyRequired)
        checkForError(form, data, expectedError)
      }

      s"fail to bind when $field is blank" in {
        val data = validData + (field -> "")
        val expectedError = error(field, errorKeyBlank)
        checkForError(form, data, expectedError)
      }

  }

  def formWithMandatoryNumberFields(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is omitted" in {
        val data = validData - field
        val expectedError = error(field, "error.required")
        checkForError(form, data, expectedError)
      }

      s"fail to bind when $field is blank" in {
        val data = validData + (field -> "")
        val expectedError = error(field, "error.number")
        checkForError(form, data, expectedError)
      }
    }
  }

  def formWithConditionallyMandatoryField(booleanField: String, field: String) = {
    s"bind when $booleanField is false and $field is omitted" in {
      val data = validData + (booleanField -> "false") - field
      val boundForm = form.bind(data)
      boundForm.errors.isEmpty shouldBe true
    }

    s"fail to bind when $booleanField is true and $field is omitted" in {
      val data = validData + (booleanField -> "true") - field
      val expectedError = error(field, "error.required")
      checkForError(form, data, expectedError)
    }
  }

  def formWithBooleans(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is omitted" in {
        val data = validData - field
        val expectedError = error(field, "error.boolean")
        checkForError(form, data, expectedError)
      }

      s"fail to bind when $field is invalid" in {
        val data = validData + (field -> "invalid value")
        val expectedError = error(field, "error.boolean")
        checkForError(form, data, expectedError)
      }
    }
  }

  def formWithOptionField(field: String, validValues: String*) = {
    formWithOptionFieldError(field, "error.required", validValues:_*)
  }

  def formWithOptionFieldError(formError: FormError, validValues: String*): Unit = {
    for (validValue <- validValues) {
      s"bind when ${formError.key} is set to $validValue" in {
        val data = validData + (formError.key -> validValue)
        val boundForm = form.bind(data)
        boundForm.errors.isEmpty shouldBe true
      }
    }

    s"fail to bind when ${formError.key} is omitted" in {
      val data = validData - formError.key
      checkForError(form, data, Seq(formError))
    }

    s"fail to bind when ${formError.key} is invalid" in {
      val data = validData + (formError.key -> "invalid value")
      val expectedError = error(formError.key, "error.unknown")
      checkForError(form, data, expectedError)
    }
  }

  def formWithOptionFieldError(field: String, errorMessage: String, validValues: String*): Unit = {
    formWithOptionFieldError(FormError(field, errorMessage), validValues: _*)
  }

  def formWithDateField(field: String) = {
    s"fail to bind when $field day is omitted" in {
      val data = validData - s"$field.day"
      val expectedError = error(s"$field.day", "error.date.day_blank")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field day is 0" in {
      val data = validData + (s"$field.day" -> "0")
      val expectedError = error(s"$field.day", "error.date.day_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field day is greater than 31" in {
      val data = validData + (s"$field.day" -> "32")
      val expectedError = error(s"$field.day", "error.date.day_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field day is negative" in {
      val data = validData + (s"$field.day" -> "-1")
      val expectedError = error(s"$field.day", "error.date.day_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field day is non-numeric" in {
      val data = validData + (s"$field.day" -> "invalid")
      val expectedError = error(s"$field.day", "error.date.day_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field month is omitted" in {
      val data = validData - s"$field.month"
      val expectedError = error(s"$field.month", "error.date.month_blank")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field month is 0" in {
      val data = validData + (s"$field.month" -> "0")
      val expectedError = error(s"$field.month", "error.date.month_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field month is greater than 12" in {
      val data = validData + (s"$field.month" -> "13")
      val expectedError = error(s"$field.month", "error.date.month_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field month is negative" in {
      val data = validData + (s"$field.month" -> "-1")
      val expectedError = error(s"$field.month", "error.date.month_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field month is non-numeric" in {
      val data = validData + (s"$field.month" -> "invalid")
      val expectedError = error(s"$field.month", "error.date.month_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field year is omitted" in {
      val data = validData - s"$field.year"
      val expectedError = error(s"$field.year", "error.date.year_blank")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field year is 0" in {
      val data = validData + (s"$field.year" -> "0")
      val expectedError = error(s"$field.year", "error.date.year_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field year is greater than 2050" in {
      val data = validData + (s"$field.year" -> "2051")
      val expectedError = error(s"$field.year", "error.date.year_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field year is negative" in {
      val data = validData + (s"$field.year" -> "-1")
      val expectedError = error(s"$field.year", "error.date.year_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when $field year is non-numeric" in {
      val data = validData + (s"$field.year" -> "invalid")
      val expectedError = error(s"$field.year", "error.date.year_invalid")
      checkForError(form, data, expectedError)
    }

    s"fail to bind when the $field is invalid" in {
      val data = validData + (s"$field.day" -> "30") + (s"$field.month" -> "2")
      val expectedError = error("dateOfBirth", "error.invalid_date")
      checkForError(form, data, expectedError)
    }

  }

  def formWithDecimalField(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is not a decimal" in {
        val data = validData + (field -> "invalid")
        val expectedError = error(field, s"$field.error.invalid")
        checkForError(form, data, expectedError)
      }
    }
  }

  def formWithMinimumValue(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is less than minimum value" in {
        minimumValue(field)
      }
    }
  }

  def formWithInRange(fields: String*) = {
    for (field <- fields) {
      s"fail to bind when $field is less than minimum value" in {
        minimumValue(field)
      }

      s"fail to bind when $field is greater than maximum value" in {
        val data = validData + (field -> (maxValue+1).toString())
        val expectedError = error(field, s"$field.error.invalid")
        checkForError(form, data, expectedError)
      }
    }
  }
}
