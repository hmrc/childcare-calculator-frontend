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

class ResultViewSpec extends ViewBehaviours with ViewSpecBase {

  "Result view" must {
    val view = () => result(frontendAppConfig,
      ResultsViewModel(tc = Some(400)),
      new Utils)(fakeRequest, messages)


    "have the correct banner title" in {
      val doc = asDocument(view())
      val nav = doc.getElementById("proposition-menu")
      val span = nav.children.first
      span.text mustBe messagesApi("site.service_name")
    }

    "display the correct browser title" in {
      val doc = asDocument(view())
      assertEqualsValue(doc, "title", messagesApi(s"result.title")+" - "+messagesApi("site.service_name")+" - GOV.UK")
    }

    "display the correct page title" in {
      val doc = asDocument(view())
      assertPageTitleEqualsMessage(doc, s"result.title")
    }

    "display a beta banner" in {
      val doc = asDocument(view())
      assertRenderedByCssSelector(doc, ".beta-banner")
    }

    "not display HMRC branding" in {
      val doc = asDocument(view())
      assertNotRenderedByCssSelector(doc, ".organisation-logo")
    }



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
  }
}
