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

import org.mockito.Matchers._
import org.mockito.Mockito._
//import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import scala.language.implicitConversions

class TaxFreeChildcareSpec extends SchemeSpec with MockitoSugar with GeneratorDrivenPropertyChecks {

  def tfc(tfcHousehold: ModelFactory = new ModelFactory): TaxFreeChildcare = spy(new TaxFreeChildcare(tfcHousehold))

  val applicableBenefits: Seq[WhichBenefitsEnum.Value] =
    Seq(CARERSALLOWANCE)

  val answers: UserAnswers = mock[UserAnswers]
  val modelFactory: ModelFactory = mock[ModelFactory]
  val household = mock[ModelFactory]

  ".eligibility" must {

    "return `NotDetermined` if `household` is undefined" in {
      when(answers.hasApprovedCosts) thenReturn Some(true)
      when(household(any())) thenReturn None
      tfc(household).eligibility(answers) mustEqual NotDetermined
    }

    "return `NotDetermined` if `hasApprovedCosts` is undefined for joint household" in {
      when(answers.hasApprovedCosts) thenReturn None
      when(household(any())) thenReturn Some(JointHousehold(Parent(false, true, true, true, Set.empty), Parent(true, false, true, false, Set.empty)))
      tfc(household).eligibility(answers) mustEqual NotDetermined
    }

    "return `NotEligible` when `hasApprovedCosts` is false" in {
      when(answers.hasApprovedCosts) thenReturn Some(false)
      when(household(any())) thenReturn Some(SingleHousehold(Parent(true, true, false, false, Set.empty)))
      tfc(household).eligibility(answers) mustEqual NotEligible
    }

    "single household" when {

      "return `NotEligible` when minimum earnings not satisfied" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `NotEligible` when parent earns over maximum earnings" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(SingleHousehold(Parent(true, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `Eligible` when parent satisfies minimum earnings and maximum earnings" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(SingleHousehold(Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, true, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is self employed" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(SingleHousehold(Parent(false, true, true, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }
    }

    "joint household" when {

      "return `NotEligible` when parent satisfy and partner not satisfy minimum earnings" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(true, false, true, false, Set.empty), Parent(false, true, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `NotEligible` when partner satisfy and parent not satisfy minimum earnings" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, true, false, false, Set.empty), Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual NotEligible
      }

      "return `Eligible` when partner and parent satisfy minimum and maximum earnings" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(true, false, false, false, Set.empty), Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent satify min and max earnings and partner is an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(true, false, false, false, Set.empty), Parent(false, false, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when partner satify min and max earnings and parent is an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, true, Set.empty), Parent(true, false, false, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when parent is self employed and partner is an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, true, false, Set.empty), Parent(false, false, false, true, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when partner is self employed and parent is an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, true, Set.empty), Parent(false, false, true, false, Set.empty)))
        tfc(household).eligibility(answers) mustEqual Eligible
      }

      applicableBenefits.foreach {
        benefit =>

          s"return `Eligible` when parent satisfy min and max earnings and partner is getting $benefit" in {
            when(answers.hasApprovedCosts) thenReturn Some(true)
            when(household(any())) thenReturn Some(JointHousehold(Parent(true, false, false, false, Set.empty), Parent(false, false, false, false, Set(benefit))))
            tfc(household).eligibility(answers) mustEqual Eligible
          }

          s"return `Eligible` when partner satisfy min and max earnings and parent is getting $benefit" in {
            when(answers.hasApprovedCosts) thenReturn Some(true)
            when(household(any())) thenReturn Some(JointHousehold(Parent(false, false, false, false, Set(benefit)), Parent(true, false, false, false, Set.empty)))
            tfc(household).eligibility(answers) mustEqual Eligible
          }

      }
    }
  }

    //    "return `NotDetermined` if any dependent data is missing" in {
    //
    //      implicit val household: Arbitrary[Option[Household]] =
    //        Arbitrary(Gen.oneOf(Gen.const(None), genHousehold.map(Some.apply)))
    //
    //      forAll ("hasApprovedCosts", "household") {
    //        (hasApprovedCosts: Option[Boolean], household: Option[Household]) =>
    //
    //          whenever(hasApprovedCosts.isEmpty || household.isEmpty) {
    //            when(answers.hasApprovedCosts) thenReturn hasApprovedCosts
    //            when(modelFactory(any())) thenReturn household
    //            tfcScheme.eligibility(answers) mustEqual NotDetermined
    //          }
    //      }
    //    }


    //    "single household" when {
    //
    //      "return `Eligible` when the parent is eligible" in {
    //
    //        forAll("parent") {
    //          (parent: Parent) =>
    //
    //            whenever(eligible(parent)) {
    //              when(answers.hasApprovedCosts) thenReturn Some(true)
    //              when(modelFactory(any())) thenReturn Some(SingleHousehold(parent))
    //              tfcScheme.eligibility(answers) mustEqual Eligible
    //            }
    //        }
    //      }
    //
    //      "return `NotEligible` when the parent is not eligible" in {
    //
    //        forAll("parent") {
    //          (parent: Parent) =>
    //
    //            whenever(!eligible(parent)) {
    //              when(answers.hasApprovedCosts) thenReturn Some(true)
    //              when(modelFactory(any())) thenReturn Some(SingleHousehold(parent))
    //              tfcScheme.eligibility(answers) mustEqual NotEligible
    //            }
    //        }
    //      }
    //    }

    //    "joint household" when {
    //
    //      "return `Eligible` when both parents are eligible" in {
    //
    //        forAll("parent", "partner") {
    //          (parent: Parent, partner: Parent) =>
    //
    //            whenever(eligible(parent) && eligible(partner)) {
    //              when(answers.hasApprovedCosts) thenReturn Some(true)
    //              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
    //              tfcScheme.eligibility(answers) mustEqual Eligible
    //            }
    //        }
    //      }
    //
    //      "return `Eligible` when one parent is eligible, the other is not but claims carers allowance" in {
    //        forAll("parent", "partner") {
    //          (parent: Parent, partner: Parent) =>
    //
    //            whenever(
    //              (eligible(parent) && !eligible(partner) && partner.benefits.contains(CARERSALLOWANCE)) ||
    //                (!eligible(parent) && eligible(partner) && parent.benefits.contains(CARERSALLOWANCE))
    //            ) {
    //              when(answers.hasApprovedCosts) thenReturn Some(true)
    //              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
    //              tfcScheme.eligibility(answers) mustEqual Eligible
    //            }
    //        }
    //      }
    //
    //      "return `NotEligible` when either user is not eligible" in {
    //        forAll("parent", "partner") {
    //          (parent: Parent, partner: Parent) =>
    //
    //            whenever(!eligible(parent) || !eligible(partner)) {
    //              when(answers.hasApprovedCosts) thenReturn Some(true)
    //              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
    //            }
    //        }
    //      }
    //    }

    //  "test eligible" must {

    //    "return `true` when the user meets any eligibility criteria and they aren't over the maximum hours threshold" in {
    //
    //      forAll("minEarnings", "selfEmployed", "apprentice", "benefits") {
    //        (minEarnings: Boolean, selfEmployed: Boolean, apprentice: Boolean, benefits: Set[WhichBenefitsEnum.Value]) =>
    //
    //          whenever(minEarnings || selfEmployed || apprentice) {
    //
    //            val model = Parent(
    //              minEarnings,
    //              maxEarnings = false,
    //              selfEmployed = selfEmployed,
    //              apprentice = apprentice, benefits
    //            )
    //            eligible(model) mustEqual true
    //          }
    //      }
    //    }

    //    "return `false` when they earn under the minimum threshold and are not an apprentice or self employed" in {
    //
    //      forAll("maxEarnings", "benefits") {
    //        (maxEarnings: Boolean, benefits: Set[WhichBenefitsEnum.Value]) =>
    //          eligible(Parent(false, maxEarnings, false, false, benefits)) mustEqual false
    //      }
    //    }

    //    "return `false` when the user earns over the maximum threshold" in {
    //
    //      forAll("minEarnings", "selfEmployed", "apprentice", "benefits") {
    //        (minEarnings: Boolean, selfEmployed: Boolean, apprentice: Boolean, benefits: Set[WhichBenefitsEnum.Value]) =>
    //
    //          val model = Parent(
    //            minEarnings,
    //            maxEarnings = true,
    //            selfEmployed = selfEmployed,
    //            apprentice = apprentice,
    //            benefits
    //          )
    //          eligible(model) mustEqual false
    //      }
    //    }
    //  }

    //  def eligible(parent: Parent): Boolean = {
    //    println(s"***************PARENT>>>>>>$parent")
    //    (parent.minEarnings && !parent.maxEarnings) || (!parent.minEarnings && (parent.apprentice || parent.selfEmployed))
    //  }

    //  implicit val genBenefits: Gen[Set[WhichBenefitsEnum.Value]] =
    //    Gen.containerOf[Set, WhichBenefitsEnum.Value](Gen.oneOf(WhichBenefitsEnum.values.toSeq))

    //  implicit val genParent: Gen[Parent] = {
    //    for {
    //      minEarnings  <- arbitrary[Boolean]
    //      maxEarnings  <- arbitrary[Boolean]
    //      selfEmployed <- arbitrary[Boolean]
    //      apprentice   <- arbitrary[Boolean]
    //      benefits     <- genBenefits
    //    } yield Parent(minEarnings, maxEarnings, selfEmployed, apprentice, benefits)
    //  }

    //  implicit val genSingle: Gen[SingleHousehold] =
    //    for {
    //      parent <- genParent
    //    } yield SingleHousehold(parent)
    //
    //  implicit val genJoint: Gen[JointHousehold] =
    //    for {
    //      parent <- genParent
    //      partner <- genParent
    //    } yield JointHousehold(parent, partner)
    //
    //  implicit val genHousehold: Gen[Household] =
    //    Gen.oneOf(genSingle, genJoint)

    //  implicit def genOpt[A](implicit gen: Gen[A]): Gen[Option[A]] =
    //    Gen.oneOf(Gen.const(None), gen.map(Some.apply))

    //  implicit def arb[A](implicit gen: Gen[A]): Arbitrary[A] = Arbitrary(gen)

}
