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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowMuchBothPayPensionPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.HowMuchBothPayPensionPYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPensionPY, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howMuchBothPayPensionPY
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HowMuchBothPayPensionPYController @Inject()(appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  howMuchBothPayPensionPY: howMuchBothPayPensionPY) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.howMuchBothPayPensionPY match {
        case None => HowMuchBothPayPensionPYForm()
        case Some(value) => HowMuchBothPayPensionPYForm().fill(value)
      }
      Ok(howMuchBothPayPensionPY(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      HowMuchBothPayPensionPYForm().bindFromRequest().fold(
        (formWithErrors: Form[HowMuchBothPayPensionPY]) =>
          Future.successful(BadRequest(howMuchBothPayPensionPY(appConfig, formWithErrors, mode))),
        value =>
          dataCacheConnector.save[HowMuchBothPayPensionPY](request.sessionId, HowMuchBothPayPensionPYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(HowMuchBothPayPensionPYId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
