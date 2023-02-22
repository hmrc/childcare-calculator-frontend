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

import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class HowMuchPartnerPayPensionFormSpec extends FormSpec {

  val errorKeyBlank = howMuchPartnerPayPensionRequiredErrorKey
  val errorKeyInvalid = howMuchPartnerPayPensionInvalidErrorKey

  "HowMuchPartnerPayPension Form" must {

    "bind positive numbers" in {
      val form = HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal numbers up to the threshold of 9999.99" in {
      val form = HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "9999.99"))
      form.get shouldBe 9999.99
    }

    "fail to bind numbers below the threshold of 1" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "0.9"), expectedError)
    }

    "fail to bind numbers above the threshold of 9999.99" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "10000"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(HowMuchPartnerPayPensionForm(errorKeyBlank, errorKeyInvalid), emptyForm, expectedError)
    }
  }
}
