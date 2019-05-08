/*
 * Copyright 2019 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import scala.language.implicitConversions

class TaxFreeChildcareSpec extends SchemeSpec with MockitoSugar {

  def tfc(tfcHousehold: ModelFactory = new ModelFactory): TaxFreeChildcare = spy(new TaxFreeChildcare(tfcHousehold))

  val applicableBenefits: Seq[WhichBenefitsEnum.Value] = Seq(CARERSALLOWANCE)

  val answers: UserAnswers = mock[UserAnswers]
  val modelFactory: ModelFactory = mock[ModelFactory]
  val household = mock[ModelFactory]

  "eligibility" must {

    "return `NotDetermined` if `household` is undefined" in {
      when(household(any())) thenReturn None
      tfc(household).eligibility(answers) mustEqual NotDetermined
    }

    "single household" when {

      "return `NotEligible` when minimum earnings not satisfied" in {
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `NotEligible` when parent earns over maximum earnings" in {
        when(household(any())) thenReturn Some(SingleHousehold(Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `Eligible` when parent satisfies minimum earnings and maximum earnings" in {
        when(household(any())) thenReturn Some(SingleHousehold(Parent(true, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is an apprentice" in {
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, false, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is self employed" in {
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, false, true, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }
    }

    "joint household" when {

      "return `NotEligible` when parent satisfy and partner not satisfy minimum earnings" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, true, false, Set.empty), Parent(false, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `NotEligible` when partner satisfy and parent not satisfy minimum earnings" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, true, false, false, Set.empty), Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `Eligible` when parent and partner satisfy minimum and maximum earnings" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(true, true, false, false, Set.empty), Parent(true, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent satify min and max earnings and partner is an apprentice" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(true, true, false, false, Set.empty), Parent(false, false, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when partner satify min and max earnings and parent is an apprentice" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, true, Set.empty), Parent(true, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is self employed and partner is an apprentice" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, true, false, Set.empty), Parent(false, false, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when partner is self employed and parent is an apprentice" in {
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, true, Set.empty), Parent(false, false, true, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      applicableBenefits.foreach {
        benefit =>

          s"return `Eligible` when parent satisfy min and max earnings and partner is getting $benefit" in {
            when(household(any())) thenReturn Some(JointHousehold(Parent(true, true, false, false, Set.empty), Parent(false, false, false, false, Set(benefit))))
            tfc(household).eligibility(answers) mustEqual Eligible
          }

          s"return `Eligible` when partner satisfy min and max earnings and parent is getting $benefit" in {
            when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, false, Set(benefit)), Parent(true, true, false, false, Set.empty)))
            tfc(household).eligibility(answers) mustEqual Eligible
          }

      }
    }
  }

}
