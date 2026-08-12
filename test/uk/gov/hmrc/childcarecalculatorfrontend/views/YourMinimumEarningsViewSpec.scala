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

import org.scalatest.BeforeAndAfterEach
import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings

class YourMinimumEarningsViewSpec extends NewYesNoViewBehaviours with BeforeAndAfterEach {

  val view: yourMinimumEarnings      = inject[yourMinimumEarnings]
  val messageKeyPrefix               = "yourMinimumEarnings"
  val averageWeeklyEarningsKeyPrefix = "yourMinimumEarnings.averageWeekly"
  override val form: Form[Boolean]   = BooleanForm()
  val bereavedPartnersPaternityLeave = "bereaved partner’s paternity leave"

  def render(
      form: Form[Boolean] = this.form,
      amount: BigDecimal = 0,
      location: Location = Location.England
  ): Html = view(form, amount, location)(using fakeRequest, messages)

  "YourMinimumEarnings view" must {

    behave.like(
      normalPageWithTitleAsString(
        view = () => render(),
        messageKeyPrefix = averageWeeklyEarningsKeyPrefix,
        messageKeyPostfix = "",
        title = messages("yourMinimumEarnings.averageWeekly.title", 0),
        heading = Some(""),
        expectedGuidanceKeys = Seq(),
        args = 0
      )
    )
    behave.like(pageWithBackLink(() => render()))

    behave.like(
      yesNoPage(
        (form: Form[Boolean]) => render(form = form),
        messageKeyPrefix,
        routes.YourMinimumEarningsController.onSubmit().url,
        legend = Some(messages(s"$messageKeyPrefix.heading", 0))
      )
    )

    "include bereaved partner's paternity leave on page" when {
      "the location is England" in {
        render(location = Location.England).toString must include(
          bereavedPartnersPaternityLeave
        )
      }

      "the location is Scotland" in {
        render(location = Location.Scotland).toString must include(
          bereavedPartnersPaternityLeave
        )
      }

      "the location is Wales" in {
        render(location = Location.Wales).toString must include(
          bereavedPartnersPaternityLeave
        )
      }

    }

    "NOT include bereaved partner's paternity leave on page" when {
      "the location is Northern Ireland" in
        (render(location = Location.NorthernIreland).toString must not)
          .include(bereavedPartnersPaternityLeave)

    }

    "display the correct guidance text" when {
      "the location is other than Northern Ireland" in {
        val view1 = render()
        val doc   = asDocument(view1)

        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.heading"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para1"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para2"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.heading2"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para3"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para4"))

      }
    }

    "display the correct guidance text" when {
      "the location is Northern Ireland" in {
        val view1 = render(location = Location.NorthernIreland)
        val doc   = asDocument(view1)

        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.heading"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para1"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.northern-ireland.para2"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.heading2"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para3"))
        assertContainsText(doc, messages(s"$averageWeeklyEarningsKeyPrefix.para4"))

      }
    }
  }

}
