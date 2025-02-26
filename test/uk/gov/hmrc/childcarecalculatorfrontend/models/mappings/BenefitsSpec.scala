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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits._
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.Benefits

class BenefitsSpec extends PlaySpec {

  "Benefits" must {

    "return empty Option" when {
      "provided with empty Option" in {
        Benefits.from(None) mustBe None
      }
    }

    "return all benefits as false" when {

      "income benefits Set is empty" in {
        val inputBenefits = Some(Set.empty[ParentsBenefits])

        Benefits.from(inputBenefits) mustBe Some(Benefits())
      }

      "income benefits Set contains NICreditsForIncapacity" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(NICreditsForIncapacityOrLimitedCapabilityForWork))

        Benefits.from(inputBenefits) mustBe Some(Benefits())}

      "income benefits Set contains CarersCredit" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(CarersCredit))

        Benefits.from(inputBenefits) mustBe Some(Benefits())}

      "income benefits Set contains NoneOfThese" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(NoneOfThese))

        Benefits.from(inputBenefits) mustBe Some(Benefits())}
    }

    "return benefits with only carersAllowance set to true" when {

      "income benefits Set contains CarersAllowance" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(CarersAllowance))

        Benefits.from(inputBenefits) mustBe Some(Benefits(carersAllowance = true))
      }

      "income benefits Set contains IncapacityBenefit" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(IncapacityBenefit))

        Benefits.from(inputBenefits) mustBe Some(Benefits(carersAllowance = true))
      }

      "income benefits Set contains SevereDisablement" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(SevereDisablementAllowance))

        Benefits.from(inputBenefits) mustBe Some(Benefits(carersAllowance = true))
      }

      "income benefits Set contains EmploymentAndSupportAllowance" in {
        val inputBenefits: Option[Set[ParentsBenefits]] = Some(Set(ContributionBasedEmploymentAndSupportAllowance))

        Benefits.from(inputBenefits) mustBe Some(Benefits(carersAllowance = true))
      }

      "income benefits Set contains all types of benefits" in {
        val inputBenefits: Option[Set[ParentsBenefits]] =
          Some(
            Set(
              CarersAllowance,
              IncapacityBenefit,
              SevereDisablementAllowance,
              ContributionBasedEmploymentAndSupportAllowance,
              NICreditsForIncapacityOrLimitedCapabilityForWork,
              CarersCredit,
            )
          )

        Benefits.from(inputBenefits) mustBe Some(Benefits(carersAllowance = true))
      }
    }
  }
}
