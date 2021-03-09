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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourIncomeInfo

class YourIncomeInfoViewSpec extends ViewBehaviours {

  val view = app.injector.instanceOf[yourIncomeInfo]
  val taxYearInfo = new TaxYearInfo

  def createView = () => view(frontendAppConfig, taxYearInfo)(fakeRequest, messages)
  val messageKeyPrefix = "yourIncomeInfo"

  "Your Income Info view" must {
    behave like normalPage(createView, messageKeyPrefix, "guidance",
      "li.income_paid_work", "li.pensions", "li.other_income", "li.benefits_income", "li.birth_or_adoption")

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("yourIncomeInfo.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd))
    }

    "contain the link for parent paid work for current year" in {
      val doc = asDocument(createView())
      val continueLink = doc.getElementById("target-page-link")

      assertContainsText(doc, messages("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode).url

    }
  }
}
