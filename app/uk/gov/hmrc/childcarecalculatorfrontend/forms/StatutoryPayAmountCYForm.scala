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

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayAmountCY

class StatutoryPayAmountCYForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  val minValue: Double = appConfig.minStatutoryPay
  val maxValue: Double = appConfig.maxStatutoryPay

  def apply(): Form[StatutoryPayAmountCY] = Form(
    mapping(
      "parentStatutoryPayAmount" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("parentStatutoryPayAmount.required"),
        validateDecimalInRange("parentStatutoryPayAmount.invalid", minValue, maxValue))),
      "partnerStatutoryPayAmount" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("partnerStatutoryPayAmount.required"),
        validateDecimalInRange("partnerStatutoryPayAmount.invalid", minValue, maxValue)))
    )(StatutoryPayAmountCY.apply)(StatutoryPayAmountCY.unapply)
  )
}
