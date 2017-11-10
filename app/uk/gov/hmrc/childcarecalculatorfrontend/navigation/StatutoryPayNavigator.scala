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
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NormalMode, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

/**
  * Contains the navigation for current and previous year statutory pay pages
  */
class StatutoryPayNavigator @Inject() (utils: Utils, scheme: TaxCredits) extends SubNavigator {

  override protected def routeMap = Map(
    YourStatutoryPayCYId -> yourStatutoryPayRouteCY,
    YourStatutoryPayPYId -> yourStatutoryPayRoutePY,
    PartnerStatutoryPayPYId -> partnerStatutoryPayRoutePY,
    BothStatutoryPayPYId->bothStatutoryPayRoutePY,
    WhoGetsStatutoryPYId->whoGetsStatutoryRoutePY,
    YouNoWeeksStatPayPYId-> youNoWeeksStatutoryPayRoutePY,
    PartnerNoWeeksStatPayPYId->partnerNoWeeksStatutoryPayRoutePY,
    BothNoWeeksStatPayPYId->bothNoWeeksStatutoryPayRoutePY,
    PartnerStatutoryPayAmountPYId->partnerStatutoryPayAmountRoutePY,
    StatutoryPayAmountPYId -> bothStatutoryPayAmountRoutePY,
    PartnerStatutoryPayCYId->partnerStatutoryPayRouteCY,
    BothStatutoryPayCYId->bothStatutoryPayRouteCY,
    WhoGetsStatutoryCYId->whoGetsStatutoryRouteCY,
    YouNoWeeksStatPayCYId->youNoWeeksStatutoryPayRouteCY,
    PartnerNoWeeksStatPayCYId->partnerNoWeeksStatutoryPayRouteCY,
    BothNoWeeksStatPayCYId->bothNoWeeksStatutoryPayRouteCY,
    YourStatutoryPayAmountCYId-> yourStatutoryPayAmountRouteCY,
    PartnerStatutoryPayAmountCYId->partnerStatutoryPayAmountRouteCY,
    StatutoryPayAmountCYId->bothStatutoryPayAmountRouteCY
  )

  private def yourStatutoryPayRouteCY(answers: UserAnswers) = {
    utils.getCall(answers.yourStatutoryPayCY) {
      case true => routes.YouNoWeeksStatPayCYController.onPageLoad(NormalMode)
      case false => yourStatutoryPayRouteCYForNoSelection(answers)
    }
  }

  private def partnerStatutoryPayRouteCY(answers: UserAnswers) = {
    utils.getCall(answers.partnerStatutoryPayCY) {
      case true => routes.PartnerNoWeeksStatPayCYController.onPageLoad(NormalMode)
      case false => statutoryPayRouteCYForNoSelection(answers)
    }
  }

  private def bothStatutoryPayRouteCY(answers: UserAnswers) = {
    utils.getCall(answers.bothStatutoryPayCY) {
      case true => routes.WhoGetsStatutoryCYController.onPageLoad(NormalMode)
      case false => statutoryPayRouteCYForNoSelection(answers)
    }
  }

  private def whoGetsStatutoryRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsStatutoryCY) {
      case You => routes.YouNoWeeksStatPayCYController.onPageLoad(NormalMode)
      case Partner => routes.PartnerNoWeeksStatPayCYController.onPageLoad(NormalMode)
      case Both => routes.BothNoWeeksStatPayCYController.onPageLoad(NormalMode)
    }

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
  private def youNoWeeksStatutoryPayRouteCY(answers: UserAnswers) =
    utils.getCall(answers.youNoWeeksStatPayCY) { case _ => routes.StatutoryPayAWeekController.onPageLoad(NormalMode)}

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
  private def partnerNoWeeksStatutoryPayRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerNoWeeksStatPayCY){ case _ => routes.StatutoryPayAWeekController.onPageLoad(NormalMode)}

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
  private def bothNoWeeksStatutoryPayRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothNoWeeksStatPayCY){ case _ => routes.StatutoryPayAWeekController.onPageLoad(NormalMode)}

  private def yourStatutoryPayAmountRouteCY(answers: UserAnswers) =
    utils.getCall(answers.yourStatutoryPayAmountCY){ case _ => yourStatutoryPayRouteCYForNoSelection(answers)}

  private def partnerStatutoryPayAmountRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerStatutoryPayAmountCY){ case _ => statutoryPayRouteCYForNoSelection(answers)}

  private def bothStatutoryPayAmountRouteCY(answers: UserAnswers) =
    utils.getCall(answers.statutoryPayAmountCY){ case _ => statutoryPayRouteCYForNoSelection(answers)}

  private def yourStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourStatutoryPayPY) {
      case true => routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
      case false => routes.MaxFreeHoursResultController.onPageLoad()
    }

  private def partnerStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerStatutoryPayPY) {
      case true => routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
      case false => routes.MaxFreeHoursResultController.onPageLoad()
    }

  private def bothStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothStatutoryPayPY) {
      case true => routes.WhoGetsStatutoryPYController.onPageLoad(NormalMode)
      case false => routes.MaxFreeHoursResultController.onPageLoad()
    }

  private def whoGetsStatutoryRoutePY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsStatutoryPY) {
      case You => routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
      case Partner => routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
      case Both => routes.BothNoWeeksStatPayPYController.onPageLoad(NormalMode)
    }

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
  private def youNoWeeksStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.youNoWeeksStatPayPY){case _ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)}

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
  private def partnerNoWeeksStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerNoWeeksStatPayPY){case _ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)}

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
  private def bothNoWeeksStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothNoWeeksStatPayPY){case _ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)}

  private def partnerStatutoryPayAmountRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerStatutoryPayAmountPY){ case _ => routes.MaxFreeHoursResultController.onPageLoad()}

  private def bothStatutoryPayAmountRoutePY(answers: UserAnswers) =
    utils.getCall(answers.statutoryPayAmountPY){case _ => routes.MaxFreeHoursResultController.onPageLoad()}

  private def yourStatutoryPayRouteCYForNoSelection(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val eligibility =  scheme.eligibility(answers)

    (hasPartner, eligibility) match {
      case (false, Eligible) => routes.YourIncomeInfoPYController.onPageLoad()
      case (true, Eligible) => routes.PartnerIncomeInfoPYController.onPageLoad()
      case (_, NotEligible) => routes.MaxFreeHoursResultController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def statutoryPayRouteCYForNoSelection(answers: UserAnswers) =
    scheme.eligibility(answers) match {
      case Eligible => routes.PartnerIncomeInfoPYController.onPageLoad()
      case NotEligible => routes.MaxFreeHoursResultController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

}
