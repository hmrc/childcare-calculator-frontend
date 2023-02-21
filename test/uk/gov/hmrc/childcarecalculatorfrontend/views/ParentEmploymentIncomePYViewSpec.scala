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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ParentEmploymentIncomePYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.parentEmploymentIncomePY

class ParentEmploymentIncomePYViewSpec extends NewBigDecimalViewBehaviours {

  val view = application.injector.instanceOf[parentEmploymentIncomePY]
  val taxYearInfo = new TaxYearInfo

  val form = new ParentEmploymentIncomePYForm(frontendAppConfig).apply()
  val messageKeyPrefix = "parentEmploymentIncomePY"

  def createView = () => view(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => view(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "ParentEmploymentIncomePY view" must {
    behave like normalPage(createView, messageKeyPrefix, "hint")

    behave like pageWithBackLink(createView)

    behave like bigDecimalPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.ParentEmploymentIncomePYController.onSubmit(NormalMode).url,
      Some(messages(s"$messageKeyPrefix.info"))
    )

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.tax_year", taxYearInfo.previousTaxYearStart, taxYearInfo.previousTaxYearEnd))
    }
  }
}
