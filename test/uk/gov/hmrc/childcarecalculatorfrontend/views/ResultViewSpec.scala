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

import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result

class ResultViewSpec extends ViewBehaviours with MockitoSugar {

  val answers: UserAnswers = mock[UserAnswers]
  def createView() = () => result(frontendAppConfig, ResultsViewModel(tc = Some(400)), List.empty, None, new Utils)(fakeRequest, messages)
  
  "Result view" must {

    behave like normalPage(createView(),"result")

    "Contain results" when {
      "We have introductory paragraph" in {
        val model = ResultsViewModel("This is the first paragraph")
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "This is the first paragraph")
      }

      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 1 scheme")
      }

      "user is eligible for more than one of the schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 2 schemes")
      }
    }

    "display correct contents when user is not eligible for any of the schemes" in {
      val model = ResultsViewModel()
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.heading.not.eligible"))
      assertNotContainsText(view, messages("result.more.info.title"))
    }

    "display correct guidance when user is eligible for all the schemes" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), esc = Some(230), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.and.vouchers.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but Vouchers" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.ineligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but TFC" in {
      val model = ResultsViewModel( tc = Some(200), esc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance"))
    }

    "display correct guidance when user is eligible for all the schemes but TC" in {
      val model = ResultsViewModel( esc = Some(250), tfc = Some(300), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance"))
    }

    "display correct guidance when user is eligible for all schemes but Free Hours" in {
      val model = ResultsViewModel( esc = Some(250), tfc = Some(300), tc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.and.vouchers.guidance"))
      assertNotContainsText(view, messages("result.schemes.free.hours.eligibility.guidance"))
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".freeHours")

        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.title"))
        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para1"))
        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para2"))
      }
    }

    "not display free hours contents" when {
      "user is not eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".freeHours")
      }
    }

    "display TC contents" when {
      "user is eligible for TC scheme" in {

        val model = ResultsViewModel(tc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".tc")

        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.title"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para1"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para2"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part1"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part2"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.eligibility.checker"))

        view.getElementById("eligibilityChecker").attr("href") mustBe messages("aboutYourResults.tc.para3.eligibility.checker.link")

      }
    }

    "not display TC contents" when {
      "user is not eligible for TC scheme" in {

        val model = ResultsViewModel(tc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tc")
        assertNotRenderedById(view, "eligibilityChecker")

      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {

        val model = ResultsViewModel(tfc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.title"))
        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para1"))
        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {

        val model = ResultsViewModel(tc = Some(2000), tfc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tfc")
        assertNotContainsText(view, messages("aboutYourResults.tfc.para1"))
        assertNotContainsText(view, messages("aboutYourResults.tfc.para2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.title"))
        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {

        val model = ResultsViewModel(tc = Some(3000), esc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".esc")
        assertNotContainsText(view, messages("aboutYourResults.esc.title"))
        assertNotContainsText(view, messages("aboutYourResults.esc.para1"))
        assertNotContainsText(view, messages("aboutYourResults.esc.para2"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {

        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".freeHours")
        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.title"))
        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para1"))
        view.getElementsByClass("freeHours").text().contains(messages("aboutYourResults.free.childcare.hours.para2"))

        assertRenderedByCssSelector(view, ".tc")
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.title"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para1"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para2"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part1"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.part2"))
        view.getElementsByClass("tc").text().contains(messages("aboutYourResults.tc.para3.eligibility.checker"))
        view.getElementById("eligibilityChecker").attr("href") mustBe messages("aboutYourResults.tc.para3.eligibility.checker.link")

        assertRenderedByCssSelector(view, ".tfc")
        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.title"))
        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para1"))
        view.getElementsByClass("tfc").text().contains(messages("aboutYourResults.tfc.para2"))

        assertRenderedByCssSelector(view, ".esc")
        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.title"))
        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
        view.getElementsByClass("esc").text().contains(messages("aboutYourResults.esc.para1"))
      }
    }

    "display TFC warning message" when {
      "it is needed" in {
        val model = ResultsViewModel( esc = Some(250), tfc = Some(300), tc = Some(200),showTFCWarning = true, tfcWarningMessage = "this is a test")
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(view, "this is a test")
      }
    }

    "not display TFC warning message" when {
      "it is not needed" in {
        val model = ResultsViewModel( esc = Some(250), tfc = Some(300), tc = None, showTFCWarning = false, tfcWarningMessage = "this is a test")
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotContainsText(view, "this is a test")
      }
    }
  }
}
