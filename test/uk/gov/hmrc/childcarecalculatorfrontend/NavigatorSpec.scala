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

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{Scheme, Schemes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

class NavigatorSpec extends SpecBase with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator(new Schemes())

  case object UnknownIdentifier extends Identifier

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(userAnswers()) mustBe routes.WhatToTellTheCalculatorController.onPageLoad()
      }

      "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
        val answers = spy(userAnswers())
        when(answers.location) thenReturn Some("england") thenReturn Some("wales") thenReturn Some("scotland")

        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
        val answers = spy(userAnswers())
        when(answers.location) thenReturn Some("northernIreland")
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Child Aged Two" in {
        navigator.nextPage(ChildAgedTwoId, NormalMode)(spy(userAnswers())) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(spy(userAnswers())) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
      }

      "go to expect approved childcare cost from childcare cost when you have childcare cost or not yet decided" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("yes") thenReturn Some("notYet")

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
      }

      "go to results page from childcare cost if you are not eligible for free hours and don't have the child care cost" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedThreeOrFour) thenReturn Some(false)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to results page from childcare cost if you are eligible for free hours, have child aged 3 or 4 years and don't have the child care cost" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northern-ireland")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to results page from childcare cost if you are eligible for free hours, have child aged 2 and don't have the child care cost" in {
      val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(false)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to free hours results page if you are eligible for free hours, have child 2 & 3 or 4 years, don't have childcare cost & lives in wales, scotland" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to free hours info page if you are eligible for free hours, have child aged 3 or 4 years and don't have childcare cost and lives in england" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
      }

      "go to free hours info page if you are eligible for free hours, have child 2 and 3 or 4 years, don't have childcare cost and lives in england" in {
        val answers = spy(userAnswers())
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
      }

    }

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        navigator.nextPage(UnknownIdentifier, CheckMode)(userAnswers()) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    ".nextPage" must {

      def scheme(e: Eligibility): Scheme = new Scheme {
        override def eligibility(answers: UserAnswers): Eligibility = e
      }

      val eligible: Scheme = scheme(Eligible)
      val notEligible: Scheme = scheme(NotEligible)
      val notDetermined: Scheme = scheme(NotDetermined)

      "return a redirect to the Results page" when {

        "all schemes are determined" in {
          val navigator = new Navigator(new Schemes(eligible, notEligible))
          navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustEqual routes.FreeHoursResultController.onPageLoad()
        }
      }
    }
  }
}
