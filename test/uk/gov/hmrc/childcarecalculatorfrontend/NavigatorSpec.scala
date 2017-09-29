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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {
      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("england") thenReturn Some("wales") thenReturn Some("scotland")

        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("northernIreland")
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Child Aged Two" in {
        navigator.nextPage(ChildAgedTwoId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
      }

      "go to expect approved childcare cost from childcare cost" in {//TODO - go to approved childcare costs
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("yes") thenReturn Some("notYet")
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to results page from childcare cost when no child of 2, 3 or 4 years old" in {//TODO - go to results page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(false)
        when(answers.childAgedThreeOrFour) thenReturn Some(false)
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to results page from childcare cost, has child of 3 or 4 years old" in {//TODO - go to results page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(false)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northernIreland")
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to results page from childcare cost, has child of 2, 3 or 4 years old" in {//TODO - go to results page
      val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northernIreland")
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to info page from childcare cost, has child of 3 or 4 years old and in england" in {//TODO - go to info page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(false)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to info page from childcare cost, has child of 2, 3 or 4 years old and in england" in {//TODO - go to info page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }

      "go to partner page from childcare cost, has child of 2 years old" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(false)
        navigator.nextPage(ChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }

    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
