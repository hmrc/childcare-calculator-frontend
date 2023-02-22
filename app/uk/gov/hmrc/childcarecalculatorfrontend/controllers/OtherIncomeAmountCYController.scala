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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.OtherIncomeAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.OtherIncomeAmountCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, OtherIncomeAmountCY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.otherIncomeAmountCY
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OtherIncomeAmountCYController @Inject()(appConfig: FrontendAppConfig,
                                              mcc: MessagesControllerComponents,
                                              dataCacheConnector: DataCacheConnector,
                                              navigator: Navigator,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              form : OtherIncomeAmountCYForm,
                                              otherIncomeAmountCY: otherIncomeAmountCY) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.otherIncomeAmountCY match {
        case None => form()
        case Some(value) => form().fill(value)
      }
      Ok(otherIncomeAmountCY(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      form().bindFromRequest().fold(
        (formWithErrors: Form[OtherIncomeAmountCY]) =>
          Future.successful(BadRequest(otherIncomeAmountCY(appConfig, formWithErrors, mode))),
        value =>
          dataCacheConnector.save[OtherIncomeAmountCY](request.sessionId, OtherIncomeAmountCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(OtherIncomeAmountCYId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
