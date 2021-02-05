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

import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

class MaxFreeHoursInfoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "maxFreeHoursInfo"

  def answers(value: Option[Boolean] = None): UserAnswers =
    new UserAnswers(CacheMap("", Map())) {
      override def max30HoursEnglandContent: Option[Boolean] = value
    }

  def view = () => maxFreeHoursInfo(frontendAppConfig, Eligible, Eligible, Eligible, answers()) (fakeRequest, messages)

  "MaxFreeHoursInfo view" must {

    behave like normalPage(view, messageKeyPrefix, "could.get.max.hours", "info", "info.link", "info.link.url", "still.to.check")

    "display correct message when only eligible for tax free childcare" in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, NotEligible, NotEligible, answers()) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tfc"))

    }

    "display the correct message when only eligible for childcare vouchers" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, Eligible, NotEligible, answers()) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.vouchers"))
      assertNotContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tax_credits"))
      assertNotContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tfc"))
    }

    "display the correct message when only eligible for tax credits" in {
      val view = maxFreeHoursInfo(frontendAppConfig, NotEligible, NotEligible, Eligible, answers()) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tax_credits"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.still.to.check"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.give.more.info"))
      assertNotContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tfc"))
      assertNotContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.vouchers"))
    }

    "display correct message when only eligible for tax free childcare, childcare vouchers, tax credits " in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, Eligible, Eligible, answers()) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tfc"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.vouchers"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tax_credits"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.still.to.check"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.give.more.info"))
    }

    "display correct message when user selected Universal credits and only eligible for tax free childcare, " +
      "childcare vouchers " in {

      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, Eligible, Eligible, answers()) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.tfc"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.li.vouchers"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.still.to.check"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.give.more.info"))

    }

    "display the alternate message when max30HoursEnglandContent is true" in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, Eligible, Eligible, answers(Some(true))) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.hasVouchers.info"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.li.otherChildren"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.p1.voucher"))
    }

    "display the alternate message when max30HoursEnglandContent is false" in {
      val view = maxFreeHoursInfo(frontendAppConfig, Eligible, Eligible, Eligible, answers(Some(false))) (fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.noVouchers.info"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.li.otherChildren"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.england.p1"))

    }
  }
}
