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

import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultNotEligible

class ResultNotEligibleViewSpec extends NewViewBehaviours with MockitoSugar {

  lazy val appResultNotEligible: resultNotEligible = application.injector.instanceOf[resultNotEligible]

  val answers: UserAnswers = mock[UserAnswers]
  val locationEngland: Location.Value = Location.ENGLAND

  "Result not eligible view" must {
    "contain results" when {
      "We don't have free hours value for non england" in {
        val model = ResultsViewModel(freeHours = None, taxCreditsOrUC = None, location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultNotEligible(model)(messages))

        assertContainsMessages(view, "Free childcare hours")
        view.getElementById("notEligibleFreeHours").text() mustBe messages("result.free.hours.not.eligible.3to4")
      }

      "We don't have Free Childcare For Working Parents" in {
        val model = ResultsViewModel(freeHours = None, freeChildcareWorkingParentsEligibilityMsg = Some("not working"), taxCreditsOrUC = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultNotEligible(model)(messages))

        view.getElementById("notEligibleFreeChildcareWorkingParents").text() mustBe messages("not working")
      }

      "User answered UC to do you get tax credits or universal credits and hideTC is true " in {
        val model = ResultsViewModel(taxCreditsOrUC = Some("uc"), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(appResultNotEligible(model)(messages))

        assertNotContainsText(view, "Tax credits")
      }

      "User is not eligible for TFC scheme" in {
        val model = ResultsViewModel(tfc = None, taxCreditsOrUC = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, taxFreeChildcareEligibilityMsg = Some("not working"))
        val view = asDocument(appResultNotEligible(model)(messages))

        assertContainsMessages(view, "Tax-Free Childcare")
        view.getElementById("notEligibleTFC").text() mustBe messages("not working")
      }
    }
  }
}
