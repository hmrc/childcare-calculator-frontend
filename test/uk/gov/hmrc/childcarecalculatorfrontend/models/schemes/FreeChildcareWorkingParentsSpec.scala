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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, ParentsBenefit}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class FreeChildcareWorkingParentsSpec extends PlaySpec with Matchers with BeforeAndAfterEach {

  private val freeChildcareEligibilityCalculator = mock[FreeChildcareEligibilityCalculator]
  private val userAnswers: UserAnswers           = mock[UserAnswers]

  private val freeChildcareWorkingParents: FreeChildcareWorkingParents =
    new FreeChildcareWorkingParents(freeChildcareEligibilityCalculator)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(freeChildcareEligibilityCalculator)
    reset(userAnswers)
  }

  private val FreeChildcareEligibleBenefits: Set[ParentsBenefit] = Set(
    ParentsBenefit.CarersAllowance,
    ParentsBenefit.IncapacityBenefit,
    ParentsBenefit.SevereDisablementAllowance,
    ParentsBenefit.ContributionBasedEmploymentAndSupportAllowance,
    ParentsBenefit.NICreditsForIncapacityOrLimitedCapabilityForWork,
    ParentsBenefit.CarersCredit
  )

  "FreeChildcareWorkingParents on eligibility" when {

    "location is empty" must {
      "return NotDetermined" in {
        when(userAnswers.location).thenReturn(None)

        freeChildcareWorkingParents.eligibility(userAnswers) mustBe Eligibility.NotDetermined
      }
    }

    Seq(Location.Scotland, Location.Wales, Location.NorthernIreland).foreach { location =>
      s"location is ${location.toString}" must {

        "NOT call FreeChildcareEligibilityCalculator" in {
          when(userAnswers.location).thenReturn(Some(location))

          freeChildcareWorkingParents.eligibility(userAnswers)

          verifyNoInteractions(freeChildcareEligibilityCalculator)
        }

        "return NotEligible" in {
          when(userAnswers.location).thenReturn(Some(location))

          freeChildcareWorkingParents.eligibility(userAnswers) mustBe Eligibility.NotEligible
        }
      }
    }

    "location is England" when {

      "user does NOT have children in any of the qualifying age groups" must {

        def initMocks(): Unit = {
          when(userAnswers.location).thenReturn(Some(Location.England))
          when(userAnswers.isChildAgedNineTo23Months).thenReturn(Some(false))
          when(userAnswers.isChildAgedTwo).thenReturn(Some(false))
          when(userAnswers.isChildAgedThreeOrFour).thenReturn(Some(false))
        }

        "NOT call FreeChildcareEligibilityCalculator" in {
          initMocks()

          freeChildcareWorkingParents.eligibility(userAnswers)

          verifyNoInteractions(freeChildcareEligibilityCalculator)
        }

        "return NotEligible" in {
          initMocks()

          freeChildcareWorkingParents.eligibility(userAnswers) mustBe Eligibility.NotEligible
        }
      }

      "user has a 9 to 23 months old child" must {

        def initMocks(): Unit = {
          when(userAnswers.location).thenReturn(Some(Location.England))
          when(userAnswers.isChildAgedNineTo23Months).thenReturn(Some(true))
          when(userAnswers.isChildAgedTwo).thenReturn(Some(false))
          when(userAnswers.isChildAgedThreeOrFour).thenReturn(Some(false))
        }

        "call FreeChildcareEligibilityCalculator, providing correct set of eligible benefits" in {
          initMocks()
          when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligibility.Eligible)

          freeChildcareWorkingParents.eligibility(userAnswers)

          verify(freeChildcareEligibilityCalculator).calculateEligibility(
            eqTo(userAnswers),
            eqTo(FreeChildcareEligibleBenefits)
          )
        }

        Seq(Eligibility.Eligible, Eligibility.NotEligible, Eligibility.NotDetermined).foreach { calcResult =>
          s"return value returned from FreeChildcareEligibilityCalculator when it is ${calcResult.toString}" in {
            initMocks()
            when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(calcResult)

            freeChildcareWorkingParents.eligibility(userAnswers) mustBe calcResult
          }
        }
      }

      "user has a 2 years old child" must {

        def initMocks(): Unit = {
          when(userAnswers.location).thenReturn(Some(Location.England))
          when(userAnswers.isChildAgedNineTo23Months).thenReturn(Some(false))
          when(userAnswers.isChildAgedTwo).thenReturn(Some(true))
          when(userAnswers.isChildAgedThreeOrFour).thenReturn(Some(false))
        }

        "call FreeChildcareEligibilityCalculator, providing correct set of eligible benefits" in {
          initMocks()
          when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligibility.Eligible)

          freeChildcareWorkingParents.eligibility(userAnswers)

          verify(freeChildcareEligibilityCalculator).calculateEligibility(
            eqTo(userAnswers),
            eqTo(FreeChildcareEligibleBenefits)
          )
        }

        Seq(Eligibility.Eligible, Eligibility.NotEligible, Eligibility.NotDetermined).foreach { calcResult =>
          s"return value returned from FreeChildcareEligibilityCalculator when it is ${calcResult.toString}" in {
            initMocks()
            when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(calcResult)

            freeChildcareWorkingParents.eligibility(userAnswers) mustBe calcResult
          }
        }
      }

      "user has a 3 or 4 years old child" must {

        def initMocks(): Unit = {
          when(userAnswers.location).thenReturn(Some(Location.England))
          when(userAnswers.isChildAgedNineTo23Months).thenReturn(Some(false))
          when(userAnswers.isChildAgedTwo).thenReturn(Some(false))
          when(userAnswers.isChildAgedThreeOrFour).thenReturn(Some(true))
        }

        "call FreeChildcareEligibilityCalculator, providing correct set of eligible benefits" in {
          initMocks()
          when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligibility.Eligible)

          freeChildcareWorkingParents.eligibility(userAnswers)

          verify(freeChildcareEligibilityCalculator).calculateEligibility(
            eqTo(userAnswers),
            eqTo(FreeChildcareEligibleBenefits)
          )
        }

        Seq(Eligibility.Eligible, Eligibility.NotEligible, Eligibility.NotDetermined).foreach { calcResult =>
          s"return value returned from FreeChildcareEligibilityCalculator when it is ${calcResult.toString}" in {
            initMocks()
            when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(calcResult)

            freeChildcareWorkingParents.eligibility(userAnswers) mustBe calcResult
          }
        }
      }

      "user has children in several qualifying age groups" must {

        def initMocks(): Unit = {
          when(userAnswers.location).thenReturn(Some(Location.England))
          when(userAnswers.isChildAgedNineTo23Months).thenReturn(Some(true))
          when(userAnswers.isChildAgedTwo).thenReturn(Some(false))
          when(userAnswers.isChildAgedThreeOrFour).thenReturn(Some(true))
        }

        "call FreeChildcareEligibilityCalculator, providing correct set of eligible benefits" in {
          initMocks()
          when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligibility.Eligible)

          freeChildcareWorkingParents.eligibility(userAnswers)

          verify(freeChildcareEligibilityCalculator).calculateEligibility(
            eqTo(userAnswers),
            eqTo(FreeChildcareEligibleBenefits)
          )
        }

        Seq(Eligibility.Eligible, Eligibility.NotEligible, Eligibility.NotDetermined).foreach { calcResult =>
          s"return value returned from FreeChildcareEligibilityCalculator when it is ${calcResult.toString}" in {
            initMocks()
            when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(calcResult)

            freeChildcareWorkingParents.eligibility(userAnswers) mustBe calcResult
          }
        }
      }
    }
  }

}
