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
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomeCY

class EmploymentIncomeCYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "parentEmploymentIncomeCY" -> "1",
    "partnerEmploymentIncomeCY" -> "2"
  )

  override val maxValue: BigDecimal = 100000
  override val minValue: BigDecimal = 1

  val form = new EmploymentIncomeCYForm(frontendAppConfig).apply()

  "EmploymentIncomeCY form" must {
    behave like questionForm(EmploymentIncomeCY(1, 2))

    behave like formWithMandatoryTextFieldWithErrorMsgs("parentEmploymentIncomeCY",
      "parentEmploymentIncomeCY.blank", "parentEmploymentIncomeCY.blank")

    behave like formWithMandatoryTextFieldWithErrorMsgs("partnerEmploymentIncomeCY",
      "partnerEmploymentIncomeCY.blank", "partnerEmploymentIncomeCY.blank")

    behave like formWithDecimalField("parentEmploymentIncomeCY", "partnerEmploymentIncomeCY")

    behave like formWithInRange("parentEmploymentIncomeCY", "partnerEmploymentIncomeCY")
  }
}
