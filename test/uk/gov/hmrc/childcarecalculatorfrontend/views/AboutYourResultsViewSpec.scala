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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourResults

class AboutYourResultsViewSpec extends ViewBehaviours {

  def createView() = () => aboutYourResults(frontendAppConfig, ResultsViewModel())(fakeRequest, messages)

  "AboutYourResults view" must {

    behave like normalPage(createView(), "aboutYourResults")

    behave like resultPage(createView())

    "contain back to results link" in {

      val doc = asDocument(aboutYourResults(frontendAppConfig, ResultsViewModel())(fakeRequest, messages))

      doc.getElementById("returnToResults").text() mustBe messages("aboutYourResults.return.link")
      doc.getElementById("returnToResults").attr("href") mustBe ResultController.onPageLoad().url
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertRenderedByCssSelector(doc, ".freeHours")

        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.title"))
        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para1"))
        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para2"))
      }
    }

    "not display free hours contents" when {
      "user is not eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".freeHours")
      }
    }

    "display TC contents" when {
      "user is eligible for TC scheme" in {

        val model = ResultsViewModel(tc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertRenderedByCssSelector(doc, ".tc")

        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.title"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para1"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para2"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part1"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part2"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.eligibility.checker"))

        doc.getElementById("eligibilityChecker").attr("href") mustBe messages("aboutYourResults.tc.para3.eligibility.checker.link")

      }
    }

    "not display TC contents" when {
      "user is not eligible for TC scheme" in {

        val model = ResultsViewModel(tc = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".tc")
        assertNotRenderedById(doc, "eligibilityChecker")

      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {

        val model = ResultsViewModel(tfc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.title"))
        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para1"))
        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para2"))
      }
    }

   "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {

        val model = ResultsViewModel(tc = Some(2000), tfc = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".tfc")
        assertNotContainsText(doc, messages("aboutYourResults.tfc.para1"))
        assertNotContainsText(doc, messages("aboutYourResults.tfc.para2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.title"))
        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {

        val model = ResultsViewModel(tc = Some(3000), esc = None)
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".esc")
        assertNotContainsText(doc, messages("aboutYourResults.esc.title"))
        assertNotContainsText(doc, messages("aboutYourResults.esc.para1"))
        assertNotContainsText(doc, messages("aboutYourResults.esc.para2"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {

        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000))
        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertRenderedByCssSelector(doc, ".freeHours")
        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.title"))
        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para1"))
        doc.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para2"))

        assertRenderedByCssSelector(doc, ".tc")
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.title"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para1"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para2"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part1"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part2"))
        doc.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.eligibility.checker"))
        doc.getElementById("eligibilityChecker").attr("href") mustBe messages("aboutYourResults.tc.para3.eligibility.checker.link")

        assertRenderedByCssSelector(doc, ".tfc")
        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.title"))
        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para1"))
        doc.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para2"))

        assertRenderedByCssSelector(doc, ".esc")
        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.title"))
        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
        doc.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
      }
    }

    "display more info about the schemes" in {
      val model = ResultsViewModel(freeHours = Some(15), tc = Some(200))

      val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

      assertRenderedByCssSelector(doc, ".moreInfo")

      doc.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.title"))
      doc.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para1"))
      doc.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para2"))
      doc.getElementById("moreInfoHelp").attr("href") mustBe messages("aboutYourResults.more.info.para1.tfc.help.link")
    }

    "display guidance for 2 years old" when {
      "user lives in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Some(Location.ENGLAND), childAgedTwo = true)

        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertRenderedByCssSelector(doc, ".twoYearsOld")

        doc.getElementsByClass("twoYearsOld").text().contains( messages("aboutYourResults.two.years.old.guidance.title"))
        doc.getElementsByClass("twoYearsOld").text().contains( messages("aboutYourResults.two.years.old.guidance.para1"))
        doc.getElementById("twoYearsOldHelp").attr("href") mustBe messages("aboutYourResults.two.years.old.guidance.para1.help.link")
      }
    }

    "not display guidance for 2 years old" when {
      "user does not live in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Some(Location.SCOTLAND), childAgedTwo = true)

        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".twoYearsOld")
        assertNotContainsText(doc, messages("aboutYourResults.two.years.old.guidance.title"))
        assertNotContainsText(doc, messages("aboutYourResults.two.years.old.guidance.para1"))

        assertNotRenderedById(doc, "twoYearsOldHelp")
      }
    }

    "not display guidance for 2 years old" when {
      "user does not have 2 years old child" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Some(Location.ENGLAND), childAgedTwo = false)

        val doc = asDocument(aboutYourResults(frontendAppConfig, model)(fakeRequest, messages))

        assertNotRenderedByCssSelector(doc, ".twoYearsOld")
        assertNotContainsText(doc, messages("aboutYourResults.two.years.old.guidance.title"))
        assertNotContainsText(doc, messages("aboutYourResults.two.years.old.guidance.para1"))

        assertNotRenderedById(doc, "twoYearsOldHelp")
      }
    }
  }
}
