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

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.DataRetrievalAction
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.LocationId
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.location
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LocationController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    location: location
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData { request =>
    given OptionalDataRequest[AnyContent] = request
    val preparedForm = request.userAnswers.flatMap(x => x.location) match {
      case None        => LocationForm()
      case Some(value) => LocationForm().fill(value)
    }
    Ok(location(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = getData.async { request =>
    given OptionalDataRequest[AnyContent] = request
    LocationForm()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[Location]) => Future.successful(BadRequest(location(formWithErrors))),
        value =>
          dataCacheService
            .save(LocationId, value)
            .map(cacheMap => Redirect(navigator.nextPage(LocationId)(new UserAnswers(cacheMap))))
      )
  }

}
