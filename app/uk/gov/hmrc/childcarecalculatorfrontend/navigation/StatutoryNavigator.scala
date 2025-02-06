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

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year statutory pay pages
  */
class StatutoryNavigator @Inject() (utils: Utils, scheme: TaxCredits) extends SubNavigator {

  override protected def routeMap = Map(
    BothStatutoryPayId -> bothStatutoryPayRoute,
    YouStatutoryPayId -> yourStatutoryPayRoute,
    PartnerStatutoryPayId -> partnerStatutoryPayRoute,
    WhoGotStatutoryPayId -> whoGotStatutoryPayRoute,
    YourStatutoryPayTypeId -> yourStatutoryPayTypeRoute,
    YourStatutoryStartDateId -> yourStatutoryStartDateRoute,
    YourStatutoryWeeksId -> yourStatutoryWeeksRoute,
    YourStatutoryPayBeforeTaxId -> yourStatutoryPayBeforeTaxRoute,
    YourStatutoryPayPerWeekId -> yourStatutoryPayPerWeekRoute
  )

  private def bothStatutoryPayRoute(answers: UserAnswers) = {
    utils.getCall(answers.bothStatutoryPay) {
      case true => routes.WhoGotStatutoryPayController.onPageLoad(NormalMode)
      case false =>  routes.ResultController.onPageLoad()
    }
  }


  private def yourStatutoryPayRoute(answers: UserAnswers) = {
    utils.getCall(answers.youStatutoryPay) {
      case true => routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
      case false => routes.ResultController.onPageLoad()
    }
  }

  private def partnerStatutoryPayRoute(answers: UserAnswers) = {
    utils.getCall(answers.partnerStatutoryPay) {
      case true => routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
      case false => routes.ResultController.onPageLoad()
    }
  }

  private def whoGotStatutoryPayRoute(answers: UserAnswers) =
    utils.getCall(answers.whoGotStatutoryPay) {
      case YouPartnerBothEnum.YOU => routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
      case YouPartnerBothEnum.PARTNER => routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
      case YouPartnerBothEnum.BOTH => routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
    }

  private def yourStatutoryPayTypeRoute(answers: UserAnswers)  =
    utils.getCall(answers.yourStatutoryPayType) { case _ => routes.YourStatutoryStartDateController.onPageLoad(NormalMode)}

  private def yourStatutoryStartDateRoute(answers: UserAnswers) =
    utils.getCall(answers.yourStatutoryStartDate) {
      case _ => routes.YourStatutoryWeeksController.onPageLoad(NormalMode)
    }

  private def yourStatutoryWeeksRoute(answers: UserAnswers) =
    utils.getCall(answers.yourStatutoryWeeks) {
      case _ => routes.YourStatutoryPayBeforeTaxController.onPageLoad(NormalMode)
    }

  private def yourStatutoryPayBeforeTaxRoute(answers: UserAnswers) = {
    utils.getCall(answers.yourStatutoryPayBeforeTax) {
      case true => routes.YourStatutoryPayPerWeekController.onPageLoad(NormalMode)
      case false =>  nextPageForYourStatutoryPayBeforeTaxNoSelection(answers)
    }
  }

  private def nextPageForYourStatutoryPayBeforeTaxNoSelection(answers: UserAnswers) = {

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val whoGotStatutoryPay: Option[YouPartnerBothEnum.Value] = answers.whoGotStatutoryPay

    if(hasPartner){
      utils.getCall(whoGotStatutoryPay){
        case YouPartnerBothEnum.YOU => routes.ResultController.onPageLoad()
        case _ => routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
      }
    }else{
      routes.ResultController.onPageLoad()
    }
  }

  private def yourStatutoryPayPerWeekRoute(answers: UserAnswers) = {
    utils.getCall(answers.yourStatutoryPayPerWeek) { case _ => nextPageYourStatutoryPayPerWeek(answers)}
  }


  private def nextPageYourStatutoryPayPerWeek(answers: UserAnswers) = {
    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val whoGotStatutoryPay: Option[YouPartnerBothEnum.Value] = answers.whoGotStatutoryPay

    whoGotStatutoryPay match {
      case Some(_) if hasPartner =>
        utils.getCall(whoGotStatutoryPay){
          case YouPartnerBothEnum.YOU => routes.ResultController.onPageLoad()
          case _ => routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
        }
      case None => routes.ResultController.onPageLoad()
    }
  }



}
