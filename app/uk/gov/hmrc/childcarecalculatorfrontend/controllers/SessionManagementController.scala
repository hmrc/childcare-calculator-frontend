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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.SessionDataClearId
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SessionManagementController @Inject() (
    val appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def extendSession: Action[AnyContent] = Action.async {
    Future.successful(Ok("OK"))
  }

  def clearSessionData: Action[AnyContent] = getData.async { implicit request =>
    // value has been hard coded as "sessionData" as there is no form associated with this controller
    dataCacheConnector
      .save[String](request.sessionId, SessionDataClearId.toString, "sessionData")
      .map(cacheMap => Redirect(navigator.nextPage(SessionDataClearId)(new UserAnswers(cacheMap))))
  }

}
