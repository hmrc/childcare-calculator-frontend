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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryWeeksForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourStatutoryWeeksId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, StatutoryPayTypeEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.StatutoryPayWeeksViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryWeeks

import scala.concurrent.Future

class YourStatutoryWeeksController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        yourStatutoryWeeksForm: YourStatutoryWeeksForm) extends FrontendController with I18nSupport {

  private def sessionExpired(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

  private def validateStatutoryPayType[A](block: (StatutoryPayTypeEnum.Value) => Future[Result])
                                         (implicit request: DataRequest[A]): Future[Result] = {

    request.userAnswers.yourStatutoryPayType.map {
      payType => block(payType)
    }.getOrElse(sessionExpired)
  }

  def onPageLoad(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val viewModel = new StatutoryPayWeeksViewModel(appConfig, statutoryType)

          val preparedForm = request.userAnswers.yourStatutoryWeeks match {
            case None => yourStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage)
            case Some(value) => yourStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage).fill(value)
          }
          Future.successful(Ok(yourStatutoryWeeks(appConfig, preparedForm, mode, viewModel)))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val viewModel = new StatutoryPayWeeksViewModel(appConfig, statutoryType)

          yourStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage).bindFromRequest().fold(
            (formWithErrors: Form[Int]) =>
              Future.successful(BadRequest(yourStatutoryWeeks(appConfig, formWithErrors, mode, viewModel))),
            (value) =>
              dataCacheConnector.save[Int](request.sessionId, YourStatutoryWeeksId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(YourStatutoryWeeksId, mode)(new UserAnswers(cacheMap))))
          )
      }
  }
}
