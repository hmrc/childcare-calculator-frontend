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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsVouchersForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsVouchers

class WhoGetsVouchersViewSpec extends NewViewBehaviours {

  val view             = application.injector.instanceOf[whoGetsVouchers]
  val messageKeyPrefix = "whoGetsVouchers"

  def createView = () => view(frontendAppConfig, WhoGetsVouchersForm())(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => view(frontendAppConfig, form)(fakeRequest, messages)

  "WhoGetsVouchers view" must {
    behave.like(normalPage(createView, messageKeyPrefix))

    behave.like(pageWithBackLink(createView))
  }

  "WhoGetsVouchers view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(WhoGetsVouchersForm()))
        for (option <- WhoGetsVouchersForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }
    }

    for (option <- WhoGetsVouchersForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(WhoGetsVouchersForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- WhoGetsVouchersForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }

}
