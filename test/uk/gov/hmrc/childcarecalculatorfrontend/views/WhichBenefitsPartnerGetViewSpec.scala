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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsPartnerGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsPartnerGet

class WhichBenefitsPartnerGetViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichBenefitsPartnerGet"

  val answer = Some(Set("options1", "option2"))

  def createView = () => whichBenefitsPartnerGet(frontendAppConfig, answer, WhichBenefitsPartnerGetForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Set[String]]) => whichBenefitsPartnerGet(frontendAppConfig, answer, form, NormalMode)(fakeRequest, messages)

  "WhichBenefitsPartnerGet view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

//  "WhichBenefitsPartnerGet view" when {
//    "rendered" must {
//      "contain check boxes for the value" in {
//        val doc = asDocument(createViewUsingForm(WhichBenefitsPartnerGetForm()))
//        for (option <- WhichBenefitsPartnerGetForm.options) {
//          assertContainsRadioButton(doc, option.id, "value", option.value, false)
//        }
//      }
//    }

//    for(option <- WhichBenefitsPartnerGetForm.options) {
//      s"rendered with a value of '${option.value}'" must {
//        s"have the '${option.value}' radio button selected" in {
//          val doc = asDocument(createViewUsingForm(WhichBenefitsPartnerGetForm().bind(Map("value" -> s"${option.value}"))))
//          assertContainsRadioButton(doc, option.id, "value", option.value, true)
//
//          for(unselectedOption <- WhichBenefitsPartnerGetForm.options.filterNot(o => o == option)) {
//            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
//          }
//        }
//      }
//    }
//  }
}
