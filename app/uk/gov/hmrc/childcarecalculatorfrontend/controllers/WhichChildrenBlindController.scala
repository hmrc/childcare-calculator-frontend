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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenBlindForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichChildrenBlindId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenBlind

import scala.concurrent.Future

class WhichChildrenBlindController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      withValues {
        values =>
          val answer = request.userAnswers.whichChildrenBlind
          val preparedForm = answer match {
            case None => WhichChildrenBlindForm()
            case Some(value) => WhichChildrenBlindForm().fill(value)
          }
          Future.successful(Ok(whichChildrenBlind(appConfig, preparedForm, mode, options(values))))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      withValues {
        values =>
          WhichChildrenBlindForm(values.values.toSeq: _*).bindFromRequest().fold(
            (formWithErrors: Form[_]) => {
              Future.successful(BadRequest(whichChildrenBlind(appConfig, formWithErrors, mode, options(values))))
            },
            (value) => {
              dataCacheConnector.save[Set[Int]](request.sessionId, WhichChildrenBlindId.toString, value).map {
                cacheMap =>
                  Redirect(navigator.nextPage(WhichChildrenBlindId, mode)(new UserAnswers(cacheMap)))
              }
            }
          )
      }
  }

  private def options(values: Map[String, Int]): Map[String, String] =
    values.map {
      case (k, v) =>
        (k, v.toString)
    }

  private def withValues[A](block: Map[String, Int] => Future[Result])(implicit request: DataRequest[A]): Future[Result] = {
    request.userAnswers.aboutYourChild.map {
      aboutYourChild =>
        val values: Map[String, Int] = aboutYourChild.map {
          case (i, model) =>
            model.name -> i
        }
        block(values)
    }.getOrElse(Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,"withValues",Some(request.userAnswers)))))
  }
}
