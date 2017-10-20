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

class PartnerStatutoryPayAmountPYFormSpec extends FormSpec {
  val statutoryForm = new PartnerStatutoryPayAmountPYForm(frontendAppConfig).apply()
  val errorKeyBlank = "partnerStatutoryPayAmountPY.required"
  val errorKeyInvalid = "partnerStatutoryPayAmountPY.invalid"

  "PartnerStatutoryPayAmountPY Form" must {

    "bind positive numbers" in {
      val form = statutoryForm.bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number" in {
      val form = statutoryForm.bind(Map("value" -> "10.80"))
      form.get shouldBe 10.80
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(statutoryForm, Map("value" -> "-1"), expectedError)
    }

    Seq("0.0", "100").foreach { value =>
      s"fail to bind number $value not within the range" in {
        val expectedError = error("value", errorKeyInvalid)
        checkForError(statutoryForm, Map("value" -> value), expectedError)
      }
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(statutoryForm, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(statutoryForm, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(statutoryForm, emptyForm, expectedError)
    }

  }
}
