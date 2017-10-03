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

import org.jsoup.nodes.Element
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.{Section, AnswerRow, AnswerSection}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResult

class FreeHoursResultViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "freeHoursResult"

  def createView = () => freeHoursResult(frontendAppConfig, "", NotEligible, Seq())(fakeRequest, messages)

  def createViewWithAnswers = (location: String,
                               eligibility:Eligibility,
                               answerSections: Seq[Section]) => freeHoursResult(frontendAppConfig,
                                                                                location,
                                                                                eligibility,
                                                                                answerSections)(fakeRequest, messages)

  "FreeHoursResult view" must {

    behave like normalPage(createView,
      messageKeyPrefix,
      "notEligibleInfo",
      "info.esc",
      "info.tfc",
      "info.tc",
      "notEligible.heading",
      "toBeEligible.heading",
      "summary.heading",
      "summary.info")

    behave like pageWithBackLink(createView)

  }

  "FreeHoursResult view" when {
    "rendered" must {
      "contain correct guidance when not eligible for location other than northern-ireland" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.england",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers("england", NotEligible, answerSections))

        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.end"))

        val childAged3Link: Element = doc.getElementById("free-hours-results-child-aged-3-link")
        childAged3Link.attr("href") mustBe routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url
        childAged3Link.text mustBe messagesApi("freeHoursResult.toBeEligible.info1.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.end"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(CheckMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.info2.link.text")

      }

      "contain correct guidance when not eligible for location northern-ireland" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.northern-ireland",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers("northernIreland", NotEligible, answerSections))

        assertContainsText(doc, messagesApi("freeHoursResult.notEligible.info.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.end"))

        val childAged3Link: Element = doc.getElementById("free-hours-results-child-aged-3-link")
        childAged3Link.attr("href") mustBe routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url
        childAged3Link.text mustBe messagesApi("freeHoursResult.toBeEligible.info1.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.end"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(CheckMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.info2.link.text")
      }

      "eligible for 16 free hours for scotland and not eligible for other schemes" in {

        val doc = asDocument(createViewWithAnswers("scotland", Eligible, Seq()))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.scotland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.info1"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {

        val doc = asDocument(createViewWithAnswers("wales", Eligible, Seq()))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.wales"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.info1"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {

        val doc = asDocument(createViewWithAnswers("northern-ireland", Eligible, Seq()))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.info1"))
      }

     "display all the answer rows with correct contents " in {
       val answerSections = Seq(AnswerSection(None, Seq(
         AnswerRow("childAgedTwo.checkYourAnswersLabel",
           "site.no",
           true,
           routes.ChildAgedTwoController.onPageLoad(CheckMode).url),
         AnswerRow("childAgedThreeOrFour.checkYourAnswersLabel",
           "site.no",
           true,
           routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url),
         AnswerRow("expectChildcareCosts.checkYourAnswersLabel",
           "expectChildcareCosts.yes",
           true,
           routes.ChildcareCostsController.onPageLoad(CheckMode).url)
       )))

       val doc = asDocument(createViewWithAnswers("england", NotEligible, answerSections))

       assertContainsText(doc, messagesApi("childAgedTwo.checkYourAnswersLabel"))
       assertContainsText(doc, messagesApi("site.no"))
       assertContainsText(doc, messagesApi(messages("site.edit")))
       assertContainsText(doc, routes.ChildAgedTwoController.onPageLoad(CheckMode).url)

       assertContainsText(doc, messagesApi("childAgedThreeOrFour.checkYourAnswersLabel"))
       assertContainsText(doc, routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url)

       assertContainsText(doc, messagesApi("expectChildcareCosts.checkYourAnswersLabel"))
       assertContainsText(doc, routes.ChildcareCostsController.onPageLoad(CheckMode).url)
     }

    }

  }

}
