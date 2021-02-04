/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BothBenefitsIncomePYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.BothBenefitsIncomePYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BothBenefitsIncomePY, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothBenefitsIncomePY
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BothBenefitsIncomePYController @Inject()(appConfig: FrontendAppConfig,
                                               mcc: MessagesControllerComponents,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.bothBenefitsIncomePY match {
        case None => BothBenefitsIncomePYForm()
        case Some(value) => BothBenefitsIncomePYForm().fill(value)
      }
      Ok(bothBenefitsIncomePY(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BothBenefitsIncomePYForm().bindFromRequest().fold(
        (formWithErrors: Form[BothBenefitsIncomePY]) =>
          Future.successful(BadRequest(bothBenefitsIncomePY(appConfig, formWithErrors, mode))),
        value =>
          dataCacheConnector.save[BothBenefitsIncomePY](request.sessionId, BothBenefitsIncomePYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BothBenefitsIncomePYId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
