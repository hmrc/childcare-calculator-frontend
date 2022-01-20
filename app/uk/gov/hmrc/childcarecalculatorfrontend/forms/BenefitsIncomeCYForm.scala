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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsIncomeCY

object BenefitsIncomeCYForm extends FormErrorHelper {

  private val parentIncomeInvalidKey = "parentBenefitsIncome.error.invalid"
  private val partnerIncomeInvalidKey = "partnerBenefitsIncome.error.invalid"

  def apply(): Form[BenefitsIncomeCY] = Form(
    mapping(
      "parentBenefitsIncome" ->
        decimal("parentBenefitsIncome.error.required", parentIncomeInvalidKey)
          .verifying(minimumValue[BigDecimal](1, parentIncomeInvalidKey))
          .verifying(maximumValue[BigDecimal](9999.99, parentIncomeInvalidKey)),
      "partnerBenefitsIncome" ->
        decimal("partnerBenefitsIncome.error.required", partnerIncomeInvalidKey)
          .verifying(minimumValue[BigDecimal](1, partnerIncomeInvalidKey))
          .verifying(maximumValue[BigDecimal](9999.99, partnerIncomeInvalidKey))
    )(BenefitsIncomeCY.apply)(BenefitsIncomeCY.unapply)
  )
}
