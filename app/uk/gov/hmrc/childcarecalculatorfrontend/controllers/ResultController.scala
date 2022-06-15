/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.MoreInfoService
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ResultsViewModelId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.services.ResultsService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResultController @Inject()(val appConfig: FrontendAppConfig,
                                 mcc: MessagesControllerComponents,
                                 dataCacheConnector: DataCacheConnector,
                                 getData: DataRetrievalAction,
                                 requireData: DataRequiredAction,
                                 resultsService: ResultsService,
                                 moreInfoResults: MoreInfoService,
                                 utils: Utils,
                                 result: result) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      request.userAnswers.location match {
        case Some(location) => renderResultsPage(hideTC = request.userAnswers.notEligibleForTaxCredits, location)
        case None           => Future.successful(Redirect(routes.LocationController.onPageLoad(NormalMode)))
      }
  }

  def onPageLoadHideTC: Action[AnyContent] = (getData andThen requireData).async { implicit request =>
    request.userAnswers.location match {
      case Some(location) => renderResultsPage(hideTC = true, location)
      case None => Future.successful(Redirect(routes.LocationController.onPageLoad(NormalMode)))
    }
  }

  private def renderResultsPage(hideTC: Boolean, location: Location.Value)(implicit request: DataRequest[_], hc: HeaderCarrier) = {
    resultsService.getResultsViewModel(request.userAnswers, location).map(model => {
      dataCacheConnector.save[ResultsViewModel](request.sessionId, ResultsViewModelId.toString, model)

      implicit val lang: Lang = request.lang
      val amendedModel = if (hideTC) model.copy(tc = None) else model
      Ok(result(appConfig, amendedModel,
        moreInfoResults.getSchemeContent(location, amendedModel, hideTC)(request.lang),
        moreInfoResults.getSummary(location, amendedModel)(request.lang), utils,
        hideTC)
      )
    })
  }


}
