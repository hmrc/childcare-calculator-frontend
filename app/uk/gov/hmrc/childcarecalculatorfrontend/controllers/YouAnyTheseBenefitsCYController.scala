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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YouAnyTheseBenefitsCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit.CarersAllowance
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsCY
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class YouAnyTheseBenefitsCYController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    youAnyTheseBenefitsCY: youAnyTheseBenefitsCY
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[AnyContent] = request
    request.userAnswers.location match {
      case None =>
        Redirect(routes.LocationController.onPageLoad())

      case Some(location) =>
        val preparedForm = request.userAnswers.youAnyTheseBenefits match {
          case None        => BooleanForm()
          case Some(value) => BooleanForm().fill(value)
        }
        Ok(youAnyTheseBenefitsCY(preparedForm, location))
    }
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    request.userAnswers.location match {
      case None =>
        Future.successful(Redirect(routes.LocationController.onPageLoad()))
      case Some(location) =>
        val boundForm = BooleanForm(youAnyTheseBenefitsCYErrorKey).bindFromRequest()
        validateCarersAllowance(boundForm, request.userAnswers).fold(
          (formWithErrors: Form[Boolean]) =>
            Future
              .successful(BadRequest(youAnyTheseBenefitsCY(formWithErrors, location))),
          value =>
            dataCacheService
              .save(YouAnyTheseBenefitsCYId, value)
              .map(cacheMap => Redirect(navigator.nextPage(YouAnyTheseBenefitsCYId)(new UserAnswers(cacheMap))))
        )
    }
  }

  /** Checks whether parent has Carer Allowance benefits when user selects No for the question, if yes then populate the
    * form with error else return the original form
    *
    * @param boundForm
    *   is a boolean form
    * @param userAnswers
    *   contains the user's input saved in cache
    * @return
    *   boundForm original or modified bound form
    */
  private def validateCarersAllowance(boundForm: Form[Boolean], userAnswers: UserAnswers) =
    userAnswers.doYouGetAnyBenefits match {
      case Some(benefits) if !boundForm.hasErrors =>
        val hasCarerAllowance   = benefits.contains(CarersAllowance)
        val youAnyBenefitsValue = boundForm.value.getOrElse(true)
        val isScotland          = userAnswers.location.get.equals(Location.Scotland)

        if (hasCarerAllowance && !youAnyBenefitsValue && isScotland) {
          boundForm.withError("value", youAnyTheseBenefitsCYScottishCarerAllowanceErrorKey)
        } else if (hasCarerAllowance && !youAnyBenefitsValue && !isScotland) {
          boundForm.withError("value", youAnyTheseBenefitsCYCarerAllowanceErrorKey)
        } else {
          boundForm
        }

      case _ => boundForm
    }

}
