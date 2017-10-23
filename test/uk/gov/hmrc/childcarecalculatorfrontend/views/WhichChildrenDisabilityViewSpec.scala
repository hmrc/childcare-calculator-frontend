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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenDisabilityForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenDisability

class WhichChildrenDisabilityViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichChildrenDisability"

  val answer = Some(Set("options1", "option2"))

  def createView = () => whichChildrenDisability(frontendAppConfig, answer, WhichChildrenDisabilityForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Set[String]]) => whichChildrenDisability(frontendAppConfig, answer, form, NormalMode)(fakeRequest, messages)

  "WhichChildrenDisability view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

//  "WhichChildrenDisability view" when {
//    "rendered" must {
//      "contain check box for the value" in {
//        val doc = asDocument(createViewUsingForm(WhichChildrenDisabilityForm()))
//        for (option <- WhichChildrenDisabilityForm.options) {
//          assertContainsRadioButton(doc, option.id, "value", option.value, false)
//        }
//      }
//    }
//
//    for(option <- WhichChildrenDisabilityForm.options) {
//      s"rendered with a value of '${option.value}'" must {
//        s"have the '${option.value}' check box selected" in {
//          val doc = asDocument(createViewUsingForm(WhichChildrenDisabilityForm().bind(Map("value" -> s"${option.value}"))))
//          assertContainsRadioButton(doc, option.id, "value", option.value, true)
//
//          for(unselectedOption <- WhichChildrenDisabilityForm.options.filterNot(o => o == option)) {
//            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
//          }
//        }
//      }
//    }
//  }
}
