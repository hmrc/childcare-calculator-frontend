/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerStatutoryPayBeforeTaxId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryPayBeforeTax

import scala.concurrent.Future

class PartnerStatutoryPayBeforeTaxController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  val errorKey = "partnerStatutoryPayBeforeTax.error.notCompleted"

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def validateStatutoryPayType[A](block: (String) => Future[Result])
                                         (implicit request: DataRequest[A]): Future[Result] = {

    request.userAnswers.partnerStatutoryPayType.map {
      payType => block(Messages(s"statutoryPayTypeLower.$payType"))
    }.getOrElse(sessionExpired("validateStatutoryPayType",Some(request.userAnswers)))
  }

  def onPageLoad(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val preparedForm = request.userAnswers.partnerStatutoryPayBeforeTax match {
            case None => BooleanForm(errorKey, statutoryType)
            case Some(value) => BooleanForm(errorKey, statutoryType).fill(value)
          }
          Future.successful(Ok(partnerStatutoryPayBeforeTax(appConfig, preparedForm, mode, statutoryType)))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          BooleanForm(errorKey, statutoryType).bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(partnerStatutoryPayBeforeTax(appConfig, formWithErrors, mode, statutoryType))),
            (value) =>
              dataCacheConnector.save[Boolean](request.sessionId, PartnerStatutoryPayBeforeTaxId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(PartnerStatutoryPayBeforeTaxId, mode)(new UserAnswers(cacheMap))))
      )
    }
  }
}
