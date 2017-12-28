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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.BothAnyTheseBenefitsCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothAnyTheseBenefitsCY

import scala.concurrent.Future

class BothAnyTheseBenefitsCYController @Inject()(appConfig: FrontendAppConfig,
                                                 override val messagesApi: MessagesApi,
                                                 dataCacheConnector: DataCacheConnector,
                                                 navigator: Navigator,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 taxYearInfo: TaxYearInfo) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.bothAnyTheseBenefitsCY match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(bothAnyTheseBenefitsCY(appConfig, preparedForm, mode, taxYearInfo))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      val boundForm = BooleanForm(bothAnyTheseBenefitsCYErrorKey).bindFromRequest()

      errorCheckForCarersAllowance(boundForm, request.userAnswers).fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(bothAnyTheseBenefitsCY(appConfig, formWithErrors, mode, taxYearInfo))),
        (value) =>
          dataCacheConnector.save[Boolean](request.sessionId, BothAnyTheseBenefitsCYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(BothAnyTheseBenefitsCYId, mode)(new UserAnswers(cacheMap))))
      )
  }

  private def errorCheckForCarersAllowance(boundForm: Form[Boolean], userAnswers: UserAnswers) = {

    val parentBenefits = userAnswers.whichBenefitsYouGet
    val partnerBenefits = userAnswers.whichBenefitsPartnerGet

    val benefitExists = List(parentBenefits, partnerBenefits).flatten.nonEmpty

    if (benefitExists && !boundForm.hasErrors) {
      val bothAnyBenefitsValue = boundForm.value.getOrElse(true)

      (parentBenefits, partnerBenefits) match {

        case (Some(parentBen), Some(partnerBen)) => {
          val hasParentCarerAllowance = parentBen.exists(x => x.equals(WhichBenefitsEnum.CARERSALLOWANCE.toString))
          val hasPartnerCarerAllowance = partnerBen.exists(x => x.equals(WhichBenefitsEnum.CARERSALLOWANCE.toString))

          if ((hasParentCarerAllowance || hasPartnerCarerAllowance) && !bothAnyBenefitsValue) {
            boundForm.withError("value", "bothAnyTheseBenefitsCY.error.carers.allowance")
          } else {
            boundForm
          }
        }

        case (Some(parentBen), _) => getBoundFormForBenefits(bothAnyBenefitsValue, boundForm, parentBen)
        case (_, Some(partnerBen)) => getBoundFormForBenefits(bothAnyBenefitsValue, boundForm, partnerBen)
        case _ => boundForm
      }

    } else {
      boundForm
    }

  }

  private def getBoundFormForBenefits(selectedValue: Boolean,
                              boundForm: Form[Boolean],
                              benefits: Set[String]) = {

    val hasCarerAllowance = benefits.exists(x => x.equals(WhichBenefitsEnum.CARERSALLOWANCE.toString))

    if (hasCarerAllowance && !selectedValue) {
      boundForm.withError("value", "bothAnyTheseBenefitsCY.error.carers.allowance")
    } else {
      boundForm
    }
  }

}
