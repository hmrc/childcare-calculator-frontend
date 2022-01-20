/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoHasChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhoHasChildcareCostsController @Inject()(
                                                appConfig: FrontendAppConfig,
                                                mcc: MessagesControllerComponents,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                whoHasChildcareCosts: whoHasChildcareCosts) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      withValues {
        values =>
          val answer = request.userAnswers.whoHasChildcareCosts
          val childrenUnderSixteen = request.userAnswers.childrenBelow16AndExactly16Disabled
          val preparedForm = answer match {
            case None => WhoHasChildcareCostsForm()
            case Some(value) => WhoHasChildcareCostsForm().fill(value)
          }
          Future.successful(Ok(whoHasChildcareCosts(appConfig, preparedForm, mode, options(values,childrenUnderSixteen).toSeq)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      withValues {
        values =>
          val childrenUnderSixteen = request.userAnswers.childrenBelow16AndExactly16Disabled
          WhoHasChildcareCostsForm(values.values.toSeq: _*).bindFromRequest().fold(
            (formWithErrors: Form[_]) => {
              Future.successful(BadRequest(whoHasChildcareCosts(appConfig, formWithErrors, mode, options(values,childrenUnderSixteen).toSeq)))
            },
            value => {
              dataCacheConnector.save[Set[Int]](request.sessionId, WhoHasChildcareCostsId.toString, value).map {
                cacheMap =>
                  Redirect(navigator.nextPage(WhoHasChildcareCostsId, mode)(new UserAnswers(cacheMap)))
              }
            }
          )
      }
  }

  private def options(values: Map[String, Int], childrenUnder16: Seq[Int]): Map[String, String] = {
    values.filter(c=> childrenUnder16.contains(c._2)).map {
      case (k, v) => (k, v.toString)
    }
  }

  private def withValues[A](block: Map[String, Int] => Future[Result])(implicit request: DataRequest[A]): Future[Result] = {
    request.userAnswers.aboutYourChild.map {
      aboutYourChild =>
        val values: Map[String, Int] = aboutYourChild.map {
          case (i, model) =>
            model.name -> i
        }
        block(values)

      }.getOrElse(Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,"withValues",Some(request.userAnswers),request.uri))))
  }
}
