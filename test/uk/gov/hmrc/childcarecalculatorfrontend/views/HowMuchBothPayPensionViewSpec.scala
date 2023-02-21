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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowMuchBothPayPensionForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPension, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewQuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howMuchBothPayPension

class HowMuchBothPayPensionViewSpec extends NewQuestionViewBehaviours[HowMuchBothPayPension] {

  val view = application.injector.instanceOf[howMuchBothPayPension]
  val messageKeyPrefix = "howMuchBothPayPension"

  def createView = () => view(frontendAppConfig, HowMuchBothPayPensionForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[HowMuchBothPayPension]) => view(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = HowMuchBothPayPensionForm()

  "HowMuchBothPayPension view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm,
      messageKeyPrefix,
      routes.HowMuchBothPayPensionController.onSubmit(NormalMode).url,
      "howMuchYouPayPension", "howMuchPartnerPayPension")

    "contain the currencySymbol class and £ for parent and partner input text boxes" in {
      val doc = asDocument(createView())

      assertRenderedByCssSelector(doc, ".govuk-input__prefix")

      val parentCurrencySymbol = doc.getElementById("howMuchYouPayPension").firstElementSibling().text()
      val partnerCurrencySymbol = doc.getElementById("howMuchPartnerPayPension").firstElementSibling().text()

      parentCurrencySymbol mustBe "£"
      partnerCurrencySymbol mustBe "£"
    }
  }

}
