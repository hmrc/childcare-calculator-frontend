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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import uk.gov.hmrc.childcarecalculatorfrontend.models.{FourYears, Location, ThreeYears, TwoYears}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultEligible

class ResultEligibleViewSpec extends NewViewBehaviours {

  lazy val appResultEligible: resultEligible = application.injector.instanceOf[resultEligible]

  val utils = new Utils

  "Result eligible view" must {

    "Contain results" when {
      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertContainsMessages(view, "15")
      }

      "User is eligible for TC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(tc = Some(500),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val modelWithMoreThan1000 = ResultsViewModel(tc = Some(1500),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val viewWithLessThan1000 = asDocument(appResultEligible(modelWithLessThan1000, utils, "england")(messages))
        val viewWithMoreThan1000 = asDocument(appResultEligible(modelWithMoreThan1000, utils, "england")(messages))

        assertContainsMessages(viewWithLessThan1000, "500")
        assertContainsMessages(viewWithMoreThan1000, "1,500")
      }

     "User is eligible for TFC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(tfc = Some(600),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val modelWithMoreThan1000 = ResultsViewModel(tfc = Some(1600),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val viewWithLessThan1000 = asDocument(appResultEligible(modelWithLessThan1000, utils, "england")(messages))
        val viewWithMoreThan1000 = asDocument(appResultEligible(modelWithMoreThan1000, utils, "england")(messages))

        assertContainsMessages(viewWithLessThan1000, "600")
        assertContainsMessages(viewWithMoreThan1000, "1,600")
      }

      "User is eligible for ESC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(esc = Some(900),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val modelWithMoreThan1000 = ResultsViewModel(tfc = Some(1900),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val viewWithLessThan1000 = asDocument(appResultEligible(modelWithLessThan1000, utils, "england")(messages))
        val viewWithMoreThan1000 = asDocument(appResultEligible(modelWithMoreThan1000, utils, "england")(messages))

        assertContainsMessages(viewWithLessThan1000, "900")
        assertContainsMessages(viewWithMoreThan1000, "1,900")
      }

    }

    "display correct no of free hours and text when user is eligible for free hours" when {
      "location is England and no of hours is 15" in {
        val model = ResultsViewModel(freeHours = Some(BigDecimal(15)), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertContainsMessages(view, "15")
        assertContainsText(view, messages("result.free.hours.title.3to4"))
        assertContainsText(view, messages("result.you.could.get.up.to"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.england", 570))
      }

      "location is Wales" in {
        val model = ResultsViewModel(freeHours = Some(10), location =Location.WALES, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "wales")(messages))

        assertContainsMessages(view, "10")
        assertContainsText(view, messages("result.free.hours.title.3to4"))
        assertContainsText(view, messages("result.you.could.get"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.wales"))
      }

      "location is Scotland" in {
        val model = ResultsViewModel(freeHours = Some(16), location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "scotland")(messages))

        assertContainsMessages(view, "16")
        assertContainsText(view, messages("result.free.hours.title.3to4"))
        assertContainsText(view, messages("result.you.could.get"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.scotland"))
      }

      "location is NI" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "northern-ireland")(messages))

        assertContainsMessages(view, "12 and a half hours")
        assertContainsText(view, messages("result.free.hours.title.3to4"))
        assertContainsText(view, messages("result.you.could.get.up.to"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.northern-ireland"))
      }
    }

    "display the correct content for Free Hours For Working Parents" when {
      "location is England and working parents with just 2 year old" in {
        val model = ResultsViewModel(freeHours = Some(30), freeChildcareWorkingParents = true, location = Location.ENGLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertContainsText(view, messages("result.free.childcare.working.parents.title"))
        assertContainsText(view, messages("result.free.childcare.working.parents.two.year.old", frontendAppConfig.maxFreeHoursAmount))
        assertNotContainsText(view, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
      }

      "location is England and working parents with just 3 or 4 year old" in {
        val model = ResultsViewModel(freeHours = Some(30), freeChildcareWorkingParents = true, location = Location.ENGLAND, childrenAgeGroups = Set(ThreeYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertContainsText(view, messages("result.free.childcare.working.parents.title"))
        assertContainsText(view, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
        assertNotContainsText(view, messages("result.free.childcare.working.parents.two.year.old"))
      }

      "location is England and working parents with 2 year old and 3 or 4 year old" in {
        val model = ResultsViewModel(freeHours = Some(30), freeChildcareWorkingParents = true, location = Location.ENGLAND, childrenAgeGroups = Set(TwoYears, ThreeYears, FourYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertContainsText(view, messages("result.free.childcare.working.parents.title"))
        assertContainsText(view, messages("result.free.childcare.working.parents.two.year.old", frontendAppConfig.maxFreeHoursAmount))
        assertContainsText(view, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
      }

      "location is England and not working parents with 2 year old and 3 or 4 year old" in {
        val model = ResultsViewModel(freeHours = Some(15), location = Location.ENGLAND, childrenAgeGroups = Set(TwoYears, FourYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultEligible(model, utils, "england")(messages))

        assertNotContainsText(view, messages("result.free.childcare.working.parents.title"))
      }
    }
  }
}
