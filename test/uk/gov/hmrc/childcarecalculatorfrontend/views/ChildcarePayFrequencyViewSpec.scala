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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildcarePayFrequencyForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.ChildcarePayFrequency
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency

class ChildcarePayFrequencyViewSpec extends NewViewBehaviours {

  val view: childcarePayFrequency = inject[childcarePayFrequency]

  val messageKeyPrefix = "childcarePayFrequency"
  val cardinal: String = messages("nth.0")

  val form: Form[ChildcarePayFrequency] = ChildcarePayFrequencyForm("Foo")

  def render(form: Form[ChildcarePayFrequency] = this.form, index: Int = 0, name: String = "Foo"): Html =
    view(form, index, name)(using fakeRequest, messages)

  "ChildcarePayFrequency view" must {

    behave.like(
      normalPageWithTitleParameters(
        view = () => render(),
        messageKeyPrefix = messageKeyPrefix,
        messageKeyPostfix = "",
        expectedGuidanceKeys = Seq(),
        args = Seq("Foo"),
        titleArgs = Seq(cardinal)
      )
    )

    behave.like(pageWithBackLink(() => render()))
  }

  "ChildcarePayFrequency view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(render(form = form))
        for (option <- ChildcarePayFrequencyForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }

      "have hidden legend text with child name" in {
        val doc     = asDocument(render(form = form))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe messages(s"$messageKeyPrefix.heading", "Foo")
      }
    }

    for (option <- ChildcarePayFrequencyForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc =
            asDocument(render(form = form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- ChildcarePayFrequencyForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }

}
