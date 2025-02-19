/*
 * Copyright 2025 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits.{CarersAllowance, CarersCredit, ContributionBasedEmploymentAndSupportAllowance, IncapacityBenefit, NICreditsForIncapacityOrLimitedCapabilityForWork, NoneOfThese, SevereDisablementAllowance}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible, ParentsBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.{JointHousehold, ModelFactory, Parent, SingleHousehold}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers}

class FreeChildcareEligibilityCalculatorSpec extends PlaySpec with Matchers with BeforeAndAfterEach {

  private val modelFactory = mock[ModelFactory]

  private val eligibilityCalculator = new FreeChildcareEligibilityCalculator(modelFactory)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(modelFactory)
  }

  private val userAnswers: UserAnswers = new UserAnswers(CacheMap("test-id", Map.empty))

  private val eligibleBenefits: Set[ParentsBenefits] = Set(
    CarersAllowance,
    IncapacityBenefit,
    SevereDisablementAllowance
  )

  "FreeChildcareEligibilityCalculator on calculateEligibility" when {

    "should always call ModelFactory" in {
      when(modelFactory(any())) thenReturn None

      eligibilityCalculator.calculateEligibility(userAnswers, Set.empty)

      verify(modelFactory).apply(eqTo(userAnswers))
    }

    "ModelFactory returns empty Option" should {
      "return NotDetermined" in {
        when(modelFactory(any())) thenReturn None

        eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotDetermined
      }
    }

    "ModelFactory returns SingleHousehold" should {

      "return NotEligible" when {

        "parent earns less than minimum earnings" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }

        "parent earns over maximum earnings" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = true, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }
      }

      "return Eligible" when {

        "parent earns between minimum and maximum earnings" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns below minimum earnings and is an apprentice" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns below minimum earnings and is self employed" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = true, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns between minimum and maximum earnings and is an apprentice" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns between minimum and maximum earnings and is self-employed" in {
          val household = SingleHousehold(
            Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = true, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }
      }
    }

    "ModelFactory returns JointHousehold" should {

      "return NotEligible" when {

        "both parents do not earn above minimum earnings" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }

        "both parents earn above maximum earnings" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = true, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = true, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }

        "parent does, but partner does NOT earn above minimum earnings" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }

        "parent does NOT, but partner does earn above minimum earnings" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe NotEligible
        }

        "parent earns between minimum and maximum earnings and partner is unemployed" when {

          "partner gets NO benefits" in {
            val benefits: Set[ParentsBenefits] = Set(NoneOfThese)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
              partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }

          "partner gets one benefit and it is NOT eligible one" in {
            val benefits: Set[ParentsBenefits] = Set(ContributionBasedEmploymentAndSupportAllowance)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
              partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }

          "partner gets several benefits, but NONE of them are eligible benefits" in {
            val benefits: Set[ParentsBenefits] = Set(NICreditsForIncapacityOrLimitedCapabilityForWork, CarersCredit)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
              partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }
        }

        "partner earns between minimum and maximum earnings and parent is unemployed" when {

          "parent gets NO benefits" in {
            val benefits: Set[ParentsBenefits] = Set(NoneOfThese)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits),
              partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }

          "parent gets one benefit and it is NOT eligible one" in {
            val benefits: Set[ParentsBenefits] = Set(ContributionBasedEmploymentAndSupportAllowance)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits),
              partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }

          "parent gets several benefits, but NONE of them are eligible benefits" in {
            val benefits: Set[ParentsBenefits] = Set(NICreditsForIncapacityOrLimitedCapabilityForWork, CarersCredit)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits),
              partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe NotEligible
          }
        }
      }

      "return Eligible" when {

        "both parent and partner earn between minimum and maximum earnings" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns between minimum and maximum earnings and partner is an apprentice" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "partner earns between minimum and maximum earnings and parent is an apprentice" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "both parents earn below minimum earnings, but parent is self employed and partner is an apprentice" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = true, apprentice = false, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "both parents earn below minimum earnings, but parent is an apprentice and partner is self employed" in {
          val household = JointHousehold(
            parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = true, benefits = Set.empty),
            partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = true, apprentice = false, benefits = Set.empty)
          )
          when(modelFactory(any())) thenReturn Some(household)

          eligibilityCalculator.calculateEligibility(userAnswers, Set.empty) mustBe Eligible
        }

        "parent earns between minimum and maximum earnings and partner is unemployed" when {

          "partner gets one of eligible benefits" in {
            val benefits: Set[ParentsBenefits] = Set(CarersAllowance)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
              partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe Eligible
          }

          "partner gets several benefits, but some of them are eligible benefits" in {
            val benefits: Set[ParentsBenefits] =
              Set(CarersAllowance, NICreditsForIncapacityOrLimitedCapabilityForWork, CarersCredit)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty),
              partner = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe Eligible
          }
        }

        "partner earns between minimum and maximum earnings and parent is unemployed" when {

          "parent gets one of eligible benefits" in {
            val benefits: Set[ParentsBenefits] = Set(CarersAllowance)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits),
              partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe Eligible
          }

          "parent gets several benefits, but some of them are eligible benefits" in {
            val benefits: Set[ParentsBenefits] =
              Set(CarersAllowance, NICreditsForIncapacityOrLimitedCapabilityForWork, CarersCredit)
            val household = JointHousehold(
              parent = Parent(earnsAboveMinEarnings = false, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = benefits),
              partner = Parent(earnsAboveMinEarnings = true, earnsAboveMaxEarnings = false, selfEmployed = false, apprentice = false, benefits = Set.empty)
            )
            when(modelFactory(any())) thenReturn Some(household)

            eligibilityCalculator.calculateEligibility(userAnswers, eligibleBenefits) mustBe Eligible
          }
        }
      }
    }
  }

}
