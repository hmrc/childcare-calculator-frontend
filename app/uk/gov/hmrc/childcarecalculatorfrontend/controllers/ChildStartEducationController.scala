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

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildStartEducationForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildStartEducationId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childStartEducation

import scala.concurrent.Future

class ChildStartEducationController @Inject() (
                                                appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction
                                             ) extends FrontendController with I18nSupport with MapFormats {

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers)))

  private def validateIndex[A](childIndex: Int)(block: (String, LocalDate) => Future[Result])
                              (implicit request: DataRequest[A]): Future[Result] = {
    for {
      child <- request.userAnswers.aboutYourChild(childIndex)
      name  <- request.userAnswers.childApprovedEducation(childIndex).flatMap {
        case true  => Some(child.name)
        case false => None
      }
    } yield block(name, child.dob)
  }.getOrElse(sessionExpired("validateIndex",Some(request.userAnswers)))

  def onPageLoad(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        (name, dob) =>
          val preparedForm = request.userAnswers.childStartEducation(childIndex) match {
            case None => ChildStartEducationForm(dob)
            case Some(value) => ChildStartEducationForm(dob).fill(value)
          }
          Future.successful(Ok(childStartEducation(appConfig, preparedForm, mode, childIndex, name)))
      }
  }

  def onSubmit(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        (name, dob) =>
          ChildStartEducationForm(dob).bindFromRequest().fold(
            (formWithErrors: Form[LocalDate]) =>
              Future.successful(BadRequest(childStartEducation(appConfig, formWithErrors, mode, childIndex, name))),
            (value) =>
              dataCacheConnector.saveInMap[Int, LocalDate](
                request.sessionId,
                ChildStartEducationId.toString,
                childIndex,
                value
              ).map {
                cacheMap =>
                  Redirect(navigator.nextPage(ChildStartEducationId(childIndex), mode)(new UserAnswers(cacheMap)))
              }
          )
      }
  }
}
