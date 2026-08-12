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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AreYouSelfEmployedOrApprenticeForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.AreYouSelfEmployedOrApprenticeId
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.EmploymentStatus
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.areYouSelfEmployedOrApprentice
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AreYouSelfEmployedOrApprenticeController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    areYouSelfEmployedOrApprentice: areYouSelfEmployedOrApprentice
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[AnyContent] = request
    val preparedForm = request.userAnswers.areYouSelfEmployedOrApprentice match {
      case None        => AreYouSelfEmployedOrApprenticeForm()
      case Some(value) => AreYouSelfEmployedOrApprenticeForm().fill(value)
    }
    Ok(areYouSelfEmployedOrApprentice(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    AreYouSelfEmployedOrApprenticeForm()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[EmploymentStatus]) =>
          Future.successful(BadRequest(areYouSelfEmployedOrApprentice(formWithErrors))),
        value =>
          dataCacheService
            .save(AreYouSelfEmployedOrApprenticeId, value)
            .map(cacheMap => Redirect(navigator.nextPage(AreYouSelfEmployedOrApprenticeId)(new UserAnswers(cacheMap))))
      )
  }

}
