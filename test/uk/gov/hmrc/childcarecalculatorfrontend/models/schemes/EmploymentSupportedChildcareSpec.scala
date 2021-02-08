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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes


import org.mockito.Mockito._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers



class EmploymentSupportedChildcareSpec extends SchemeSpec{

  val esc = new EmploymentSupportedChildcare

  val answers = spy(helper())

  "EmploymentSupportedChildcare" must {
    "return  Eligible when single person has childcare costs and childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.yourChildcareVouchers) thenReturn Some(true)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }

    "return  NotEligible when single person has no childcare costs and childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.childcareCosts) thenReturn Some(NO)
      when(answers.yourChildcareVouchers) thenReturn Some(true)

      esc.eligibility(answers: UserAnswers) mustBe NotEligible
    }


    "return  Eligible when joint claim parent has childcare costs and childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(You)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.yourChildcareVouchers) thenReturn Some(true)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }

    "return Eligible when joint claim partner has the childcare costs and can get childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.partnerChildcareVouchers) thenReturn Some(true)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }

    "return Eligible when joint claim partner, do not have childcare costs yet and can get childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.NOTYET.toString)
      when(answers.partnerChildcareVouchers) thenReturn Some(true)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }



    "return Eligible when joint claim, both work, partner has the childcare costs and can get childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.whoGetsVouchers) thenReturn Some(Partner)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }

    "return Eligible when joint claim where they have childcare costs and both can get childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.whoGetsVouchers) thenReturn Some(Both)

      esc.eligibility(answers: UserAnswers) mustBe Eligible
    }

    "return NotEligible when single claim person chooses NO when asked about childcare vouchers" in {

      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.yourChildcareVouchers) thenReturn Some(false)

      esc.eligibility(answers: UserAnswers) mustBe NotEligible
    }

    "return NotEligible when joint claim partner has the childcare costs and chooses NO when asked about getting childcare vouchers" in{

      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
      when(answers.childcareCosts) thenReturn Some(YesNoNotYetEnum.YES.toString)
      when(answers.partnerChildcareVouchers) thenReturn Some(false)

      esc.eligibility(answers: UserAnswers) mustBe NotEligible
    }


  }
}

