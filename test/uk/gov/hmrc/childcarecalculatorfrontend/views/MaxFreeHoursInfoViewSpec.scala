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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo

class MaxFreeHoursInfoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "maxFreeHoursInfo"

  def view = () => maxFreeHoursInfo(frontendAppConfig)(fakeRequest, messages)

  "MaxFreeHoursInfo view" must {


    behave like normalPage(view, messageKeyPrefix, "what.else.you.could.get", "still.to.check")
  }


  "MaxFreeHoursInfo view " must {
      s"display correct content when loaded" in {
        val view = maxFreeHoursInfo(frontendAppConfig)(fakeRequest, messages)
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.could.get.max.hours"))
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.could.get.tfc"))
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.could.get.esc"))
        assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.give.more.info"))
      }
  }

}
