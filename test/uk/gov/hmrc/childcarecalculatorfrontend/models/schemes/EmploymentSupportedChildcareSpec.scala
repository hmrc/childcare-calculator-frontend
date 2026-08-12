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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.Mockito.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.Eligibility
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{
  YesNoNotYet,
  YouPartnerBothNeither,
  YouPartnerBothNeitherNotSure
}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class EmploymentSupportedChildcareSpec extends SchemeSpec {

  val esc = new EmploymentSupportedChildcare

  val answers: UserAnswers = spy(helper())

  "EmploymentSupportedChildcare" must {
    "return Eligible when single person has childcare costs and childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.areYouInPaidWork).thenReturn(Some(true))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.yourChildcareVouchers).thenReturn(Some(true))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return NotEligible when single person has no childcare costs and childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.areYouInPaidWork).thenReturn(Some(true))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.No))
      when(answers.yourChildcareVouchers).thenReturn(Some(true))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.NotEligible
    }

    "return Eligible when joint claim parent has childcare costs and childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.You))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.yourChildcareVouchers).thenReturn(Some(true))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return Eligible when joint claim partner has the childcare costs and can get childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Partner))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.partnerChildcareVouchers).thenReturn(Some(true))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return Eligible when joint claim partner, do not have childcare costs yet and can get childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Partner))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.NotYet))
      when(answers.partnerChildcareVouchers).thenReturn(Some(true))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return Eligible when joint claim, both work, partner has the childcare costs and can get childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return Eligible when joint claim where they have childcare costs and both can get childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Both))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.Eligible
    }

    "return NotEligible when single claim person chooses NO when asked about childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.areYouInPaidWork).thenReturn(Some(true))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.yourChildcareVouchers).thenReturn(Some(false))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.NotEligible
    }

    "return NotEligible when joint claim partner has the childcare costs and chooses NO when asked about getting childcare vouchers" in {

      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Partner))
      when(answers.childcareCosts).thenReturn(Some(YesNoNotYet.Yes))
      when(answers.partnerChildcareVouchers).thenReturn(Some(false))

      esc.eligibility(answers: UserAnswers) mustBe Eligibility.NotEligible
    }

  }

}
