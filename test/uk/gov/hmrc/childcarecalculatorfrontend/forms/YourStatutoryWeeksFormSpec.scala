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

import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum.MATERNITY

class YourStatutoryWeeksFormSpec extends FormSpec {

  val statutoryType = MATERNITY

  val errorInvalid = error(
    "value",
    "yourStatutoryWeeks.error.invalid",
    frontendAppConfig.minNoWeeksStatPay,
    frontendAppConfig.maxNoWeeksMaternityPay,
    statutoryType.toString)

  val errorRequired = error(
    "value",
    "yourStatutoryWeeks.error.required",
    frontendAppConfig.minNoWeeksStatPay,
    frontendAppConfig.maxNoWeeksMaternityPay,
    statutoryType.toString)

  private def form = new YourStatutoryWeeksForm(frontendAppConfig).apply(statutoryType, statutoryType.toString)

  "YourStatutoryWeeks Form" must {

    "bind numbers within range" in {
      form.bind(Map("value" -> "1")).get shouldBe 1
    }

    "fail to bind numbers below the threshold" in {
      checkForError(form, Map("value" -> "0"), errorInvalid)
    }

    "fail to bind numbers above the threshold" in {
      val invalidNumberOfWeeks = frontendAppConfig.maxNoWeeksMaternityPay + 1
      checkForError(form, Map("value" -> invalidNumberOfWeeks.toString), errorInvalid)
    }

    "fail to bind non-numerics" in {
      checkForError(form, Map("value" -> "not a number"), errorInvalid)
    }

    "fail to bind a blank value" in {
      checkForError(form, Map("value" -> ""), errorRequired)
    }

    "fail to bind when value is omitted" in {
      checkForError(form, emptyForm, errorRequired)
    }

    "fail to bind decimal numbers" in {
      checkForError(form, Map("value" -> "1.23"), errorInvalid)
    }
  }
}
