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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{ChildcarePayFrequency, DisabilityBenefit}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.{DataGenerator, SpecBase}

class ChildrenCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Children Journey" when {
    "Save noOfChildren data " must {
      "remove relevant data in child journey when noOfChildren value is changed" in {
        val originalCacheMap: CacheMap = DataGenerator.sample

        val result = cascadeUpsert(NoOfChildrenId, 4, originalCacheMap)

        result.data mustBe Map(NoOfChildrenId.withValue(4))
      }

      "remove relevant data in child journey when noOfChildren value is changed from single child" in {
        val originalCacheMap: CacheMap = DataGenerator.sample

        val result = cascadeUpsert(NoOfChildrenId, 4, originalCacheMap)

        result.data mustBe Map(NoOfChildrenId.withValue(4))
      }
    }

    "Save childrenDisabilityBenefits data " must {
      "remove whichChildrenDisability and whichDisabilityBenefits data when childrenDisabilityBenefits is false" in {
        val originalCacheMap: CacheMap = DataGenerator.sample

        val result = cascadeUpsert(ChildrenDisabilityBenefitsId, false, originalCacheMap)

        result.getEntry(WhichDisabilityBenefitsId) mustBe None
        result.getEntry(WhichChildrenDisabilityId) mustBe None
      }

      "remove whichDisabilityBenefits data when childDisabilityBenefits is false" in {
        val originalCacheMap: CacheMap = DataGenerator.sample

        val result = cascadeUpsert(ChildDisabilityBenefitsId, false, originalCacheMap)

        result.getEntry(WhichDisabilityBenefitsId) mustBe None
      }
    }

    "Save whichChildrenDisability data " must {
      "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed" in {
        val originalCacheMap: CacheMap = DataGenerator.sample

        val result = cascadeUpsert(WhichChildrenDisabilityId, Set(0, 1), originalCacheMap)

        result.getEntry(WhichDisabilityBenefitsId) mustBe Some(
          Map(0 -> Set(DisabilityBenefit.DisabilityBenefits))
        )
      }

      "Save whichChildrenDisability data " must {
        "not remove anything if there is no object" in {
          val originalCacheMap = DataGenerator.sample.removed(WhichDisabilityBenefitsId)

          val result = cascadeUpsert(WhichChildrenDisabilityId, Set(0, 2), originalCacheMap)

          result.getEntry(WhichDisabilityBenefitsId) mustBe None
        }

        "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed for 5 children " in {
          val originalCacheMap = DataGenerator.sample
            .overwritten(
              WhichChildrenDisabilityId.withValue(Set(0, 1, 2, 4)),
              WhichDisabilityBenefitsId.withValue(
                Map(
                  0 -> Set(DisabilityBenefit.DisabilityBenefits),
                  1 -> Set(DisabilityBenefit.HigherDisabilityBenefits),
                  2 -> Set(DisabilityBenefit.DisabilityBenefits, DisabilityBenefit.HigherDisabilityBenefits),
                  4 -> Set(DisabilityBenefit.HigherDisabilityBenefits)
                )
              )
            )

          val result = cascadeUpsert(WhichChildrenDisabilityId, Set(0, 3), originalCacheMap)

          result.getEntry(WhichDisabilityBenefitsId) mustBe Some(
            Map(0 -> Set(DisabilityBenefit.DisabilityBenefits))
          )
        }
      }

      "Save registeredBlind data " must {
        "remove whichChildrenBlind data when registeredBlind is false" in {
          val originalCacheMap: CacheMap = DataGenerator.sample.overwritten(WhichChildrenBlindId.withValue(Set(0, 2)))

          val result = cascadeUpsert(RegisteredBlindId, false, originalCacheMap)
          result.getEntry(WhichChildrenBlindId) mustBe None
        }
      }

      "Save whoHasChildcareCosts data " must {
        "remove childcarePayFrequency and expectedChildcareCosts data accordingly when whoHasChildcareCosts is changed " in {
          val originalCacheMap = DataGenerator.sample
            .overwritten(
              WhoHasChildcareCostsId.withValue(Set(0, 1)),
              ChildcarePayFrequencyId.withValue(
                Map(
                  0 -> ChildcarePayFrequency.Monthly,
                  1 -> ChildcarePayFrequency.Weekly
                )
              ),
              ExpectedChildcareCostsId.withValue(
                Map(
                  0 -> 123,
                  1 -> 224
                )
              )
            )

          val result = cascadeUpsert(WhoHasChildcareCostsId, Set(0, 2), originalCacheMap)
          result.getEntry(ChildcarePayFrequencyId) mustBe Some(
            Map(0 -> ChildcarePayFrequency.Monthly)
          )
          result.getEntry(ExpectedChildcareCostsId) mustBe Some(Map(0 -> 123))
        }

        "remove childcarePayFrequency and expectedChildcareCosts data accordingly when whoHasChildcareCosts is changed for 5 children " in {
          val originalCacheMap = DataGenerator.sample.overwritten(
            WhoHasChildcareCostsId.withValue(Set(0, 1, 3, 4)),
            ChildcarePayFrequencyId.withValue(
              Map(
                0 -> ChildcarePayFrequency.Monthly,
                1 -> ChildcarePayFrequency.Weekly,
                3 -> ChildcarePayFrequency.Weekly,
                4 -> ChildcarePayFrequency.Weekly
              )
            ),
            ExpectedChildcareCostsId.withValue(
              Map(
                0 -> 123,
                1 -> 224,
                3 -> 500,
                4 -> 340
              )
            )
          )

          val result = cascadeUpsert(WhoHasChildcareCostsId, Set(0, 4), originalCacheMap)

          result.getEntry(ChildcarePayFrequencyId) mustBe Some(
            Map(0 -> ChildcarePayFrequency.Monthly, 4 -> ChildcarePayFrequency.Weekly)
          )
          result.getEntry(ExpectedChildcareCostsId) mustBe Some(
            Map(0 -> 123, 4 -> 340)
          )
        }
      }
    }
  }

}
