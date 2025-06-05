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
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.FreeHours
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, Location, YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.Inject

class MinimumHoursNavigator @Inject() (freeHours: FreeHours) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId             -> locationRoute,
    ChildrenAgeGroupsId    -> (_ => routes.ChildcareCostsController.onPageLoad()),
    ChildAgedTwoId         -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad()),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad()),
    ChildcareCostsId       -> costRoute,
    ApprovedProviderId     -> approvedChildCareRoute
  )

  private def locationRoute(answers: UserAnswers): Call =
    if (answers.location.contains(Location.ENGLAND)) {
      routes.ChildrenAgeGroupsController.onPageLoad()
    } else if (answers.location.contains(Location.NORTHERN_IRELAND) || answers.location.contains(Location.WALES)) {
      routes.ChildAgedThreeOrFourController.onPageLoad()
    } else {
      routes.ChildAgedTwoController.onPageLoad()
    }

  private def costRoute(answers: UserAnswers): Call = {
    val No = YesNoNotYetEnum.NO.toString
    if (answers.childcareCosts.contains(No)) {
      if (freeHours.eligibility(answers) == Eligible && answers.location.contains(Location.ENGLAND)) {
        routes.FreeHoursInfoController.onPageLoad
      } else if (
        (answers.isChildAgedTwo.getOrElse(false) || answers.isChildAgedNineTo23Months.getOrElse(
          false
        )) && answers.location.contains(Location.ENGLAND)
      ) {
        routes.DoYouLiveWithPartnerController.onPageLoad()
      } else {
        routes.ResultController.onPageLoad()
      }
    } else {
      routes.ApprovedProviderController.onPageLoad()
    }
  }

  private def approvedChildCareRoute(answers: UserAnswers): Call = {
    val No = YesNoUnsureEnum.NO.toString

    if (answers.approvedProvider.contains(No)) {
      if (freeHours.eligibility(answers) == Eligible && answers.location.contains(Location.ENGLAND)) {
        routes.FreeHoursInfoController.onPageLoad
      } else if (
        (answers.isChildAgedTwo.getOrElse(false) || answers.isChildAgedNineTo23Months.getOrElse(
          false
        )) && answers.location.contains(Location.ENGLAND)
      ) {
        routes.DoYouLiveWithPartnerController.onPageLoad()
      } else {
        routes.ResultController.onPageLoad()
      }
    } else {
      if (freeHours.eligibility(answers) == Eligible) {
        routes.FreeHoursInfoController.onPageLoad
      } else {
        routes.DoYouLiveWithPartnerController.onPageLoad()
      }
    }
  }

}
