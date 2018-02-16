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


import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig}

import scala.concurrent.Future

class SessionManagementController @Inject()(val appConfig: FrontendAppConfig,
                                            getData: DataRetrievalAction,
                                            dataCacheConnector: DataCacheConnector,
                                            val messagesApi: MessagesApi,
                                             utils: Utils) extends FrontendController with I18nSupport {

  def sessionExtend = Action.async {
    Future.successful(Ok("OK"))
  }

  def sessionClearData = Action.async {
    Future.successful(Ok("OK"))
  }
//  def sessionClearData = getData.async {
//    implicit request =>
//     request.userAnswers.map{
//        x => {
//            dataCacheConnector.save[Location.Value](request.sessionId, LocationId.toString, x).map(cacheMap =>
//            Redirect(navigator.nextPage(LocationId, mode)(new UserAnswers(cacheMap))))
//        }
//      }
//  }
}

//    implicit request =>
//        (formWithErrors: Form[_]) =>
//          Future.successful(BadRequest(location(appConfig, formWithErrors, mode))),
//        (value) =>
//          dataCacheConnector.save[Location.Value](request.sessionId, LocationId.toString, value).map(cacheMap =>
//            Redirect(navigator.nextPage(LocationId, mode)(new UserAnswers(cacheMap))))
//

