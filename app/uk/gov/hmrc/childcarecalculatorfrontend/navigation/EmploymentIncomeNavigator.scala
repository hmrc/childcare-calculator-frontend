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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

/** Contains the navigation for current and previous year employment income pages
  */
class EmploymentIncomeNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    PartnerPaidWorkCYId         -> partnerPaidWorkCYRoute,
    ParentPaidWorkCYId          -> parentPaidWorkCYRoute,
    ParentEmploymentIncomeCYId  -> parentEmploymentIncomeCYRoute,
    PartnerEmploymentIncomeCYId -> partnerEmploymentIncomeCYRoute,
    EmploymentIncomeCYId        -> employmentIncomeCYRoute
  )

  private def partnerPaidWorkCYRoute(answers: UserAnswers) =
    utils.getCall(answers.partnerPaidWorkCY) {
      case false => routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)
      case true  => routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
    }

  private def parentPaidWorkCYRoute(answers: UserAnswers) =
    utils.getCall(answers.parentPaidWorkCY) {
      case false => routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)
      case true  => routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
    }

  private def parentEmploymentIncomeCYRoute(answers: UserAnswers) =
    utils.getCall(answers.doYouLiveWithPartner) {
      case true =>
        utils.getCall(answers.whoIsInPaidEmployment) {
          case You => routes.YouPaidPensionCYController.onPageLoad(NormalMode)
          case _   => routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }
      case false => routes.YouPaidPensionCYController.onPageLoad(NormalMode)
    }

  private def partnerEmploymentIncomeCYRoute(answers: UserAnswers) =
    utils.getCall(answers.whoIsInPaidEmployment) {
      case Partner => routes.PartnerPaidPensionCYController.onPageLoad(NormalMode)
      case _       => routes.BothPaidPensionCYController.onPageLoad(NormalMode)
    }

  private def employmentIncomeCYRoute(answers: UserAnswers) = routes.BothPaidPensionCYController.onPageLoad(NormalMode)

}
