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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year employment income pages
  */
class EmploymentIncomeNavigator @Inject() (utils:Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    PartnerPaidWorkCYId -> partnerPaidWorkCYRoute,
    ParentPaidWorkCYId -> parentPaidWorkCYRoute,
    ParentEmploymentIncomeCYId -> parentEmploymentIncomeCYRoute,
    PartnerEmploymentIncomeCYId -> partnerEmploymentIncomeCYRoute,
    EmploymentIncomeCYId -> employmentIncomeCYRoute,
    ParentEmploymentIncomePYId -> parentEmploymentIncomePYRoute,
    PartnerPaidWorkPYId -> partnerPaidWorkPYRoute,
    ParentPaidWorkPYId -> parentPaidWorkPYRoute,
    PartnerEmploymentIncomePYId -> partnerEmploymentIncomePYRoute,
    EmploymentIncomePYId -> employmentIncomePYRoute
  )

  private def partnerPaidWorkCYRoute(answers: UserAnswers) =
    utils.getCall(answers.partnerPaidWorkCY) (_=>  routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode))

  private def parentPaidWorkCYRoute(answers: UserAnswers) =
    utils.getCall(answers.parentPaidWorkCY) (_=>  routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode))

  private def parentEmploymentIncomeCYRoute(answers: UserAnswers) =
    utils.getCall(answers.parentEmploymentIncomeCY) (_=>  routes.YouPaidPensionCYController.onPageLoad(NormalMode))

  private def partnerEmploymentIncomeCYRoute(answers: UserAnswers) =
    utils.getCall(answers.partnerEmploymentIncomeCY) (_=>  routes.PartnerPaidPensionCYController.onPageLoad(NormalMode))

  private def employmentIncomeCYRoute(answers: UserAnswers) =
    utils.getCall(answers.employmentIncomeCY) (_=>  routes.BothPaidPensionCYController.onPageLoad(NormalMode))

  private def parentEmploymentIncomePYRoute(answers: UserAnswers) =
    utils.getCall(answers.parentEmploymentIncomePY) (_=>  routes.YouPaidPensionPYController.onPageLoad(NormalMode))

  private def partnerPaidWorkPYRoute(answers: UserAnswers) =
    utils.getCall(answers.partnerPaidWorkPY) (_=>  routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode))

  private def parentPaidWorkPYRoute(answers: UserAnswers) =
    utils.getCall(answers.parentPaidWorkPY) (_=>  routes.PartnerEmploymentIncomePYController.onPageLoad(NormalMode))

  private def partnerEmploymentIncomePYRoute(answers: UserAnswers) =
    utils.getCall(answers.partnerEmploymentIncomePY) (_=>  routes.PartnerPaidPensionPYController.onPageLoad(NormalMode))

  private def employmentIncomePYRoute(answers: UserAnswers) =
    utils.getCall(answers.employmentIncomePY) (_=>  routes.BothPaidPensionPYController.onPageLoad(NormalMode))
}
