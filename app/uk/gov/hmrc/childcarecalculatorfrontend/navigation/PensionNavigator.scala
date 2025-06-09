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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

import javax.inject.Inject

/** Contains the navigation for current and previous year pension pages
  */
class PensionNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    YouPaidPensionCYId         -> yourPensionRouteCY,
    PartnerPaidPensionCYId     -> partnerPensionRouteCY,
    BothPaidPensionCYId        -> bothPensionRouteCY,
    WhoPaysIntoPensionId       -> whoPaysPensionRouteCY,
    HowMuchYouPayPensionId     -> howMuchYouPayPensionRouteCY,
    HowMuchPartnerPayPensionId -> howMuchPartnerPayPensionRouteCY,
    HowMuchBothPayPensionId    -> howMuchBothPayPensionRouteCY
  )

  private def yourPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.YouPaidPensionCY) {
      case true => routes.HowMuchYouPayPensionController.onPageLoad()
      case false =>
        utils.getCall(answers.doYouLiveWithPartner) {
          case true  => routes.BothAnyTheseBenefitsCYController.onPageLoad()
          case false => routes.YouAnyTheseBenefitsCYController.onPageLoad()
        }
    }

  private def partnerPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.PartnerPaidPensionCY) {
      case true  => routes.HowMuchPartnerPayPensionController.onPageLoad()
      case false => routes.BothAnyTheseBenefitsCYController.onPageLoad()
    }

  private def bothPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.bothPaidPensionCY) {
      case true  => routes.WhoPaysIntoPensionController.onPageLoad()
      case false => routes.BothAnyTheseBenefitsCYController.onPageLoad()
    }

  private def whoPaysPensionRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.whoPaysIntoPension) {
      case `you`     => routes.HowMuchYouPayPensionController.onPageLoad()
      case `partner` => routes.HowMuchPartnerPayPensionController.onPageLoad()
      case `both`    => routes.HowMuchBothPayPensionController.onPageLoad()
    }

  private def howMuchYouPayPensionRouteCY(answers: UserAnswers): Call = utils.getCall(answers.doYouLiveWithPartner) {
    case true  => routes.BothAnyTheseBenefitsCYController.onPageLoad()
    case false => routes.YouAnyTheseBenefitsCYController.onPageLoad()
  }

  private def howMuchPartnerPayPensionRouteCY(answers: UserAnswers): Call =
    routes.BothAnyTheseBenefitsCYController.onPageLoad()

  private def howMuchBothPayPensionRouteCY(answers: UserAnswers): Call =
    routes.BothAnyTheseBenefitsCYController.onPageLoad()

}
