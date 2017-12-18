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

    behave like normalPage(() => result(frontendAppConfig,ResultsViewModel(""))(fakeRequest, messages), "result")

    "Contain results" when {
      "We have introductory paragraph" in {
        val model = ResultsViewModel("This is the first paragraph")
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "This is the first paragraph")
      }

      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 1 scheme:")
      }

      "user is eligible for more than one of the schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200))
        val view = asDocument(result(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 2 schemes:")
      }

    }
  }
}
