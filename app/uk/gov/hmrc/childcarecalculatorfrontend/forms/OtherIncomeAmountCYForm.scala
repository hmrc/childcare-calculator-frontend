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

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.OtherIncomeAmountCY

@Singleton
class OtherIncomeAmountCYForm @Inject() (appConfig: FrontendAppConfig) extends FormErrorHelper {

  val minValue: Double = appConfig.minIncome
  val maxValue: Double = appConfig.maxIncome

  private val parentIncomeInvalidKey  = "parentOtherIncome.error.invalid"
  private val partnerIncomeInvalidKey = "partnerOtherIncome.error.invalid"

  def apply(): Form[OtherIncomeAmountCY] = Form(
    mapping(
      "parentOtherIncome" ->
        decimal("parentOtherIncome.error.required", parentIncomeInvalidKey)
          .verifying(minimumValue[BigDecimal](minValue, parentIncomeInvalidKey))
          .verifying(maximumValue[BigDecimal](maxValue, parentIncomeInvalidKey)),
      "partnerOtherIncome" ->
        decimal("partnerOtherIncome.error.required", partnerIncomeInvalidKey)
          .verifying(minimumValue[BigDecimal](minValue, partnerIncomeInvalidKey))
          .verifying(maximumValue[BigDecimal](maxValue, partnerIncomeInvalidKey))
    )(OtherIncomeAmountCY.apply)(OtherIncomeAmountCY.unapply)
  )

}
