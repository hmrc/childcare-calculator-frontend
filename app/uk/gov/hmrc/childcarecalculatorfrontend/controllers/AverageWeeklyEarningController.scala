/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{
  partnerAverageWeeklyEarnings,
  yourAndPartnerAverageWeeklyEarnings,
  yourAverageWeeklyEarnings
}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class AverageWeeklyEarningController @Inject() (
    mcc: MessagesControllerComponents,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    yourAverageWeeklyEarnings: yourAverageWeeklyEarnings,
    partnerAverageWeeklyEarnings: partnerAverageWeeklyEarnings,
    yourAndPartnerAverageWeeklyEarnings: yourAndPartnerAverageWeeklyEarnings
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val whoIsInPaidEmployment = request.userAnswers.whoIsInPaidEmployment
    val location              = request.userAnswers.location
    location match {
      case None =>
        Redirect(routes.LocationController.onPageLoad())
      case Some(location) =>
        val who = request.userAnswers.isYouPartnerOrBoth(whoIsInPaidEmployment).toLowerCase
        who match {
          case ChildcareConstants.you     => Ok(yourAverageWeeklyEarnings(location))
          case ChildcareConstants.both    => Ok(yourAndPartnerAverageWeeklyEarnings(location))
          case ChildcareConstants.partner => Ok(partnerAverageWeeklyEarnings(location))
        }
    }

  }

}
