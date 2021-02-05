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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.EmploymentIncomePYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomePY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.employmentIncomePY

class EmploymentIncomePYViewSpec extends QuestionViewBehaviours[EmploymentIncomePY] {

  val taxYearInfo = new TaxYearInfo

  override val form = new EmploymentIncomePYForm(frontendAppConfig).apply()
  val messageKeyPrefix = "employmentIncomePY"

  def createView = () => employmentIncomePY(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[EmploymentIncomePY]) => employmentIncomePY(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "EmploymentIncomePY view" must {

    behave like normalPage(createView, messageKeyPrefix, "hint")

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm,
      messageKeyPrefix,
      routes.EmploymentIncomePYController.onSubmit(NormalMode).url,
      "parentEmploymentIncomePY", "partnerEmploymentIncomePY")

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.tax_year", taxYearInfo.previousTaxYearStart, taxYearInfo.previousTaxYearEnd))
    }

    "contain the currencySymbol class and £ " in {
      val doc = asDocument(createView())

      assertRenderedByCssSelector(doc, ".currencySymbol")

      val parentCurrencySymbol = doc.getElementById("parentEmploymentIncomePY").firstElementSibling().text()
      val partnerCurrencySymbol = doc.getElementById("partnerEmploymentIncomePY").firstElementSibling().text()

      parentCurrencySymbol mustBe "£"
      partnerCurrencySymbol mustBe "£"
    }
  }
}
