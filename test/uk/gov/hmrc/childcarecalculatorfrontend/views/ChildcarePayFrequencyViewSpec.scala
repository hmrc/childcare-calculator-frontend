/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildcarePayFrequencyForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildcarePayFrequency, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency

class ChildcarePayFrequencyViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "childcarePayFrequency"

  def createView = () =>
    childcarePayFrequency(frontendAppConfig, ChildcarePayFrequencyForm("Foo"), 0, "Foo", NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ChildcarePayFrequency.Value]) =>
    childcarePayFrequency(frontendAppConfig, form, 0, "Foo", NormalMode)(fakeRequest, messages)

  "ChildcarePayFrequency view" must {

    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      messages(s"$messageKeyPrefix.title"),
      Some(messages(s"$messageKeyPrefix.heading", "Foo"))
    )

    behave like pageWithBackLink(createView)
  }

  "ChildcarePayFrequency view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(ChildcarePayFrequencyForm("Foo")))
        for (option <- ChildcarePayFrequencyForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- ChildcarePayFrequencyForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(ChildcarePayFrequencyForm("Foo").bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- ChildcarePayFrequencyForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
