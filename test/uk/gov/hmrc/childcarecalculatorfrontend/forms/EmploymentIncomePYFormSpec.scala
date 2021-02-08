/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomePY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class EmploymentIncomePYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "parentEmploymentIncomePY" -> "1",
    "partnerEmploymentIncomePY" -> "2"
  )

  val form = new EmploymentIncomePYForm(frontendAppConfig).apply()


  "EmploymentIncomePY form" must {
    behave like questionForm(EmploymentIncomePY(1, 2))

    behave like formWithMandatoryTextFieldWithErrorMsgs("parentEmploymentIncomePY",
      "parentEmploymentIncomePY.error.required", "parentEmploymentIncomePY.error.required")

    behave like formWithMandatoryTextFieldWithErrorMsgs("partnerEmploymentIncomePY",
      "partnerEmploymentIncomePY.error.required", "partnerEmploymentIncomePY.error.required")

    "not bind when either value is above the threshold of 999999.99" in {
      val expectedErrors =
        error("parentEmploymentIncomePY", parentEmploymentIncomePYInvalidErrorKey) ++
          error("partnerEmploymentIncomePY", partnerEmploymentIncomePYInvalidErrorKey)

      val data = Map(
        "parentEmploymentIncomePY" -> "1000000.0",
        "partnerEmploymentIncomePY" -> "1000000.0"
      )

      checkForError(form, data, expectedErrors)
    }
  }
}
