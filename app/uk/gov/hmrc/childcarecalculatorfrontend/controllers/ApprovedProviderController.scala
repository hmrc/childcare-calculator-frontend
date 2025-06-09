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
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ApprovedProviderForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ApprovedProviderId
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.approvedProvider
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApprovedProviderController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    approvedProvider: approvedProvider
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val childcareCostsMaybeInFuture = checkIfUnsureAboutChildcareCosts(request.userAnswers)

    val preparedForm = request.userAnswers.approvedProvider match {
      case None        => ApprovedProviderForm()
      case Some(value) => ApprovedProviderForm().fill(value)
    }
    Ok(approvedProvider(appConfig, preparedForm, childcareCostsMaybeInFuture))
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    ApprovedProviderForm()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[String]) => {
          val childcareCostsMaybeInFuture = checkIfUnsureAboutChildcareCosts(request.userAnswers)
          Future.successful(BadRequest(approvedProvider(appConfig, formWithErrors, childcareCostsMaybeInFuture)))
        },
        value =>
          dataCacheConnector
            .save[String](request.sessionId, ApprovedProviderId.toString, value)
            .map(cacheMap => Redirect(navigator.nextPage(ApprovedProviderId)(new UserAnswers(cacheMap))))
      )
  }

  private def checkIfUnsureAboutChildcareCosts(answers: UserAnswers): Boolean =
    answers.childcareCosts.getOrElse("no") == YesNoNotYetEnum.NOTYET.toString

}
