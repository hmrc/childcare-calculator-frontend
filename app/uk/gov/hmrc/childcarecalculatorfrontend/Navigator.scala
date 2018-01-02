/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.navigation._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class NavigatorImpl(navigators: SubNavigator*) extends Navigator {

  @Inject()
  def this(
            minHours: MinimumHoursNavigator,
            maxHours: MaximumHoursNavigator,
            pensions: PensionNavigator,
            employment: EmploymentIncomeNavigator,
            benefits: BenefitsNavigator,
            otherIncome: OtherIncomeNavigator,
            incomeInfo: IncomeInfoNavigator,
            childcare: ChildcareNavigator,
            statutoryPay: StatutoryNavigator
          ) {
    this(Seq(minHours, maxHours, pensions, employment, benefits, otherIncome, incomeInfo, childcare, statutoryPay): _*)
  }

  override def nextPage(id: Identifier, mode: Mode): UserAnswers => Call =
    navigators.map(_.nextPage(id, mode)).reduce(_ orElse _)
      .getOrElse {
        mode match {
          case NormalMode =>
            _ => routes.WhatToTellTheCalculatorController.onPageLoad()
          case CheckMode =>
            _ => routes.CheckYourAnswersController.onPageLoad()
        }
      }
  }

@ImplementedBy(classOf[NavigatorImpl])
trait Navigator {

  protected def routeMap: Map[Identifier, UserAnswers => Call] = Map.empty
  protected def editRouteMap: Map[Identifier, UserAnswers => Call] = Map.empty

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = {
    answers =>
      mode match {
        case NormalMode =>
          routeMap.getOrElse(id, (_: UserAnswers) => routes.WhatToTellTheCalculatorController.onPageLoad())(answers)
        case CheckMode =>
          editRouteMap.getOrElse(id, (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad())(answers)
      }
  }
}

trait SubNavigator {

  protected def routeMap: PartialFunction[Identifier, UserAnswers => Call] = Map.empty
  protected def editRouteMap: PartialFunction[Identifier, UserAnswers => Call] = Map.empty

  def nextPage(id: Identifier, mode: Mode): Option[UserAnswers => Call] = {
    mode match {
      case NormalMode =>
        routeMap.lift(id)
      case CheckMode =>
        editRouteMap.lift(id)
    }
  }
}
