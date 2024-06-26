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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers.benefits

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.benefits.DoYouOrPartnerGetBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.doYouOrPartnerGetBenefitsErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits.doYouOrPartnerGetBenefits
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DoYouOrPartnerGetBenefitsController @Inject()(mcc: MessagesControllerComponents,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    doYouOrPartnerGetBenefits: doYouOrPartnerGetBenefits
                                                   )(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.doYouOrPartnerGetBenefits match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(doYouOrPartnerGetBenefits(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BooleanForm(doYouOrPartnerGetBenefitsErrorKey).bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(doYouOrPartnerGetBenefits(formWithErrors))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, DoYouOrPartnerGetBenefitsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(DoYouOrPartnerGetBenefitsId, NormalMode)(new UserAnswers(cacheMap))))
      )
  }
}
