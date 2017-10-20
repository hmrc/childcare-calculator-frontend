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

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.OtherIncomeAmountPY

@Singleton
class OtherIncomeAmountPYForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  val minValue: Double = appConfig.minIncome
  val maxValue: Double = appConfig.maxIncome

  def apply(): Form[OtherIncomeAmountPY] = Form(
    mapping(
      "parentOtherIncomeAmountPY" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("parentOtherIncomeAmountPY.required"),
        validateDecimalInRange("parentOtherIncomeAmountPY.invalid", minValue, maxValue))),
      "partnerOtherIncomeAmountPY" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("partnerOtherIncomeAmountPY.required"),
        validateDecimalInRange("partnerOtherIncomeAmountPY.invalid", minValue, maxValue)))
    )(OtherIncomeAmountPY.apply)(OtherIncomeAmountPY.unapply)
  )
}
