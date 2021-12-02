/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.mvc.{MessagesControllerComponents, RequestHeader, Result, Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AboutYourChildForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.AboutYourChildId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AboutYourChildController @Inject()(
                                          appConfig: FrontendAppConfig,
                                          mcc: MessagesControllerComponents,
                                          dataCacheConnector: DataCacheConnector,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          aboutYourChild: aboutYourChild
                                        ) extends FrontendController(mcc) with I18nSupport with MapFormats {

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def validateIndex[A](childIndex: Int)(block: Int => Future[Result])
                              (implicit request: DataRequest[A]): Future[Result] = {
    request.userAnswers.noOfChildren.map {
      noOfChildren =>
        if (childIndex >= 0 && childIndex < noOfChildren) {
          block(noOfChildren)
        } else {
          sessionExpired("validateIndex",Some(request.userAnswers))
        }
    }.getOrElse(sessionExpired("validateIndex",None))
  }

  def onPageLoad(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        noOfChildren =>
          val preparedForm = request.userAnswers.aboutYourChild(childIndex) match {
            case None => AboutYourChildForm(childIndex, noOfChildren)
            case Some(value) => AboutYourChildForm(childIndex, noOfChildren, request.userAnswers.aboutYourChild).fill(value)
          }
          Future.successful(Ok(aboutYourChild(appConfig, preparedForm, mode, childIndex, noOfChildren)))
      }
  }

  def onSubmit(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        noOfChildren =>
          AboutYourChildForm(childIndex, noOfChildren, request.userAnswers.aboutYourChild).bindFromRequest().fold(
            (formWithErrors: Form[AboutYourChild]) =>
              Future.successful(BadRequest(aboutYourChild(appConfig, formWithErrors, mode, childIndex, noOfChildren))),
              value =>
          dataCacheConnector.saveInMap[Int, AboutYourChild](
            request.sessionId,
            AboutYourChildId.toString,
            childIndex,
            value
          ).map {
            cacheMap =>
              Redirect(navigator.nextPage(AboutYourChildId(childIndex), mode)(new UserAnswers(cacheMap)))
          }
        )
      }
  }
}
