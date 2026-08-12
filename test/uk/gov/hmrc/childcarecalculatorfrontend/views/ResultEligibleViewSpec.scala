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

import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultEligible

class ResultEligibleViewSpec extends NewViewBehaviours {

  val view: resultEligible = inject[resultEligible]

  def render(model: ResultsViewModel): Html = view(model)(using messages)

  "Result eligible view" must {

    "Contain results" when {
      "We have free hours value" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertContainsMessages(document, "15")
      }

      "User is eligible for TFC scheme" when {
        "User is eligible for less than £1,000" in {
          val model = ResultsViewModel(
            tfc = Some(600),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )

          val document = asDocument(render(model))

          assertContainsMessages(document, "600")
        }

        "User is eligible for more than £1,000" in {

          val model = ResultsViewModel(
            tfc = Some(1600),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )

          val document = asDocument(render(model))

          assertContainsMessages(document, "1,600")
        }

      }

      "User is eligible for ESC scheme" when {
        "User is eligible for less than £1,000" in {
          val model = ResultsViewModel(
            esc = Some(900),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )

          val document = asDocument(render(model))

          assertContainsMessages(document, "900")
        }
        "User is eligible for more than £1,000" in {
          val model = ResultsViewModel(
            tfc = Some(1900),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )

          val document = asDocument(render(model))

          assertContainsMessages(document, "1,900")
        }

      }

    }

    "display correct no of free hours and text when user is eligible for free hours" when {
      "location is England and no of hours is 15" in {
        val model = ResultsViewModel(
          freeHours = Some(BigDecimal(15)),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertContainsMessages(document, "15")
        assertContainsText(document, messages("result.free.hours.title.3to4"))
        assertContainsText(document, messages("result.you.could.get.up.to"))
        assertContainsText(document, messages("result.free.hours.hours"))
        assertContainsText(document, messages("result.free.hours.period.england", 570))
      }

      "location is Wales" in {
        val model = ResultsViewModel(
          freeHours = Some(10),
          location = Location.Wales,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertContainsMessages(document, "10")
        assertContainsText(document, messages("result.free.hours.title.3to4"))
        assertContainsText(document, messages("result.you.could.get"))
        assertContainsText(document, messages("result.free.hours.hours"))
        assertContainsText(document, messages("result.free.hours.period.wales"))
      }

      "location is Scotland" in {
        val model = ResultsViewModel(
          freeHours = Some(16),
          location = Location.Scotland,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertContainsMessages(document, "16")
        assertContainsText(document, messages("result.free.hours.title.3to4"))
        assertContainsText(document, messages("result.you.could.get"))
        assertContainsText(document, messages("result.free.hours.hours"))
        assertContainsText(document, messages("result.free.hours.period.scotland"))
      }

      "location is NI" in {
        val model = ResultsViewModel(
          freeHours = Some(12.5),
          location = Location.NorthernIreland,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertContainsMessages(document, "12 and a half hours")
        assertContainsText(document, messages("result.free.hours.title.3to4"))
        assertContainsText(document, messages("result.you.could.get.up.to"))
        assertContainsText(document, messages("result.free.hours.hours"))
        assertContainsText(document, messages("result.free.hours.period.northern-ireland"))
      }
    }

    "display the correct content for Free Hours For Working Parents" when {
      "location is England and working parents with just 2 year old" in {
        val model = ResultsViewModel(
          freeHours = Some(30),
          freeChildcareWorkingParents = true,
          location = Location.England,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertContainsText(document, messages("result.free.childcare.working.parents.title"))
        assertContainsText(
          document,
          messages("result.free.childcare.working.parents.two.year.old", frontendAppConfig.maxFreeHoursAmount)
        )
        assertNotContainsText(document, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
      }

      "location is England and working parents with just 3 or 4 year old" in {
        val model = ResultsViewModel(
          freeHours = Some(30),
          freeChildcareWorkingParents = true,
          location = Location.England,
          childrenAgeGroups = Set(ChildAgeGroup.ThreeYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertContainsText(document, messages("result.free.childcare.working.parents.title"))
        assertContainsText(document, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
        assertNotContainsText(document, messages("result.free.childcare.working.parents.two.year.old"))
      }

      "location is England and working parents with 2 year old and 3 or 4 year old" in {
        val model = ResultsViewModel(
          freeHours = Some(30),
          freeChildcareWorkingParents = true,
          location = Location.England,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears, ChildAgeGroup.ThreeYears, ChildAgeGroup.FourYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertContainsText(document, messages("result.free.childcare.working.parents.title"))
        assertContainsText(
          document,
          messages("result.free.childcare.working.parents.two.year.old", frontendAppConfig.maxFreeHoursAmount)
        )
        assertContainsText(document, messages("result.free.childcare.working.parents.threeOrFour.year.old"))
      }

      "location is England and not working parents with 2 year old and 3 or 4 year old" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.England,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears, ChildAgeGroup.FourYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertNotContainsText(document, messages("result.free.childcare.working.parents.title"))
      }
    }
  }

}
