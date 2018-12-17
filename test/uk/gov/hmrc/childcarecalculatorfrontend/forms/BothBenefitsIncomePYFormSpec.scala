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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.BothBenefitsIncomePY

class BothBenefitsIncomePYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "parentBenefitsIncomePY" -> "1",
    "partnerBenefitsIncomePY" -> "2"
  )

  val form = BothBenefitsIncomePYForm()

  "BothBenefitsIncomePY form" must {
    behave like questionForm(BothBenefitsIncomePY(1, 2))

    behave like formWithMandatoryTextFieldWithErrorMsgs("parentBenefitsIncomePY",
      "parentBenefitsIncomePY.error.required", "parentBenefitsIncomePY.error.required")

    behave like formWithMandatoryTextFieldWithErrorMsgs("partnerBenefitsIncomePY",
      "partnerBenefitsIncomePY.error.required", "partnerBenefitsIncomePY.error.required")
  }

  "fail to bind if either value is below the threshold of 1" in {
    val data = Map(
      "parentBenefitsIncomePY" -> "0.9",
      "partnerBenefitsIncomePY" -> "0.9"
    )

    val expectedErrors =
      error("parentBenefitsIncomePY", "parentBenefitsIncomePY.error.invalid") ++
        error("partnerBenefitsIncomePY", "partnerBenefitsIncomePY.error.invalid")

    checkForError(form, data, expectedErrors)
  }

  "fail to bind if either value is above the threshold of 9999.99" in {
    val data = Map(
      "parentBenefitsIncomePY" -> "10000",
      "partnerBenefitsIncomePY" -> "10000"
    )

    val expectedErrors =
      error("parentBenefitsIncomePY", "parentBenefitsIncomePY.error.invalid") ++
        error("partnerBenefitsIncomePY", "partnerBenefitsIncomePY.error.invalid")

    checkForError(form, data, expectedErrors)
  }
}
