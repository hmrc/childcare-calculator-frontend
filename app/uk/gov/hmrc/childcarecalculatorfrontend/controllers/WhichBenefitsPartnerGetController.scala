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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{BooleanForm, WhichBenefitsPartnerGetForm}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichBenefitsPartnerGetId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsPartnerGet
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class WhichBenefitsPartnerGetController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        whichBenefitsPartnerGet: whichBenefitsPartnerGet)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      request.userAnswers.location match {
        case None =>
          Redirect(routes.LocationController.onPageLoad(mode))

        case Some(location) =>
          val preparedForm = request.userAnswers.whichBenefitsPartnerGet match {
            case None => WhichBenefitsPartnerGetForm(location)
            case Some(value) => WhichBenefitsPartnerGetForm(location).fill(value)
          }
          Ok(whichBenefitsPartnerGet(appConfig, preparedForm, mode, location))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      if (request.userAnswers.location.isEmpty) {
        Future.successful(Redirect(routes.LocationController.onPageLoad(mode)))
      } else {
        val location = request.userAnswers.location.get
        WhichBenefitsPartnerGetForm(location).bindFromRequest().fold(
          (formWithErrors: Form[Set[String]]) => {
            Future.successful(BadRequest(whichBenefitsPartnerGet(appConfig, formWithErrors, mode, location)))
          },
          value => {
            dataCacheConnector.save[Set[String]](request.sessionId, WhichBenefitsPartnerGetId.toString, value).map(cacheMap =>
              Redirect(navigator.nextPage(WhichBenefitsPartnerGetId, mode)(new UserAnswers(cacheMap))))
          }
        )
      }
  }
}
