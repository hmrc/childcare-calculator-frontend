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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

import javax.inject.{Inject, Singleton}

/** Contains the navigation for current and previous year benefits pages
  */
@Singleton
private[navigation] class BenefitsIncomeNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected def routeMap: Map[Identifier, UserAnswers => Call] = Map(
    YouAnyTheseBenefitsIdCY   -> yourBenefitsRouteCY,
    BothAnyTheseBenefitsCYId  -> bothBenefitsRouteCY,
    WhosHadBenefitsId         -> whosHadBenefitsRouteCY,
    YouBenefitsIncomeCYId     -> yourBenefitsIncomeRouteCY,
    PartnerBenefitsIncomeCYId -> partnerBenefitsIncomeRouteCY,
    BenefitsIncomeCYId        -> bothBenefitsIncomeRouteCY
  )

  private def yourBenefitsRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.youAnyTheseBenefits) {
      case true => routes.YouBenefitsIncomeCYController.onPageLoad()
      case false =>
        utils.getCall(answers.doYouLiveWithPartner) {
          case true  => routes.BothOtherIncomeThisYearController.onPageLoad()
          case false => routes.YourOtherIncomeThisYearController.onPageLoad()
        }
    }

  private def bothBenefitsRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.bothAnyTheseBenefitsCY) {
      case true  => routes.WhosHadBenefitsController.onPageLoad()
      case false => routes.BothOtherIncomeThisYearController.onPageLoad()
    }

  private def whosHadBenefitsRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whosHadBenefits) {
      case YOU     => routes.YouBenefitsIncomeCYController.onPageLoad()
      case PARTNER => routes.PartnerBenefitsIncomeCYController.onPageLoad()
      case BOTH    => routes.BenefitsIncomeCYController.onPageLoad()
    }

  private def yourBenefitsIncomeRouteCY(answers: UserAnswers) = utils.getCall(answers.doYouLiveWithPartner) {
    case true  => routes.BothOtherIncomeThisYearController.onPageLoad()
    case false => routes.YourOtherIncomeThisYearController.onPageLoad()
  }

  private def partnerBenefitsIncomeRouteCY(answers: UserAnswers) =
    routes.BothOtherIncomeThisYearController.onPageLoad()

  private def bothBenefitsIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.benefitsIncomeCY) { case _ =>
      routes.BothOtherIncomeThisYearController.onPageLoad()
    }

}
