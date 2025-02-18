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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NormalMode, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{ TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{you, both, partner, universalCredits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
 * Contains the navigation for current and previous year other income pages
 */
class OtherIncomeNavigator @Inject()(utils: Utils, tfc: TaxFreeChildcare) extends SubNavigator {

  override protected def routeMap = Map(
    YourOtherIncomeThisYearId -> yourOtherIncomeRouteCY,
    PartnerAnyOtherIncomeThisYearId -> partnerOtherIncomeRouteCY,
    BothOtherIncomeThisYearId -> bothOtherIncomeRouteCY,
    WhoGetsOtherIncomeCYId -> whoGetsOtherIncomeRouteCY,
    YourOtherIncomeAmountCYId -> howMuchYourOtherIncomeRouteCY,
    PartnerOtherIncomeAmountCYId -> howMuchPartnerOtherIncomeRouteCY,
    OtherIncomeAmountCYId -> howMuchBothOtherIncomeRouteCY
  )

  private def yourOtherIncomeRouteCY(answers: UserAnswers) = {

    utils.getCall(answers.yourOtherIncomeThisYear) {
      case true => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false => routes.ResultController.onPageLoad()
    }
  }

  private def partnerOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.partnerAnyOtherIncomeThisYear) {
      case true => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case false =>  routes.ResultController.onPageLoad()
    }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.bothOtherIncomeThisYear) {
      case true => routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
      case false =>routes.ResultController.onPageLoad()
    }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) =
    utils.getCall(answers.whoGetsOtherIncomeCY) {
      case `you` => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case `partner` => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case `both` => routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
    }

  private def howMuchYourOtherIncomeRouteCY(answers: UserAnswers) = {
    routes.ResultController.onPageLoad()

  }

  private def howMuchPartnerOtherIncomeRouteCY(answers: UserAnswers) =
    routes.ResultController.onPageLoad()

  private def howMuchBothOtherIncomeRouteCY(answers: UserAnswers) =
    routes.ResultController.onPageLoad()

}