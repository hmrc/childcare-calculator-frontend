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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result

class ResultViewSpec extends ViewBehaviours {
  
  "Result view" must {

    behave like normalPage(() => result(frontendAppConfig,ResultsViewModel())(fakeRequest, messages), "result")

    "Contain results" when {
      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "15")
      }

      "We don't have free hours value" in {
        val model = ResultsViewModel(freeHours = None)
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "Not entitled to free hours")
      }

      "User is eligible for TC scheme" in {
        val model = ResultsViewModel(tc = Some(500))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "500")
      }

      "User is not eligible for TC scheme" in {
        val modelWithNoneValue = ResultsViewModel(tc = None)
        val modelWithTCValueZero = ResultsViewModel(tc = Some(0))

        val viewWithNoneValue = asDocument(result(frontendAppConfig, modelWithNoneValue)(fakeRequest, messages))
        val viewWithTCValueZero = asDocument(result(frontendAppConfig, modelWithTCValueZero)(fakeRequest, messages))

        assertContainsMessages(viewWithNoneValue, "Not entitled for TC scheme")
        assertContainsMessages(viewWithTCValueZero, "Not entitled for TC scheme")
      }

      "User is eligible for TFC scheme" in {
        val model = ResultsViewModel(tfc = Some(600))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "600")
      }

      "User is not eligible for TFC scheme" in {
        val modelWithNoneValue = ResultsViewModel(tfc = None)
        val modelWithTFCValueZero = ResultsViewModel(tfc = Some(0))

        val viewWithNoneValue = asDocument(result(frontendAppConfig, modelWithNoneValue)(fakeRequest, messages))
        val viewWithTFCValueZero = asDocument(result(frontendAppConfig, modelWithTFCValueZero)(fakeRequest, messages))

        assertContainsMessages(viewWithNoneValue, "Not entitled for TFC scheme")
        assertContainsMessages(viewWithTFCValueZero, "Not entitled for TFC scheme")
      }

      "User is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(1000))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "1000")
      }

      "User is not eligible for ESC scheme" in {
        val modelWithNoneValue = ResultsViewModel(esc = None)
        val modelWithESCValueZero = ResultsViewModel(esc = Some(0))

        val viewWithNoneValue = asDocument(result(frontendAppConfig, modelWithNoneValue)(fakeRequest, messages))
        val viewWithESCValueZero = asDocument(result(frontendAppConfig, modelWithESCValueZero)(fakeRequest, messages))

        assertContainsMessages(viewWithNoneValue, "Not entitled for ESC scheme")
        assertContainsMessages(viewWithESCValueZero, "Not entitled for ESC scheme")
      }

    }
  }
}
