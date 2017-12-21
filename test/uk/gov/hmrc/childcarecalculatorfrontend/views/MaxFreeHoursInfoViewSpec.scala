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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NormalMode, NotEligible, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo

class MaxFreeHoursInfoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "maxFreeHoursInfo"

  def view = () => maxFreeHoursInfo(frontendAppConfig, Eligible, true, true) (fakeRequest, messages)

  "MaxFreeHoursInfo view" must {

    behave like normalPage(view, messageKeyPrefix, "could.get.max.hours", "info", "still.to.check")

    "display correct message when only eligible for tax free chjldcare" in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, false, false) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.taxFreeChildcare"))

    }

    "display the correct message when only eligible for childcare vouchers" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, true, false) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.childcareVouchers"))
    }

    "display the correct message when only eligible for tax credits" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, false, true) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.taxCredits"))
    }

    "display correct message when only eligible for tax free chjldcare, childcare vouchers, tax credits " in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, true, true) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.taxFreeChildcare"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.childcareVouchers"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.taxFreeChildcare"))

    }

  }

}
