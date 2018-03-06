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
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
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
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible for all the schemes" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), esc = Some(230), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.tc.vouchers.eligibility.guidance.bullet"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible for all the schemes but Vouchers" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance.bullet"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible for all the schemes but TC" in {
      val model = ResultsViewModel( esc = Some(250), tfc = Some(300), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance.bullet"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible for all the schemes but TFC" in {
      val model = ResultsViewModel( tc = Some(200), esc = Some(250), freeHours = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.bullet"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible for all schemes but Free Hours" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(250), tfc = Some(300), tc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.tfc.tc.vouchers.eligibility.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for Free hours and TC" in {
      val model = ResultsViewModel(freeHours = Some(30), esc = None, tfc = None, tc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for ESC and TC" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(300), tfc = None, tc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for Free hours and TFC" in {
      val model = ResultsViewModel(esc = None, freeHours = Some(300), tc = None, tfc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for Free hours and ESC" in {
      val model = ResultsViewModel(tfc = None, freeHours = Some(300), tc = None, esc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for ESC and TFC" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(300), tc = None, tfc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display correct guidance when user is eligible only for TC and TFC" in {
      val model = ResultsViewModel(freeHours = None, tc = Some(300), esc = None, tfc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance.para"))
      assertContainsText(view, messages("result.changes.to.circumstances.heading"))
      assertContainsText(view, messages("result.changes.to.circumstances.para"))
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".freeHours")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detail.summary"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara1"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara2"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.you.can"))
        view.getElementById("contactLocalCouncil").attr("href") mustBe messages("result.free.hours.detailPara3.link")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.link.text"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara4"))
      }
    }

    "not display free hours contents" when {
      "user is not eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".freeHours")
        assertNotContainsText(view, messages("result.free.hours.detail.summary"))
        assertNotContainsText(view, messages("result.free.hours.detailPara1"))
        assertNotContainsText(view, messages("result.free.hours.detailPara2"))
        assertNotContainsText(view, messages("result.free.hours.detailPara3.link.text"))
        assertNotContainsText(view, messages("result.free.hours.detailPara4"))
      }
    }

    "display TC contents" when {
      "user is eligible for TC scheme" in {

        val model = ResultsViewModel(tc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".tc")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detail.summary"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara1"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc.link.text"))
        view.getElementById("findOutUCEligibility").attr("href") mustBe messages("result.tc.detailPara2.tax.credit.replace.uc.link")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara3"))

      }
    }

    "not display TC contents" when {
      "user is not eligible for TC scheme" in {

        val model = ResultsViewModel(tc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tc")
        assertNotContainsText(view, messages("result.tc.detail.summary"))
        assertNotContainsText(view, messages("result.tc.detailPara1"))
        assertNotContainsText(view, messages("result.tc.detailPara2.tax.credit.replace.uc"))
        assertNotContainsText(view, messages("result.tc.detailPara2.tax.credit.replace.uc.link.text"))
        assertNotContainsText(view, messages("result.tc.detailPara3"))

      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {

        val model = ResultsViewModel(tfc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".tfc")
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detail.summary"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara1"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {

        val model = ResultsViewModel(tc = Some(2000), tfc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tfc")
        assertNotContainsText(view, messages("result.tfc.detail.summary"))
        assertNotContainsText(view, messages("result.tfc.detailPara1"))
        assertNotContainsText(view, messages("result.tfc.detailPara2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".esc")
        view.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {

        val model = ResultsViewModel(tc = Some(3000), esc = None)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".esc")
        assertNotContainsText(view, messages("result.esc.detail.summary"))
        assertNotContainsText(view, messages("result.esc.detailPara1"))
        assertNotContainsText(view, messages("result.esc.detailPara2"))
        assertNotContainsText(view, messages("result.esc.detailPara3"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {

        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000))
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".freeHours")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detail.summary"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara1"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara2"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.you.can"))
        view.getElementById("contactLocalCouncil").attr("href") mustBe messages("result.free.hours.detailPara3.link")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.link.text"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara4"))

        assertRenderedByCssSelector(view, ".tc")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detail.summary"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara1"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc.link.text"))
        view.getElementById("findOutUCEligibility").attr("href") mustBe messages("result.tc.detailPara2.tax.credit.replace.uc.link")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara3"))

        assertRenderedByCssSelector(view, ".tfc")
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detail.summary"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara1"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara2"))
        view.getElementsByClass("tfc").text().contains(messages("result.schemes.tfc.tc.warning"))

        assertRenderedByCssSelector(view, ".esc")
        view.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "display more info about the schemes" in {
      val model = ResultsViewModel(freeHours = Some(15), tc = Some(200))
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertRenderedByCssSelector(view, ".moreInfo")

      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.title"))
      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para1"))
      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para2"))
    }

    "display guidance for 2 years old" when {
      "user lives in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Some(Location.ENGLAND), childAgedTwo = true)

        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.title"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.text.after.link"))
      }
    }

    "not display guidance for 2 years old" when {
      "user does not live in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Some(Location.SCOTLAND), childAgedTwo = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".twoYearsOld")
        assertNotContainsText(view, messages("results.two.years.old.guidance.title"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.before.link"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.link.text"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.after.link"))
        assertNotRenderedById(view, "twoYearsOldHelp")
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
