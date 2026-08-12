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

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AreYouSelfEmployedOrApprenticeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.EmploymentStatus
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.areYouSelfEmployedOrApprentice

class AreYouSelfEmployedOrApprenticeViewSpec extends NewViewBehaviours {

  val messageKeyPrefix                     = "areYouSelfEmployedOrApprentice"
  val view: areYouSelfEmployedOrApprentice = inject[areYouSelfEmployedOrApprentice]

  val form: Form[EmploymentStatus] = AreYouSelfEmployedOrApprenticeForm()

  def render(form: Form[EmploymentStatus] = form): Html =
    view(form)(using fakeRequest, messages)

  "AreYouSelfEmployedOrApprentice view" must {
    behave.like(normalPage(() => render(), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render()))
  }

  "AreYouSelfEmployedOrApprentice view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(render())
        for (option <- AreYouSelfEmployedOrApprenticeForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }
    }

    for (option <- AreYouSelfEmployedOrApprenticeForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(
            render(form = form.bind(Map("value" -> s"${option.value}")))
          )
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- AreYouSelfEmployedOrApprenticeForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }

}
