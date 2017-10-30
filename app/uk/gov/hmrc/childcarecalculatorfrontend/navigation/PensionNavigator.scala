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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

/**
  * Contains the navigation for current and previous year pension pages
  */
class PensionNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    YouPaidPensionCYId -> yourPensionRouteCY,
    PartnerPaidPensionCYId -> partnerPensionRouteCY,
    BothPaidPensionCYId -> bothPensionRouteCY,
    WhoPaysIntoPensionId -> whoPaysPensionRouteCY,
    HowMuchYouPayPensionId -> howMuchYouPayPensionRouteCY,
    HowMuchPartnerPayPensionId -> howMuchPartnerPayPensionRouteCY,
    HowMuchBothPayPensionId -> howMuchBothPayPensionRouteCY,
    YouPaidPensionPYId -> yourPensionRoutePY,
    PartnerPaidPensionPYId -> partnerPensionRoutePY,
    BothPaidPensionPYId -> bothPensionRoutePY,
    WhoPaidIntoPensionPYId -> whoPaysPensionRoutePY,
    HowMuchYouPayPensionPYId -> howMuchYouPayPensionRoutePY,
    HowMuchPartnerPayPensionPYId -> howMuchPartnerPayPensionRoutePY,
    HowMuchBothPayPensionPYId -> howMuchBothPayPensionRoutePY
  )

  private def yourPensionRouteCY(answers: UserAnswers): Call = {
    val youPaidPensionValue = answers.YouPaidPensionCY
    youPaidPensionValue match {
      case Some(true) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def partnerPensionRouteCY(answers: UserAnswers): Call = {
    val partnerPaidPensionValue = answers.PartnerPaidPensionCY
    partnerPaidPensionValue match {
      case Some(true) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def bothPensionRouteCY(answers: UserAnswers): Call = {
    val bothPaidPensionValue = answers.bothPaidPensionCY
    bothPaidPensionValue match {
      case Some(true) => routes.WhoPaysIntoPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def whoPaysPensionRouteCY(answers: UserAnswers): Call = {
    val WhoPaysPensionValue = answers.whoPaysIntoPension
    WhoPaysPensionValue match {
      case Some(You) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(Both) => routes.HowMuchBothPayPensionController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def howMuchYouPayPensionRouteCY(answers: UserAnswers): Call = {
    val howMuchYouPayPensionValue = answers.howMuchYouPayPension
    utils.getCallOrSessionExpired(
      howMuchYouPayPensionValue,
      routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
    )
  }

  private def howMuchPartnerPayPensionRouteCY(answers: UserAnswers): Call = {
    val howMuchPartnerPayPensionValue = answers.howMuchPartnerPayPension

    utils.getCallOrSessionExpired(howMuchPartnerPayPensionValue,
      routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode))
  }

  private def howMuchBothPayPensionRouteCY(answers: UserAnswers): Call = {
    val howMuchBothPayPensionValue = answers.howMuchBothPayPension

    utils.getCallOrSessionExpired(howMuchBothPayPensionValue,
      routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode))
 }

  private def yourPensionRoutePY(answers: UserAnswers) = {

    val youPaidPensionPYValue = answers.youPaidPensionPY
    youPaidPensionPYValue match {
      case Some(true) => routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def partnerPensionRoutePY(answers: UserAnswers) = {

    val partnerPaidPensionPYValue = answers.partnerPaidPensionPY
    partnerPaidPensionPYValue match {
      case Some(true) => routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def bothPensionRoutePY(answers: UserAnswers) = {

    val bothPaidPensionPYValue = answers.bothPaidPensionPY
    bothPaidPensionPYValue match {
      case Some(true) => routes.WhoPaidIntoPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def whoPaysPensionRoutePY(answers: UserAnswers) = {

    val whoPaidIntoPensionPYValue = answers.whoPaidIntoPensionPY
    whoPaidIntoPensionPYValue match {
      case Some(You) => routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case Some(Both) => routes.HowMuchBothPayPensionPYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def howMuchYouPayPensionRoutePY(answers: UserAnswers) = {
    val howMuchYouPayPensionPYValue = answers.howMuchYouPayPensionPY

    utils.getCallOrSessionExpired(howMuchYouPayPensionPYValue,
      routes.YourOtherIncomeLYController.onPageLoad(NormalMode))
  }

  private def howMuchPartnerPayPensionRoutePY(answers: UserAnswers) = {
    val howMuchPartnerPayPensionPYValue = answers.howMuchPartnerPayPensionPY

    utils.getCallOrSessionExpired(howMuchPartnerPayPensionPYValue,
      routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode))
  }

  private def howMuchBothPayPensionRoutePY(answers: UserAnswers) = {
    val howMuchBothPayPensionPYValue = answers.howMuchBothPayPensionPY

    utils.getCallOrSessionExpired(howMuchBothPayPensionPYValue,
      routes.BothOtherIncomeLYController.onPageLoad(NormalMode))
  }
}
