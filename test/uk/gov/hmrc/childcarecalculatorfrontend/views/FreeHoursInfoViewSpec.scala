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

import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo

class FreeHoursInfoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "freeHoursInfo"

  def createView = () => freeHoursInfo(frontendAppConfig, false, "")(fakeRequest, messages)

  "FreeHoursInfo view" must {
    behave like normalPage(createView, messageKeyPrefix, "heading2", "guidance", "li.30hours",
      "li.vouchers", "li.tfc", "li.tax_credits")

    Seq("england", "scotland", "wales").foreach { location =>
      s"display correct content when user with location $location and have child aged 2" in {
        val view = freeHoursInfo(frontendAppConfig, true, location)(fakeRequest, messages)
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.para1.$location"))
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.2year"))
      }
    }

    Seq("england", "scotland", "wales", "northernIreland").foreach { location =>
      s"display correct content when user with location $location and don't have child aged 2" in {
        val view = freeHoursInfo(frontendAppConfig, false, location)(fakeRequest, messages)
        val doc = asDocument(view)
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.para1.$location"))
        assertNotContainsText(doc, messagesApi(s"$messageKeyPrefix.para2.$location"))
      }
    }
  }
}
