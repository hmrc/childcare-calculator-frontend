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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{Utils, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils._

import scala.concurrent.Future

class YourMinimumEarningsController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         utils: Utils) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.yourMinimumEarnings match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }

      Ok(yourMinimumEarnings(appConfig,
        preparedForm,
        mode,
        utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourAge)))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      BooleanForm(yourMinimumEarningsErrorKey).bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(yourMinimumEarnings(appConfig,
            formWithErrors,
            mode,
            utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, request.userAnswers.yourAge)))),
        (value) =>
          dataCacheConnector.save[Boolean](request.sessionId, YourMinimumEarningsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(YourMinimumEarningsId, mode)(new UserAnswers(cacheMap))))
      )
  }

}
