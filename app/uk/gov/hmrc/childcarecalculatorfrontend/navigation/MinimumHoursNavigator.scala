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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, LocationEnum, NormalMode, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MinimumHoursNavigator @Inject() () extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> costRoute,
    ApprovedProviderId -> approvedChildCareRoute
  )

  def locationRoute(answers: UserAnswers): Call = {
    val Ni = LocationEnum.NORTHERNIRELAND.toString

    if(answers.location.contains(Ni)) {
      routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    } else {
      routes.ChildAgedTwoController.onPageLoad(NormalMode)
    }
  }

  def costRoute(answers: UserAnswers): Call = {
    val No = YesNoUnsureEnum.NO.toString
    if(answers.childcareCosts.contains(No)) {
      if (answers.isEligibleForMaxFreeHours == Eligible) {
        routes.FreeHoursInfoController.onPageLoad()
      } else {
        routes.FreeHoursResultController.onPageLoad()
      }
    } else {
      routes.ApprovedProviderController.onPageLoad(NormalMode)
    }
  }

  def approvedChildCareRoute(answers: UserAnswers): Call = {
    val No = YesNoUnsureEnum.NO.toString

    if(answers.approvedProvider.contains(No)) {
      if(answers.isEligibleForMaxFreeHours == Eligible){
        routes.FreeHoursInfoController.onPageLoad()
      } else {
        routes.FreeHoursResultController.onPageLoad()
      }
    } else {
      if(answers.isEligibleForFreeHours == Eligible) {
        routes.FreeHoursInfoController.onPageLoad()
      } else {
        routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }
    }
  }
}
