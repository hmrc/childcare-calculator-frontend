/*
 * Copyright 2025 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerAverageWeeklyEarnings

class PartnerAverageWeeklyEarningsViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "partnerAverageWeeklyEarnings"
  val view             = application.injector.instanceOf[partnerAverageWeeklyEarnings]

  def createView = () => view()(fakeRequest, messages)

  "PartnerAverageWeeklyEarnings view" must {
    behave.like(
      normalPageWithTitleAsString(
        view = createView,
        messageKeyPrefix = messageKeyPrefix,
        messageKeyPostfix = "",
        title = messages("partnerAverageWeeklyEarnings.heading", 0),
        heading = Some(""),
        expectedGuidanceKeys = Seq(),
        args = 0
      )
    )

    behave.like(pageWithBackLink(createView))

    "display the correct guidance text " in {
      val view1 = view()(fakeRequest, messages)
      val doc   = asDocument(view1)

      assertContainsText(doc, messages(s"$messageKeyPrefix.para1"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading2"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para2"))
      assertNotRenderedByCssSelector(doc, "bullets")
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading3"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para3"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para4"))

    }
    "contain the link for Your minimum earning" in {
      val doc          = asDocument(createView())
      val continueLink = doc.getElementsByClass("govuk-button")

      assertContainsText(doc, messages("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.PartnerMinimumEarningsController.onPageLoad().url

    }
  }

}
