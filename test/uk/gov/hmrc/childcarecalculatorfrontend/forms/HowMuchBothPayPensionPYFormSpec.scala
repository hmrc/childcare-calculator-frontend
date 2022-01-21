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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.HowMuchBothPayPensionPY

class HowMuchBothPayPensionPYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "howMuchYouPayPensionPY" -> "1",
    "howMuchPartnerPayPensionPY" -> "2"
  )

  val form = HowMuchBothPayPensionPYForm()

  "HowMuchBothPayPensionPY form" must {
    behave like questionForm(HowMuchBothPayPensionPY(1, 2))

    behave like formWithMandatoryTextFieldWithErrorMsgs("howMuchYouPayPensionPY",
      "howMuchYouPayPensionPY.error.required", "howMuchYouPayPensionPY.error.required")

    behave like formWithMandatoryTextFieldWithErrorMsgs("howMuchPartnerPayPensionPY",
      "howMuchPartnerPayPensionPY.error.required", "howMuchPartnerPayPensionPY.error.required")

    "fail to bind if either value is below the threshold of 1" in {
      val data = Map(
        "howMuchYouPayPensionPY" -> "0.9",
        "howMuchPartnerPayPensionPY" -> "0.9"
      )

      val expectedErrors =
        error("howMuchYouPayPensionPY", "howMuchYouPayPensionPY.error.invalid") ++
          error("howMuchPartnerPayPensionPY", "howMuchPartnerPayPensionPY.error.invalid")

      checkForError(form, data, expectedErrors)
    }

    "fail to bind if either value is above the threshold of 9999.99" in {
      val data = Map(
        "howMuchYouPayPensionPY" -> "10000",
        "howMuchPartnerPayPensionPY" -> "10000"
      )

      val expectedErrors =
        error("howMuchYouPayPensionPY", "howMuchYouPayPensionPY.error.invalid") ++
          error("howMuchPartnerPayPensionPY", "howMuchPartnerPayPensionPY.error.invalid")

      checkForError(form, data, expectedErrors)
    }
  }
}
