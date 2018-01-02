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

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ResultsViewModelId
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourResults

@Singleton
class AboutYourResultsController @Inject()(val appConfig: FrontendAppConfig,
                                      val messagesApi: MessagesApi,
                                      dataCacheConnector: DataCacheConnector,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData).async { implicit request =>

  val resultViewModel = dataCacheConnector.getEntry[ResultsViewModel](request.sessionId, ResultsViewModelId.toString)

    resultViewModel.map{
        case Some(model) => Ok(aboutYourResults(appConfig, model))
        case _ =>  Redirect(routes.SessionExpiredController.onPageLoad())
    }.recover{
      case _ => Redirect(routes.SessionExpiredController.onPageLoad())
    }
  }
}
