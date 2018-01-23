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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{RoutingUtils, UserAnswers}

/**
  * Contains the navigation for current and previous year pension pages
  */
class PensionNavigator @Inject()(routing: RoutingUtils) extends SubNavigator {

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

  private def yourPensionRouteCY(answers: UserAnswers) = routing.basedOnEquality(answers.YouPaidPensionCY)(HowMuchYouPayPensionController.onPageLoad(NormalMode))(routing.basedOnEquality(answers.doYouLiveWithPartner)(BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))(YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)))

  private def partnerPensionRouteCY(answers: UserAnswers) = routing.basedOnEquality(answers.PartnerPaidPensionCY)(HowMuchPartnerPayPensionController.onPageLoad(NormalMode))(BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))

  private def bothPensionRouteCY(answers: UserAnswers) = routing.basedOnEquality(answers.bothPaidPensionCY)(WhoPaysIntoPensionController.onPageLoad(NormalMode))(BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))

  private def whoPaysPensionRouteCY(answers: UserAnswers) = routing.basedOnYouPartnerBoth(answers.whoPaysIntoPension, HowMuchYouPayPensionController.onPageLoad(NormalMode), HowMuchPartnerPayPensionController.onPageLoad(NormalMode), HowMuchBothPayPensionController.onPageLoad(NormalMode))

  private def howMuchYouPayPensionRouteCY(answers: UserAnswers)= routing.basedOnEquality(answers.doYouLiveWithPartner)(BothAnyTheseBenefitsCYController.onPageLoad(NormalMode))(YouAnyTheseBenefitsCYController.onPageLoad(NormalMode))

  private def howMuchPartnerPayPensionRouteCY(answers: UserAnswers) = BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)

  private def howMuchBothPayPensionRouteCY(answers: UserAnswers) = BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)

  private def yourPensionRoutePY(answers: UserAnswers) = routing.basedOnEquality(answers.youPaidPensionPY)(HowMuchYouPayPensionPYController.onPageLoad(NormalMode))(routing.basedOnEquality(answers.doYouLiveWithPartner)(BothAnyTheseBenefitsPYController.onPageLoad(NormalMode))(YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)))

  private def partnerPensionRoutePY(answers: UserAnswers) = routing.basedOnEquality(answers.partnerPaidPensionPY)(HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode))(BothAnyTheseBenefitsPYController.onPageLoad(NormalMode))

  private def bothPensionRoutePY(answers: UserAnswers) = routing.basedOnEquality(answers.bothPaidPensionPY)(WhoPaidIntoPensionPYController.onPageLoad(NormalMode))(BothAnyTheseBenefitsPYController.onPageLoad(NormalMode))

  private def whoPaysPensionRoutePY(answers: UserAnswers) = routing.basedOnYouPartnerBoth(answers.whoPaidIntoPensionPY, HowMuchYouPayPensionPYController.onPageLoad(NormalMode), HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode), HowMuchBothPayPensionPYController.onPageLoad(NormalMode))

  private def howMuchYouPayPensionRoutePY(answers: UserAnswers) = routing.basedOnEquality(answers.doYouLiveWithPartner)(BothAnyTheseBenefitsPYController.onPageLoad(NormalMode))(YouAnyTheseBenefitsPYController.onPageLoad(NormalMode))

  private def howMuchPartnerPayPensionRoutePY(answers: UserAnswers) = BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)

  private def howMuchBothPayPensionRoutePY(answers: UserAnswers) = BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
}
