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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.partnerMinimumEarningsErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerMinimumEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PartnerMinimumEarningsController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    utils: Utils,
    partnerMinimumEarnings: partnerMinimumEarnings
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    if (request.userAnswers.yourPartnersAge.isEmpty) {
      logger.warn(
        s"Arrived at ${request.uri} without an age value, redirecting to ${routes.YourPartnersAgeController.onPageLoad().url}"
      )
      Redirect(routes.YourPartnersAgeController.onPageLoad())
    } else {
      val earningsForAge =
        utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourPartnersAge)

      val preparedForm = request.userAnswers.partnerMinimumEarnings match {
        case None        => BooleanForm(partnerMinimumEarningsErrorKey, earningsForAge)
        case Some(value) => BooleanForm(partnerMinimumEarningsErrorKey, earningsForAge).fill(value)
      }
      Ok(partnerMinimumEarnings(appConfig, preparedForm, earningsForAge))
    }
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    val earningsForAge =
      utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourPartnersAge)

    BooleanForm(partnerMinimumEarningsErrorKey, earningsForAge)
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(partnerMinimumEarnings(appConfig, formWithErrors, earningsForAge))),
        value =>
          dataCacheConnector
            .save[Boolean](request.sessionId, PartnerMinimumEarningsId.toString, value)
            .map(cacheMap => Redirect(navigator.nextPage(PartnerMinimumEarningsId)(new UserAnswers(cacheMap))))
      )
  }

}
