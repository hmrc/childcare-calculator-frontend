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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultEligible

class ResultEligibleViewSpec extends ViewBehaviours {

  "Result eligible view" must {

    "Contain results" when {
      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(resultEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "15")
      }


      "User is eligible for TC scheme" in {
        val model = ResultsViewModel(tc = Some(500))
        val view = asDocument(resultEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "500")
      }



      "User is eligible for TFC scheme" in {
        val model = ResultsViewModel(tfc = Some(600))
        val view = asDocument(resultEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "600")
      }


      "User is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(900))
        val view = asDocument(resultEligible(model)(fakeRequest, messages))

        assertContainsMessages(view, "900")
      }

    }
  }
}
