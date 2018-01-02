/*
 * Copyright 2018 HM Revenue & Customs
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

class YourOtherIncomeAmountPYFormSpec extends FormSpec {

  val yourOtherIncomeAmountPYForm: Form[BigDecimal] = new YourOtherIncomeAmountPYForm(frontendAppConfig).apply()
  val errorKeyBlank = "parentOtherIncomeAmountPY.required"
  val errorKeyInvalid = "parentOtherIncomeAmountPY.invalid"

  "YourOtherIncomeAmountPY Form" must {

    "bind positive numbers" in {
      val form = yourOtherIncomeAmountPYForm.bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number" in {
      val form = yourOtherIncomeAmountPYForm.bind(Map("value" -> "10.80"))
      form.get shouldBe 10.80
    }

    Seq("0.9", "9999999.99", "10000000").foreach { value =>
      s"fail to bind number $value not within the range" in {
        val expectedError = error("value", errorKeyInvalid)
        checkForError(yourOtherIncomeAmountPYForm, Map("value" -> value), expectedError)
      }
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(yourOtherIncomeAmountPYForm, Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(yourOtherIncomeAmountPYForm, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(yourOtherIncomeAmountPYForm, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(yourOtherIncomeAmountPYForm, emptyForm, expectedError)
    }
  }
}
