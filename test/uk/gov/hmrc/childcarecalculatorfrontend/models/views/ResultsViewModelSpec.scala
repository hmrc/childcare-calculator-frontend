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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.{FourYears, Location, ThreeYears, TwoYears}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{freeHoursForEngland, freeHoursForNI, freeHoursForScotland, freeHoursForWales}

class ResultsViewModelSpec extends SpecBase {

  val location: Location.Value = Location.ENGLAND

  "ResultViewModel" must {
    "let you know if you are eligible to all schemes" in {
      val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(3), esc = Some(2), location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      resultsView.isEligibleToAllSchemes mustBe true
    }

    "return correct number of eligible schemes" in {
      val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = None, esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      resultsView.noOfEligibleSchemes mustBe 2
    }

    "return number of eligible schemes 0 when there is no eligible scheme" in {
      val resultsView = ResultsViewModel(location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      resultsView.noOfEligibleSchemes mustBe 0
    }

    "return number of eligible schemes 0 when there is no eligible scheme with hideTC" in {
      val resultsView = ResultsViewModel(location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      resultsView.noOfEligibleSchemes mustBe 0
    }

    "display information about your two year old" when {
      "user does not live in Northern Ireland, has a two year old and either has a three year old or is eligible to any scheme" in {
        val model = ResultsViewModel(tc = Some(200), location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears, FourYears))
        model.showTwoYearOldInfo mustBe true
      }

      "user does not live in Northern Ireland, has a two year old and does not have a three year old and not eligible to any scheme " in {
        val model = ResultsViewModel(location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears))
        model.showTwoYearOldInfo mustBe true
      }

      "user does not live in Northern Ireland, has a two year old and does not have a three year old and not eligible to any scheme with hideTC" in {
        val model = ResultsViewModel(location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears))
        model.showTwoYearOldInfo mustBe true
      }
    }

    "not display information about your two year old" when {
      "user does live in Northern Ireland, has a two year old and either has a three year old or is eligible to any scheme" in {
        val model = ResultsViewModel(tc = Some(200), location = Location.NORTHERN_IRELAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears, ThreeYears))
        model.showTwoYearOldInfo mustBe false
      }

      "user does live in Northern Ireland, has a two year old and either has a three year old or is eligible to any scheme with hideTC" in {
        val model = ResultsViewModel(tc = Some(200), location = Location.NORTHERN_IRELAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears, ThreeYears))
        model.showTwoYearOldInfo mustBe false
      }
    }
  }

  "isEligibleOnlyToMinimumFreeHours" must {
    "return true" when {
      "user is eligible only to 15 Free Hours and no other schems" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(freeHoursForEngland), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleOnlyToMinimumFreeHours mustBe true
      }

      "user is eligible only to 10 Free Hours and no other schemes" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(freeHoursForWales), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleOnlyToMinimumFreeHours mustBe true
      }

      "user is eligible only to 22 Free Hours and no other schemes" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(freeHoursForScotland), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleOnlyToMinimumFreeHours mustBe true
      }

      "user is eligible only to 12.5 Free Hours and no other schemes" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(freeHoursForNI), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleOnlyToMinimumFreeHours mustBe true
      }
    }

    "return false" when {
      "user is not eligible to 15 Free Hours" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(30), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleOnlyToMinimumFreeHours mustBe false
      }
    }
  }

  "isEligibleToMaximumFreeHours" must{
    "return true" when {
      "user is eligible to 30 free hours" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(30), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleToMaximumFreeHours mustBe true
      }
      "user is not eligible to 30 free hours" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(15), esc = None, location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        resultsView.isEligibleToMaximumFreeHours mustBe false
      }
    }
  }

}