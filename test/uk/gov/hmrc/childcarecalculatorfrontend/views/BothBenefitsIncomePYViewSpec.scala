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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BothBenefitsIncomePYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, BothBenefitsIncomePY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothBenefitsIncomePY

class BothBenefitsIncomePYViewSpec extends QuestionViewBehaviours[BothBenefitsIncomePY] {

  val messageKeyPrefix = "bothBenefitsIncomePY"
  val view = application.injector.instanceOf[bothBenefitsIncomePY]

  def createView = () => view(frontendAppConfig, BothBenefitsIncomePYForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BothBenefitsIncomePY]) => view(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = BothBenefitsIncomePYForm()

  "BothBenefitsIncomePY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm,
      messageKeyPrefix,
      routes.BothBenefitsIncomePYController.onSubmit(NormalMode).url,
      "parentBenefitsIncomePY", "partnerBenefitsIncomePY")
  }

  "contain the currencySymbol class and £ " in {
    val doc = asDocument(createView())

    assertRenderedByCssSelector(doc, ".currencySymbol")

    val parentCurrencySymbol = doc.getElementById("parentBenefitsIncomePY").firstElementSibling().text()
    val partnerCurrencySymbol = doc.getElementById("partnerBenefitsIncomePY").firstElementSibling().text()

    parentCurrencySymbol mustBe "£"
    partnerCurrencySymbol mustBe "£"

  }
}
