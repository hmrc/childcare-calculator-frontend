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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourAndPartnerAverageWeeklyEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location

class YourAndPartnerAverageWeeklyEarningsViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "yourAndPartnerAverageWeeklyEarnings"
  val view             = application.injector.instanceOf[yourAndPartnerAverageWeeklyEarnings]
  val location         = Location.ENGLAND

  def createView = () => view(location)(fakeRequest, messages)

  "YourAndPartnerAverageWeeklyEarnings view" must {
    behave.like(
      normalPageWithTitleAsString(
        view = createView,
        messageKeyPrefix = messageKeyPrefix,
        messageKeyPostfix = "",
        title = messages("yourAndPartnerAverageWeeklyEarnings.heading", 0),
        heading = Some(""),
        expectedGuidanceKeys = Seq(),
        args = 0
      )
    )

    behave.like(pageWithBackLink(createView))

    "display the correct guidance text " in {
      val view1 = view(location)(fakeRequest, messages)
      val doc   = asDocument(view1)

      assertContainsText(doc, messages(s"$messageKeyPrefix.para1"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading2"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para2"))
      assertNotRenderedByCssSelector(doc, "bullets")
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading3"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para3"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para4"))

    }

    "display the correct bullet list" in {
      val partnerAverageWeeklyEarningsView = view(location)(fakeRequest, messages)
      val doc                              = asDocument(partnerAverageWeeklyEarningsView)
      val bulletItemsSelector              = "ul.govuk-list--bullet li"

      val expected = Seq(
        "yourAndPartnerAverageWeeklyEarnings.li.adoption",
        "yourAndPartnerAverageWeeklyEarnings.li.bereavedPartnersPaternity",
        "yourAndPartnerAverageWeeklyEarnings.li.maternity",
        "yourAndPartnerAverageWeeklyEarnings.li.neonatalCare",
        "yourAndPartnerAverageWeeklyEarnings.li.paternity",
        "yourAndPartnerAverageWeeklyEarnings.li.sickLeave"
      )

      assertBulletListValues(doc, expected, bulletItemsSelector)
    }

    "display the correct bullet list when location is Northern Ireland" in {
      val NIPartnerWeeklyEarningsView = view(Location.NORTHERN_IRELAND)(fakeRequest, messages)
      val doc                         = asDocument(NIPartnerWeeklyEarningsView)
      val bulletItemsSelector         = "ul.govuk-list--bullet li"

      val expected = Seq(
        "yourAndPartnerAverageWeeklyEarnings.li.adoption",
        "yourAndPartnerAverageWeeklyEarnings.li.maternity",
        "yourAndPartnerAverageWeeklyEarnings.li.neonatalCare",
        "yourAndPartnerAverageWeeklyEarnings.li.paternity",
        "yourAndPartnerAverageWeeklyEarnings.li.sickLeave"
      )

      assertBulletListValues(doc, expected, bulletItemsSelector)
    }

    "contain the link for Your minimum earning" in {
      val doc          = asDocument(createView())
      val continueLink = doc.getElementsByClass("govuk-button")

      assertContainsText(doc, messages("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.YourMinimumEarningsController.onPageLoad().url

    }
  }

}
