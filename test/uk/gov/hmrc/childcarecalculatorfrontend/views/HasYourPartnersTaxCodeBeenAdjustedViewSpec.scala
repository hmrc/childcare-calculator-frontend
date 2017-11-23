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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HasYourPartnersTaxCodeBeenAdjustedForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.hasYourPartnersTaxCodeBeenAdjusted

class HasYourPartnersTaxCodeBeenAdjustedViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "hasYourPartnersTaxCodeBeenAdjusted"

  def createView = () => hasYourPartnersTaxCodeBeenAdjusted(frontendAppConfig, HasYourPartnersTaxCodeBeenAdjustedForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => hasYourPartnersTaxCodeBeenAdjusted(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "HasYourPartnersTaxCodeBeenAdjusted view" must {
    behave like normalPage(createView, messageKeyPrefix, "info")

    behave like pageWithBackLink(createView)
  }

  "HasYourPartnersTaxCodeBeenAdjusted view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(HasYourPartnersTaxCodeBeenAdjustedForm()))
        for (option <- HasYourPartnersTaxCodeBeenAdjustedForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- HasYourPartnersTaxCodeBeenAdjustedForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(HasYourPartnersTaxCodeBeenAdjustedForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- HasYourPartnersTaxCodeBeenAdjustedForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
