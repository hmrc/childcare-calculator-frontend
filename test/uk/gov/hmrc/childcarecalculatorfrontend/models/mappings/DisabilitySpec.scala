/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.DisabilityBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.Disability

class DisabilitySpec extends PlaySpec {
  "Disability" must {
    "Return no disabilities" when {
      "There is no disabilities" in {
         val mappedDisability = Disability.populateFromRawData(1,None,None)

         mappedDisability mustBe None
      }
    }

    "Map correctly from raw data" when {
      "Disability is true for first child" in {
         val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(0-> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
         val mappedDisability = Disability.populateFromRawData(0,rawWhichDisabilityBenefits)

         mappedDisability.get.disabled mustBe true
      }

      "Disability is true for second child" in {
        val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(0-> Set(DisabilityBenefits.DISABILITY_BENEFITS),
          1-> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
        val mappedDisability = Disability.populateFromRawData(1,rawWhichDisabilityBenefits)

        mappedDisability.get.disabled mustBe true
      }

      "First child has no disability" in {
        val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(1-> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
        val mappedDisability = Disability.populateFromRawData(0,rawWhichDisabilityBenefits)

        mappedDisability mustBe None
      }

      "Child is severely disabled" in {
        val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(0-> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS)))
        val mappedDisability = Disability.populateFromRawData(0,rawWhichDisabilityBenefits)

        mappedDisability.get.severelyDisabled mustBe true
      }

      "Child is blind and not severely disabled" in {
        val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(0-> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
        val rawWhichChildBlind : Option[Boolean] = Some(true)
        val mappedDisability = Disability.populateFromRawData(0,rawWhichDisabilityBenefits,rawWhichChildBlind).get

        mappedDisability.blind mustBe true
        mappedDisability.severelyDisabled mustBe false
      }

      "Child is disabled, severely disabled and blind" in {
        val rawWhichDisabilityBenefits : Option[Map[Int, Set[DisabilityBenefits.Value]]] = Some(Map(5-> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS,
          DisabilityBenefits.DISABILITY_BENEFITS)))
        val rawWhichChildBlind : Option[Boolean] = Some(true)
        val mappedDisability = Disability.populateFromRawData(5: Int,rawWhichDisabilityBenefits,rawWhichChildBlind).get

        mappedDisability.severelyDisabled mustBe true
        mappedDisability.blind mustBe true
        mappedDisability.disabled mustBe true
      }
    }
  }
}
