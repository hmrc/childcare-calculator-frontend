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
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Earnings, Location}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result

class ResultViewSpec extends NewViewBehaviours with MockitoSugar {

  val view: result = inject[result]

  val defaultModel: ResultsViewModel =
    ResultsViewModel(
      location = Location.England,
      hasChildcareCosts = true,
      hasCostsWithApprovedProvider = true,
      isAnyoneInPaidEmployment = true,
      livesWithPartner = true
    )

  def render(model: ResultsViewModel = defaultModel): Html = view(model)(using fakeRequest, messages)

  "Result view" must {

    behave.like(normalPage(() => render(), "result"))

    "contain two year old section" in {
      val model = ResultsViewModel(
        esc = Some(30),
        tfc = None,
        freeHours = Some(15),
        location = Location.Scotland,
        hasChildcareCosts = true,
        hasCostsWithApprovedProvider = true,
        isAnyoneInPaidEmployment = true,
        livesWithPartner = true,
        childrenAgeGroups = Set(ChildAgeGroup.TwoYears)
      )
      val document = asDocument(render(model))

      assertContainsMessages(document, messages("results.two.years.old.guidance.england.title"))
    }

    "Contain results" when {
      "We have introductory paragraph when we are eligible to anything other than freehours on its own" in {
        val model = ResultsViewModel(
          List("This is the first paragraph"),
          freeHours = Some(15),
          tfc = Some(20),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertContainsMessages(document, "This is the first paragraph")
      }

      "With no introductary paragraph" when {
        "we are only entitled to free hours" in {
          val model = ResultsViewModel(
            List("This is the first paragraph"),
            freeHours = Some(15),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )
          val document = asDocument(render(model))

          assertNotContainsText(document, "This is the first paragraph")
        }

        "we are not entitled to anything" in {
          val model = ResultsViewModel(
            List("This is the first paragraph"),
            location = Location.England,
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )
          val document = asDocument(render(model))

          assertNotContainsText(document, "This is the first paragraph")
        }
      }
    }

    "display correct contents when user is not eligible for any of the schemes" in {
      val model = ResultsViewModel(
        location = Location.England,
        hasChildcareCosts = true,
        hasCostsWithApprovedProvider = true,
        isAnyoneInPaidEmployment = true,
        livesWithPartner = true
      )
      val document = asDocument(render(model))

      assertContainsMessages(document, messages("result.heading.not.eligible"))
      assertNotContainsText(document, messages("result.more.info.title"))
    }

    "display free hours contents" when {
      "user is eligible for free hours scheme" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertRenderedById(document, "freeHours")
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detail.summary"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara1"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara2"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.you.can"))
        document.getElementById("contactLocalCouncil").attr("href") mustBe messages(
          "result.free.hours.detailPara3.link"
        )
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.link.text"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara4"))
      }
    }

    "not display free hours contents" when {
      "user is not eligible for free hours scheme" in {
        val model = ResultsViewModel(
          freeHours = None,
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertNotRenderedByCssSelector(document, ".freeHours")
        assertNotContainsText(document, messages("result.free.hours.detail.summary"))
        assertNotContainsText(document, messages("result.free.hours.detailPara1"))
        assertNotContainsText(document, messages("result.free.hours.detailPara2"))
        assertNotContainsText(document, messages("result.free.hours.detailPara3.link.text"))
        assertNotContainsText(document, messages("result.free.hours.detailPara4"))
      }
    }

    "display TFC contents" when {
      "user is eligible for TFC scheme" in {
        val model = ResultsViewModel(
          tfc = Some(2000),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertRenderedById(document, "tfc")
        document.getElementsByClass("tfc").text().contains(messages("result.tfc.detail.summary"))
        document.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara1"))
        document.getElementsByClass("tfc").text().contains(messages("result.tfc.detailPara2"))
      }
    }

    "not display TFC contents" when {
      "user is not eligible for TFC scheme" in {
        val model = ResultsViewModel(
          tfc = None,
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertNotRenderedByCssSelector(document, ".tfc")
        assertNotContainsText(document, messages("result.tfc.detail.summary"))
        assertNotContainsText(document, messages("result.tfc.detailPara1"))
        assertNotContainsText(document, messages("result.tfc.detailPara2"))
      }
    }

    "display ESC contents" when {
      "user is eligible for ESC scheme" in {
        val model = ResultsViewModel(
          esc = Some(2000),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertRenderedById(document, "esc")
        document.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.start"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.link.text"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara2.end"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "not display ESC contents" when {
      "user is not eligible for ESC scheme" in {
        val model = ResultsViewModel(
          esc = None,
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertNotRenderedByCssSelector(document, ".esc")
        assertNotContainsText(document, messages("result.esc.detail.summary"))
        assertNotContainsText(document, messages("result.esc.detailPara1"))
        assertNotContainsText(document, messages("result.esc.detailPara2.start"))
        assertNotContainsText(document, messages("result.esc.detailPara2.link.text"))
        assertNotContainsText(document, messages("result.esc.detailPara2.end"))
        assertNotContainsText(document, messages("result.esc.detailPara2.link"))
        assertNotContainsText(document, messages("result.esc.detailPara3"))
      }
    }

    "display contents for all the schemes" when {
      "user is eligible for all the schemes" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          tfc = Some(2300),
          esc = Some(2000),
          location = Location.England,
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertRenderedById(document, "freeHours")
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detail.summary"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara1"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara2"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.you.can"))
        document.getElementById("contactLocalCouncil").attr("href") mustBe messages(
          "result.free.hours.detailPara3.link"
        )
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara3.link.text"))
        document.getElementsByClass("freeHours").text().contains(messages("result.free.hours.detailPara4"))

        assertRenderedById(document, "esc")
        document.getElementsByClass("esc").text().contains(messages("result.esc.detail.summary"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara1"))
        document.getElementsByClass("esc").text().contains(messages("result.esc.detailPara3"))
      }
    }

    "not display more info about the schemes when only eligible to free hours" in {
      val model = ResultsViewModel(
        freeHours = Some(15),
        location = Location.England,
        hasChildcareCosts = true,
        hasCostsWithApprovedProvider = true,
        isAnyoneInPaidEmployment = true,
        livesWithPartner = true
      )

      val document = asDocument(render(model))

      assertNotContainsText(document, messages("aboutYourResults.more.info.title"))
    }

    "display guidance for 2 years old" when {
      "user lives in England" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.England,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.england.two.freehours"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.england.title"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.england.text.before.link"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.england.link.text"))
        document.getElementById("twoYearsOldHelp").attr("href") mustBe messages(
          "results.two.years.old.guidance.england.para1.help.link"
        )
        document
          .getElementById("twoYearsOldHelp")
          .text()
          .contains(messages("results.two.years.old.guidance.england.text.after.link"))
      }
    }

    "display guidance for 2 years old" when {
      "user lives in Scotland" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.Scotland,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.scotland.two.freehours"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.scotland.title"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.scotland.text.before.link"))
        document
          .getElementsByClass("twoYearsOld")
          .text()
          .contains(messages("results.two.years.old.guidance.scotland.link.text"))
        document.getElementById("twoYearsOldHelp").attr("href") mustBe messages(
          "results.two.years.old.guidance.scotland.para1.help.link"
        )
        document
          .getElementById("twoYearsOldHelp")
          .text()
          .contains(messages("results.two.years.old.guidance.scotland.text.after.link"))
      }
    }

    "do not display guidance for 2 years old" when {
      "user lives in Wales" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.Wales,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )

        val document = asDocument(render(model))

        assertNotRenderedById(document, "twoYearsOldHelp")
        assertNotContainsText(document, messages("results.two.years.old.guidance.title"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.text.before.link"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.link.text"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.text.after.link"))
      }
    }

    "not display guidance for 2 years old" when {
      "user does not live in England" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.NorthernIreland,
          childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
          hasChildcareCosts = true,
          hasCostsWithApprovedProvider = true,
          isAnyoneInPaidEmployment = true,
          livesWithPartner = true
        )
        val document = asDocument(render(model))

        assertNotContainsText(document, messages("results.two.years.old.guidance.title"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.text.before.link"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.link.text"))
        assertNotContainsText(document, messages("results.two.years.old.guidance.text.after.link"))
        assertNotRenderedById(document, "twoYearsOldHelp")
      }

      "display guidance for 2 years old" when {
        "user lives in England" in {
          val model = ResultsViewModel(
            freeHours = None,
            tfc = None,
            esc = None,
            location = Location.England,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false
          )
          val document = asDocument(render(model))

          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.england.two.freehours"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.england.title"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.england.text.before.link"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.england.link.text"))
          document.getElementById("twoYearsOldHelp").attr("href") mustBe messages(
            "results.two.years.old.guidance.england.para1.help.link"
          )
          document
            .getElementById("twoYearsOldHelp")
            .text()
            .contains(messages("results.two.years.old.guidance.england.text.after.link"))
        }
      }

      "display guidance for 2 years old" when {
        "user lives in Scotland" in {
          val model = ResultsViewModel(
            freeHours = None,
            tfc = None,
            esc = None,
            location = Location.Scotland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )
          val document = asDocument(render(model))

          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.scotland.two.freehours"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.scotland.title"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.scotland.text.before.link"))
          document
            .getElementsByClass("twoYearsOld")
            .text()
            .contains(messages("results.two.years.old.guidance.scotland.link.text"))
          document.getElementById("twoYearsOldHelp").attr("href") mustBe messages(
            "results.two.years.old.guidance.scotland.para1.help.link"
          )
          document
            .getElementById("twoYearsOldHelp")
            .text()
            .contains(messages("results.two.years.old.guidance.scotland.text.after.link"))
        }
      }

      "do not display guidance for 2 years old" when {
        "user lives in Wales and not eligible for any schemes" in {
          val model = ResultsViewModel(
            freeHours = None,
            tfc = None,
            esc = None,
            location = Location.Wales,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )
          val document = asDocument(render(model))

          assertNotRenderedByCssSelector(document, ".twoYearsOld")
          assertNotContainsText(document, messages("results.two.years.old.guidance.title"))
          assertNotContainsText(document, messages("results.two.years.old.guidance.text.before.link"))
          assertNotContainsText(document, messages("results.two.years.old.guidance.link.text"))
          assertNotContainsText(document, messages("results.two.years.old.guidance.text.after.link"))
          assertNotRenderedById(document, "twoYearsOldHelp")
        }
      }
    }
  }

  "Early results page" when {
    "rendered" must {
      "eligible for 22 free hours for scotland and not eligible for other schemes" in {
        val model = ResultsViewModel(
          freeHours = Some(22),
          location = Location.Scotland,
          isAnyoneInPaidEmployment = true,
          hasChildcareCosts = false,
          livesWithPartner = false,
          hasCostsWithApprovedProvider = false
        )
        val doc = asDocument(render(model))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.scotland"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {
        val model = ResultsViewModel(
          freeHours = Some(10),
          location = Location.Wales,
          isAnyoneInPaidEmployment = true,
          hasChildcareCosts = false,
          livesWithPartner = false,
          hasCostsWithApprovedProvider = false
        )
        val doc = asDocument(render(model))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.wales"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {
        val model = ResultsViewModel(
          freeHours = Some(12.5),
          location = Location.NorthernIreland,
          isAnyoneInPaidEmployment = true,
          hasChildcareCosts = false,
          livesWithPartner = false,
          hasCostsWithApprovedProvider = false
        )
        val doc = asDocument(render(model))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.northern-ireland"))
      }

      "eligible for 15 free hours for England and not eligible for other schemes" in {
        val model = ResultsViewModel(
          freeHours = Some(15),
          location = Location.England,
          isAnyoneInPaidEmployment = true,
          hasChildcareCosts = false,
          livesWithPartner = false,
          hasCostsWithApprovedProvider = false
        )
        val doc = asDocument(render(model))

        assertContainsText(doc, messages("freeHoursResult.info.entitled.england"))
      }

      "display user research banner" when {
        "user reaches the results page" in {
          val model = ResultsViewModel(
            freeHours = Some(15),
            location = Location.NorthernIreland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = true
          )
          val document = asDocument(render(model))
          assertContainsText(document, "This is a new service. Help us improve it")

        }
      }

      "show appropriate help links for Scotland" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Scotland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = false,
            livesWithPartner = false
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Scotland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.LessThanMinimum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Scotland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.BetweenMinimumAndMaximum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Scotland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.GreaterThanMaximum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.1")
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.scotland.li.2")
        }
      }

      "show appropriate help links for Wales" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Wales,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = false,
            livesWithPartner = false
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.wales.li.1.notWorking")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Wales,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.LessThanMinimum)
          )
          val document = asDocument(render(model))
          assertNotContainsText(document, messages("freeHoursResult.info.extraHelp.heading"))
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Wales,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.BetweenMinimumAndMaximum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.wales.li.1.working")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.Wales,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.GreaterThanMaximum)
          )
          val document = asDocument(render(model))
          assertNotContainsText(document, messages("freeHoursResult.info.extraHelp.heading"))
        }
      }

      "show appropriate help links for Northern Ireland" when {
        "the user is unemployed" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.NorthernIreland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = false,
            livesWithPartner = false
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
        "the user earns less than the national minimum wage" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.NorthernIreland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.LessThanMinimum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
        "the user earns more than the national minimum wage and less than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.NorthernIreland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.BetweenMinimumAndMaximum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
        "the user earns more than £100,000" in {
          val model = ResultsViewModel(
            freeHours = None,
            location = Location.NorthernIreland,
            childrenAgeGroups = Set(ChildAgeGroup.TwoYears),
            hasChildcareCosts = true,
            hasCostsWithApprovedProvider = true,
            isAnyoneInPaidEmployment = true,
            livesWithPartner = false,
            yourEarnings = Some(Earnings.GreaterThanMaximum)
          )
          val document = asDocument(render(model))
          assertContainsMessages(document, "freeHoursResult.info.extraHelp.northern-ireland.li.1")
        }
      }
    }
  }

}
