/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ApprovedProviderForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ApprovedProviderId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.approvedProvider

import scala.concurrent.Future

class ApprovedProviderController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.approvedProvider match {
        case None => ApprovedProviderForm()
        case Some(value) => ApprovedProviderForm().fill(value)
      }
      Ok(approvedProvider(appConfig, preparedForm,YesNoNotYetEnum.YES, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      ApprovedProviderForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(approvedProvider(appConfig, formWithErrors,YesNoNotYetEnum.YES, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, ApprovedProviderId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ApprovedProviderId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
