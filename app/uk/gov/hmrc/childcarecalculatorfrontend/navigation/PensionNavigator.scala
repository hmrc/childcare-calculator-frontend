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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year pension pages
  */
class PensionNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    YouPaidPensionCYId -> yourPensionRouteCY,
    PartnerPaidPensionCYId -> partnerPensionRouteCY,
    BothPaidPensionCYId -> bothPensionRouteCY,
    WhoPaysIntoPensionId -> whoPaysPensionRouteCY,
    HowMuchYouPayPensionId -> howMuchYouPayPensionRouteCY,
    HowMuchPartnerPayPensionId -> howMuchPartnerPayPensionRouteCY,
    HowMuchBothPayPensionId -> howMuchBothPayPensionRouteCY,
    YouPaidPensionPYId -> yourPensionRoutePY,
    PartnerPaidPensionPYId -> partnerPensionRoutePY,
    BothPaidPensionPYId -> bothPensionRoutePY,
    WhoPaidIntoPensionPYId -> whoPaysPensionRoutePY,
    HowMuchYouPayPensionPYId -> howMuchYouPayPensionRoutePY,
    HowMuchPartnerPayPensionPYId -> howMuchPartnerPayPensionRoutePY,
    HowMuchBothPayPensionPYId -> howMuchBothPayPensionRoutePY
  )

  private def yourPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.YouPaidPensionCY) {
      case true =>  routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case false => {
        utils.getCall(answers.doYouLiveWithPartner) {
          case true =>routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
          case false => routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }
      }
    }

  private def partnerPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.PartnerPaidPensionCY) {
      case true =>  routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    }

  private def bothPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.bothPaidPensionCY) {
      case true =>  routes.WhoPaysIntoPensionController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    }

  private def whoPaysPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.whoPaysIntoPension) {
      case `you` =>  routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case `partner` =>  routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case `both` =>  routes.HowMuchBothPayPensionController.onPageLoad(NormalMode)
    }

  private def howMuchYouPayPensionRouteCY(answers: UserAnswers): Call = utils.getCall(answers.doYouLiveWithPartner) {
    case true => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    case false => routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
  }

  private def howMuchPartnerPayPensionRouteCY(answers: UserAnswers): Call = routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)


  private def howMuchBothPayPensionRouteCY(answers: UserAnswers): Call = routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)

  private def yourPensionRoutePY(answers: UserAnswers) =
    utils.getCall(answers.youPaidPensionPY) {
      case true =>  routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case false => {
        utils.getCall(answers.doYouLiveWithPartner) {
          case true => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
          case false => routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        }
      }
    }

  private def partnerPensionRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerPaidPensionPY) {
      case true =>  routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def bothPensionRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothPaidPensionPY) {
      case true =>  routes.WhoPaidIntoPensionPYController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def whoPaysPensionRoutePY(answers: UserAnswers) =
    utils.getCall(answers.whoPaidIntoPensionPY) {
      case `you` =>  routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case `partner` =>  routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case `both` =>  routes.HowMuchBothPayPensionPYController.onPageLoad(NormalMode)
    }

  private def howMuchYouPayPensionRoutePY(answers: UserAnswers) =
    utils.getCall(answers.doYouLiveWithPartner) {
      case true => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
      case false => routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def howMuchPartnerPayPensionRoutePY(answers: UserAnswers) = routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)

  private def howMuchBothPayPensionRoutePY(answers: UserAnswers) = routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
}
