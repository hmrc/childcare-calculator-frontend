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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultNotEligible
import uk.gov.hmrc.play.test.UnitSpec

class ResultNotEligibleViewSpec extends ViewBehaviours {

  "Result not eligible view" must {

    "contain results" when {
      "We don't have free hours value" in {
        val model = ResultsViewModel(freeHours = None)
        val view = asDocument(resultNotEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "Free childcare hours")
        view.getElementById("notEligibleFreeHours").text() mustBe messages("result.free.hours.not.eligible")
      }


      "User is not eligible for TC scheme" in {
        val model = ResultsViewModel(tc = None)
        val view = asDocument(resultNotEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "Tax credits")
        view.getElementById("notEligibleTC1").text() mustBe messages("result.tc.not.eligible.para1")
        view.getElementById("notEligibleTC2").text() mustBe messages("result.tc.not.eligible.para2")

      }


      "User is not eligible for TFC scheme" in {
        val model = ResultsViewModel(tfc = None)
        val view = asDocument(resultNotEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "Tax-Free Childcare")
        view.getElementById("notEligibleTFC").text() mustBe messages("result.free.hours.not.eligible")

      }


      "User is not eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = None)
        val view = asDocument(resultNotEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "Childcare vouchers")
        view.getElementById("notEligibleESC1").text() mustBe messages("result.esc.not.eligible.para1")
        view.getElementById("notEligibleESC2").text() mustBe messages("result.esc.not.eligible.para2")
      }


    }
  }
}
