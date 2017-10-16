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
  val Apprentice: String = SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
  val Neither: String = SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString

  def areYouSelfEmployedOrApprenticeRoute(answers: UserAnswers) = {

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val areYouInPaidWork = answers.areYouInPaidWork.getOrElse(true)
    val paidEmployment = answers.paidEmployment
    val whoIsInPaidEmp = answers.whoIsInPaidEmployment
    val parentMinEarning = answers.yourMinimumEarnings
    val partnerMinEarnings = answers.partnerMinimumEarnings
    val areYouSelfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice

    (hasPartner, areYouInPaidWork, paidEmployment, whoIsInPaidEmp, parentMinEarning, partnerMinEarnings, areYouSelfEmployedOrApprentice) match {
      case (false, true, _, _, Some(false), _, Some(SelfEmployed)) => routes.YourSelfEmployedController.onPageLoad(NormalMode)
      case (false, true, _, _, Some(false), _, _) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(You), Some(false), _, Some(SelfEmployed)) => routes.YourSelfEmployedController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(You), Some(false), _, _) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(Both), Some(false), Some(true), Some(SelfEmployed)) => routes.YourSelfEmployedController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(Both), Some(false), Some(true), _) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(Both), Some(false), Some(false), Some(SelfEmployed)) => routes.YourSelfEmployedController.onPageLoad(NormalMode)
      case (true, _, Some(true), Some(Both), Some(false), Some(false), _) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  }

  def partnerSelfEmployedOrApprenticeRoute(answers: UserAnswers) = {

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val paidEmployment = answers.paidEmployment
    val whoIsInPaidEmp: Option[String] = answers.whoIsInPaidEmployment

    (hasPartner, paidEmployment, whoIsInPaidEmp) match {
      case (true, Some(true), Some(Partner)) => partnerSelfEmpOrAppRouteAsPerPaidEmp(Some(Partner), answers)
      case (true, Some(true), Some(Both)) => partnerSelfEmpOrAppRouteAsPerPaidEmp(Some(Both), answers)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  }

  def partnerSelfEmpOrAppRouteAsPerPaidEmp(whoIsInPaidEmp: Option[String], answers: UserAnswers) = {
    val parentMinEarning = answers.yourMinimumEarnings
    val partnerMinEarnings = answers.partnerMinimumEarnings
    val areYouSelfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice
    val partnerSelfEmployedOrApprentice = answers.partnerSelfEmployedOrApprentice

    if (whoIsInPaidEmp.contains(Partner)) {

      (parentMinEarning, partnerMinEarnings, areYouSelfEmployedOrApprentice, partnerSelfEmployedOrApprentice) match {
        case (_, Some(false), _, Some(SelfEmployed)) => routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
        case (_, Some(false), _, _) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    } else {
      (parentMinEarning, partnerMinEarnings, areYouSelfEmployedOrApprentice, partnerSelfEmployedOrApprentice) match {
        case (Some(false), Some(true), _, _) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
        case (Some(true), Some(false), _, Some(SelfEmployed)) => routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
        case (Some(true), Some(false), _, _) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
        case (Some(false), Some(false), _, Some(SelfEmployed)) => routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
        case (Some(false), Some(false), Some(SelfEmployed), _) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case (Some(false), Some(false), Some(Apprentice), Some(Apprentice)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case (Some(false), Some(false), Some(Neither), Some(Neither)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case (Some(false), Some(false), Some(Neither), Some(Apprentice)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case (Some(false), Some(false), Some(Apprentice), Some(Neither)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    }
  }
}
