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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.models.BothBenefitsIncomePY

object BothBenefitsIncomePYForm extends FormErrorHelper {

  private val parentIncomeInvalidKey = "parentBenefitsIncomePY.invalid"
  private val partnerIncomeInvalidKey = "partnerBenefitsIncomePY.invalid"

  def apply(): Form[BothBenefitsIncomePY] = Form(
    mapping(
      "parentBenefitsIncomePY" ->
        decimal("parentBenefitsIncomePY.required", parentIncomeInvalidKey)
          .verifying(inRange[BigDecimal](1, 9999.99, parentIncomeInvalidKey)),
      "partnerBenefitsIncomePY" ->
        decimal("partnerBenefitsIncomePY.required", partnerIncomeInvalidKey)
          .verifying(inRange[BigDecimal](1, 9999.99, partnerIncomeInvalidKey))
    )(BothBenefitsIncomePY.apply)(BothBenefitsIncomePY.unapply)
  )
}
