/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result

class ResultViewSpec extends NewViewBehaviours with MockitoSugar {

  val resultView: result = application.injector.instanceOf[result]

  val locationEngland: Location.Value = Location.ENGLAND
  val locationScotland: Location.Value = Location.SCOTLAND
  val locationWales: Location.Value = Location.WALES

  def createView(): () => HtmlFormat.Appendable = () => resultView(frontendAppConfig: FrontendAppConfig, ResultsViewModel(tc = Some(400), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true), new Utils)(fakeRequest, messages)


  "Result view" must {

    behave like normalPage(createView(), "result")

    "contain two year old section" in {
      val model = ResultsViewModel(esc = Some(30), tc = Some(30), tfc = None, freeHours = Some(15), taxCreditsOrUC = None, location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true, childrenAgeGroups = Set(TwoYears))
      val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("results.two.years.old.guidance.england.title"))
    }

    "Contain results" when {
      "We have introductory paragraph when we are eligible to anything other than freehours on its own" in {
        val model = ResultsViewModel(List("This is the first paragraph"), freeHours = Some(15), tc = Some(200), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsMessages(view, "This is the first paragraph")
      }


      "With no introductary paragraph" when {
        "we are only entitled to free hours" in {
          val model = ResultsViewModel(List("This is the first paragraph"), freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

          assertNotContainsText(view, "This is the first paragraph")
        }

        "we are not entitled to anything" in {
          val model = ResultsViewModel(List("This is the first paragraph"), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

          assertNotContainsText(view, "This is the first paragraph")
        }
      }
    }

    "display correct contents when user is not eligible for any of the schemes" in {
      val model = ResultsViewModel(location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
      val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertContainsMessages(view, messages("result.heading.not.eligible"))
      assertNotContainsText(view, messages("result.more.info.title"))
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {
        val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedById(view, "freeHours")
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
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

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
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedById(view, "tc")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detail.summary"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara1"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc.link.text"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara3"))

      }
    }

    "not display TC contents" when {
      "user is not eligible for TC scheme" in {
        val model = ResultsViewModel(tc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tc")
        assertNotContainsText(view, messages("result.tc.detail.summary"))
        assertNotContainsText(view, messages("result.tc.detailPara1"))
        assertNotContainsText(view, messages("result.tc.detailPara2.tax.credit.replace.uc"))
        assertNotContainsText(view, messages("result.tc.detailPara3"))

      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {
        val model = ResultsViewModel(tfc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedById(view, "tfc")
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detail.summary"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara1"))
        view.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {
        val model = ResultsViewModel(tc = Some(2000), tfc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".tfc")
        assertNotContainsText(view, messages("result.tfc.detail.summary"))
        assertNotContainsText(view, messages("result.tfc.detailPara1"))
        assertNotContainsText(view, messages("result.tfc.detailPara2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(esc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedById(view, "esc")
        view.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.start"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.link.text"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.end"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {
        val model = ResultsViewModel(tc = Some(3000), esc = None, location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".esc")
        assertNotContainsText(view, messages("result.esc.detail.summary"))
        assertNotContainsText(view, messages("result.esc.detailPara1"))
        assertNotContainsText(view, messages("result.esc.detailPara2.start"))
        assertNotContainsText(view, messages("result.esc.detailPara2.link.text"))
        assertNotContainsText(view, messages("result.esc.detailPara2.end"))
        assertNotContainsText(view, messages("result.esc.detailPara2.link"))
        assertNotContainsText(view, messages("result.esc.detailPara3"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), tfc = Some(2300), esc = Some(2000), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedById(view, "freeHours")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detail.summary"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara1"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara2"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.you.can"))
        view.getElementById("contactLocalCouncil").attr("href") mustBe messages("result.free.hours.detailPara3.link")
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.link.text"))
        view.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara4"))

        assertRenderedById(view, "tc")
        view.getElementsByClass("tc").text().contains(messages("result.tc.detail.summary"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara1"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara2.tax.credit.replace.uc.link.text"))
        view.getElementsByClass("tc").text().contains(messages("result.tc.detailPara3"))

        assertRenderedById(view, "esc")
        view.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        view.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "not display more info about the schemes when only eligible to free hours" in {
      val model = ResultsViewModel(freeHours = Some(15), location = locationEngland, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

      val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

      assertNotContainsText(view, messages("aboutYourResults.more.info.title"))
    }

    "display guidance for 2 years old" when {
      "user lives in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = locationEngland, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.two.freehours"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.title"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.england.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.text.after.link"))
      }
    }

    "display guidance for 2 years old" when {
      "user lives in Scotland" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertRenderedByCssSelector(view, ".twoYearsOld")

        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.two.freehours"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.title"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.text.before.link"))
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.link.text"))
        view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.scotland.para1.help.link")
        view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.text.after.link"))
      }
    }

    "do not display guidance for 2 years old" when {
      "user lives in Wales" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".twoYearsOld")
        assertNotRenderedById(view, "twoYearsOldHelp")
        assertNotContainsText(view, messages("results.two.years.old.guidance.title"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.before.link"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.link.text"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.after.link"))
      }
    }

    "not display guidance for 2 years old" when {
      "user does not live in England" in {
        val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
        val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertNotRenderedByCssSelector(view, ".twoYearsOld")
        assertNotContainsText(view, messages("results.two.years.old.guidance.title"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.before.link"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.link.text"))
        assertNotContainsText(view, messages("results.two.years.old.guidance.text.after.link"))
        assertNotRenderedById(view, "twoYearsOldHelp")
      }

      "display guidance for 2 years old" when {
        "user lives in England" in {
          val model = ResultsViewModel(freeHours = None, tc = None, tfc = None, esc = None, location = locationEngland, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

          assertRenderedByCssSelector(view, ".twoYearsOld")

          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.two.freehours"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.title"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.text.before.link"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.link.text"))
          view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.england.para1.help.link")
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.england.text.after.link"))
        }
      }

      "display guidance for 2 years old" when {
        "user lives in Scotland" in {
          val model = ResultsViewModel(freeHours = None, tc = None, tfc = None, esc = None, location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

          assertRenderedByCssSelector(view, ".twoYearsOld")

          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.two.freehours"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.title"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.text.before.link"))
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.link.text"))
          view.getElementById("twoYearsOldHelp").attr("href") mustBe messages("results.two.years.old.guidance.scotland.para1.help.link")
          view.getElementsByClass("twoYearsOld").text().contains(messages("results.two.years.old.guidance.scotland.text.after.link"))
        }
      }

      "do not display guidance for 2 years old" when {
        "user lives in Wales and not eligible for any schemes" in {
          val model = ResultsViewModel(freeHours = None, tc = None, tfc = None, esc = None, location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

          assertNotRenderedByCssSelector(view, ".twoYearsOld")
          assertNotContainsText(view, messages("results.two.years.old.guidance.title"))
          assertNotContainsText(view, messages("results.two.years.old.guidance.text.before.link"))
          assertNotContainsText(view, messages("results.two.years.old.guidance.link.text"))
          assertNotContainsText(view, messages("results.two.years.old.guidance.text.after.link"))
          assertNotRenderedById(view, "twoYearsOldHelp")
        }
      }
    }
  }

  "Early results page" when {
    "rendered" must {
      "eligible for 22 free hours for scotland and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(22), location = Location.SCOTLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false, livesWithPartner = false, hasCostsWithApprovedProvider = false)
        val doc = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.scotland"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(10), location = Location.WALES, isAnyoneInPaidEmployment = true, hasChildcareCosts = false, livesWithPartner = false, hasCostsWithApprovedProvider = false)
        val doc = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.wales"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false, livesWithPartner = false, hasCostsWithApprovedProvider = false)
        val doc = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.northern-ireland"))
      }

      "eligible for 15 free hours for England and not eligible for other schemes" in {
        val model = ResultsViewModel(freeHours = Some(15), location = Location.ENGLAND, isAnyoneInPaidEmployment = true, hasChildcareCosts = false, livesWithPartner = false, hasCostsWithApprovedProvider = false)
        val doc = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.england"))
      }

      "display user research banner" when {
        "user reaches the reults page" in {
          val model = ResultsViewModel(freeHours = Some(15), tc = Some(200), location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsText(view, "Help make GOV.UK better")

        }
      }

      "show appropriate help links for Scotland" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = false, livesWithPartner = false)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.2")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.notWorking")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.LessThanMinimum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.BetweenMinimumAndMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.2")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.working")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.SCOTLAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.GreaterThanMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
      }

      "show appropriate help links for Wales" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = false, livesWithPartner = false)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.wales.li.1.notWorking")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.notWorking")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.LessThanMinimum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertNotContainsText(view, messages("freeHoursResult.info.extraHelp.heading"))
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.BetweenMinimumAndMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.wales.li.1.working")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.working")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.WALES, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.GreaterThanMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertNotContainsText(view, messages("freeHoursResult.info.extraHelp.heading"))
        }
      }

      "show appropriate help links for Northern Ireland" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = false, livesWithPartner = false)
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.notWorking")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.LessThanMinimum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.BetweenMinimumAndMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.tfc.li.working")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(freeHours = None, tc = None, location = Location.NORTHERN_IRELAND, childrenAgeGroups = Set(TwoYears), hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = false, yourEarnings = Some(EarningsEnum.GreaterThanMaximum))
          val view = asDocument(resultView(frontendAppConfig, model, new Utils)(fakeRequest, messages))
          assertContainsMessages(view, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
      }
    }
  }
}