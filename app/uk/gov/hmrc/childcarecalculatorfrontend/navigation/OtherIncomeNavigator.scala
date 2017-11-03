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

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year other income pages
  */
class OtherIncomeNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected def routeMap = Map(
    YourOtherIncomeThisYearId -> yourOtherIncomeRouteCY,
    PartnerAnyOtherIncomeThisYearId -> partnerOtherIncomeRouteCY,
    BothOtherIncomeThisYearId -> bothOtherIncomeRouteCY,
    WhoGetsOtherIncomeCYId -> whoGetsOtherIncomeRouteCY,
    YourOtherIncomeAmountCYId -> howMuchYourOtherIncomeRouteCY,
    PartnerOtherIncomeAmountCYId -> howMuchPartnerOtherIncomeRouteCY,
    OtherIncomeAmountCYId -> howMuchBothOtherIncomeRouteCY,
    YourOtherIncomeLYId -> yourOtherIncomeRoutePY,
    PartnerAnyOtherIncomeLYId -> partnerOtherIncomeRoutePY,
    BothOtherIncomeLYId -> bothOtherIncomeRoutePY,
    WhoOtherIncomePYId -> whoGetsOtherIncomeRoutePY,
    YourOtherIncomeAmountPYId -> howMuchYourOtherIncomeRoutePY,
    PartnerOtherIncomeAmountPYId -> howMuchPartnerOtherIncomeRoutePY,
    OtherIncomeAmountPYId -> howMuchBothOtherIncomeRoutePY
  )

  private def yourOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeThisYear) {
      case true =>  routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false =>  routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    }

  private def partnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeThisYear) {
      case true =>  routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false =>  routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeThisYear) {
      case true =>  routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsOtherIncomeCY) {
      case You =>  routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Partner =>  routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Both =>  routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeAmountCY) (_=>
      utils.getCall(answers.whoIsInPaidEmployment) {
        case You => routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      })

  private def howMuchPartnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerOtherIncomeAmountCY)(_=>
      utils.getCall(answers.whoIsInPaidEmployment) {
      case Partner => routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      case Both => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    })

  private def howMuchBothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.otherIncomeAmountCY)(_ => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))

  private def yourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeLY) {
      case true =>  routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false =>  routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def partnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeLY) {
      case true =>  routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false =>  routes.PartnerAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def bothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeLY) {
      case true =>  routes.WhoOtherIncomePYController.onPageLoad(NormalMode)
      case false =>  routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    }

  private def whoGetsOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.whoOtherIncomePY) {
      case You =>  routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case Partner =>  routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case Both =>  routes.OtherIncomeAmountPYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeAmountPY)(_ =>
      utils.getCall(answers.whoIsInPaidEmployment) {
        case You => routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      })

  private def howMuchPartnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerOtherIncomeAmountPY)(_ =>
      utils.getCall(answers.whoIsInPaidEmployment) {
        case Partner => routes.PartnerAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      })

  private def howMuchBothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.otherIncomeAmountPY)(_ => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode))
}
