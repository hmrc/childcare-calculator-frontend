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

package uk.gov.hmrc.childcarecalculatorfrontend

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, Schemes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject() (schemes: Schemes) {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> costRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    ApprovedProviderId -> approvedProviderRoutes
  )

  // TODO temporary routes while page is under construction
  private def approvedProviderRoutes(answers: UserAnswers): Call = answers.approvedProvider match {
    case Some("option1") | Some("option3") =>
      if (FreeHours.eligibility(answers) == Eligible) {
        routes.FreeHoursInfoController.onPageLoad()
      } else {
        routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }
    case Some("option2") =>
      routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
    case _ =>
      routes.SessionExpiredController.onPageLoad()
  }

  private def costRoute(answers: UserAnswers) = answers.childcareCosts match {
    case Some("no") =>
      if (FreeHours.eligibility(answers) == Eligible) {
        routes.FreeHoursInfoController.onPageLoad()
      } else {
        routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }
    case Some(_) =>
      routes.ApprovedProviderController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def locationRoute(answers: UserAnswers) = answers.location match {
    case Some("northernIreland") => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    case Some(_) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(
  )

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = {
    answers =>
      if (schemes.allSchemesDetermined(answers)) {
        routes.FreeHoursResultController.onPageLoad()
      } else {
        mode match {
          case NormalMode =>
            routeMap.getOrElse(id, (_: UserAnswers) => routes.WhatToTellTheCalculatorController.onPageLoad())(answers)
          case CheckMode =>
            editRouteMap.getOrElse(id, (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad())(answers)
        }
      }
  }
}
