/*
 * Copyright 2017 HM Revenue & Customs
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

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothIncomeInfoPY

class BothIncomeInfoPYViewSpec extends ViewBehaviours {

  def createView = () => bothIncomeInfoPY(frontendAppConfig, Call("GET", "test"), taxYearInfo)(fakeRequest, messages)

  val taxYearInfo = new TaxYearInfo

  def createViewWithNextPageLink = (nextPage: Call) => bothIncomeInfoPY(frontendAppConfig, nextPage, taxYearInfo)(fakeRequest, messages)

  val messageKeyPrefix = "bothIncomeInfoPY"

  "Partner Income Info PY view" must {
    behave like normalPage(createView, messageKeyPrefix, "guidance")
  }

  "contain the link for next page" in {
    val testCall = Call("GET", "http://google.com")

    val doc = asDocument(createViewWithNextPageLink(testCall))
    val continueLink = doc.getElementById("target-page-link")

    assertContainsText(doc, messagesApi("site.save_and_continue"))
    continueLink.attr("href") mustBe testCall.url
  }

  "contain tax year info" in {
    val doc = asDocument(createView())
    assertContainsText(doc, messages(s"$messageKeyPrefix.tax_year", taxYearInfo.previousTaxYearStart, taxYearInfo.previousTaxYearEnd))
  }
}
