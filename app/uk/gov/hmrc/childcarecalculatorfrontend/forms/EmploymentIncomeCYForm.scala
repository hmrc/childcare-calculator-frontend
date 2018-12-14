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

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._


class EmploymentIncomeCYForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  val minValue: Double = appConfig.minEmploymentIncome
  val maxValue: Double = appConfig.maxEmploymentIncome

  def apply(): Form[EmploymentIncomeCY] = Form(
    mapping(
      "parentEmploymentIncomeCY" ->
        decimal("parentEmploymentIncomeCY.error.blank", parentEmploymentIncomeBlankErrorKey)
          .verifying(minimumValue[BigDecimal](minValue, parentEmploymentIncomeInvalidErrorKey))
          .verifying(maximumValue[BigDecimal](maxValue, parentEmploymentIncomeInvalidErrorKey)),
      "partnerEmploymentIncomeCY" ->
        decimal("partnerEmploymentIncomeCY.error.blank", partnerEmploymentIncomeBlankErrorKey)
          .verifying(minimumValue[BigDecimal](minValue, partnerEmploymentIncomeInvalidErrorKey))
          .verifying(maximumValue[BigDecimal](maxValue, partnerEmploymentIncomeInvalidErrorKey)))
    (EmploymentIncomeCY.apply)(EmploymentIncomeCY.unapply))
}