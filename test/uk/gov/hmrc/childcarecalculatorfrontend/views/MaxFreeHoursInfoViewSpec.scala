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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{
  ChildAgeGroup,
  Eligible,
  NineTo23Months,
  NotEligible,
  ThreeYears,
  TwoYears
}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class MaxFreeHoursInfoViewSpec extends NewViewBehaviours {

  val view             = application.injector.instanceOf[maxFreeHoursInfo]
  val messageKeyPrefix = "maxFreeHoursInfo"

  def answers(
      max30HoursEnglandContentAns: Option[Boolean] = None,
      childAgeGroupAns: Option[Set[ChildAgeGroup]] = None
  ): UserAnswers =
    new UserAnswers(CacheMap("", Map())) {
      override def max30HoursEnglandContent: Option[Boolean]     = max30HoursEnglandContentAns
      override def childrenAgeGroups: Option[Set[ChildAgeGroup]] = childAgeGroupAns
    }

  def createView = () => view(frontendAppConfig, Eligible, Eligible, answers())(fakeRequest, messages)

  "MaxFreeHoursInfo view" must {
    val view1 = view(frontendAppConfig, Eligible, NotEligible, answers())(fakeRequest, messages)

    behave.like(normalPage(createView, messageKeyPrefix, "info", "info", "info.link", "info.link.url", "get.more.help"))

    "display correct message when only eligible for tax free childcare" in {
      val view1 = view(frontendAppConfig, Eligible, NotEligible, answers())(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.tfc"))

    }

    "display the correct message when only eligible for childcare vouchers" in {
      val view1 = view(frontendAppConfig, NotEligible, Eligible, answers())(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.vouchers"))
      assertNotContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.tfc"))
    }

    "display correct message when only eligible for tax free childcare, childcare vouchers " in {
      val view1 = view(frontendAppConfig, Eligible, Eligible, answers())(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.tfc"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.vouchers"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.get.more.help"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.give.more.info"))
    }

    "display correct message when user selected Universal credits and only eligible for tax free childcare, " +
      "childcare vouchers " in {

        val view1 = view(frontendAppConfig, Eligible, Eligible, answers())(fakeRequest, messages)
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.tfc"))
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.vouchers"))
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.get.more.help"))
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.give.more.info"))
      }

    "display the alternate message when max30HoursEnglandContent is true" in {
      val view1 = view(frontendAppConfig, Eligible, Eligible, answers(Some(true), None))(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.hasVouchers.info"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.li.otherChildren"))
    }

    "display the alternate message when max30HoursEnglandContent is false" in {
      val view1 = view(frontendAppConfig, Eligible, Eligible, answers(Some(false), None))(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.noVouchers.info"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.li.childcare"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.england.li.otherChildren"))
    }

    "display the alternate message when childAgedTwo is true" in {
      val view1 = view(frontendAppConfig, Eligible, Eligible, answers(None, Some(Set(TwoYears))))(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(
        asDocument(view1),
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
    }

    "display the alternate message when childAgedThreeOrFour is true" in {
      val view1 =
        view(frontendAppConfig, Eligible, Eligible, answers(None, Some(Set(ThreeYears))))(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }

    "display the alternate message when childAgedTwo and childAgedThreeOrFour both are true" in {
      val view1 = view(frontendAppConfig, Eligible, Eligible, answers(None, Some(Set(TwoYears, ThreeYears))))(
        fakeRequest,
        messages
      )
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get.with.colon"))
      assertContainsText(
        asDocument(view1),
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }

    "display the alternate message when nineTo23Months is selected" in {
      val view1 =
        view(frontendAppConfig, Eligible, Eligible, answers(None, Some(Set(NineTo23Months))))(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get"))
      assertContainsText(
        asDocument(view1),
        messages(s"$messageKeyPrefix.you.can.get.nineTo23Months", frontendAppConfig.maxFreeHoursAmount)
      )
    }

    "display the alternate message when nineTo23Months, childAgedTwo and childAgedThreeOrFour are all true" in {
      val view1 = view(
        frontendAppConfig,
        Eligible,
        Eligible,
        answers(None, Some(Set(NineTo23Months, TwoYears, ThreeYears)))
      )(fakeRequest, messages)
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get.with.colon"))
      assertContainsText(
        asDocument(view1),
        messages(s"$messageKeyPrefix.you.can.get.nineTo23Months", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(
        asDocument(view1),
        messages(s"$messageKeyPrefix.you.can.get.twoYears", frontendAppConfig.maxFreeHoursAmount)
      )
      assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.you.can.get.threeAndFourYears"))
    }
  }

}
