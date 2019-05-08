/*
 * Copyright 2019 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, Schemes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{SpecBase, SubNavigator}
import uk.gov.hmrc.http.cache.client.CacheMap


class MinimumHoursNavigatorSpec extends SpecBase with MockitoSugar {

  "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
    val answers = spy(userAnswers())
    when(answers.location) thenReturn Some(ENGLAND) thenReturn Some(WALES) thenReturn Some(SCOTLAND)

    navigator.nextPage(LocationId, NormalMode).value(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
    navigator.nextPage(LocationId, NormalMode).value(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
    navigator.nextPage(LocationId, NormalMode).value(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
  }

  "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
    val answers = spy(userAnswers())
    when(answers.location) thenReturn Some(NORTHERN_IRELAND)
    navigator.nextPage(LocationId, NormalMode).value(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
  }

  "go to Child Aged Three or Four from Child Aged Two" in {
    navigator.nextPage(ChildAgedTwoId, NormalMode).value(spy(userAnswers())) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
  }

  "go to Childcare Costs from Child Aged Three or Four" in {
    navigator.nextPage(ChildAgedThreeOrFourId, NormalMode).value(spy(userAnswers())) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
  }

  "from childcare costs" when {

    "go to `expect approved childcare cost` when you have childcare cost or not yet decided" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString) thenReturn Some(YesNoNotYetEnum.NOTYET.toString)
      when(freeHours.eligibility(any())) thenReturn NotDetermined
      navigator(freeHours, schemes).nextPage(ChildcareCostsId, NormalMode).value(answers) mustEqual routes.ApprovedProviderController.onPageLoad(NormalMode)
      navigator(freeHours, schemes).nextPage(ChildcareCostsId, NormalMode).value(answers) mustEqual routes.ApprovedProviderController.onPageLoad(NormalMode)
    }

    "go to `free hours results page` when user is not eligible for free hours and selects NO" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.NO.toString)
      when(freeHours.eligibility(any())) thenReturn NotEligible
      navigator(freeHours, schemes).nextPage(ChildcareCostsId, NormalMode).value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours result page` if you are eligible for free hours and selects NO" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.NO.toString)
      when(freeHours.eligibility(any())) thenReturn Eligible
      navigator(freeHours, schemes).nextPage(ChildcareCostsId, NormalMode).value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours info page` if you are eligible for free hours, in England and select NO" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.NO.toString)
      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(freeHours.eligibility(any())) thenReturn Eligible
      navigator(freeHours, schemes).nextPage(ChildcareCostsId, NormalMode).value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad()
    }
  }

  "Will your childcare costs be with an approved provider" when {

    "go to `free hours results page` when user's eligibility for all schemes is determined" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.approvedProvider) thenReturn Some(YesNoUnsureEnum.NO.toString)
      when(freeHours.eligibility(any())) thenReturn NotEligible
      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours info page` if you are eligible for free hours but not all schemes have been determined" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.approvedProvider) thenReturn Some(YesNoUnsureEnum.NO.toString)
      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(freeHours.eligibility(any())) thenReturn Eligible
      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad()
    }

    "go to `free hours info` if you are eligible for free hours and in England and not all schemes have been determined" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.approvedProvider) thenReturn Some(YesNoUnsureEnum.YES.toString) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(freeHours.eligibility(any())) thenReturn Eligible
      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad()
      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad()
    }

    "go to `free hours result` if user lives in England, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.approvedProvider) thenReturn Some(YesNoUnsureEnum.NO.toString)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.childAgedTwo) thenReturn Some(false)
      when(answers.childAgedThreeOrFour) thenReturn Some(false)
      when(freeHours.eligibility(any())) thenReturn NotEligible

      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours result` if user lives in NI, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers = spy(userAnswers())
      val freeHours = mock[FreeHours]
      val schemes = mock[Schemes]
      when(answers.approvedProvider) thenReturn Some(YesNoUnsureEnum.NO.toString)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
      when(answers.childAgedThreeOrFour) thenReturn Some(false)
      when(freeHours.eligibility(any())) thenReturn NotEligible

      navigator(freeHours, schemes).nextPage(ApprovedProviderId, NormalMode).value(answers) mustEqual routes.ResultController.onPageLoad()
    }
  }

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  def navigator(freeHours: FreeHours, schemes: Schemes): SubNavigator = new MinimumHoursNavigator(freeHours, schemes)
  def navigator: SubNavigator = new MinimumHoursNavigator(new FreeHours)
}
