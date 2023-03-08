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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourStatutoryPayBeforeTaxId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryPayBeforeTax
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class YourStatutoryPayBeforeTaxController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        yourStatutoryPayBeforeTax: yourStatutoryPayBeforeTax)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  val errorKey = "yourStatutoryPayBeforeTax.error.notCompleted"

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def validateStatutoryPayType[A](block: (String) => Future[Result])
                                         (implicit request: DataRequest[A]): Future[Result] = {

    request.userAnswers.yourStatutoryPayType.map {
      payType => block(Messages(s"statutoryPayTypeLower.$payType"))
    }.getOrElse(sessionExpired("validateStatutoryPayType",Some(request.userAnswers)))
  }

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val preparedForm = request.userAnswers.yourStatutoryPayBeforeTax match {
            case None => BooleanForm(errorKey, statutoryType)
            case Some(value) => BooleanForm(errorKey, statutoryType).fill(value)
          }
          Future.successful(Ok(yourStatutoryPayBeforeTax(appConfig, preparedForm, mode, statutoryType)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          BooleanForm(errorKey, statutoryType).bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(yourStatutoryPayBeforeTax(appConfig, formWithErrors, mode, statutoryType))),
            value =>
              dataCacheConnector.save[Boolean](request.sessionId, YourStatutoryPayBeforeTaxId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(YourStatutoryPayBeforeTaxId, mode)(new UserAnswers(cacheMap))))
          )
      }
  }
}
