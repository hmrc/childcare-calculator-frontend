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

import org.jsoup.nodes.Element
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result

class ResultViewSpec extends ViewBehaviours with MockitoSugar {

  val locationEngland = Location.ENGLAND
  def createView() = () => result(frontendAppConfig, ResultsViewModel(tc = Some(400),location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true), List.empty, None, new Utils)(fakeRequest, messages)

  "Result view" must {

    behave like normalPage(createView(),"result")

    "Contain results" when {
      "We have introductory paragraph when we are eligible to anything other than freehours on its own" in {
        val model = ResultsViewModel("This is the first paragraph",freeHours = Some(15), tc = Some(200),location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "This is the first paragraph")
      }



      "With no introductary paragraph" when {
        "we are only entitled to free hours" in {
          val model = ResultsViewModel("This is the first paragraph",freeHours = Some(15),location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

          assertNotContainsText(view, "This is the first paragraph")
        }

        "we are not entitled to anything" in {
          val model = ResultsViewModel("This is the first paragraph",location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

          assertNotContainsText(view, "This is the first paragraph")
        }
      }

      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 1 scheme")
      }

      "user is eligible for more than one of the schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200),location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "You are eligible for help from 2 schemes")
      }
    }

    "display correct contents when user is not eligible for any of the schemes" in {
      val model = ResultsViewModel(location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.heading.not.eligible"))
      assertNotContainsText(view, messages("result.more.info.title"))
  }

    "display correct guidance when user is eligible for all the schemes" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), esc = Some(230), freeHours = Some(200),location=locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.tc.vouchers.eligibility.guidance.bullet"))

    }

    "display correct guidance when user is eligible for all the schemes but Vouchers" in {
      val model = ResultsViewModel( tc = Some(200), tfc = Some(250), freeHours = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance.bullet"))
    }

    "display correct guidance when user is eligible for all the schemes but TC" in {
      val model = ResultsViewModel( esc = Some(250), tfc = Some(300), freeHours = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.bullet"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance.bullet"))
    }

    "display correct guidance when user is eligible for all the schemes but TFC" in {
      val model = ResultsViewModel( tc = Some(200), esc = Some(250), freeHours = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertContainsMessages(view, messages("result.title"))
      assertContainsText(view, messages("result.more.info.title"))
      assertContainsText(view, messages("result.more.info.para"))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.bullet"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.bullet"))
    }

    "display correct guidance when user is eligible for all schemes but Free Hours" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(250), tfc = Some(300), tc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.tfc.tc.vouchers.eligibility.guidance.para"))
    }

    "display correct guidance when user is eligible only for Free hours and TC" in {
      val model = ResultsViewModel(freeHours = Some(30), esc = None, tfc = None, tc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.with.tc.para"))
    }

    "display correct guidance when user is eligible only for ESC and TC" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(300), tfc = None, tc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.tax.credit.eligibility.with.vouchers.guidance.para"))
    }

    "display correct guidance when user is eligible only for Free hours and TFC" in {
      val model = ResultsViewModel(esc = None, freeHours = Some(300), tc = None, tfc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.para"))

    }

    "display correct guidance when user is eligible only for Free hours and ESC" in {
      val model = ResultsViewModel(tfc = None, freeHours = Some(300), tc = None, esc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.free.hours.eligibility.guidance.para"))
    }

    "display correct guidance when user is eligible only for ESC and TFC" in {
      val model = ResultsViewModel(freeHours = None, esc = Some(300), tc = None, tfc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.vouchers.guidance.para"))
    }

    "display correct guidance when user is eligible only for TC and TFC" in {
      val model = ResultsViewModel(freeHours = None, tc = Some(300), esc = None, tfc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
      assertContainsText(view, messages("result.estimates.income.para1"))
      assertContainsText(view, messages("result.schemes.tfc.ineligibility.taxCredits.guidance.para"))
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {

        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(freeHours = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(tc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(tc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(tfc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".tfc")
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detail.summary"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara1"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {

        val model = ResultsViewModel(tc = Some(2000), tfc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tfc")
        assertNotContainsText(view, messages("result.tfc.detail.summary"))
        assertNotContainsText(view, messages("result.tfc.detailPara1"))
        assertNotContainsText(view, messages("result.tfc.detailPara2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(tc = Some(3000), esc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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

        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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
      val model = ResultsViewModel(freeHours = Some(30), tc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertRenderedByCssSelector(view, ".moreInfo")

      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.title"))
      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para1"))
      view.getElementsByClass("moreInfo").text().contains(messages("aboutYourResults.more.info.para2"))
    }

    "not display more info about the schemes when only eligible to free hours" in {
      val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

      val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

      assertNotContainsText(view,messages("aboutYourResults.more.info.title"))
    }

    "display guidance for 2 years old" when {
      "user lives in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200),location = locationEngland, childAgedTwo = true, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.england.two.freehours"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.england.title"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.england.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.england.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.england.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.england.text.after.link"))
      }
    }


    "display guidance for 2 years old" when {
      "user lives in Scotland" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.SCOTLAND, childAgedTwo = true, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.scotland.two.freehours"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.scotland.title"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.scotland.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.scotland.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.scotland.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.scotland.text.after.link"))
      }
    }

    "display guidance for 2 years old" when {
      "user lives in Wales" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.WALES, childAgedTwo = true, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.wales.two.freehours"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.wales.title"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.wales.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.wales.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.wales.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains( messages("results.two.years.old.guidance.wales.text.after.link"))
      }
    }

    "not display guidance for 2 years old" when {
      "user does not live in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.NORTHERN_IRELAND, childAgedTwo = true, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
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
        val model = ResultsViewModel( esc = Some(250), tfc = Some(300), tc = Some(200),showTFCWarning = true, tfcWarningMessage = "this is a test", location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(view, "this is a test")
      }
    }

    "not display TFC warning message" when {
      "it is not needed" in {
        val model = ResultsViewModel( esc = Some(250), tfc = Some(300), tc = None, showTFCWarning = false, tfcWarningMessage = "this is a test", location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertNotContainsText(view, "this is a test")
      }
    }
  }

  "Early results page" when {
    "rendered" must {
      "contain correct guidance when not eligible for location other than northern-ireland when don't have childcare cost and not eligible" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.childcare.cost.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))
      }

      "contain correct guidance when not eligible for location other than northern-ireland when childcare cost not with approved provider and not eligible" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val childCareCostLink: Element = doc.getElementById("free-hours-results-approved-provider-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.approved.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.start"))
        childCareCostLink.attr("href") mustBe routes.ApprovedProviderController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.approved.provider.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.end"))
      }

      "contain correct guidance when not eligible for location other than northern-ireland when not in employment and not eligible" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val paidWorkLink: Element = doc.getElementById("free-hours-results-paid-work-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.paidwork.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.start"))
        paidWorkLink.attr("href") mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode).url
        paidWorkLink.text mustBe messagesApi("freeHoursResult.toBeEligible.paid.work.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.end"))
      }

      "contain correct guidance when not eligible for location other than northern-ireland when don't have childcare cost and eligible" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.childcare.cost.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))
      }

      "contain correct guidance when not eligible for location other than northern-ireland when childcare cost not with approved provider and eligible" in {

        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val childCareCostLink: Element = doc.getElementById("free-hours-results-approved-provider-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.approved.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.start"))
        childCareCostLink.attr("href") mustBe routes.ApprovedProviderController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.approved.provider.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.end"))
      }

      "contain correct guidance when not eligible for location other than northern-ireland when not in employment and eligible" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, isAnyoneInPaidEmployment = false, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val paidWorkLink: Element = doc.getElementById("free-hours-results-paid-work-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.paidwork.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.start"))
        paidWorkLink.attr("href") mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode).url
        paidWorkLink.text mustBe messagesApi("freeHoursResult.toBeEligible.paid.work.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.end"))
      }

      "contain correct guidance when not eligible for location northern-ireland" in {
        val model = ResultsViewModel(location = Location.NORTHERN_IRELAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))
        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.free.hours"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))
      }

      "eligible for 16 free hours for scotland and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(16), location = Location.SCOTLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(10), location = Location.WALES, isAnyoneInPaidEmployment = true, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
      }

      "eligible for 15 free hours for England and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), location = Location.ENGLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false,livesWithPartner = false,hasCostsWithApprovedProvider = false)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.england"))
      }

      "eligible for 16 free hours for scotland, gets tc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(16), location = Location.SCOTLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 16 free hours for scotland, gets uc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(16), location = Location.SCOTLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 10 free hours for wales, gets tc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(10), location = Location.WALES, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = true,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }
      /******/
      "eligible for 10 free hours for wales, gets uc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(10), location = Location.WALES, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 12.5 free hours for northern-ireland, gets tc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = true,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 12.5 free hours for northern-ireland, gets uc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = false,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 15 free hours for England, gets tc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), location = Location.ENGLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = true,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.england"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }

      "eligible for 15 free hours for England, gets uc and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), location = Location.ENGLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = true,livesWithPartner = true,hasCostsWithApprovedProvider = true)
        val doc = asDocument(result(frontendAppConfig, model, List.empty, None, new Utils )(fakeRequest, messages))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.england"))
        assertContainsText(doc, messagesApi("result.tc.title"))
        assertContainsText(doc, messagesApi("result.tfc.title"))
        assertContainsText(doc, messagesApi("result.tfc.not.eligible"))
        assertContainsText(doc, messagesApi("result.esc.title"))
        assertContainsText(doc, messagesApi("result.esc.not.eligible.para1"))
      }
    }
  }
}
