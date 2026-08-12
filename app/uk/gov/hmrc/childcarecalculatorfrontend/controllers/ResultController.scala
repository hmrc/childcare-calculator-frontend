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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ResultsViewModelId
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.services.{DataCacheService, ResultsService}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ResultController @Inject() (
    val
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    resultsService: ResultsService,
    result: result
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    request.userAnswers.location match {
      case Some(location) => renderResultsPage(location)
      case None           => Future.successful(Redirect(routes.LocationController.onPageLoad()))
    }
  }

  private def renderResultsPage(
      location: Location
  )(using request: DataRequest[?], hc: HeaderCarrier): Future[Result] =
    resultsService.getResultsViewModel(request.userAnswers, location).map { model =>
      dataCacheService.save(ResultsViewModelId, model)
      Ok(result(model))
    }

}
