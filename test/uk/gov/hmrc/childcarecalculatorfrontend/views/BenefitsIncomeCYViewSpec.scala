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
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BenefitsIncomeCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewQuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefitsIncomeCY

class BenefitsIncomeCYViewSpec extends NewQuestionViewBehaviours[BenefitsIncomeCY] {

  val messageKeyPrefix       = "benefitsIncomeCY"
  val view: benefitsIncomeCY = inject[benefitsIncomeCY]

  override val form: Form[BenefitsIncomeCY] = BenefitsIncomeCYForm()

  def render(form: Form[BenefitsIncomeCY] = this.form): Html = view(form)(using fakeRequest, messages)

  "BenefitsIncomeCY view" must {

    behave.like(normalPage(() => render(), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render()))

    behave.like(
      pageWithTextFields(
        form => render(form = form),
        messageKeyPrefix,
        routes.BenefitsIncomeCYController.onSubmit().url,
        "parentBenefitsIncome",
        "partnerBenefitsIncome"
      )
    )

    "contain the currencySymbol class and £ for parent and partner input text boxes" in {
      val doc = asDocument(render())

      assertRenderedByCssSelector(doc, ".govuk-input__prefix")

      val parentCurrencySymbol  = doc.getElementById("parentBenefitsIncome").firstElementSibling().text()
      val partnerCurrencySymbol = doc.getElementById("partnerBenefitsIncome").firstElementSibling().text()

      parentCurrencySymbol mustBe "£"
      partnerCurrencySymbol mustBe "£"
    }
  }

}
