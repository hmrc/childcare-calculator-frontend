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


object EmploymentIncomeCYForm extends FormErrorHelper {

  def apply(): Form[EmploymentIncomeCY] = Form(
    mapping(
      "parentEmploymentIncome" ->
        decimal("parentEmploymentIncomeCY.invalid", parentEmploymentIncomeInvalidErrorKey)
        .verifying(minimumValue[BigDecimal](1, parentEmploymentIncomeInvalidErrorKey))
        .verifying(maximumValue[BigDecimal](999999.99, parentEmploymentIncomeInvalidErrorKey)),
      "partnerEmploymentIncome" ->
        decimal("partnerEmploymentIncomeCY.invalid", partnerEmploymentIncomeInvalidErrorKey)
          .verifying(minimumValue[BigDecimal](1, partnerEmploymentIncomeInvalidErrorKey))
          .verifying(maximumValue[BigDecimal](999999.99, partnerEmploymentIncomeInvalidErrorKey))
    ) (EmploymentIncomeCY.apply)(EmploymentIncomeCY.unapply)
  )
}