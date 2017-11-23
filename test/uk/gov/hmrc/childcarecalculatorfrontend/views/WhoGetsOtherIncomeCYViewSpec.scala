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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsOtherIncomeCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsOtherIncomeCY

class WhoGetsOtherIncomeCYViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whoGetsOtherIncomeCY"

  def createView = () => whoGetsOtherIncomeCY(frontendAppConfig, WhoGetsOtherIncomeCYForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => whoGetsOtherIncomeCY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhoGetsOtherIncomeCY view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "WhoGetsOtherIncomeCY view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(WhoGetsOtherIncomeCYForm()))
        for (option <- WhoGetsOtherIncomeCYForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- WhoGetsOtherIncomeCYForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(WhoGetsOtherIncomeCYForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- WhoGetsOtherIncomeCYForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
