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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.HowMuchBothPayPension

class HowMuchBothPayPensionFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "howMuchYouPayPension" -> "1",
    "howMuchPartnerPayPension" -> "2"
  )

  val form = HowMuchBothPayPensionForm()

  "HowMuchBothPayPension form" must {
    behave like questionForm(HowMuchBothPayPension(1, 2))

    behave like formWithMandatoryTextFieldWithErrorMsgs("howMuchYouPayPension",
      "howMuchYouPayPension.required", "howMuchYouPayPension.required")

    behave like formWithMandatoryTextFieldWithErrorMsgs("howMuchPartnerPayPension",
      "howMuchPartnerPayPension.required", "howMuchPartnerPayPension.required")

    "not bind when either value is above the threshold of 9999.99" in {
      val expectedErrors =
        error("howMuchYouPayPension", "howMuchYouPayPension.invalid") ++
        error("howMuchPartnerPayPension", "howMuchPartnerPayPension.invalid")

      val data = Map(
        "howMuchYouPayPension" -> "10000.0",
        "howMuchPartnerPayPension" -> "10000.0"
      )

      checkForError(HowMuchBothPayPensionForm(), data, expectedErrors)
    }
  }
}
