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

import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year other income pages
  */
class OtherIncomeNavigator @Inject() (utils: Utils) extends SubNavigator {

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

    val youOtherIncomeValue = answers.yourOtherIncomeThisYear
    youOtherIncomeValue match {
      case Some(true) => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Some(false) => routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def partnerOtherIncomeRouteCY(answers: UserAnswers) = {

    val partnerOtherIncomeValue = answers.partnerAnyOtherIncomeThisYear
    partnerOtherIncomeValue match {
      case Some(true) => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def bothOtherIncomeRouteCY(answers: UserAnswers) = {

    val bothOtherIncomeValue = answers.bothOtherIncomeThisYear
    bothOtherIncomeValue match {
      case Some(true) => routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
      case Some(false) => routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def whoGetsOtherIncomeRouteCY(answers: UserAnswers) = {

    val WhoGetsOtherIncomeValue = answers.whoGetsOtherIncomeCY
    WhoGetsOtherIncomeValue match {
      case Some(You) => routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
      case Some(Both) => routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def howMuchYourOtherIncomeRouteCY(answers: UserAnswers) = {
    val howMuchYourOtherIncomeValue = answers.yourOtherIncomeAmountCY

    utils.getCallOrSessionExpired(howMuchYourOtherIncomeValue,
                            routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode))
  }

  private def howMuchPartnerOtherIncomeRouteCY(answers: UserAnswers) = {
    val howMuchPartnerOtherIncomeValue = answers.partnerOtherIncomeAmountCY

    utils.getCallOrSessionExpired(howMuchPartnerOtherIncomeValue,
      routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode))
  }

  private def howMuchBothOtherIncomeRouteCY(answers: UserAnswers) = {
    val howMuchBothOtherIncomeValue = answers.otherIncomeAmountCY

    utils.getCallOrSessionExpired(howMuchBothOtherIncomeValue,
      routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))
  }
}
