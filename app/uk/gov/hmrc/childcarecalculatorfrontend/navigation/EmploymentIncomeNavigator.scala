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

  private def partnerPaidWorkCYRoute(answers: UserAnswers) = {
    val partnerPaidWorkCYValue = answers.partnerPaidWorkCY

    utils.getCallOrSessionExpired(partnerPaidWorkCYValue,
      routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode))
  }

  private def parentPaidWorkCYRoute(answers: UserAnswers) = {
    val parentPaidWorkCYValue = answers.parentPaidWorkCY

    utils.getCallOrSessionExpired(parentPaidWorkCYValue,
      routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode))
  }

  private def parentEmploymentIncomeCYRoute(answers: UserAnswers) = {
    val parentEmploymentIncomeCYValue = answers.parentEmploymentIncomeCY

    utils.getCallOrSessionExpired(parentEmploymentIncomeCYValue,
      routes.YouPaidPensionCYController.onPageLoad(NormalMode))
  }

  private def partnerEmploymentIncomeCYRoute(answers: UserAnswers) = {
    val partnerEmploymentIncomeCYValue = answers.partnerEmploymentIncomeCY

    utils.getCallOrSessionExpired(partnerEmploymentIncomeCYValue,
      routes.PartnerPaidPensionCYController.onPageLoad(NormalMode))
  }

  private def employmentIncomeCYRoute(answers: UserAnswers) = {
    val employmentIncomeCYValue = answers.employmentIncomeCY

    utils.getCallOrSessionExpired(employmentIncomeCYValue,
      routes.BothPaidPensionCYController.onPageLoad(NormalMode))
  }

  private def parentEmploymentIncomePYRoute(answers: UserAnswers) = {
    val parentEmploymentIncomePYValue = answers.parentEmploymentIncomePY

    utils.getCallOrSessionExpired(parentEmploymentIncomePYValue,
      routes.YouPaidPensionPYController.onPageLoad(NormalMode))
  }

  private def partnerPaidWorkPYRoute(answers: UserAnswers) = {
    val partnerPaidWorkPYValue = answers.partnerPaidWorkPY

    utils.getCallOrSessionExpired(partnerPaidWorkPYValue,
      routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode))
  }

  private def parentPaidWorkPYRoute(answers: UserAnswers) = {
    val parentPaidWorkPYValue = answers.parentPaidWorkPY

    utils.getCallOrSessionExpired(parentPaidWorkPYValue,
      routes.PartnerEmploymentIncomePYController.onPageLoad(NormalMode))
  }

 private def partnerEmploymentIncomePYRoute(answers: UserAnswers) = {
   val partnerEmploymentIncomePYValue = answers.partnerEmploymentIncomePY

   utils.getCallOrSessionExpired(partnerEmploymentIncomePYValue,
     routes.PartnerPaidPensionPYController.onPageLoad(NormalMode))
 }

  private def employmentIncomePYRoute(answers: UserAnswers) =  {
    val employmentIncomePYValue = answers.employmentIncomePY

    utils.getCallOrSessionExpired(employmentIncomePYValue,
      routes.BothPaidPensionPYController.onPageLoad(NormalMode))
  }

}
