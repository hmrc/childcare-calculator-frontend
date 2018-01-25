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

  def createView = () => freeHoursResult(frontendAppConfig, Location.ENGLAND, NotEligible, Seq(),false, true, false, false)(fakeRequest, messages)

  def createViewWithAnswers = (location: Location.Value,
                               eligibility:Eligibility,
                               answerSections: Seq[Section], inPaidWork:Boolean, childcareCost: Boolean, livingWithPartner: Boolean, approvedProvider: Boolean) => freeHoursResult(frontendAppConfig,
                                                                                location,
                                                                                eligibility,
                                                                                answerSections,inPaidWork, childcareCost, livingWithPartner, approvedProvider)(fakeRequest, messages)

  "FreeHoursResult view" must {
      behave like normalPage(createView,
      messageKeyPrefix,
      "toBeEligible.heading")

    behave like pageWithBackLink(createView)
  }

  "FreeHoursResult view" when {
    "rendered" must {
      "contain correct guidance when not eligible for location other than northern-ireland when not in employment" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.england",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, Eligible, answerSections,false, true, false, true))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.start"))

        val paidWorkLink: Element = doc.getElementById("free-hours-results-paid-work-link")
        paidWorkLink.attr("href") mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode).url
        paidWorkLink.text mustBe messagesApi("freeHoursResult.toBeEligible.paid.work.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.paid.work.end"))

      }

      "contain correct guidance when not eligible for location other than northern-ireland when childcare cost not with approved provider and not in employment" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.england",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, Eligible, answerSections,false, true, false, false))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.start"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-approved-provider-link")
        childCareCostLink.attr("href") mustBe routes.ApprovedProviderController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.approved.provider.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.approved.provider.end"))

      }

      "contain correct guidance when not eligible for location other than northern-ireland when don't have childcare cost and not in employment" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.england",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, Eligible, answerSections,false, false, false, false))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))

      }

      "contain correct guidance when not eligible for location other than northern-ireland when in employment" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.england",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers(Location.ENGLAND, NotEligible, answerSections, true, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.free.hours"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))

      }

      "contain correct guidance when not eligible for location northern-ireland" in {

        val answerRow = AnswerRow("location.checkYourAnswersLabel",
          "location.northern-ireland",
          true,
          routes.LocationController.onPageLoad(CheckMode).url)

        val answerSections = Seq(AnswerSection(None, Seq(answerRow)))

        val doc = asDocument(createViewWithAnswers(Location.NORTHERN_IRELAND, NotEligible, answerSections, true, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.free.hours"))

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.heading"))
        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.start"))

        val childCareCostLink: Element = doc.getElementById("free-hours-results-childCare-cost-link")
        childCareCostLink.attr("href") mustBe routes.ChildcareCostsController.onPageLoad(NormalMode).url
        childCareCostLink.text mustBe messagesApi("freeHoursResult.toBeEligible.childcare.cost.link.text")

        assertContainsText(doc, messagesApi("freeHoursResult.toBeEligible.childcare.cost.end"))
      }

      "eligible for 16 free hours for scotland and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.SCOTLAND, Eligible, Seq(), true, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.scotland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.scotland"))
      }

      "eligible for 10 free hours for wales and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.WALES, Eligible, Seq(), true, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.wales"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.wales"))
      }

      "eligible for 12.5 free hours for northern-ireland and not eligible for other schemes" in {
        val doc = asDocument(createViewWithAnswers(Location.NORTHERN_IRELAND, Eligible, Seq(), true, false, false, false))

        assertContainsText(doc, messagesApi("freeHoursResult.info.entitled.northern-ireland"))
        assertContainsText(doc, messagesApi("freeHoursResult.partialEligible.guidance.northern-ireland"))
      }
    }
  }

}
