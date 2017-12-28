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
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YouAnyTheseBenefitsIdCY
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

import scala.concurrent.Future

class YouAnyTheseBenefitsCYController @Inject()(appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                taxYearInfo: TaxYearInfo) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.youAnyTheseBenefits match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(youAnyTheseBenefitsCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val boundForm = BooleanForm(youAnyTheseBenefitsCYErrorKey).bindFromRequest()

      validateCarersAllowance(boundForm, request.userAnswers).fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(youAnyTheseBenefitsCY(appConfig, formWithErrors, mode, taxYearInfo))),
        (value) =>
          dataCacheConnector.save[Boolean](request.sessionId, YouAnyTheseBenefitsIdCY.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(YouAnyTheseBenefitsIdCY, mode)(new UserAnswers(cacheMap))))
      )
  }

  /**
    * Checks whether parent has Carer Allowance benefits,
    * if yes then populate the form with error else return the original form
    *
    * @param boundForm
    * @param userAnswers
    * @return
    */
  private def validateCarersAllowance(boundForm: Form[Boolean], userAnswers: UserAnswers) = {
      userAnswers.whichBenefitsYouGet match {
      case Some(benefits) if !boundForm.hasErrors => {
        val hasCarerAllowance = benefits.exists( x => x.equals(WhichBenefitsEnum.CARERSALLOWANCE.toString))
        val youAnyBenefitsValue = boundForm.value.getOrElse(true)

        if(hasCarerAllowance && !youAnyBenefitsValue) {
            boundForm.withError("value", youAnyTheseBenefitsCYCarerAllowanceErrorKey)
        } else {
          boundForm
        }
      }
      case _ => boundForm
    }

  }
}
