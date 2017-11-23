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

import play.api.data.FormError
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency.WEEKLY

class ExpectedChildcareCostsFormSpec extends FormSpec {

  val errorKeyBlank = "expectedChildcareCosts.error"
  val errorKeyInvalid = "expectedChildcareCosts.invalid"
  val form = ExpectedChildcareCostsForm(WEEKLY)

  "ExpectedChildcareCosts Form" must {

    "bind positive numbers" in {
      val result = form.bind(Map("value" -> "1.0"))
      result.get shouldEqual 1.0
    }

    "bind positive decimal number" in {
      val result = form.bind(Map("value" -> "10.80"))
      result.get shouldEqual 10.80
    }

    "bind the upper bound" in {
      val result = form.bind(Map("value" -> "999.99"))
      result.get shouldEqual 999.99
    }

    "fail to bind 0" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "0"), expectedError)
    }

    "fail to bind a number less than 1" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "0.9"), expectedError)
    }

    "fail to bind a number greater than 999.99" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "1000"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "-1"), expectedError)
    }

    "fail to bind a decimal with more than 2 decimal places" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "10.888"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(form, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = Seq(FormError("value", errorKeyBlank, Seq(WEEKLY)))
      checkForError(form, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = Seq(FormError("value", errorKeyBlank, Seq(WEEKLY)))
      checkForError(form, emptyForm, expectedError)
    }
  }
}
