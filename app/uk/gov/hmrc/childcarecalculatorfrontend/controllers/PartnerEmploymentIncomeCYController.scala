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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{FormErrorHelper, PartnerEmploymentIncomeCYForm}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerEmploymentIncomeCYId
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.partnerEmploymentIncomeInvalidMaxEarningsErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerEmploymentIncomeCY
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PartnerEmploymentIncomeCYController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    taxYearInfo: TaxYearInfo,
    form: PartnerEmploymentIncomeCYForm,
    partnerEmploymentIncomeCY: partnerEmploymentIncomeCY
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with FormErrorHelper
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val preparedForm = request.userAnswers.partnerEmploymentIncomeCY match {
      case None        => form()
      case Some(value) => form().fill(value)
    }

    Ok(partnerEmploymentIncomeCY(appConfig, preparedForm, taxYearInfo))
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    val maxEarnings = request.userAnswers.partnerMaximumEarnings
    val boundForm   = form().bindFromRequest()

    validateForm(maxEarnings, boundForm).fold(
      (formWithErrors: Form[BigDecimal]) =>
        Future.successful(BadRequest(partnerEmploymentIncomeCY(appConfig, formWithErrors, taxYearInfo))),
      value =>
        dataCacheConnector
          .save[BigDecimal](request.sessionId, PartnerEmploymentIncomeCYId.toString, value)
          .map(cacheMap => Redirect(navigator.nextPage(PartnerEmploymentIncomeCYId)(new UserAnswers(cacheMap))))
    )
  }

  private def validateForm(maxEarnings: Option[Boolean], boundForm: Form[BigDecimal]) =
    if (boundForm.hasErrors) {
      boundForm
    } else {
      validateMaxIncomeEarnings(
        maxEarnings,
        appConfig.maxIncome,
        partnerEmploymentIncomeInvalidMaxEarningsErrorKey,
        boundForm
      )
    }

}
