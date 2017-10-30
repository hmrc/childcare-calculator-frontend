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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

/**
  * Contains the navigation for current and previous year employment income pages
  */
class EmploymentIncomeNavigator @Inject() () extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    PartnerPaidWorkCYId -> (_ =>  partnerPaidWorkCYRoute),
    ParentPaidWorkCYId -> (_ => parentPaidWorkCYRoute),
    ParentEmploymentIncomeCYId -> (_ => parentEmploymentIncomeCYRoute),
    PartnerEmploymentIncomeCYId -> (_ => partnerEmploymentIncomeCYRoute),
    EmploymentIncomeCYId -> (_ => employmentIncomeCYRoute)
  )

  private def partnerPaidWorkCYRoute = routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)

  private def parentPaidWorkCYRoute = routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)

  private def parentEmploymentIncomeCYRoute = routes.YouPaidPensionCYController.onPageLoad(NormalMode)

  private def partnerEmploymentIncomeCYRoute= routes.PartnerPaidPensionCYController.onPageLoad(NormalMode)

  private def employmentIncomeCYRoute = routes.BothPaidPensionCYController.onPageLoad(NormalMode)
}
