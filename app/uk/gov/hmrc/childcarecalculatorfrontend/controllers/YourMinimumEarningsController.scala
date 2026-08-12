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

import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.config.NmwConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.yourMinimumEarningsErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class YourMinimumEarningsController @Inject() (
    nmwConfig: NmwConfig,
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    yourMinimumEarnings: yourMinimumEarnings
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[AnyContent] = request
    request.userAnswers.location match {
      case None =>
        Redirect(routes.LocationController.onPageLoad())

      case Some(_) if request.userAnswers.yourAge.isEmpty =>
        logger.warn(
          s"Arrived at ${request.uri} without an age value, redirecting to ${routes.YourAgeController.onPageLoad().url}"
        )
        Redirect(routes.YourAgeController.onPageLoad())

      case Some(location) =>
        val earningsForAge =
          nmwConfig.getEarningsForAgeRange(LocalDate.now, request.userAnswers.yourAge)

        val preparedForm = request.userAnswers.yourMinimumEarnings match {
          case None        => BooleanForm(yourMinimumEarningsErrorKey, earningsForAge)
          case Some(value) => BooleanForm(yourMinimumEarningsErrorKey, earningsForAge).fill(value)
        }
        Ok(yourMinimumEarnings(preparedForm, earningsForAge, location))
    }
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    val earningsForAge =
      nmwConfig.getEarningsForAgeRange(LocalDate.now, request.userAnswers.yourAge)

    request.userAnswers.location match {
      case None => Future.successful(Redirect(routes.LocationController.onPageLoad()))
      case Some(location) =>
        BooleanForm(yourMinimumEarningsErrorKey, earningsForAge)
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(yourMinimumEarnings(formWithErrors, earningsForAge, location))),
            value =>
              dataCacheService
                .save(YourMinimumEarningsId, value)
                .map(cacheMap => Redirect(navigator.nextPage(YourMinimumEarningsId)(new UserAnswers(cacheMap))))
          )
    }
  }

}
