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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryPayBeforeTaxForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryPayBeforeTax

class YourStatutoryPayBeforeTaxViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "yourStatutoryPayBeforeTax"

  val statutoryType = "maternity"

  def createView = () => yourStatutoryPayBeforeTax(frontendAppConfig, YourStatutoryPayBeforeTaxForm(), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => yourStatutoryPayBeforeTax(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  "YourStatutoryPayBeforeTax view" must {
    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.title", statutoryType))
    )
    behave like pageWithBackLink(createView)
  }

  "YourStatutoryPayBeforeTax view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(YourStatutoryPayBeforeTaxForm()))
        for (option <- YourStatutoryPayBeforeTaxForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- YourStatutoryPayBeforeTaxForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(YourStatutoryPayBeforeTaxForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- YourStatutoryPayBeforeTaxForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
