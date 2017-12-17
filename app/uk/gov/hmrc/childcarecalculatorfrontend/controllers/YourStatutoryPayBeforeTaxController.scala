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

import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryPayBeforeTaxForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourStatutoryPayBeforeTaxId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryPayBeforeTax

import scala.concurrent.Future

class YourStatutoryPayBeforeTaxController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  private def sessionExpired(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

  private def validateStatutoryPayType[A](block: (String) => Future[Result])
                                         (implicit request: DataRequest[A]): Future[Result] = {

    request.userAnswers.yourStatutoryPayType.map {
      payType => block(Messages(s"statutoryPayTypeLower.$payType"))
    }.getOrElse(sessionExpired)
  }

  def onPageLoad(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val preparedForm = request.userAnswers.yourStatutoryPayBeforeTax match {
            case None => YourStatutoryPayBeforeTaxForm()
            case Some(value) => YourStatutoryPayBeforeTaxForm().fill(value)
          }
          Future.successful(Ok(yourStatutoryPayBeforeTax(appConfig, preparedForm, mode, statutoryType)))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          YourStatutoryPayBeforeTaxForm().bindFromRequest().fold(
            (formWithErrors: Form[String]) =>
              Future.successful(BadRequest(yourStatutoryPayBeforeTax(appConfig, formWithErrors, mode, statutoryType))),
            (value) =>
              dataCacheConnector.save[String](request.sessionId, YourStatutoryPayBeforeTaxId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(YourStatutoryPayBeforeTaxId, mode)(new UserAnswers(cacheMap))))
          )
      }
  }
}
