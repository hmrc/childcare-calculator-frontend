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

class YourStatutoryWeeksFormSpec extends FormSpec {

  val statutoryType = "maternity"
  val errorInvalid = error("value", "yourStatutoryWeeks.invalid", statutoryType)
  val errorRequired = error("value", "yourStatutoryWeeks.required", statutoryType)

  "YourStatutoryWeeks Form" must {

    "bind numbers within range" in {
      val form = YourStatutoryWeeksForm(statutoryType).bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "fail to bind numbers below the threshold" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), Map("value" -> "0"), errorInvalid)
    }

    "fail to bind numbers above the threshold" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), Map("value" -> "49"), errorInvalid)
    }

    "fail to bind non-numerics" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), Map("value" -> "not a number"), errorInvalid)
    }

    "fail to bind a blank value" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), Map("value" -> ""), errorRequired)
    }

    "fail to bind when value is omitted" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), emptyForm, errorRequired)
    }

    "fail to bind decimal numbers" in {
      checkForError(YourStatutoryWeeksForm(statutoryType), Map("value" -> "1.23"), errorInvalid)
    }
  }
}
