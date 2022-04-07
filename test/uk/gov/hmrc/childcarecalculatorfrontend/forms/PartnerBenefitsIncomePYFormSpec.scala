/*
 * Copyright 2022 HM Revenue & Customs
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

class PartnerBenefitsIncomePYFormSpec extends FormSpec {

  val errorKeyBlank = partnerBenefitsIncomePYRequiredErrorKey
  val errorKeyInvalid = partnerBenefitsIncomePYInvalidErrorKey

  "PartnerBenefitsIncomePY Form" must {

    "bind positive numbers" in {
      val form = PartnerBenefitsIncomePYForm().bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number" in {
      val form = PartnerBenefitsIncomePYForm().bind(Map("value" -> "10.80"))
      form.get shouldBe 10.80
    }

    "fail to bind numbers below the threshold of 1" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerBenefitsIncomePYForm(), Map("value" -> "0.9"), expectedError)
    }

    "fail to bind numbers above the threshold of 9999.99" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerBenefitsIncomePYForm(), Map("value" -> "10000"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerBenefitsIncomePYForm(), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerBenefitsIncomePYForm(), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PartnerBenefitsIncomePYForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PartnerBenefitsIncomePYForm(), emptyForm, expectedError)
    }

  }
}
