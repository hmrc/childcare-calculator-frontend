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
import play.api.mvc.{Request, Result}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildrenDisabilityBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childrenDisabilityBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childDisabilityBenefits

import scala.concurrent.Future

class ChildrenDisabilityBenefitsController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      withData {
        case (noOfChildren, name) =>
          val preparedForm = request.userAnswers.childrenDisabilityBenefits match {
            case None => BooleanForm()
            case Some(value) => BooleanForm().fill(value)
          }
          Future.successful(Ok(view(appConfig, preparedForm, name, mode, noOfChildren)))
      }
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      withData {
        case (noOfChildren, name) =>
          BooleanForm("childrenDisabilityBenefits.error.notCompleted").bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(view(appConfig, formWithErrors, name, mode, noOfChildren))),
            (value) =>
              dataCacheConnector.save[Boolean](request.sessionId, ChildrenDisabilityBenefitsId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(ChildrenDisabilityBenefitsId, mode)(new UserAnswers(cacheMap))))
          )
      }
  }

  private def withData[A](block: (Int, String) => Future[Result])
                         (implicit request: DataRequest[A]): Future[Result] = {
    for {
      noOfChildren <- request.userAnswers.noOfChildren
      name         <- request.userAnswers.aboutYourChild(0).map(_.name)
    } yield block(noOfChildren, name)
  }.getOrElse(Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,"withData",Some(request.userAnswers),request.uri))))

  private def view(appConfig: FrontendAppConfig, form: Form[Boolean], name: String, mode: Mode, noOfChildren: Int)
                  (implicit request: Request[_]): Html =
    if (noOfChildren == 1) {
      childDisabilityBenefits(appConfig, form, name, mode)
    } else {
      childrenDisabilityBenefits(appConfig, form, mode)
    }
}
