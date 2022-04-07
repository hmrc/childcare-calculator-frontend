/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsCY

class YouAnyTheseBenefitsCYViewSpec extends NewYesNoViewBehaviours {

  override val form = BooleanForm()
  val view = app.injector.instanceOf[youAnyTheseBenefitsCY]
  val taxYearInfo = new TaxYearInfo

  val messageKeyPrefix = "youAnyTheseBenefitsCY"

  def createView = () => view(frontendAppConfig, BooleanForm(), NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "YouAnyTheseBenefits view" must {

    behave like normalPage(createView, messageKeyPrefix, "li.income_support", "li.jobseekers_allowance", "li.carers",
      "li.employment_support", "li.pensions", "li.disability" )

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YouAnyTheseBenefitsCYController.onSubmit(NormalMode).url)

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd))
    }
  }
}
