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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoIsInPaidEmploymentForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Location, YouPartnerBothNeither}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoIsInPaidEmployment

class WhoIsInPaidEmploymentViewSpec extends NewViewBehaviours with BeforeAndAfterEach {

  val view: whoIsInPaidEmployment    = inject[whoIsInPaidEmployment]
  val messageKeyPrefix               = "whoIsInPaidEmployment"
  val bereavedPartnersPaternityLeave = "bereaved partner&#x27;s paternity leave"

  val form: Form[YouPartnerBothNeither] = WhoIsInPaidEmploymentForm()

  def render(
      form: Form[YouPartnerBothNeither] = this.form,
      location: Location = Location.England
  ): Html = view(form, location)(using fakeRequest, messages)

  "WhoIsInPaidEmployment view" must {
    behave.like(normalPage(() => render(), messageKeyPrefix, "para1"))

    behave.like(pageWithBackLink(() => render()))

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

  "WhoIsInPaidEmployment view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(render(form = WhoIsInPaidEmploymentForm()))
        for (option <- WhoIsInPaidEmploymentForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }
    }

    for (option <- WhoIsInPaidEmploymentForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc =
            asDocument(render(form = WhoIsInPaidEmploymentForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- WhoIsInPaidEmploymentForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }

}
