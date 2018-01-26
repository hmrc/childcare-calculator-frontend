/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo

class MaxFreeHoursInfoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "maxFreeHoursInfo"

  def view = () => maxFreeHoursInfo(frontendAppConfig, Eligible, true, Eligible) (fakeRequest, messages)

  "MaxFreeHoursInfo view" must {

    behave like normalPage(view, messageKeyPrefix, "could.get.max.hours", "info", "info.link", "info.link.url", "info.part2", "still.to.check")

    "display correct message when only eligible for tax free childcare" in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, false, NotEligible) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tfc"))

    }

    "display the correct message when only eligible for childcare vouchers" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, true, NotEligible) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.vouchers"))
      assertNotContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tax_credits"))
      assertNotContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tfc"))
    }

    "display the correct message when only eligible for tax credits" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, false, Eligible) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tax_credits"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.still.to.check"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.give.more.info"))
      assertNotContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tfc"))
      assertNotContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.vouchers"))
    }

    "display correct message when only eligible for tax free childcare, childcare vouchers, tax credits " in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, true, Eligible) (fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tfc"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.vouchers"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.li.tax_credits"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.still.to.check"))
      assertContainsText(asDocument(view), messagesApi(s"$messageKeyPrefix.give.more.info"))
    }
  }

}
