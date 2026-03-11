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

import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings

class YourMinimumEarningsViewSpec extends NewYesNoViewBehaviours with BeforeAndAfterEach {

  val view                           = application.injector.instanceOf[yourMinimumEarnings]
  val messageKeyPrefix               = "yourMinimumEarnings"
  val averageWeeklyEarningsKeyPrefix = "yourMinimumEarnings.averageWeekly"
  override val form: Form[Boolean]   = BooleanForm()
  val bereavedPartnersPaternityLeave = "bereaved partner’s paternity leave"

  val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

  def constructView(
      form: Form[Boolean] = this.form,
      amount: BigDecimal = 0,
      location: Location.Value = Location.ENGLAND
  ): HtmlFormat.Appendable = view(appConfig, form, amount, location)(fakeRequest, messages)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(appConfig)
  }

  "YourMinimumEarnings view" when {
    "the bpplContentEnabled flag is set to false " must {

      behave.like(
        normalPageWithTitleAsString(
          view = () => {
            when(appConfig.bpplContentEnabled).thenReturn(false)
            constructView()
          },
          messageKeyPrefix = messageKeyPrefix,
          messageKeyPostfix = "",
          title = messages("yourMinimumEarnings.title", 0),
          heading = Some(""),
          expectedGuidanceKeys = Seq(),
          args = 0
        )
      )
      behave.like(pageWithBackLink { () =>
        when(appConfig.bpplContentEnabled).thenReturn(false)
        constructView()
      })

      behave.like(
        yesNoPage(
          (form: Form[Boolean]) => {
            when(appConfig.bpplContentEnabled).thenReturn(false)
            constructView(form = form)
          },
          messageKeyPrefix,
          routes.YourMinimumEarningsController.onSubmit().url,
          legend = Some(messages(s"$messageKeyPrefix.heading", 0))
        )
      )

      "show correct guidance and value of minimum earnings" in {
        val amount = BigDecimal(40)

        when(appConfig.bpplContentEnabled).thenReturn(false)
        val doc = asDocument(constructView(amount = amount))
        assertContainsText(doc, messages(s"$messageKeyPrefix.heading", amount))
      }
    }

    "the bpplContentEnabled flag is set to true" must {

      behave.like(
        normalPageWithTitleAsString(
          view = () => {
            when(appConfig.bpplContentEnabled).thenReturn(true)
            constructView()
          },
          messageKeyPrefix = averageWeeklyEarningsKeyPrefix,
          messageKeyPostfix = "",
          title = messages("yourMinimumEarnings.averageWeekly.title", 0),
          heading = Some(""),
          expectedGuidanceKeys = Seq(),
          args = 0
        )
      )
      behave.like(pageWithBackLink { () =>
        when(appConfig.bpplContentEnabled).thenReturn(true)
        constructView()
      })

      behave.like(
        yesNoPage(
          (form: Form[Boolean]) => {
            when(appConfig.bpplContentEnabled).thenReturn(true)
            constructView(form = form)
          },
          messageKeyPrefix,
          routes.YourMinimumEarningsController.onSubmit().url,
          legend = Some(messages(s"$messageKeyPrefix.heading", 0))
        )
      )

      "include bereaved partner's paternity leave on page" when {
        "the location is England" in {
          when(appConfig.bpplContentEnabled).thenReturn(true)
          constructView(location = Location.ENGLAND).toString must include(
            bereavedPartnersPaternityLeave
          )
        }

        "the location is Scotland" in {
          when(appConfig.bpplContentEnabled).thenReturn(true)
          constructView(location = Location.SCOTLAND).toString must include(
            bereavedPartnersPaternityLeave
          )
        }

        "the location is Wales" in {
          when(appConfig.bpplContentEnabled).thenReturn(true)
          constructView(location = Location.WALES).toString must include(
            bereavedPartnersPaternityLeave
          )
        }

      }

      "NOT include bereaved partner's paternity leave on page" when {
        "the location is Northern Ireland" in {
          when(appConfig.bpplContentEnabled).thenReturn(true)
          (constructView(location = Location.NORTHERN_IRELAND).toString must not)
            .include(bereavedPartnersPaternityLeave)
        }

      }

      "display the correct guidance text" when {
        "the location is other than Northern Ireland" in {
          when(appConfig.bpplContentEnabled).thenReturn(true)
          val view1 = constructView()
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
          when(appConfig.bpplContentEnabled).thenReturn(true)
          val view1 = constructView(location = Location.NORTHERN_IRELAND)
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

}
