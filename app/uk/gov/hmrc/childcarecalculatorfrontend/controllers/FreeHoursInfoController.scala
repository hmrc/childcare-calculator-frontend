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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{NOTSURE, YES}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class FreeHoursInfoController @Inject()(appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        freeHoursInfo: freeHoursInfo) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val isChildAgedTwo = request.userAnswers.childAgedTwo.getOrElse(false)
      val isChildAgedThreeOrFour = request.userAnswers.childAgedThreeOrFour.getOrElse(false)
      val locationOption = request.userAnswers.location

      val hasChildcareCosts = request.userAnswers.childcareCosts.getOrElse(false) match {
        case ChildcareConstants.yes | ChildcareConstants.notYet => true
        case _ => false
      }

      val hasApprovedCosts: Boolean = request.userAnswers.approvedProvider.fold(false) {
        case YES | NOTSURE => true
        case _ => false
      }

      locationOption match {
        case None =>
          Redirect(routes.LocationController.onPageLoad(NormalMode))
        case Some(location) =>

          Ok(freeHoursInfo(appConfig,
            isChildAgedTwo,
            isChildAgedThreeOrFour,
            hasChildcareCosts,
            hasApprovedCosts,
            location,
            isEligibleForOnlyOneScheme(isChildAgedTwo, isChildAgedThreeOrFour, hasChildcareCosts, hasApprovedCosts, location)))
      }
  }

  private def isEligibleForOnlyOneScheme(isChildAgedTwo: Boolean,
                                         isChildAgedThreeOrFour: Boolean,
                                         hasChildcareCosts: Boolean,
                                         hasApprovedCosts: Boolean,
                                         location: Location.Value) = {
    (!isChildAgedTwo && isChildAgedThreeOrFour && location == Location.ENGLAND && !hasChildcareCosts) ||
      (!isChildAgedTwo && isChildAgedThreeOrFour && location == Location.ENGLAND && hasChildcareCosts && !hasApprovedCosts)
  }
}
