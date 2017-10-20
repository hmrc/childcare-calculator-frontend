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
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildApprovedEducationId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childApprovedEducation
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class ChildApprovedEducationController @Inject() (
                                                   appConfig: FrontendAppConfig,
                                                   override val messagesApi: MessagesApi,
                                                   dataCacheConnector: DataCacheConnector,
                                                   navigator: Navigator,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction
                                                ) extends FrontendController with I18nSupport with MapFormats {

  private val sessionExpired: Future[Result] =
    Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

  private def validateIndex[A](childIndex: Int)(block: Map[Int, String] => Future[Result])
                              (implicit request: DataRequest[A]): Future[Result] = {
    request.userAnswers.childrenOver16.map {
      childrenOver16 =>
        if (childrenOver16.isDefinedAt(childIndex)) {
          block(childrenOver16)
        } else {
          sessionExpired
        }
    }.getOrElse(sessionExpired)
  }

  def onPageLoad(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        childrenOver16 =>
          val preparedForm = request.userAnswers.childApprovedEducation match {
            case None => BooleanForm()
            case Some(value) => BooleanForm().fill(value)
          }
          Future.successful(Ok(childApprovedEducation(appConfig, preparedForm, mode, childIndex)))
      }
  }

  def onSubmit(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        childrenOver16 =>
          BooleanForm().bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(childApprovedEducation(appConfig, formWithErrors, mode, childIndex))),
            (value) =>
              dataCacheConnector.saveInMap[Int, Boolean](
                request.sessionId,
                ChildApprovedEducationId.toString,
                childIndex,
                value
              ).map {
                cacheMap =>
                  Redirect(navigator.nextPage(ChildApprovedEducationId, mode)(new UserAnswers(cacheMap)))
              }
          )
      }
  }
}
