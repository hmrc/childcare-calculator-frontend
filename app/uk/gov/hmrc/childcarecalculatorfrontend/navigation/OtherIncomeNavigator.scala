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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

import javax.inject.{Inject, Singleton}
import scala.annotation.unused

/** Contains the navigation for current and previous year other income pages
  */
@Singleton
private[navigation] class OtherIncomeNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected def routeMap: Map[Identifier, UserAnswers => Call] = Map(
    YourOtherIncomeThisYearId    -> yourOtherIncomeRouteCY,
    BothOtherIncomeThisYearId    -> bothOtherIncomeRouteCY,
    WhoGetsOtherIncomeCYId       -> whoGetsOtherIncomeRouteCY,
    YourOtherIncomeAmountCYId    -> howMuchYourOtherIncomeRouteCY,
    PartnerOtherIncomeAmountCYId -> howMuchPartnerOtherIncomeRouteCY,
    OtherIncomeAmountCYId        -> howMuchBothOtherIncomeRouteCY
  )

  private def yourOtherIncomeRouteCY(answers: UserAnswers) =

    utils.getCall(answers.yourOtherIncomeThisYear) {
      case true  => routes.YourOtherIncomeAmountCYController.onPageLoad()
      case false => routes.ResultController.onPageLoad()
    }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeThisYear) {
      case true  => routes.WhoGetsOtherIncomeCYController.onPageLoad()
      case false => routes.ResultController.onPageLoad()
    }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsOtherIncomeCY) {
      case YouPartnerBoth.You     => routes.YourOtherIncomeAmountCYController.onPageLoad()
      case YouPartnerBoth.Partner => routes.PartnerOtherIncomeAmountCYController.onPageLoad()
      case YouPartnerBoth.Both    => routes.OtherIncomeAmountCYController.onPageLoad()
    }

  private def howMuchYourOtherIncomeRouteCY(@unused answers: UserAnswers) =
    routes.ResultController.onPageLoad()

  private def howMuchPartnerOtherIncomeRouteCY(@unused answers: UserAnswers) =
    routes.ResultController.onPageLoad()

  private def howMuchBothOtherIncomeRouteCY(@unused answers: UserAnswers) =
    routes.ResultController.onPageLoad()

}
