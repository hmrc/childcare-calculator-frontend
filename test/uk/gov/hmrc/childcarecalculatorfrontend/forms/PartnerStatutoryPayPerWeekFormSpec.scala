/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class PartnerStatutoryPayPerWeekFormSpec extends FormSpec {

  val statutoryType = "maternity"
  val errorKeyRequired = "partnerStatutoryPayPerWeek.error.required"
  val errorKeyInvalid = "partnerStatutoryPayPerWeek.error.invalid"

  "PartnerStatutoryPayPerWeek Form" must {

    "bind positive integers in range" in {
      val form = PartnerStatutoryPayPerWeekForm(statutoryType).bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "bind positive decimals in range" in {
      val form = PartnerStatutoryPayPerWeekForm(statutoryType).bind(Map("value" -> "1.2"))
      form.get shouldBe 1.2
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), Map("value" -> "-1"), expectedError)
    }

    "fail to bind numbers below the threshold of 1" in {
      val expectedError = error("value", errorKeyInvalid, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), Map("value" -> "0.9"), expectedError)
    }

    "fail to bind numbers above the threshold of 99.99" in {
      val expectedError = error("value", errorKeyInvalid, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), Map("value" -> "100"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyRequired, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyRequired, statutoryType)
      checkForError(PartnerStatutoryPayPerWeekForm(statutoryType), emptyForm, expectedError)
    }

  }
}
