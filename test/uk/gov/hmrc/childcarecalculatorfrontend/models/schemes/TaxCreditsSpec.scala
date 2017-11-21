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
import org.scalacheck.{Arbitrary, Gen}
import Arbitrary.arbitrary
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxCreditsSpec extends SchemeSpec with MockitoSugar with GeneratorDrivenPropertyChecks {

  val answers = mock[UserAnswers]
  val modelFactory = mock[ModelFactory]
  val scheme = new TaxCredits(modelFactory)

  "eligibility" must {

    "return `NotDetermined` if household data is missing" in {

      implicit val household: Arbitrary[Option[Household]] =
        Arbitrary(Gen.oneOf(Gen.const(None), genHousehold.map(Some.apply)))

      forAll ("household") {
        (household: Option[Household]) =>
          whenever(household.isEmpty) {
            when(modelFactory(any())) thenReturn household
            scheme.eligibility(answers) mustEqual NotDetermined
          }
      }
    }

    "single household" when {

      "return `NotEligible` when the parent is not eligible" in {

        forAll("parent") {
          (parent: Parent) =>
            whenever(!eligible(parent)) {
              when(modelFactory(any())) thenReturn Some(SingleHousehold(parent))
              scheme.eligibility(answers) mustEqual NotEligible
            }
        }
      }

      "return `Eligible` when the parent is eligible" in {

        forAll("parent") {
          (parent: Parent) =>
            whenever(eligible(parent)) {
              when(modelFactory(any())) thenReturn Some(SingleHousehold(parent))
              scheme.eligibility(answers) mustEqual Eligible
            }
        }
      }

    }

    "joint household" when {

      "return `NotEligible` when either user is not eligible" in {
        forAll("parent", "partner") {
          (parent: Parent, partner: Parent) =>

            whenever(!jointEligible(parent, partner)) {
              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
              scheme.eligibility(answers) mustEqual NotEligible
            }
        }
      }

      "return `Eligible` when both parents are eligible" in {

        forAll("parent", "partner") {
          (parent: Parent, partner: Parent) =>

            whenever(jointEligible(parent, partner)) {
              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
              scheme.eligibility(answers) mustEqual Eligible
            }
        }
      }

      "return `Eligible` when one parent is eligible, the other is not but claims carers allowance" in {
        forAll("parent", "partner") {
          (parent: Parent, partner: Parent) =>

            whenever(
              (eligible(parent) && !eligible(partner) && partner.benefits.contains(CARERSALLOWANCE)) ||
                (eligible(partner) && !eligible(parent) && parent.benefits.contains(CARERSALLOWANCE))
            ) {
              when(modelFactory(any())) thenReturn Some(JointHousehold(parent, partner))
              scheme.eligibility(answers) mustEqual Eligible
            }
        }
      }

    }

  }

  private val jointHours: BigDecimal = 24.0
  private val individualHours: BigDecimal = 16.0

  private def eligible(parent: Parent): Boolean = parent.hours >= individualHours

  private def jointEligible(parent: Parent, partner: Parent): Boolean =
    (eligible(parent) || eligible(partner)) && (parent.hours + partner.hours >= jointHours)

  implicit override val generatorDrivenConfig = PropertyCheckConfiguration(
    minSuccessful = 10,
    maxDiscardedFactor = 100.0)

  implicit val genBenefits: Gen[Set[WhichBenefitsEnum.Value]] =
    Gen.containerOf[Set, WhichBenefitsEnum.Value](Gen.oneOf(Seq(CARERSALLOWANCE)))

  implicit val genParent: Gen[Parent] = {

    for {
      hours  <- arbitrary[BigDecimal]
      benefits     <- genBenefits
    } yield Parent(hours, benefits)
  }

  implicit val genSingle: Gen[SingleHousehold] =
    for {
      parent <- genParent
    } yield SingleHousehold(parent)

  implicit val genJoint: Gen[JointHousehold] =
    for {
      parent <- genParent
      partner <- genParent
    } yield JointHousehold(parent, partner)

  implicit val genHousehold: Gen[Household] =
    Gen.oneOf(genSingle, genJoint)

  implicit def genOpt[A](implicit gen: Gen[A]): Gen[Option[A]] =
    Gen.oneOf(Gen.const(None), gen.map(Some.apply))

  implicit def arb[A](implicit gen: Gen[A]): Arbitrary[A] = Arbitrary(gen)

}
