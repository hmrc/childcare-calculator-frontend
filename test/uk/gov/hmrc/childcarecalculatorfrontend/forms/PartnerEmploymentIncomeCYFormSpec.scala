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

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class PartnerEmploymentIncomeCYFormSpec extends FormSpec {

  val errorKeyBlank   = partnerEmploymentIncomeBlankErrorKey
  val errorKeyInvalid = partnerEmploymentIncomeInvalidErrorKey

  val partnerEmpIncomeCYForm: Form[BigDecimal] = new PartnerEmploymentIncomeCYForm(frontendAppConfig).apply()

  "PartnerEmploymentIncomeCY Form" must {

    "bind positive numbers" in {
      val form = partnerEmpIncomeCYForm.bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number up to the threshold of 999999.99" in {
      val form = partnerEmpIncomeCYForm.bind(Map("value" -> "999999.99"))
      form.get shouldBe 999999.99
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(partnerEmpIncomeCYForm, Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(partnerEmpIncomeCYForm, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(partnerEmpIncomeCYForm, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(partnerEmpIncomeCYForm, emptyForm, expectedError)
    }

    "fail to bind numbers below the threshold" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(partnerEmpIncomeCYForm, Map("value" -> "0.9"), expectedError)
    }
  }

}
