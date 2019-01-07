/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{BooleanForm, ParentEmploymentIncomeCYForm}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ParentEmploymentIncomeCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, YesNoEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.parentEmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{parentEmploymentIncomeInvalidErrorKey, _}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.FormErrorHelper

import scala.concurrent.Future

class ParentEmploymentIncomeCYController @Inject()(
                                                    appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    form: ParentEmploymentIncomeCYForm,
                                                    taxYearInfo: TaxYearInfo,
                                                    formErrorHelper: FormErrorHelper) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.parentEmploymentIncomeCY match {
        case None => form()
        case Some(value) => form().fill(value)
      }
      Ok(parentEmploymentIncomeCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val maxEarnings = request.userAnswers.yourMaximumEarnings
      val boundForm = form().bindFromRequest()

      validateForm(maxEarnings, boundForm).fold((formWithErrors: Form[BigDecimal]) =>
        Future.successful(BadRequest(parentEmploymentIncomeCY(appConfig, formWithErrors, mode, taxYearInfo))),
        (value) =>
          dataCacheConnector.save[BigDecimal](request.sessionId, ParentEmploymentIncomeCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ParentEmploymentIncomeCYId, mode)(new UserAnswers(cacheMap)))))
  }

  private def validateForm(maxEarnings: Option[Boolean], boundForm: Form[BigDecimal]) =
    if (boundForm.hasErrors) {
      boundForm
    } else {
      formErrorHelper.validateMaxIncomeEarnings(maxEarnings,
        appConfig.maxIncome,
        parentEmploymentIncomeInvalidMaxEarningsErrorKey,
        boundForm)
    }
}