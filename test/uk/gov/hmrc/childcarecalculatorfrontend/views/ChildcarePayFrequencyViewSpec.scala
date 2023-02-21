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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{BooleanForm, ChildcarePayFrequencyForm}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildcarePayFrequency, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency

class ChildcarePayFrequencyViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "childcarePayFrequency"
  val view = application.injector.instanceOf[childcarePayFrequency]

  def createView = () =>
    view(frontendAppConfig, ChildcarePayFrequencyForm("Foo"), 0, "Foo", NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[ChildcarePayFrequency.Value]) =>
    view(frontendAppConfig, form, 0, "Foo", NormalMode)(fakeRequest, messages)

  val cardinal = messages("nth.0")

  "ChildcarePayFrequency view" must {

    behave like normalPageWithTitleParameters(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      expectedGuidanceKeys = Seq(),
      args = Seq("Foo"),
      titleArgs = Seq(cardinal)
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

      "have hidden legend text with child name" in {
        val doc = asDocument(createViewUsingForm(ChildcarePayFrequencyForm("Foo")))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe messages(s"$messageKeyPrefix.heading", "Foo")
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
