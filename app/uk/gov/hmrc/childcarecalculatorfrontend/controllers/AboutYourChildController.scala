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
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AboutYourChildForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.AboutYourChildId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild

import scala.concurrent.Future

class AboutYourChildController @Inject()(
                                          appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          dataCacheConnector: DataCacheConnector,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          validateIndex: ChildIndexActionFilterFactory
                                        ) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode, childIndex: Int) = (getData andThen requireData andThen validateIndex(childIndex)) {
    implicit request =>
      request.userAnswers.noOfChildren.map {
        noOfChildren =>
          val preparedForm = request.userAnswers.aboutYourChild(childIndex) match {
            case None => AboutYourChildForm()
            case Some(value) => AboutYourChildForm().fill(value)
          }
          Ok(aboutYourChild(appConfig, preparedForm, mode, childIndex, noOfChildren))
      }.getOrElse(Redirect(routes.SessionExpiredController.onPageLoad()))
  }

  def onSubmit(mode: Mode, childIndex: Int) = (getData andThen requireData andThen validateIndex(childIndex)).async {
    implicit request =>
      AboutYourChildForm().bindFromRequest().fold(
        (formWithErrors: Form[AboutYourChild]) =>
          request.userAnswers.noOfChildren.map {
            noOfChildren =>
              Future.successful(BadRequest(aboutYourChild(appConfig, formWithErrors, mode, childIndex, noOfChildren)))
          }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))),
        (value) =>
          dataCacheConnector.replaceInSeq[AboutYourChild](
            request.sessionId,
            AboutYourChildId.toString,
            childIndex,
            value
          ).map {
            cacheMap =>
              Redirect(navigator.nextPage(AboutYourChildId, mode)(new UserAnswers(cacheMap)))
          }
      )
  }
}
