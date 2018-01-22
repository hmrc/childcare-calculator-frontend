/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._

class ResultViewSpec extends ViewBehaviours {
  
  "Result view" must {

    behave like normalPage(() => result(frontendAppConfig,
                                        ResultsViewModel(tc = Some(400)),
                                        new Utils)(fakeRequest, messages), "result")

    "Contain results" when {
      "We have introductory paragraph" in {
        val model = ResultsViewModel("This is the first paragraph")
        val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "This is the first paragraph")
      }

      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 1 scheme")
      }

      "user is eligible for more than one of the schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200))
        val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 2 schemes")
      }
    }

    "display link that reditects to about your results page" in {
      val model = ResultsViewModel(freeHours = Some(15))
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      view.getElementById("aboutYourResults").text() mustBe messages("result.read.more.button")
      view.getElementById("aboutYourResults").attr("href") mustBe AboutYourResultsController.onPageLoad().url
    }

    "display correct contents when user is not eligible for any of the schemes" in {
      val model = ResultsViewModel()
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertNotContainsText(view, messages("result.more.info.title"))
      assertNotContainsText(view, messages("result.more.info.para"))
      assertNotContainsText(view, messages("result.read.more.button"))
      assertNotRenderedById(view, "aboutYourResults")
    }

    "display correct guidance when user is eligible for all the schemes" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), esc = Some(230), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.read.more.button"))
      view.getElementById("aboutYourResults").attr("href") mustBe AboutYourResultsController.onPageLoad().url
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.and.vouchers.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but Vouchers" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.read.more.button"))
      view.getElementById("aboutYourResults").attr("href") mustBe AboutYourResultsController.onPageLoad().url
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.ineligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but TFC" in {
      val model = ResultsViewModel( tc = Some(200), esc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.read.more.button"))
      view.getElementById("aboutYourResults").attr("href") mustBe AboutYourResultsController.onPageLoad().url
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but TC" in {
      val model = ResultsViewModel( esc = Some(250), tfc = Some(300), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.read.more.button"))
      view.getElementById("aboutYourResults").attr("href") mustBe AboutYourResultsController.onPageLoad().url
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance"))
    }
  }
}
