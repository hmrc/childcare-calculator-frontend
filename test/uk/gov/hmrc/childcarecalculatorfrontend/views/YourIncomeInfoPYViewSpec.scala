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

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourIncomeInfoPY

class YourIncomeInfoPYViewSpec extends ViewBehaviours {

  def createView = () => yourIncomeInfoPY(frontendAppConfig)(fakeRequest, messages)
  val messageKeyPrefix = "yourIncomeInfoPY"

  "Your Income Info PY view" must {
    behave like normalPage(createView, messageKeyPrefix, "tax_year", "guidance")

    "contain the link for parent paid work for previous year" in {
      val doc = asDocument(createView())
      val continueLink = doc.getElementById("target-page-link")

      assertContainsText(doc, messagesApi("site.save_and_continue"))
      continueLink.attr("href") mustBe routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode).url

    }
  }
}
