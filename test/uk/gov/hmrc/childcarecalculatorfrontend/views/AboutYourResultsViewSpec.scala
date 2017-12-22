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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourResults

class AboutYourResultsViewSpec extends ViewBehaviours {

  def createView = () => aboutYourResults(frontendAppConfig, ResultsViewModel())(fakeRequest, messages)

  "AboutYourResults view" must {

    behave like normalPage(createView, "aboutYourResults")

    "contain back to results link" in {

      val doc = asDocument(createView())

      doc.getElementById("returnToResults").text() mustBe messages("aboutYourResults.return.link")
      doc.getElementById("returnToResults").attr("href") mustBe ResultController.onPageLoad().url
    }

    "display the correct title" in {

      val doc = asDocument(createView())
      assertContainsMessages(doc, messages("aboutYourResults.about.the.schemes"))

    }

    "display free hours content" when {
      "user is eligible for free hours display title" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.title"))
      }

      "user is eligible for free hours display first paragraph" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para1"))
      }

      "user is eligible for free hours display second paragraph" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para2"))
      }
    }

    "display TC content" when {
      "user is eligible for TC display title" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.title"))
      }

      "user is eligible for TC display first paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para1"))
      }

      "user is eligible for TC display second paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para2"))
      }

      "user is eligible for TC display third paragraph" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para3"))
      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {

        val model = ResultsViewModel(tfc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.tfc.title"))
        assertContainsMessages(doc, messages("aboutYourResults.tfc.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.tfc.para2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {

        val model = ResultsViewModel(tc = Some(2000), tfc = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertNotContainsText(doc, messages("aboutYourResults.tfc.title"))
        assertNotContainsText(doc, messages("aboutYourResults.tfc.para1"))
        assertNotContainsText(doc, messages("aboutYourResults.tfc.para2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {

        val model = ResultsViewModel(esc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertContainsMessages(doc, messages("aboutYourResults.esc.title"))
        assertContainsMessages(doc, messages("aboutYourResults.esc.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.esc.para2"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {

        val model = ResultsViewModel(tc = Some(3000), esc = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))
        assertNotContainsText(doc, messages("aboutYourResults.esc.title"))
        assertNotContainsText(doc, messages("aboutYourResults.esc.para1"))
        assertNotContainsText(doc, messages("aboutYourResults.esc.para2"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {

        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.title"))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.free.childcare.hours.para2"))

        assertContainsMessages(doc, messages("aboutYourResults.tc.title"))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para2"))
        assertContainsMessages(doc, messages("aboutYourResults.tc.para3"))

        assertContainsMessages(doc, messages("aboutYourResults.tfc.title"))
        assertContainsMessages(doc, messages("aboutYourResults.tfc.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.tfc.para2"))

        assertContainsMessages(doc, messages("aboutYourResults.esc.title"))
        assertContainsMessages(doc, messages("aboutYourResults.esc.para1"))
        assertContainsMessages(doc, messages("aboutYourResults.esc.para2"))
      }
    }
  }
}
