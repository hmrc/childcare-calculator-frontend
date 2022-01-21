/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NormalMode, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{TaxCredits, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year other income pages
  */
class OtherIncomeNavigator @Inject()(utils: Utils, taxCredits: TaxCredits, tfc: TaxFreeChildcare) extends SubNavigator {

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

  private def yourOtherIncomeRouteCY(answers: UserAnswers) = {

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val eligibleCall = if (hasPartner) { routes.BothIncomeInfoPYController.onPageLoad}
                      else {routes.YourIncomeInfoPYController.onPageLoad}
    val notEligibleCall = routes.ResultController.onPageLoad

    utils.getCall(answers.yourOtherIncomeThisYear) {
      case true => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false => taxCreditAndTfcEligibility(answers, eligibleCall, notEligibleCall)
    }
  }

  private def partnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeThisYear) {
      case true => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false => processTaxCreditsEligibility(answers, routes.BothIncomeInfoPYController.onPageLoad, routes.ResultController.onPageLoad)
    }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeThisYear) {
      case true => routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
      case false => taxCreditAndTfcEligibility(answers,
        routes.BothIncomeInfoPYController.onPageLoad,
        routes.ResultController.onPageLoad)
    }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsOtherIncomeCY) {
      case `you` => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case `partner` => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case `both` => routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRouteCY(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val successRoute = if (hasPartner) { routes.BothIncomeInfoPYController.onPageLoad}
                      else {routes.YourIncomeInfoPYController.onPageLoad}
    val failureRoute =   routes.ResultController.onPageLoad

    processCall(answers, answers.yourOtherIncomeAmountCY, successRoute, failureRoute)
  }

  private def howMuchPartnerOtherIncomeRouteCY(answers: UserAnswers) =
    processCall(answers,answers.partnerOtherIncomeAmountCY,
      routes.BothIncomeInfoPYController.onPageLoad,
      routes.ResultController.onPageLoad)

  private def howMuchBothOtherIncomeRouteCY(answers: UserAnswers) =
    processCall(answers,answers.otherIncomeAmountCY,
      routes.BothIncomeInfoPYController.onPageLoad,
      routes.ResultController.onPageLoad)



  private def yourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeLY) {
      case true => routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false => utils.getCall(answers.doYouLiveWithPartner){
        case false => routes.YouStatutoryPayController.onPageLoad(NormalMode)
        case _ => routes.BothStatutoryPayController.onPageLoad(NormalMode)
      }
    }

  private def partnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeLY) {
      case true => routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case false => routes.BothStatutoryPayController.onPageLoad(NormalMode)
    }

  private def bothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeLY) {
      case true => routes.WhoOtherIncomePYController.onPageLoad(NormalMode)
      case false => routes.BothStatutoryPayController.onPageLoad(NormalMode)
    }

  private def whoGetsOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.whoOtherIncomePY) {
      case `you` => routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case `partner` => routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
      case `both` => routes.OtherIncomeAmountPYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourOtherIncomeAmountPY) { case _ =>
      utils.getCall(answers.doYouLiveWithPartner) {
        case false => routes.YouStatutoryPayController.onPageLoad(NormalMode)
        case true => routes.BothStatutoryPayController.onPageLoad(NormalMode)
      }
    }

  private def howMuchPartnerOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerOtherIncomeAmountPY) { case _ =>
      routes.BothStatutoryPayController.onPageLoad(NormalMode)
    }

  private def howMuchBothOtherIncomeRoutePY(answers: UserAnswers) =
    utils.getCall(answers.otherIncomeAmountPY) { case _ => routes.BothStatutoryPayController.onPageLoad(NormalMode) }

  private def processCall[T](answers: UserAnswers, answersType: Option[T], successRoute: Call, failureRoute: Call) = {
    utils.getCall(answersType) {
      case _ => taxCreditAndTfcEligibility(answers, successRoute, failureRoute)
    }
  }

  private def processTaxCreditsEligibility(answers: UserAnswers, eligibleCall: Call, notEligibleCall: Call) = {
    taxCredits.eligibility(answers) match {
      case Eligible => eligibleCall
      case NotEligible | NotDetermined => notEligibleCall
    }
  }

  private def taxCreditAndTfcEligibility(answers: UserAnswers,
                                         eligibleCall: Call,
                                         notEligibleCall: Call) = {

    val tcEligibility = taxCredits.eligibility(answers)
    val tfcEligibility = tfc.eligibility(answers)
    val hasUniversalCredits = answers.taxOrUniversalCredits.contains(universalCredits)

    (tcEligibility, tfcEligibility) match {
      case (Eligible, Eligible) => if(hasUniversalCredits) notEligibleCall else eligibleCall
      case (Eligible, NotEligible) => if(!hasUniversalCredits) eligibleCall else notEligibleCall
      case _ => notEligibleCall
    }
  }

}
