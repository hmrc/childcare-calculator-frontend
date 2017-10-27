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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoHasChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts

import scala.concurrent.Future

class WhoHasChildcareCostsController @Inject()(
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
        val answer = request.userAnswers.whoHasChildcareCosts
        val preparedForm = answer match {
          case None => WhoHasChildcareCostsForm()
          case Some(value) => WhoHasChildcareCostsForm().fill(value)
        }
        Future.successful(Ok(whoHasChildcareCosts(appConfig, preparedForm, mode, values)))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      withValues {
        values =>
      WhoHasChildcareCostsForm(values.values.toSeq: _*).bindFromRequest().fold(
        (formWithErrors: Form[Set[String]]) => {
          Future.successful(BadRequest(whoHasChildcareCosts(appConfig, formWithErrors, mode, values)))
        },
        (value) => {
          dataCacheConnector.save[Set[String]](request.sessionId, WhoHasChildcareCostsId.toString, value).map {
            cacheMap =>
              Redirect(navigator.nextPage(WhoHasChildcareCostsId, mode)(new UserAnswers(cacheMap)))
          }
        }
      )
      }
  }

  private def withValues[A](block: Map[String, String] => Future[Result])(implicit request: DataRequest[A]): Future[Result]= {
    request.userAnswers.aboutYourChild.map {
      aboutYourChild =>
        val values: Map[String, String] = aboutYourChild.map {
          case (i, model) =>
            model.name -> i.toString
        }
        block(values)
      }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }
}
