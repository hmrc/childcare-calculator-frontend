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
  * Contains the navigation for current and previous year benefits pages
  */
class BenefitsNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected def routeMap = Map(
    YouAnyTheseBenefitsIdCY -> yourBenefitsRouteCY,
    PartnerAnyTheseBenefitsCYId -> partnerBenefitsRouteCY,
    BothAnyTheseBenefitsCYId -> bothBenefitsRouteCY,
    WhosHadBenefitsId -> whosHadBenefitsRouteCY,
    YouBenefitsIncomeCYId -> yourBenefitsIncomeRouteCY,
    PartnerBenefitsIncomeCYId -> partnerBenefitsIncomeRouteCY,
    BenefitsIncomeCYId -> bothBenefitsIncomeRouteCY,
    YouAnyTheseBenefitsPYId -> yourBenefitsRoutePY,
    PartnerAnyTheseBenefitsPYId -> partnerBenefitsRoutePY,
    BothAnyTheseBenefitsPYId -> bothBenefitsRoutePY,
    WhosHadBenefitsPYId -> whosHadBenefitsRoutePY
  )

  private def yourBenefitsRouteCY(answers: UserAnswers) = {
    answers.youAnyTheseBenefits match {
      case Some(true) => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(false) => routes.YourStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def partnerBenefitsRouteCY(answers: UserAnswers) = {
    answers.partnerAnyTheseBenefitsCY match {
      case Some(true) => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def bothBenefitsRouteCY(answers: UserAnswers) = {
    answers.bothAnyTheseBenefitsCY match {
      case Some(true) => routes.WhosHadBenefitsController.onPageLoad(NormalMode)
      case Some(false) => routes.BothStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def whosHadBenefitsRouteCY(answers: UserAnswers) = {
    answers.whosHadBenefits match {
      case Some(You) => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(Both) => routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def yourBenefitsIncomeRouteCY(answers: UserAnswers) = {
    val youBenefitsIncomeCYValue = answers.youBenefitsIncomeCY

    utils.getCallOrSessionExpired(youBenefitsIncomeCYValue,
                            routes.YourStatutoryPayCYController.onPageLoad(NormalMode))
  }

  private def partnerBenefitsIncomeRouteCY(answers: UserAnswers) = {
    val partnerBenefitsIncomeCYValue = answers.partnerBenefitsIncomeCY

    utils.getCallOrSessionExpired(partnerBenefitsIncomeCYValue,
      routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode))
  }

  private def bothBenefitsIncomeRouteCY(answers: UserAnswers) = {
    val benefitsIncomeCYValue = answers.benefitsIncomeCY

    utils.getCallOrSessionExpired(benefitsIncomeCYValue,
      routes.BothStatutoryPayCYController.onPageLoad(NormalMode))
  }

  private def yourBenefitsRoutePY(answers: UserAnswers) = {
    answers.youAnyTheseBenefitsPY match {
      case Some(true) => routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
      case Some(false) => routes.YourStatutoryPayPYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def partnerBenefitsRoutePY(answers: UserAnswers) = {
    answers.partnerAnyTheseBenefitsPY match {
      case Some(true) => routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerStatutoryPayPYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def bothBenefitsRoutePY(answers: UserAnswers) = {
    answers.bothAnyTheseBenefitsPY match {
      case Some(true) => routes.WhosHadBenefitsPYController.onPageLoad(NormalMode)
      case Some(false) => routes.BothStatutoryPayPYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  private def whosHadBenefitsRoutePY(answers: UserAnswers) = {
    answers.whosHadBenefitsPY match {
      case Some(You) => routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
      case Some(Both) => routes.BothBenefitsIncomePYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }


}
