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
import play.api.mvc.{Result, RequestHeader}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryStartDateForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourStatutoryStartDateId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryStartDate

import scala.concurrent.Future

class YourStatutoryStartDateController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction
                                      ) extends FrontendController with I18nSupport {

  private def sessionExpired(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))


  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.yourStatutoryStartDate match {
        case None => YourStatutoryStartDateForm()
        case Some(value) => YourStatutoryStartDateForm().fill(value)
      }

      request.userAnswers.yourStatutoryPayType match {
        case Some(x) => Ok (yourStatutoryStartDate (appConfig, preparedForm, mode, x) )
        case _ => Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>

      val statutoryType = request.userAnswers.yourStatutoryPayType.getOrElse("")

      YourStatutoryStartDateForm().bindFromRequest().fold(
        (formWithErrors: Form[LocalDate]) =>
          Future.successful(BadRequest(yourStatutoryStartDate(appConfig, formWithErrors, mode, statutoryType))),
        (value) =>
          dataCacheConnector.save[LocalDate](request.sessionId, YourStatutoryStartDateId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(YourStatutoryStartDateId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
