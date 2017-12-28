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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerAnyTheseBenefitsCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerAnyTheseBenefitsCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

import scala.concurrent.Future

class PartnerAnyTheseBenefitsCYController @Inject()(appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    taxYearInfo: TaxYearInfo) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.partnerAnyTheseBenefitsCY match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(partnerAnyTheseBenefitsCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val boundForm = BooleanForm(partnerAnyTheseBenefitsCYErrorKey).bindFromRequest()

      errorCheckForCarersAllowance(boundForm, request.userAnswers).fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(partnerAnyTheseBenefitsCY(appConfig, formWithErrors, mode, taxYearInfo))),
        (value) =>
          dataCacheConnector.save[Boolean](request.sessionId, PartnerAnyTheseBenefitsCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PartnerAnyTheseBenefitsCYId, mode)(new UserAnswers(cacheMap))))
      )
  }

  private def errorCheckForCarersAllowance(boundForm: Form[Boolean], userAnswers: UserAnswers) = {
    userAnswers.whichBenefitsPartnerGet match {
      case Some(benefits) if !boundForm.hasErrors => {
        val hasCarerAllowance = benefits.exists( x => x.equals(WhichBenefitsEnum.CARERSALLOWANCE.toString))
        val partnerAnyBenefitsValue = boundForm.value.getOrElse(true)

        if(hasCarerAllowance && !partnerAnyBenefitsValue) {
          boundForm.withError("value", "partnerAnyTheseBenefitsCY.error.carers.allowance")
        } else {
          boundForm
        }
      }
      case _ => boundForm
    }

  }
}
