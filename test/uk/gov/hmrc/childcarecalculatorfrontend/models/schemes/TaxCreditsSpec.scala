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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxCreditsSpec extends SchemeSpec with MockitoSugar with OptionValues with EitherValues {

  val applicableBenefits: Seq[WhichBenefitsEnum.Value] =
    Seq(CARERSALLOWANCE)

  def taxCredits(household: ModelFactory = new ModelFactory): TaxCredits = spy(new TaxCredits(household))

  val answers = mock[UserAnswers]
  val household = mock[ModelFactory]

  ".eligibility" must {

    "return `NotDetermined` if `household` is undefined" in {
      when(household(any())) thenReturn None
      taxCredits(household).eligibility(answers) mustEqual NotDetermined
    }

    "return `Eligible` if a user works 24 hours but their partner doesn't work and neither get benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(24, Set.empty), Parent(0, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual Eligible
    }

    "return `Eligible` if a user works 16 hours and collectively the household works at least 24 hours and they don't claim benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set.empty), Parent(8, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual Eligible
    }

    applicableBenefits.foreach {
      benefit =>

        s"return `Eligible` if a user works 16 hours, their partner doesn't work but claims $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set.empty), Parent(0, Set(benefit))))
          taxCredits(household).eligibility(answers) mustEqual Eligible
        }

        s"return `NotEligible` if a user works 16 hours, their partner doesn't work even if the user claims $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set(benefit)), Parent(0, Set.empty)))
          taxCredits(household).eligibility(answers) mustEqual NotEligible
        }

        s"return `Eligible` if a user works 16 hours, their partner works but they don't meet the 24 hour threshold, but they claim $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set.empty), Parent(4, Set(benefit))))
          taxCredits(household).eligibility(answers) mustEqual Eligible
        }

        s"return `NotEligible` if a user works 16 hours, their partner works but they don't meet the 24 hour threshold, even if the user claims $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set(benefit)), Parent(4, Set.empty)))
          taxCredits(household).eligibility(answers) mustEqual NotEligible
        }

        s"return `Eligible` if a single user works 16 hours and they claim $benefit" in {
          when(household(any())) thenReturn Some(SingleHousehold(Parent(16, Set(benefit))))
          taxCredits(household).eligibility(answers) mustEqual Eligible
        }

        s"return `NotEligible` if a single user works less than 16 hours and claims $benefit" in {
          when(household(any())) thenReturn Some(SingleHousehold(Parent(15, Set.empty)))
          taxCredits(household).eligibility(answers) mustEqual NotEligible
        }

        s"return `NotEligible` if neither parent works 16 hours, even if they work 24 hours total, even if one of them claims $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(12, Set.empty), Parent(12, Set(benefit))))
          taxCredits(household).eligibility(answers) mustEqual NotEligible
        }
    }

    "return `Eligible` if a single user works 16 hours and they don't get benefits" in {
      when(household(any())) thenReturn Some(SingleHousehold(Parent(16, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual Eligible
    }

    "return `NotEligible` if a single user works less than 16 hours and doesn't get benefits" in {
      when(household(any())) thenReturn Some(SingleHousehold(Parent(15, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }

    "return `NotEligible` if a user works 16 hours, their partner doesn't work and they don't claim benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(16, Set.empty), Parent(0, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }

    "return `NotEligible` if neither parent works 16 hours, even if they work 24 hours total, and they don't claim benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(12, Set.empty), Parent(12, Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }
  }
}
