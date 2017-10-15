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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Singleton

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers


@Singleton
class MaximumEarningsNavigation {

  val taxOUniversalCreditPage: Call = routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
  val partnerMaxEarningsPage: Call = routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
  val sessionExpiredPage: Call = routes.SessionExpiredController.onPageLoad()

  def yourMaximumEarningsRoute(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner
    val partnerMinEarnings = answers.partnerMinimumEarnings
    val yourMaxEarnings = answers.yourMaximumEarnings


    (hasPartner, partnerMinEarnings, yourMaxEarnings) match {
      case (Some(false), _, Some(_)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case (Some(true), Some(true), Some(_)) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      case (Some(true), Some (false), Some(_)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case (Some(true), _, Some(_)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  def partnerMaximumEarningsRoute(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner
    val partnerMaxEarnings = answers.partnerMaximumEarnings


    (hasPartner,partnerMaxEarnings) match {
      case (Some(true), Some(_)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }
}
