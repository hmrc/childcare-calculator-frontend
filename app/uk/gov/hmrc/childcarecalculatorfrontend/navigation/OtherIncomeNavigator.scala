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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NormalMode, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year other income pages
  */
class OtherIncomeNavigator @Inject() (utils: Utils,taxCredits: TaxCredits) extends SubNavigator {

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
      case false => {
        if (taxCredits.eligibility(answers) == NotEligible) {
          routes.YouStatutoryPayController.onPageLoad(NormalMode)
        }
        else {
          routes.YourIncomeInfoPYController.onPageLoad()
        }
      }
    }

  private def partnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeThisYear) {
      case true =>  routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false => {
        if (taxCredits.eligibility(answers) == NotEligible) {
          routes.PartnerStatutoryPayController.onPageLoad(NormalMode)
        }
        else{
          routes.PartnerIncomeInfoPYController.onPageLoad()
        }
      }
    }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeThisYear) {
      case true =>  routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
      case false =>  {
        if (taxCredits.eligibility(answers) == NotEligible) {
          routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }
        else{
          routes.YourIncomeInfoPYController.onPageLoad()
        }
      }
    }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsOtherIncomeCY) {
      case You =>  routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Partner =>  routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Both =>  routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeAmountCY) { case _=> getCallForYourOtherIncomeAsPerPaidWorkCY(answers)}

  private def howMuchPartnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerOtherIncomeAmountCY) { case _ =>
      utils.getCall(answers.whoIsInPaidEmployment) {
        case Partner => routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      }
    }

  private def howMuchBothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.otherIncomeAmountCY){case _ => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)}

  private def yourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeLY) {
      case true =>  routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false =>  routes.YouStatutoryPayController.onPageLoad(NormalMode)
    }

  private def partnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeLY) {
      case true =>  routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false =>  routes.PartnerStatutoryPayController.onPageLoad(NormalMode)
    }

  private def bothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeLY) {
      case true =>  routes.WhoOtherIncomePYController.onPageLoad(NormalMode)
      case false =>  routes.BothStatutoryPayController.onPageLoad(NormalMode)
    }

  private def whoGetsOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.whoOtherIncomePY) {
      case You =>  routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case Partner =>  routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case Both =>  routes.OtherIncomeAmountPYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeAmountPY){case _ => getCallForYourOtherIncomeAsPerPaidWorkPY(answers)}

  private def howMuchPartnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerOtherIncomeAmountPY) { case _ =>
      utils.getCall(answers.whoIsInPaidEmployment) {
        case Partner => routes.PartnerAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
      }
    }

  private def howMuchBothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.otherIncomeAmountPY){case _ => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)}

  private def getCallForYourOtherIncomeAsPerPaidWorkCY(answers: UserAnswers)=
    if(answers.areYouInPaidWork.nonEmpty) {
      routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
    } else {
      utils.getCall(answers.whoIsInPaidEmployment) {
        case You => routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    }

  private def getCallForYourOtherIncomeAsPerPaidWorkPY(answers: UserAnswers)=
    if(answers.areYouInPaidWork.nonEmpty) {
      routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
    } else {
      utils.getCall(answers.whoIsInPaidEmployment) {
        case You => routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case Both => routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    }
}
