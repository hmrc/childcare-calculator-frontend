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
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildApprovedEducationId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childApprovedEducation
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class ChildApprovedEducationController @Inject() (
                                                   appConfig: FrontendAppConfig,
                                                   mcc: MessagesControllerComponents,
                                                   dataCacheConnector: DataCacheConnector,
                                                   navigator: Navigator,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   childApprovedEducation: childApprovedEducation
                                                )(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with MapFormats {

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def validateIndex[A](childIndex: Int)(block: String => Future[Result])
                              (implicit request: DataRequest[A]): Future[Result] = {
    request.userAnswers.childrenOver16.flatMap {
      childrenOver16 =>
        childrenOver16.get(childIndex).map(child => block(child.name))
    }.getOrElse(sessionExpired("validateIndex",Some(request.userAnswers)))
  }

  def onPageLoad(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        name =>
          val preparedForm = request.userAnswers.childApprovedEducation(childIndex) match {
            case None => BooleanForm()
            case Some(value) => BooleanForm().fill(value)
          }
          Future.successful(Ok(childApprovedEducation(appConfig, preparedForm, mode, childIndex, name)))
      }
  }

  def onSubmit(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        name =>
          BooleanForm("childApprovedEducation.error.notCompleted", name).bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(childApprovedEducation(appConfig, formWithErrors, mode, childIndex, name))),
            value =>
              dataCacheConnector.saveInMap[Int, Boolean](
                request.sessionId,
                ChildApprovedEducationId.toString,
                childIndex,
                value
              ).map {
                cacheMap =>
                  Redirect(navigator.nextPage(ChildApprovedEducationId(childIndex), mode)(new UserAnswers(cacheMap)))
              }
          )
      }
  }
}
