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

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourAndPartnerAverageWeeklyEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location

class YourAndPartnerAverageWeeklyEarningsViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "yourAndPartnerAverageWeeklyEarnings"

  val applicationBpplEnabled: Application =
    new GuiceApplicationBuilder()
      .configure("feature.bpplContentEnabled" -> true)
      .build()

  val viewBpplEnabled: yourAndPartnerAverageWeeklyEarnings =
    applicationBpplEnabled.injector.instanceOf[yourAndPartnerAverageWeeklyEarnings]

  val applicationBpplDisabled: Application =
    new GuiceApplicationBuilder()
      .configure("feature.bpplContentEnabled" -> false)
      .build()

  val viewBpplDisabled: yourAndPartnerAverageWeeklyEarnings =
    applicationBpplDisabled.injector.instanceOf[yourAndPartnerAverageWeeklyEarnings]

  def constructView(
      view: yourAndPartnerAverageWeeklyEarnings,
      location: Location.Value = Location.ENGLAND
  ): HtmlFormat.Appendable = view(location)(fakeRequest, messages)

  "YourAndPartnerAverageWeeklyEarnings view" must {
    behave.like(
      normalPageWithTitleAsString(
        view = () => constructView(viewBpplEnabled),
        messageKeyPrefix = messageKeyPrefix,
        messageKeyPostfix = "",
        title = messages("yourAndPartnerAverageWeeklyEarnings.heading", 0),
        heading = Some(""),
        expectedGuidanceKeys = Seq(),
        args = 0
      )
    )

    behave.like(pageWithBackLink(() => constructView(viewBpplEnabled)))

    "display the correct guidance text " in {
      val view1 = constructView(viewBpplEnabled)
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
      val partnerAverageWeeklyEarningsView = constructView(viewBpplEnabled)
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

    "display the correct bullet list when flag bpplContentEnabled is false" in {
      val partnerAverageWeeklyEarningsView = constructView(viewBpplDisabled)
      val doc                              = asDocument(partnerAverageWeeklyEarningsView)
      val bulletItemsSelector              = "ul.govuk-list--bullet li"

      val expected = Seq(
        "yourAndPartnerAverageWeeklyEarnings.li.adoption",
        "yourAndPartnerAverageWeeklyEarnings.li.maternity",
        "yourAndPartnerAverageWeeklyEarnings.li.neonatalCare",
        "yourAndPartnerAverageWeeklyEarnings.li.paternity",
        "yourAndPartnerAverageWeeklyEarnings.li.sickLeave"
      )

      assertBulletListValues(doc, expected, bulletItemsSelector)
    }

    "display the correct bullet list when location is Northern Ireland" in {
      val NIPartnerWeeklyEarningsView = constructView(viewBpplEnabled, Location.NORTHERN_IRELAND)
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
      val doc          = asDocument(constructView(viewBpplEnabled))
      val continueLink = doc.getElementsByClass("govuk-button")

      assertContainsText(doc, messages("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.YourMinimumEarningsController.onPageLoad().url

    }
  }

}
