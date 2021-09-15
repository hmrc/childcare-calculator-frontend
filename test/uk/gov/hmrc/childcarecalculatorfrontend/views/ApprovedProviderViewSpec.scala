/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ApprovedProviderForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.approvedProvider

class ApprovedProviderViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "approvedProvider"
  val view = application.injector.instanceOf[approvedProvider]

  def createView = () => view(frontendAppConfig, ApprovedProviderForm(), false, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => view(frontendAppConfig, form, false, NormalMode)(fakeRequest, messages)

  "ApprovedProvider view" must {
    behave like normalPage(createView, messageKeyPrefix, "hint")

    behave like pageWithBackLink(createView)
  }

  "ApprovedProvider view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(ApprovedProviderForm()))
        for (option <- ApprovedProviderForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "contain right title" when {
        "we have selected 'Not Yet but maybe in the furure'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), true, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())

          assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.title.future")+" - "+messages("site.service_name")+" - GOV.UK")
        }

        "we have selected 'Yes'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), false, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())

          assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.title")+" - "+messages("site.service_name")+" - GOV.UK")
        }
      }

      "contain right heading" when {
        "we have selected 'Not Yet but maybe in the future'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), true, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())

          assertEqualsValue(doc, "h1", messages(s"$messageKeyPrefix.heading.future"))
        }

        "we have selected 'Yes'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), false, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())

          assertEqualsValue(doc, "h1", messages(s"$messageKeyPrefix.heading"))
        }
      }

      "contain right legend" when {
        "we have selected 'Not Yet but maybe in the future'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), true, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())
          val text = messages(s"$messageKeyPrefix.heading.future")
          assertEqualsValue(doc, "legend", s"<h1 class=${'"'}govuk-heading-xl govuk-!-margin-top-0 govuk-!-margin-bottom-5${'"'}>$text</h1>")
        }

        "we have selected 'Yes'" in {
          val createView = () => view(frontendAppConfig, ApprovedProviderForm(), false, NormalMode)(fakeRequest, messages)
          val doc = asDocument(createView())
          val text = messages(s"$messageKeyPrefix.heading")
          assertEqualsValue(doc, "legend", s"<h1 class=${'"'}govuk-heading-xl govuk-!-margin-top-0 govuk-!-margin-bottom-5${'"'}>$text</h1>")
        }
      }
    }

    for(option <- ApprovedProviderForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(ApprovedProviderForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- ApprovedProviderForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
