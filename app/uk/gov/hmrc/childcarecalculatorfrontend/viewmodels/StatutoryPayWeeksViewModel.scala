/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.viewmodels

import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum._

class StatutoryPayWeeksViewModel (appConfig: FrontendAppConfig, statutoryType: StatutoryPayTypeEnum.Value)(implicit messages: Messages) {

  val maxWeeks = statutoryType match {
    case MATERNITY => appConfig.maxNoWeeksMaternityPay
    case PATERNITY => appConfig.maxNoWeeksPaternityPay
    case ADOPTION => appConfig.maxNoWeeksAdoptionPay
    case SHARED_PARENTAL => appConfig.maxNoWeeksSharedParentalPay
  }

  val statutoryTypeMessage: String = Messages(s"statutoryPayTypeLower.$statutoryType")

  val guidanceKey: String = statutoryType.toString
}
