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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, SelfEmployedOrApprenticeOrNeitherEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers


@Singleton
class SelfEmployedOrApprenticeNavigation {

  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString
  val SelfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString

  def defineInPaidEmployment(whoIsInPaidEmp: Option[String]): String = {
    whoIsInPaidEmp match {
      case Some(You) => You
      case Some(Partner) => Partner
      case Some(Both) => Both
      case _ => You
    }
  }

  def areYouSelfEmployedOrApprenticeRoute(answers: UserAnswers) = {
    val whoIsInPaidEmp = answers.whoIsInPaidEmployment
    val partnerMinEarnings = answers.partnerMinimumEarnings
    val areYouSelfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice

    if(areYouSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.YourSelfEmployedController.onPageLoad(NormalMode)
    } else if(defineInPaidEmployment(whoIsInPaidEmp).contains(You)) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if(partnerMinEarnings.contains(false)) {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    } else {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }

  }

  def partnerSelfEmployedOrApprenticeRoute(answers: UserAnswers) = {
    val parentMinEarning = answers.yourMinimumEarnings
    val partnerSelfEmployedOrApprentice = answers.partnerSelfEmployedOrApprentice

    if(partnerSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    } else if(parentMinEarning.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

  }

}
