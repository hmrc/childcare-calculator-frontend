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

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildAgeGroup, Eligibility}
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Location, YesNoNotSure, YesNoNotYet}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.FreeHours
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers}

class MinimumHoursNavigatorSpec extends SpecBase with MockitoSugar {

  "go to Children Age Groups from Location when the location is England" in {
    val answers = spy(userAnswers())
    when(answers.location).thenReturn(Some(Location.England))

    navigator.nextPage(LocationId).value(answers) mustBe routes.ChildrenAgeGroupsController.onPageLoad(
    )
  }

  "go to Child Aged Two from Location when the location is Scotland" in {
    val answers = spy(userAnswers())
    when(answers.location).thenReturn(Some(Location.Scotland))

    navigator.nextPage(LocationId).value(answers) mustBe routes.ChildAgedTwoController.onPageLoad(
    )
  }

  "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
    val answers = spy(userAnswers())
    when(answers.location).thenReturn(Some(Location.NorthernIreland))
    navigator.nextPage(LocationId).value(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(
    )
  }

  "go to Child Aged Three or Four from Location when the location is Wales" in {
    val answers = spy(userAnswers())
    when(answers.location).thenReturn(Some(Location.Wales))
    navigator.nextPage(LocationId).value(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(
    )
  }

  "go to Child Aged Three or Four from Child Aged Two" in {
    navigator
      .nextPage(ChildAgedTwoId)
      .value(spy(userAnswers())) mustBe routes.ChildAgedThreeOrFourController.onPageLoad()
  }

  "go to Childcare Costs from Child Aged Three or Four" in {
    navigator
      .nextPage(ChildAgedThreeOrFourId)
      .value(spy(userAnswers())) mustBe routes.ChildcareCostsController.onPageLoad()
  }

  "from childcare costs" when {

    "go to `expect approved childcare cost` when you have childcare cost or not yet decided" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.childcareCosts)
        .thenReturn(Some(YesNoNotYet.Yes))
        .thenReturn(Some(YesNoNotYet.NotYet))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotDetermined)
      navigator(freeHours)
        .nextPage(ChildcareCostsId)
        .value(answers) mustEqual routes.ApprovedProviderController.onPageLoad()
      navigator(freeHours)
        .nextPage(ChildcareCostsId)
        .value(answers) mustEqual routes.ApprovedProviderController.onPageLoad()
    }

    "go to `free hours results page` when user is not eligible for free hours and selects NO" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.No))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)
      navigator(freeHours)
        .nextPage(ChildcareCostsId)
        .value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours result page` if you are eligible for free hours and selects NO" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.No))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.Eligible)
      navigator(freeHours)
        .nextPage(ChildcareCostsId)
        .value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours info page` if you are eligible for free hours, in England and select NO" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.No))
      when(answers.location).thenReturn(Some(Location.England))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.Eligible)
      navigator(freeHours)
        .nextPage(ChildcareCostsId)
        .value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad
    }
  }

  "Will your childcare costs be with an approved provider" when {

    "go to `free hours results page` when user's eligibility for all schemes is determined" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)
      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `free hours info page` if you are eligible for free hours but not all schemes have been determined" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(answers.location).thenReturn(Some(Location.England))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.Eligible)
      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad
    }

    "go to `free hours info` if you are eligible for free hours and in England and not all schemes have been determined" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider)
        .thenReturn(Some(YesNoNotSure.Yes))
        .thenReturn(Some(YesNoNotSure.NotSure))
      when(answers.location).thenReturn(Some(Location.England))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.Eligible)
      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad
      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.FreeHoursInfoController.onPageLoad
    }

    "go to `free hours result` if user lives in England, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.location).thenReturn(Some(Location.England))
      when(answers.childAgedTwo).thenReturn(Some(false))
      when(answers.childAgedThreeOrFour).thenReturn(Some(false))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)

      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.ResultController.onPageLoad()
    }

    "go to `do you live with partner` if user lives in England, has 2 year old, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.location).thenReturn(Some(Location.England))
      when(answers.childAgedTwo).thenReturn(Some(true))
      when(answers.childAgedThreeOrFour).thenReturn(Some(false))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)

      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.DoYouLiveWithPartnerController.onPageLoad()
    }

    "go to `do you live with partner` if user lives in England, has 9 to 22 month old, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.location).thenReturn(Some(Location.England))
      when(answers.childrenAgeGroups).thenReturn(Some(Set(ChildAgeGroup.NineTo23Months)))
      when(answers.childAgedTwo).thenReturn(Some(false))
      when(answers.childAgedThreeOrFour).thenReturn(Some(false))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)

      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.DoYouLiveWithPartnerController.onPageLoad()
    }

    "go to `do you live with partner` if user lives in England, has 9 to 22 month old and 2 year old, not eligible for min free hours, has no childcare cost" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.No))
      when(answers.location).thenReturn(Some(Location.England))
      when(answers.childrenAgeGroups).thenReturn(Some(Set(ChildAgeGroup.NineTo23Months, ChildAgeGroup.TwoYears)))
      when(answers.childAgedThreeOrFour).thenReturn(Some(false))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)

      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.DoYouLiveWithPartnerController.onPageLoad()
    }

    "go to `free hours result` if user lives in NI, not eligible for min free hours, have childcare cost but no approved provider" in {
      val answers   = spy(userAnswers())
      val freeHours = mock[FreeHours]
      when(answers.approvedProvider).thenReturn(Some(YesNoNotSure.No))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.location).thenReturn(Some(Location.NorthernIreland))
      when(answers.childAgedThreeOrFour).thenReturn(Some(false))
      when(freeHours.eligibility(any())).thenReturn(Eligibility.NotEligible)

      navigator(freeHours)
        .nextPage(ApprovedProviderId)
        .value(answers) mustEqual routes.ResultController.onPageLoad()
    }
  }

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers*)))

  def navigator(freeHours: FreeHours): SubNavigator = new MinimumHoursNavigator(freeHours)
  def navigator: SubNavigator                       = new MinimumHoursNavigator(new FreeHours)
}
