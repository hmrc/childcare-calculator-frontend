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

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.Inject

class Navigator(navigators: SubNavigator*) {

  @Inject()
  def this(
      minHours: MinimumHoursNavigator,
      maxHours: MaximumHoursNavigator,
      pensions: PensionNavigator,
      employment: EmploymentIncomeNavigator,
      benefitsIncome: BenefitsIncomeNavigator,
      otherIncome: OtherIncomeNavigator,
      incomeInfo: IncomeInfoNavigator,
      childcare: ChildcareNavigator,
      survey: SurveyNavigator
  ) =
    this(Seq(minHours, maxHours, pensions, employment, benefitsIncome, otherIncome, incomeInfo, childcare, survey): _*)

  def nextPage(id: Identifier): UserAnswers => Call =
    navigators
      .map(_.nextPage(id))
      .reduce(_ orElse _)
      .getOrElse(_ => routes.WhatToTellTheCalculatorController.onPageLoad)

}
