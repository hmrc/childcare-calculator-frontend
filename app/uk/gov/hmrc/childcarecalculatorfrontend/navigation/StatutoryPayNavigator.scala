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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

/**
  * Contains the navigation for current and previous year statutory pay pages
  */
class StatutoryPayNavigator @Inject()(utils: Utils) extends SubNavigator {

  override protected def routeMap = Map(
    YourStatutoryPayPYId -> yourStatutoryPayRoutePY,
    PartnerStatutoryPayPYId -> partnerStatutoryPayRoutePY,
    BothStatutoryPayPYId->bothStatutoryPayRoutePY,
    WhoGetsStatutoryPYId->whoGetsStatutoryRoutePY,
    YouNoWeeksStatPayPYId-> youNoWeeksStatutoryPayRoutePY,
    PartnerNoWeeksStatPayPYId->partnerNoWeeksStatutoryPayRoutePY,
    BothNoWeeksStatPayPYId->bothNoWeeksStatutoryPayRoutePY,
    YourStatutoryPayAmountPYId->yourStatutoryPayAmountRoutePY,
    PartnerStatutoryPayAmountPYId->partnerStatutoryPayAmountRoutePY,
    StatutoryPayAmountPYId -> bothStatutoryPayAmountRoutePY
  )

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
    utils.getCall(answers.youNoWeeksStatPayPY)(_ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode))


  //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
  private def partnerNoWeeksStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerNoWeeksStatPayPY)(_ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode))

  //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
  private def bothNoWeeksStatutoryPayRoutePY(answers: UserAnswers) =
    utils.getCall(answers.bothNoWeeksStatPayPY)(_ => routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode))

  private def yourStatutoryPayAmountRoutePY(answers: UserAnswers) =
    utils.getCall(answers.yourStatutoryPayAmountPY)(_ => routes.MaxFreeHoursResultController.onPageLoad())

  private def partnerStatutoryPayAmountRoutePY(answers: UserAnswers) =
    utils.getCall(answers.partnerStatutoryPayAmountPY)(_ => routes.MaxFreeHoursResultController.onPageLoad())

  private def bothStatutoryPayAmountRoutePY(answers: UserAnswers) =
    utils.getCall(answers.statutoryPayAmountPY)(_ => routes.MaxFreeHoursResultController.onPageLoad())

}
