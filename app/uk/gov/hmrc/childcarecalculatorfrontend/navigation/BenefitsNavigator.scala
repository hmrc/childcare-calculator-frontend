/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

/**
  * Contains the navigation for current and previous year benefits pages
  */
class BenefitsNavigator @Inject()(utils: Utils) extends SubNavigator {

  override protected def routeMap = Map(
    YouAnyTheseBenefitsIdCY -> yourBenefitsRouteCY,
    PartnerAnyTheseBenefitsCYId -> partnerBenefitsRouteCY,
    BothAnyTheseBenefitsCYId -> bothBenefitsRouteCY,
    WhosHadBenefitsId -> whosHadBenefitsRouteCY,
    YouBenefitsIncomeCYId -> yourBenefitsIncomeRouteCY,
    PartnerBenefitsIncomeCYId -> partnerBenefitsIncomeRouteCY,
    BenefitsIncomeCYId -> bothBenefitsIncomeRouteCY,
    YouAnyTheseBenefitsPYId -> yourBenefitsRoutePY,
    PartnerAnyTheseBenefitsPYId -> partnerBenefitsRoutePY,
    BothAnyTheseBenefitsPYId -> bothBenefitsRoutePY,
    WhosHadBenefitsPYId -> whosHadBenefitsRoutePY,
    YouBenefitsIncomePYId -> yourBenefitsIncomeRoutePY,
    PartnerBenefitsIncomePYId -> partnerBenefitsIncomeRoutePY,
    BothBenefitsIncomePYId -> bothBenefitsIncomeRoutePY
  )

  private def yourBenefitsRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.youAnyTheseBenefits) {
      case true => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case false => utils.getCall(answers.doYouLiveWithPartner) {
        case true => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        case false => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
      }
    }

  private def partnerBenefitsRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyTheseBenefitsCY) {
      case true => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case false => routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
    }

  private def bothBenefitsRouteCY(answers: UserAnswers): Call =
    utils.getCall(answers.bothAnyTheseBenefitsCY) {
      case true => routes.WhosHadBenefitsController.onPageLoad(NormalMode)
      case false => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
    }

  private def whosHadBenefitsRouteCY(answers: UserAnswers) = {
    utils.getCall(answers.whosHadBenefits) {
      case YOU => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case PARTNER => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case BOTH => routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
    }
  }

  private def yourBenefitsIncomeRouteCY(answers: UserAnswers) = utils.getCall(answers.doYouLiveWithPartner) {
    case true => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
    case false => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
  }

  private def partnerBenefitsIncomeRouteCY(answers: UserAnswers) = routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)

  private def bothBenefitsIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.benefitsIncomeCY) { case _ => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
    }

  private def yourBenefitsRoutePY(answers: UserAnswers) =
    utils.getCall(answers.youAnyTheseBenefitsPY) {
      case true => routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
      case false => {
        utils.getCall(answers.doYouLiveWithPartner) {
          case true => routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
          case false => routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
        }
      }
    }

  private def partnerBenefitsRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyTheseBenefitsPY) {
      case true => routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
      case false => routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode)
    }

  private def bothBenefitsRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothAnyTheseBenefitsPY) {
      case true => routes.WhosHadBenefitsPYController.onPageLoad(NormalMode)
      case false => routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
    }

  private def whosHadBenefitsRoutePY(answers: UserAnswers) = {
    utils.getCall(answers.whosHadBenefitsPY) {
      case YOU => routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
      case PARTNER => routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
      case BOTH => routes.BothBenefitsIncomePYController.onPageLoad(NormalMode)
    }
  }

  private def yourBenefitsIncomeRoutePY(answers: UserAnswers) = {
    utils.getCall(answers.doYouLiveWithPartner) {
      case true => routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
      case false => routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
    }
  }

  private def partnerBenefitsIncomeRoutePY(answers: UserAnswers) = routes.BothOtherIncomeLYController.onPageLoad(NormalMode)

  private def bothBenefitsIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothBenefitsIncomePY) { case _ =>
      routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
    }
}
