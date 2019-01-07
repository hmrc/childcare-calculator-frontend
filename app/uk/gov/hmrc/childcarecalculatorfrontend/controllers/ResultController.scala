/*
 * Copyright 2019 HM Revenue & Customs
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
import services.MoreInfoServiceInterface
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ResultsViewModelId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.services.ResultsService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future


@Singleton
class ResultController @Inject()(val appConfig: FrontendAppConfig,
                                 val messagesApi: MessagesApi,
                                 dataCacheConnector: DataCacheConnector,
                                 getData: DataRetrievalAction,
                                 requireData: DataRequiredAction,
                                 resultsService: ResultsService,
                                 moreInfoResults: MoreInfoServiceInterface,
                                 utils: Utils) extends FrontendController with I18nSupport {


  def onPageLoad(): Action[AnyContent] = (getData andThen requireData).async { implicit request =>
    request.userAnswers.location match {
      case Some(location) =>  {
        resultsService.getResultsViewModel(request.userAnswers,location).map(model => {
          dataCacheConnector.save[ResultsViewModel](request.sessionId, ResultsViewModelId.toString, model)
          Ok(result(appConfig, model, moreInfoResults.getSchemeContent(location, model), moreInfoResults.getSummary(location, model), utils))
        })
      }
      case None => Future.successful(Redirect(routes.LocationController.onPageLoad(NormalMode)))
    }
  }
}
