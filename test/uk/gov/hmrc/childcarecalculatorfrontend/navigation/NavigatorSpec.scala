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

import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class NavigatorSpec extends PlaySpec with OptionValues {

  "NavigatorImpl" must {

    ".nextPage" must {

      val instance1 = subnavigator(
        routes = Map(
          LocationId     -> (_ => routes.ChildAgedTwoController.onPageLoad()),
          ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad())
        )
      )

      val instance2 = subnavigator(
        routes = Map(
          ChildAgedTwoId         -> (_ => routes.LocationController.onPageLoad()),
          ChildAgedThreeOrFourId -> (_ => routes.ChildAgedTwoController.onPageLoad())
        )
      )

      val navigator = new Navigator(instance1, instance2)

      "return the default route when no route exists in the navigator" in {
        val result = navigator.nextPage(ApprovedProviderId)
        result(answers) mustEqual routes.WhatToTellTheCalculatorController.onPageLoad
      }

      "normal mode" when {

        "return a route which only exists in the first sub navigator" in {
          val result = navigator.nextPage(LocationId)
          result(answers) mustEqual routes.ChildAgedTwoController.onPageLoad()
        }

        "return a route which only exists in the second sub navigator" in {
          val result = navigator.nextPage(ChildAgedThreeOrFourId)
          result(answers) mustEqual routes.ChildAgedTwoController.onPageLoad()
        }

        "return the route from the first navigator if it also exists in subsequent navigators" in {
          val result = navigator.nextPage(ChildAgedTwoId)
          result(answers) mustEqual routes.ChildAgedThreeOrFourController.onPageLoad()
        }
      }
    }
  }

  "SubNavigator" must {

    ".nextPage" must {

      val routeMap: Map[Identifier, UserAnswers => Call] = Map(
        LocationId -> (_ => routes.ChildAgedTwoController.onPageLoad())
      )

      val instance = subnavigator(routeMap)

      "return `Some` when the relevant route exists" in {
        val result = instance.nextPage(LocationId)
        result.value(answers) mustEqual routes.ChildAgedTwoController.onPageLoad()
      }

      "return `None` when the route doesn't exist" in {
        val result = instance.nextPage(ChildAgedTwoId)
        result mustNot be(defined)
      }
    }
  }

  def subnavigator(
      routes: Map[Identifier, UserAnswers => Call] = Map.empty
  ): SubNavigator =
    new SubNavigator {
      override protected lazy val routeMap: Map[Identifier, UserAnswers => Call] = routes
    }

  lazy val answers: UserAnswers = new UserAnswers(null)
}
