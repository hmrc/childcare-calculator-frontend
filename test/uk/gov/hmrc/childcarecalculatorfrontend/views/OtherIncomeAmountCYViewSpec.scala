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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.OtherIncomeAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, OtherIncomeAmountCY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewQuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.otherIncomeAmountCY

class OtherIncomeAmountCYViewSpec extends NewQuestionViewBehaviours[OtherIncomeAmountCY] {

  val view             = application.injector.instanceOf[otherIncomeAmountCY]
  val messageKeyPrefix = "otherIncomeAmountCY"

  override val form = new OtherIncomeAmountCYForm(frontendAppConfig).apply()

  def createView = () => view(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[OtherIncomeAmountCY]) =>
    view(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "OtherIncomeAmountCY view" must {

    behave.like(normalPage(createView, messageKeyPrefix))

    behave.like(pageWithBackLink(createView))

    behave.like(
      pageWithTextFields(
        createViewUsingForm,
        messageKeyPrefix,
        routes.OtherIncomeAmountCYController.onSubmit(NormalMode).url,
        "parentOtherIncome",
        "partnerOtherIncome"
      )
    )

    "contain the currencySymbol class and £ " in {
      val doc = asDocument(createView())

      assertRenderedByCssSelector(doc, ".govuk-input__prefix")

      val parentCurrencySymbol  = doc.getElementById("parentOtherIncome").firstElementSibling().text()
      val partnerCurrencySymbol = doc.getElementById("partnerOtherIncome").firstElementSibling().text()

      parentCurrencySymbol mustBe "£"
      partnerCurrencySymbol mustBe "£"

    }
  }

}
