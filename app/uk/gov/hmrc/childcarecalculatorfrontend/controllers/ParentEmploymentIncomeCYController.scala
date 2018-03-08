/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{BooleanForm, ParentEmploymentIncomeCYForm}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ParentEmploymentIncomeCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, YesNoEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.parentEmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
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
                                                    taxYearInfo: TaxYearInfo) extends FormErrorHelper with FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.parentEmploymentIncomeCY match {
        case None => form()
        case Some(value) => form().fill(value)
      }
      Ok(parentEmploymentIncomeCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      val boundForm = form().bindFromRequest()
      val errorKeyInvalidMaxEarnings: String = parentEmploymentIncomeInvalidMaxEarningsErrorKey
      val errorKeyInvalid: String = parentEmploymentIncomeInvalidErrorKey

      validateMaxIncomeEarnings(errorKeyInvalidMaxEarnings, errorKeyInvalid, boundForm, request.userAnswers).fold(

        (formWithErrors: Form[BigDecimal]) =>
          Future.successful(BadRequest(parentEmploymentIncomeCY(appConfig, formWithErrors, mode, taxYearInfo))),
        (value) =>
          dataCacheConnector.save[BigDecimal](request.sessionId, ParentEmploymentIncomeCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ParentEmploymentIncomeCYId, mode)(new UserAnswers(cacheMap))))

      )

  }

//  private def validateMaxIncomeEarnings(boundForm: Form[BigDecimal], userAnswers: UserAnswers) = {
//
//    val maxValueFalseMaxEarnings = BigDecimal(100000)
//    val maxValueTrueMaxEarnings = BigDecimal(1000000)
//
//    userAnswers.yourMaximumEarnings match {
//      case Some(maxEarnings) if !boundForm.hasErrors => {
//        val inputtedParentEmploymentIncomeValue = boundForm.value.getOrElse(BigDecimal(0))
//
//        if (inputtedParentEmploymentIncomeValue >= maxValueFalseMaxEarnings && !maxEarnings) {
//
//          boundForm.withError("value", parentEmploymentIncomeInvalidMaxEarningsErrorKey)
//        }
//        else if (inputtedParentEmploymentIncomeValue >= maxValueTrueMaxEarnings && maxEarnings) {
//          boundForm.withError("value", parentEmploymentIncomeInvalidErrorKey)
//        }
//        else {
//          boundForm
//        }
//      }
//      case _ => {
//        boundForm
//      }
//    }
//  }
}