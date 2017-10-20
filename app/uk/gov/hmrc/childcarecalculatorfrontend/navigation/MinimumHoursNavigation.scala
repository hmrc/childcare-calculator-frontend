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

import javax.inject.Singleton

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, LocationEnum, NormalMode, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class MinimumHoursNavigation {

  def locationRoute(answers: UserAnswers) = {
    val Ni = LocationEnum.NORTHERNIRELAND.toString
    answers.location match {
      case Some(Ni) => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      case Some(_) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  def costRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString
    answers.childcareCosts match {
      case Some(No) =>
        if (answers.isEligibleForMaxFreeHours == Eligible) {
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      case Some(_) => routes.ApprovedProviderController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  def approvedChildCareRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString
    answers.approvedProvider match {
      case Some(No) =>
        if(answers.isEligibleForMaxFreeHours == Eligible){
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      case Some(_) => if(answers.isEligibleForFreeHours == Eligible)
        routes.FreeHoursInfoController.onPageLoad()
      else routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

}
