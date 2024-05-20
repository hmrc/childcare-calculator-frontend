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

package uk.gov.hmrc.childcarecalculatorfrontend.views.benefits

import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.forms.DoYouOrPartnerGetBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits.doYouOrPartnerGetBenefits

class DoYouOrPartnerGetBenefitsViewSpec extends NewViewBehaviours {

  val form: Form[String] = DoYouOrPartnerGetBenefitsForm()

  val messageKeyPrefix = "doYouOrPartnerGetBenefits"
  val view: doYouOrPartnerGetBenefits = application.injector.instanceOf[doYouOrPartnerGetBenefits]

  def createView: () => HtmlFormat.Appendable = () => view(form)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable = (form: Form[String]) => view(form)(fakeRequest, messages)

  "DoYouOrPartnerGetBenefits view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "DoYouOrPartnerGetBenefits view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouOrPartnerGetBenefitsForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- DoYouOrPartnerGetBenefitsForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- DoYouOrPartnerGetBenefitsForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
      }
    }
  }
}
