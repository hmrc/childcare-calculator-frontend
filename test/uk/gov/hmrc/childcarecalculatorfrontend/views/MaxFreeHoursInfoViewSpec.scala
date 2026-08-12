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
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo

class MaxFreeHoursInfoViewSpec extends NewViewBehaviours {

  val view: maxFreeHoursInfo = inject[maxFreeHoursInfo]
  val messageKeyPrefix       = "maxFreeHoursInfo"

  def render(
      tfcEligibility: Eligibility = Eligibility.Eligible,
      childcareVouchersEligibility: Eligibility = Eligibility.Eligible,
      childrenAgeGroups: Option[Set[ChildAgeGroup]] = None,
      max30HoursEnglandContent: Option[Boolean] = None,
      universalCredit: Option[Boolean] = None
  ): Html =
    view(tfcEligibility, childcareVouchersEligibility, childrenAgeGroups, max30HoursEnglandContent, universalCredit)(
      using fakeRequest,
      messages
    )

  "MaxFreeHoursInfo view" must {

    behave.like(
      normalPage(() => render(), messageKeyPrefix, "info", "info", "info.link", "info.link.url", "get.more.help")
    )

    "display correct message when only eligible for tax free childcare" in {
      val page = render(childcareVouchersEligibility = Eligibility.NotEligible)

      assertContainsText(asDocument(page), messages(s"$messageKeyPrefix.li.tfc"))
    }

    "display the correct message when only eligible for childcare vouchers" in {
      val page     = render(tfcEligibility = Eligibility.NotEligible)
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.li.vouchers"))
      assertNotContainsText(document, messages(s"$messageKeyPrefix.li.tfc"))
    }

    "display correct message when eligible for both tax free childcare and childcare vouchers " in {
      val page     = render()
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.li.tfc"))
      assertContainsText(document, messages(s"$messageKeyPrefix.li.vouchers"))
      assertContainsText(document, messages(s"$messageKeyPrefix.get.more.help"))
      assertContainsText(document, messages(s"$messageKeyPrefix.give.more.info"))
    }

    "display correct message when user selected Universal credits and only eligible for tax free childcare, " +
      "childcare vouchers " in {

        val page     = render()
        val document = asDocument(page)

        assertContainsText(document, messages(s"$messageKeyPrefix.li.tfc"))
        assertContainsText(document, messages(s"$messageKeyPrefix.li.vouchers"))
        assertContainsText(document, messages(s"$messageKeyPrefix.get.more.help"))
        assertContainsText(document, messages(s"$messageKeyPrefix.give.more.info"))
      }

    "display the alternate message when max30HoursEnglandContent is true" in {
      val page     = render(max30HoursEnglandContent = Some(true))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.england.hasVouchers.info"))
      assertContainsText(document, messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(document, messages(s"$messageKeyPrefix.england.li.otherChildren"))
    }

    "display the alternate message when max30HoursEnglandContent is false" in {
      val page     = render(max30HoursEnglandContent = Some(false))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.england.noVouchers.info"))
      assertContainsText(document, messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(document, messages(s"$messageKeyPrefix.england.li.otherChildren"))
    }

    "display the alternate message when childAgedTwo is true" in {
      val page     = render(childrenAgeGroups = Some(Set(ChildAgeGroup.TwoYears)))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(
        document,
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
    }

    "display the alternate message when childAgedThreeOrFour is true" in {
      val page     = render(childrenAgeGroups = Some(Set(ChildAgeGroup.ThreeYears)))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }

    "display the alternate message when childAgedTwo and childAgedThreeOrFour both are true" in {
      val page     = render(childrenAgeGroups = Some(Set(ChildAgeGroup.TwoYears, ChildAgeGroup.ThreeYears)))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get.with.colon"))
      assertContainsText(
        document,
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }

    "display the alternate message when nineTo23Months is selected" in {
      val page     = render(childrenAgeGroups = Some(Set(ChildAgeGroup.NineTo23Months)))
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(
        document,
        messages(s"$messageKeyPrefix.you.can.get.nineTo23Months", frontendAppConfig.maxFreeHoursAmount)
      )
    }

    "display the alternate message when nineTo23Months, childAgedTwo and childAgedThreeOrFour are all true" in {
      val page = render(childrenAgeGroups =
        Some(Set(ChildAgeGroup.NineTo23Months, ChildAgeGroup.TwoYears, ChildAgeGroup.ThreeYears))
      )
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get.with.colon"))
      assertContainsText(
        document,
        messages(s"$messageKeyPrefix.you.can.get.nineTo23Months", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(
        document,
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(document, messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }
  }

}
