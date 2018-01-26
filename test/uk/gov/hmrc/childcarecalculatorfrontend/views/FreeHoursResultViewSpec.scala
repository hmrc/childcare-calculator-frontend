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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.{AnswerRow, AnswerSection, Section}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResult

class FreeHoursResultViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "freeHoursResult"

  private def createView = () => freeHoursResult(frontendAppConfig, Location.ENGLAND, NotEligible,
    paidEmployment = false,
    livingWithPartner = false,
    approvedProvider = false)(fakeRequest, messages)

  private def createViewWithAnswers = (
                                        location: Location.Value,
                                        eligibility: Eligibility,
                                        paidEmployment: Boolean,
                                        livingWithPartner: Boolean,
                                        approvedProvider: Boolean) => freeHoursResult(frontendAppConfig,
    location,
    eligibility,
    paidEmployment,
    livingWithPartner,
    approvedProvider)(fakeRequest, messages)

  "FreeHoursResult view" must {
    behave like normalPage(createView,
      messageKeyPrefix,
      "toBeEligible.heading")

    behave like pageWithBackLink(createView)
  }

  "FreeHoursResult view" when {
    "rendered" must {

      "contain correct guidance when not eligible because you don't have a child under 5 and you're not with an approved provider" in {

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, NotEligible, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.freHours"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes"))
        assertContainsText(doc, messagesApi("freeHoursResult.info.OtherSchemes.text"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2b.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2b.end"))

        val approvedProvierLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        approvedProvierLink.attr("href") mustBe routes.ApprovedProviderController.onPageLoad(NormalMode).url
        approvedProvierLink.text mustBe messagesApi("freeHoursResult.toBeEligible.info2b.link.text")
      }

      "contain correct guidance when not eligible for location other than northern-ireland" in {

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, NotEligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.end"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.info2.link.text")
      }

      "contain correct guidance when not eligible for location northern-ireland" in {

        val doc = asDocument(createViewWithAnswers(Location.NORTHERN_IRELAND, NotEligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info1.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.start"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.info2.end"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.info2.link.text")
      }

      "eligible for 16 free hours for scotland and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.SCOTLAND, Eligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.scotland"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.WALES, Eligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.wales"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.NORTHERN_IRELAND, Eligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.northern-ireland"))
      }

      "eligible for 15 free hours for England and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, Eligible, true, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.england"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.england"))
      }
    }
  }

}
