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

import javax.inject.Inject
import java.time.LocalDate
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{partnerMinimumEarningsErrorKey, you}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{ChildcareConstants, UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{
  partnerAverageWeeklyEarnings,
  partnerMinimumEarnings,
  yourAndPartnerAverageWeeklyEarnings,
  yourAverageWeeklyEarnings
}

import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class AverageWeeklyEarningController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    utils: Utils,
    yourAverageWeeklyEarnings: yourAverageWeeklyEarnings,
    partnerAverageWeeklyEarnings: partnerAverageWeeklyEarnings,
    yourAndPartnerAverageWeeklyEarnings: yourAndPartnerAverageWeeklyEarnings
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def onPageLoad(mode: Mode): Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val whoIsInPaidEmployment = request.userAnswers.whoIsInPaidEmployment
    if (request.userAnswers.isYouPartnerOrBoth(whoIsInPaidEmployment).equalsIgnoreCase(ChildcareConstants.you)) {
      Ok(yourAverageWeeklyEarnings())
    } else if (
      request.userAnswers.isYouPartnerOrBoth(whoIsInPaidEmployment).equalsIgnoreCase(ChildcareConstants.both)
    ) {
      Ok(yourAndPartnerAverageWeeklyEarnings())
    } else {
      Ok(partnerAverageWeeklyEarnings())
    }

  }

}
