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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichBenefitsYouGetId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsYouGet
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhichBenefitsYouGetController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        whichBenefitsYouGet: whichBenefitsYouGet) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      implicit val lang: Lang = request.lang
      val preparedForm = request.userAnswers.whichBenefitsYouGet match {
        case None => WhichBenefitsYouGetForm()
        case Some(value) => WhichBenefitsYouGetForm().fill(value)
      }
      Ok(whichBenefitsYouGet(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      implicit val lang: Lang = request.lang
      WhichBenefitsYouGetForm().bindFromRequest().fold(
        (formWithErrors: Form[Set[String]]) => {
          Future.successful(BadRequest(whichBenefitsYouGet(appConfig, formWithErrors, mode)))
        },
        value => {
          dataCacheConnector.save[Set[String]](request.sessionId, WhichBenefitsYouGetId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(WhichBenefitsYouGetId, mode)(new UserAnswers(cacheMap))))
        }
      )
  }
}
