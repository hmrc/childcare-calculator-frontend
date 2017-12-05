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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.Benefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._

class BenefitsSpec extends PlaySpec {
  "Benefits" must {
    "Return all benefits as false" when {
      "We have no income benefits" in {
        val mappedBenefits = Benefits.populateFromRawData(None)

        mappedBenefits.carersAllowance mustBe false
        mappedBenefits.highRateDisabilityBenefits mustBe false
        mappedBenefits.disabilityBenefits mustBe false
        mappedBenefits.incomeBenefits mustBe false
      }
    }

    "Populate correctly from a Set of Strings" when {
      "We have income benefits" in {
        val rawBenefits = Some(Set(INCOMEBENEFITS.toString))
        val mappedBenefits: Benefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.incomeBenefits mustBe true
      }

      "We have disability benefits" in {
        val rawBenefits = Some(Set(DISABILITYBENEFITS.toString))
        val mappedBenefits: Benefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.disabilityBenefits mustBe true
      }

      "We have high rate disability benefits" in {
        val rawBenefits = Some(Set(HIGHRATEDISABILITYBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.highRateDisabilityBenefits mustBe true
      }

      "We have carers allowance benefits" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.carersAllowance mustBe true
      }

      "We have a known benefit and an unkonwn benefit" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString,"unknown benefit"))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.carersAllowance mustBe true
      }

      "We have all benefits" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString, HIGHRATEDISABILITYBENEFITS.toString, DISABILITYBENEFITS.toString, INCOMEBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.carersAllowance mustBe true
        mappedBenefits.highRateDisabilityBenefits mustBe true
        mappedBenefits.disabilityBenefits mustBe true
        mappedBenefits.incomeBenefits mustBe true
      }
    }
  }
}
