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

import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{EitherValues, OptionValues}
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxCreditsSpec extends SchemeSpec with MockitoSugar with OptionValues with EitherValues {

  val location = Location.SCOTLAND
  val applicableBenefits: Seq[WhichBenefitsEnum.Value] = location match {
    case Location.SCOTLAND => Seq(SCOTTISHCARERSALLOWANCE)
    case _ => Seq(CARERSALLOWANCE)
  }

  def taxCredits(household: ModelFactory = new ModelFactory): TaxCredits = spy(new TaxCredits(household))

  val answers = mock[UserAnswers]
  val household = mock[ModelFactory]

  ".eligibility" must {

    "return `NotDetermined` if `household` is undefined" in {
      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(household(any())) thenReturn None
      taxCredits(household).eligibility(answers) mustEqual NotDetermined
    }

    "return `NotEligible` if a user works 24 hours but their partner doesn't work and neither get benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(Set.empty), Parent(Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }

    "return `NotEligible` if a partner works 24 hours but parent doesn't work and neither get benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent( Set.empty), Parent(Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }


    applicableBenefits.foreach {
      benefit =>

        s"return `Eligible` if a user works 16 hours, their partner doesn't work but claims $benefit" in {
          when(household(any())) thenReturn Some(JointHousehold(Parent(Set.empty), Parent(Set(benefit))))
          taxCredits(household).eligibility(answers) mustEqual Eligible
        }
    }


    "return `NotEligible` if neither parent works 16 hours, even if they work 24 hours total, and they don't claim benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent(Set.empty), Parent(Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }

    "return `NotEligible` if only parent works 24 hours and they don't claim benefits" in {
      when(household(any())) thenReturn Some(JointHousehold(Parent( Set.empty), Parent(Set.empty)))
      taxCredits(household).eligibility(answers) mustEqual NotEligible
    }
  }
}
