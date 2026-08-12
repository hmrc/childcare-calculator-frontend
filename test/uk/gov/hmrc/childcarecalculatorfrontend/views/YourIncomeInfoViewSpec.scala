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

import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourIncomeInfo

class YourIncomeInfoViewSpec extends NewViewBehaviours {

  val view: yourIncomeInfo = inject[yourIncomeInfo]
  val taxYearInfo          = new TaxYearInfo
  val messageKeyPrefix     = "yourIncomeInfo"

  val render: () => Html = () => view()(using fakeRequest, messages)

  "Your Income Info view" must {
    behave.like(
      normalPage(
        render,
        messageKeyPrefix,
        "guidance",
        "li.income_paid_work",
        "li.pensions",
        "li.other_income",
        "li.benefits_income"
      )
    )

    "contain tax year info" in {
      val doc = asDocument(render())
      assertContainsText(
        doc,
        messages("yourIncomeInfo.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }

    "contain the link for parent paid work for current year" in {
      val doc          = asDocument(render())
      val continueLink = doc.getElementsByClass("govuk-button")

      assertContainsText(doc, messages("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.ParentEmploymentIncomeCYController.onPageLoad().url

    }
  }

}
