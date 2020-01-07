/*
 * Copyright 2020 HM Revenue & Customs
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
import org.joda.time.LocalDate
import play.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.yourMinimumEarningsErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class YourMinimumEarningsController @Inject()(appConfig: FrontendAppConfig,
                                         mcc: MessagesControllerComponents,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         utils: Utils) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      if (request.userAnswers.yourAge.isEmpty) {
        Logger.warn(s"Arrived at ${request.uri} without an age value, redirecting to ${routes.YourAgeController.onPageLoad(mode).url}")
        Redirect(routes.YourAgeController.onPageLoad(mode))
      } else {
        val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourAge)

        val preparedForm = request.userAnswers.yourMinimumEarnings match {
          case None => BooleanForm(yourMinimumEarningsErrorKey, earningsForAge)
          case Some(value) => BooleanForm(yourMinimumEarningsErrorKey, earningsForAge).fill(value)
        }
        Ok(yourMinimumEarnings(appConfig,
          preparedForm,
          mode,
          earningsForAge))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val earningsForAge = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourAge)

      BooleanForm(yourMinimumEarningsErrorKey, earningsForAge).bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(yourMinimumEarnings(appConfig,
            formWithErrors,
            mode,
            earningsForAge))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, YourMinimumEarningsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(YourMinimumEarningsId, mode)(new UserAnswers(cacheMap))))
      )
  }

}
