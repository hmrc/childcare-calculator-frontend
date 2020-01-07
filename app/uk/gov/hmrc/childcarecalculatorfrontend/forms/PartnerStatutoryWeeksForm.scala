/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum.{ADOPTION, MATERNITY, PATERNITY, SHARED_PARENTAL}

class PartnerStatutoryWeeksForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  def apply(statutoryType: StatutoryPayTypeEnum.Value, statutoryTypeMessage: String): Form[Int] = {

    val maxWeeks = statutoryType match {
      case MATERNITY => appConfig.maxNoWeeksMaternityPay
      case PATERNITY => appConfig.maxNoWeeksPaternityPay
      case ADOPTION => appConfig.maxNoWeeksAdoptionPay
      case SHARED_PARENTAL => appConfig.maxNoWeeksSharedParentalPay
    }

    val minWeeks = appConfig.minNoWeeksStatPay

    Form(
      "value" ->
        int("partnerStatutoryWeeks.error.required", "partnerStatutoryWeeks.error.invalid", minWeeks, maxWeeks, statutoryTypeMessage)
          .verifying(inRange[Int](minWeeks, maxWeeks, "partnerStatutoryWeeks.error.invalid", minWeeks, maxWeeks, statutoryTypeMessage))
    )
  }
}
