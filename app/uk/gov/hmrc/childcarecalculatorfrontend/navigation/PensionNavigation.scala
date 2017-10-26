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

/**
  * Contains the navigation for current and previous year pension pages
  */

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Singleton

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPension, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class PensionNavigation {

  def yourPensionRouteCY(answers: UserAnswers) = {

    val youPaidPensionValue = answers.YouPaidPensionCY
    youPaidPensionValue match {
      case Some(true) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => sessionExpired
    }
  }

  def partnerPensionRouteCY(answers: UserAnswers) = {

    val partnerPaidPensionValue = answers.PartnerPaidPensionCY
    partnerPaidPensionValue match {
      case Some(true) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => sessionExpired
    }
  }

  def bothPensionRouteCY(answers: UserAnswers) = {

    val bothPaidPensionValue = answers.bothPaidPensionCY
    bothPaidPensionValue match {
      case Some(true) => routes.WhoPaysIntoPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => sessionExpired
    }
  }

  def whoPaysPensionRouteCY(answers: UserAnswers) = {

    val WhoPaysPensionValue = answers.whoPaysIntoPension
    WhoPaysPensionValue match {
      case Some(You) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(Both) => routes.HowMuchBothPayPensionController.onPageLoad(NormalMode)
      case _ => sessionExpired
    }
  }

  def howMuchYouPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchYouPayPensionValue = answers.howMuchYouPayPension

    getCallOrSessionExpired(howMuchYouPayPensionValue,
                            routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode))
  }

  def howMuchPartnerPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchPartnerPayPensionValue = answers.howMuchPartnerPayPension

    getCallOrSessionExpired(howMuchPartnerPayPensionValue,
      routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode))
  }

  def howMuchBothPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchBothPayPensionValue = answers.howMuchBothPayPension

    getCallOrSessionExpired(howMuchBothPayPensionValue,
      routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode))
 }

  private def sessionExpired = routes.SessionExpiredController.onPageLoad()

  private def getCallOrSessionExpired[T](optionalElement: Option[T], call: Call) = {
    optionalElement match {
      case Some(_) => call
      case _ => sessionExpired
    }
  }

}
