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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{EmploymentIncomeCYForm, FormErrorHelper}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.EmploymentIncomeCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.employmentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class EmploymentIncomeCYController @Inject()(appConfig: FrontendAppConfig,
                                             mcc: MessagesControllerComponents,
                                             dataCacheConnector: DataCacheConnector,
                                             navigator: Navigator,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             form: EmploymentIncomeCYForm,
                                             taxYearInfo: TaxYearInfo,
                                             employmentIncomeCY: employmentIncomeCY)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with FormErrorHelper {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.employmentIncomeCY match {
        case None => form()
        case Some(value) => form().fill(value)
      }
      Ok(employmentIncomeCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val maxEarnings = maximumEarnings(request.userAnswers)
      val boundForm = form().bindFromRequest()

      validateForm(boundForm, maxEarnings).fold((formWithErrors: Form[EmploymentIncomeCY]) =>
        Future.successful(BadRequest(employmentIncomeCY(appConfig, formWithErrors, mode, taxYearInfo))),
        value =>
          dataCacheConnector.save[EmploymentIncomeCY](request.sessionId, EmploymentIncomeCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(EmploymentIncomeCYId, mode)(new UserAnswers(cacheMap))))
      )
  }

  private def validateForm(boundForm: Form[EmploymentIncomeCY], maxEarnings : Option[Boolean]) = {
    if (boundForm.hasErrors) {
      boundForm
    } else {
      validateBothMaxIncomeEarnings(maxEarnings,
        appConfig.maxIncome,
        boundForm)
    }
  }

  private def maximumEarnings(answers: UserAnswers) = {
    answers.whoIsInPaidEmployment match {
      case Some(You) => answers.yourMaximumEarnings
      case Some(Partner) => answers.partnerMaximumEarnings
      case Some(Both) => answers.eitherOfYouMaximumEarnings
      case _ => None
    }
  }
}
