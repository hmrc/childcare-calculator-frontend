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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.areYouInPaidWork

class AreYouInPaidWorkViewSpec extends NewYesNoViewBehaviours with BeforeAndAfterEach {

  override val form: Form[Boolean]   = BooleanForm()
  val messageKeyPrefix               = "areYouInPaidWork"
  val view: areYouInPaidWork         = inject[areYouInPaidWork]
  val bereavedPartnersPaternityLeave = "bereaved partner&#x27;s paternity leave"

  def render(
      form: Form[Boolean] = this.form,
      location: Location = Location.England
  ): Html = view(form, location)(using fakeRequest, messages)

  "AreYouInPaidWork view" must {

    behave.like(normalPage(() => render(), messageKeyPrefix, "heading", "para1"))

    behave.like(pageWithBackLink(() => render()))

    behave.like(
      yesNoPage(
        (form: Form[Boolean]) => render(form = form),
        messageKeyPrefix,
        routes.AreYouInPaidWorkController.onSubmit().url
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
  }

}
