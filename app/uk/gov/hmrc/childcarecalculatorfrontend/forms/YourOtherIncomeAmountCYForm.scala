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

import play.api.data.Form
import play.api.data.Forms.*
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.config.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters.DecimalFormatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*

import javax.inject.{Inject, Singleton}

@Singleton
class YourOtherIncomeAmountCYForm @Inject() (appConfig: FrontendAppConfig) extends FormErrorHelper {

  private def yourOtherIncomeAmountCYFormatter(
      missingErrorKey: String,
      invalidValueErrorKey: String
  ): Formatter[BigDecimal] =
    DecimalFormatter(missingErrorKey, invalidValueErrorKey).withRange(
      minValue = appConfig.minIncome,
      maxValue = appConfig.maxIncome,
      tooLowErrorKey = invalidValueErrorKey,
      tooHighErrorKey = invalidValueErrorKey
    )

  def apply(
      missingErrorKey: String = parentOtherIncomeRequiredErrorKey,
      invalidValueErrorKey: String = parentOtherIncomeInvalidErrorKey
  ): Form[BigDecimal] =
    Form(single("value" -> of(yourOtherIncomeAmountCYFormatter(missingErrorKey, invalidValueErrorKey))))

}
