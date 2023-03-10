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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerAnyOtherIncomeThisYearId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerAnyOtherIncomeThisYear
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class PartnerAnyOtherIncomeThisYearController @Inject()(appConfig: FrontendAppConfig,
                                                        mcc: MessagesControllerComponents,
                                                        dataCacheConnector: DataCacheConnector,
                                                        navigator: Navigator,
                                                        getData: DataRetrievalAction,
                                                        requireData: DataRequiredAction,
                                                        taxYearInfo: TaxYearInfo,
                                                        partnerAnyOtherIncomeThisYear: partnerAnyOtherIncomeThisYear)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.partnerAnyOtherIncomeThisYear match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(partnerAnyOtherIncomeThisYear(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BooleanForm("partnerAnyOtherIncomeThisYear.error.notCompleted").bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(partnerAnyOtherIncomeThisYear(appConfig, formWithErrors, mode, taxYearInfo))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, PartnerAnyOtherIncomeThisYearId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PartnerAnyOtherIncomeThisYearId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
