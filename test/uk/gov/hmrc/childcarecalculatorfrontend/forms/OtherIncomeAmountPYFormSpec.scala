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
import uk.gov.hmrc.childcarecalculatorfrontend.models.OtherIncomeAmountPY

class OtherIncomeAmountPYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "parentOtherIncomeAmountPY" -> "1",
    "partnerOtherIncomeAmountPY" -> "2"
  )

  override val maxValue: BigDecimal = 999999.99
  override val minValue: BigDecimal = 0

  val form = new OtherIncomeAmountPYForm(frontendAppConfig).apply()

  "OtherIncomeAmountPY form" must {
    behave like questionForm(OtherIncomeAmountPY("1", "2"))

    behave like formWithMandatoryTextFields("parentOtherIncomeAmountPY", "partnerOtherIncomeAmountPY")

    behave like formWithDecimalField("parentOtherIncomeAmountPY", "partnerOtherIncomeAmountPY")

    behave like formWithInRange("parentOtherIncomeAmountPY", "partnerOtherIncomeAmountPY")
  }
}
