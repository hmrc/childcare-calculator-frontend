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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, ParentsBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers}

class TaxFreeChildcareSpec extends PlaySpec with Matchers with BeforeAndAfterEach {

  private val freeChildcareEligibilityCalculator = mock[FreeChildcareEligibilityCalculator]

  private def taxFreeChildcare: TaxFreeChildcare = new TaxFreeChildcare(freeChildcareEligibilityCalculator)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(freeChildcareEligibilityCalculator)
  }

  private val userAnswers: UserAnswers = new UserAnswers(CacheMap("test-id", Map.empty))

  "TaxFreeChildcare on eligibility" must {

    "always call FreeChildcareEligibilityCalculator, providing correct set of eligible benefits" in {
      when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligible)

      taxFreeChildcare.eligibility(userAnswers)

      val expectedEligibleBenefits: Set[ParentsBenefits] = Set(
        CarersAllowance,
        IncapacityBenefit,
        SevereDisablementAllowance,
        ContributionBasedEmploymentAndSupportAllowance
      )
      verify(freeChildcareEligibilityCalculator).calculateEligibility(eqTo(userAnswers), eqTo(expectedEligibleBenefits))
    }

    "return the value returned by FreeChildcareEligibilityCalculator" in {
      when(freeChildcareEligibilityCalculator.calculateEligibility(any(), any())).thenReturn(Eligible)

      taxFreeChildcare.eligibility(userAnswers) mustBe Eligible
    }
  }

}
