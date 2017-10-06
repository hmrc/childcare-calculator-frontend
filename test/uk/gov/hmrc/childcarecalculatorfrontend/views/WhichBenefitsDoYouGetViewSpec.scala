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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsDoYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsDoYouGet

class WhichBenefitsDoYouGetViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichBenefitsDoYouGet"

  val answer = Some(Set("option1", "option2"))

  def createView = () => whichBenefitsDoYouGet(frontendAppConfig, answer, WhichBenefitsDoYouGetForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Set[String]]) => whichBenefitsDoYouGet(frontendAppConfig, answer, form, NormalMode)(fakeRequest, messages)

  "WhichBenefitsDoYouGet view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "WhichBenefitsDoYouGet view" when {
    "rendered" must {
//      "contain radio buttons for the value" in {
//        val doc = asDocument(createViewUsingForm(WhichBenefitsDoYouGetForm()))
//        for (option <- WhichBenefitsDoYouGetForm.options) {
//          assertContainsRadioButton(doc, option.id, "value", option.value, false)
//        }
//      }
    }

    for(option <- WhichBenefitsDoYouGetForm.options) {
//      s"rendered with a value of '${option.value}'" must {
//        s"have the '${option.value}' radio button selected" in {
//          val doc = asDocument(createViewUsingForm(WhichBenefitsDoYouGetForm().bind(Map("value" -> s"${option.value}"))))
//          assertContainsRadioButton(doc, option.id, "value", option.value, true)
//
//          for(unselectedOption <- WhichBenefitsDoYouGetForm.options.filterNot(o => o == option)) {
//            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
//          }
//        }
//      }
    }
  }
}
