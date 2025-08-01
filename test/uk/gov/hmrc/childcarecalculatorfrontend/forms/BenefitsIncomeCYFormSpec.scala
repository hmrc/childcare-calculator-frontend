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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsIncomeCY

class BenefitsIncomeCYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "parentBenefitsIncome"  -> "1",
    "partnerBenefitsIncome" -> "2"
  )

  val form = BenefitsIncomeCYForm()

  "BenefitsIncomeCY form" must {
    behave.like(questionForm(BenefitsIncomeCY(1, 2)))

    behave.like(
      formWithMandatoryTextFieldWithErrorMsgs(
        "parentBenefitsIncome",
        "parentBenefitsIncome.error.required",
        "parentBenefitsIncome.error.required"
      )
    )

    behave.like(
      formWithMandatoryTextFieldWithErrorMsgs(
        "partnerBenefitsIncome",
        "partnerBenefitsIncome.error.required",
        "partnerBenefitsIncome.error.required"
      )
    )

    "not bind when either value is above the threshold of 9999.99" in {
      val expectedErrors =
        error("parentBenefitsIncome", "parentBenefitsIncome.error.invalid") ++
          error("partnerBenefitsIncome", "partnerBenefitsIncome.error.invalid")

      val data = Map(
        "parentBenefitsIncome"  -> "10000.0",
        "partnerBenefitsIncome" -> "10000.0"
      )

      checkForError(BenefitsIncomeCYForm(), data, expectedErrors)
    }
  }

}
