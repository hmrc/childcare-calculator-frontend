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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ParentEmploymentIncomeCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.parentEmploymentIncomeCY

class ParentEmploymentIncomeCYViewSpec extends NewBigDecimalViewBehaviours {

  val taxYearInfo                    = new TaxYearInfo
  val view: parentEmploymentIncomeCY = inject[parentEmploymentIncomeCY]

  val messageKeyPrefix = "parentEmploymentIncomeCY"

  override val form: Form[BigDecimal] = new ParentEmploymentIncomeCYForm(frontendAppConfig).apply()

  def render(form: Form[BigDecimal] = this.form): Html = view(form)(using fakeRequest, messages)

  "ParentEmploymentIncomeCY view" must {
    behave.like(normalPage(() => render(), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render()))

    behave.like(
      bigDecimalPage(
        form => render(form = form),
        messageKeyPrefix,
        routes.ParentEmploymentIncomeCYController.onSubmit().url,
        Some(messages(s"$messageKeyPrefix.heading"))
      )
    )

    "contain tax year info" in {
      val doc = asDocument(render())
      assertContainsText(
        doc,
        messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }
  }

}
