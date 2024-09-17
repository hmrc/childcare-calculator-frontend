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
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.Benefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._

class BenefitsSpec extends PlaySpec {
  "Benefits" must {
    "Return all benefits as false" when {
      "We have no income benefits" in {
        val mappedBenefits = Benefits.populateFromRawData(None)

        mappedBenefits mustBe None
      }
    }

    "Populate correctly from a Set of Strings" when {
      "We have income benefits" in {
        val rawBenefits = Some(Set(INCOMEBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.incomeBenefits mustBe true
      }

      "We have disability benefits" in {
        val rawBenefits = Some(Set(DISABILITYBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.disabilityBenefits mustBe true
      }

      "We have high rate disability benefits" in {
        val rawBenefits = Some(Set(HIGHRATEDISABILITYBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.highRateDisabilityBenefits mustBe true
      }

      "We have carers allowance benefits for non scottish users" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.carersAllowance mustBe true
      }

      "We have Carer's Allowance or Carer Support System benefits for scottish users" in {
        val rawBenefits = Some(Set(SCOTTISHCARERSALLOWANCE.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.scottishCarersAllowance mustBe true
      }

      "We have a known benefit and an unkonwn benefit for non scottish users" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString,"unknown benefit"))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.carersAllowance mustBe true
      }

      "We have a known benefit and an unkonwn benefit for scottish users" in {
        val rawBenefits = Some(Set(SCOTTISHCARERSALLOWANCE.toString,"unknown benefit"))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits)

        mappedBenefits.get.scottishCarersAllowance mustBe true
      }

      "We have all benefits for non scottish users" in {
        val rawBenefits = Some(Set(CARERSALLOWANCE.toString, HIGHRATEDISABILITYBENEFITS.toString, DISABILITYBENEFITS.toString, INCOMEBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits).get

        mappedBenefits.carersAllowance mustBe true
        mappedBenefits.highRateDisabilityBenefits mustBe true
        mappedBenefits.disabilityBenefits mustBe true
        mappedBenefits.incomeBenefits mustBe true
      }

      "We have all benefits for scottish users" in {
        val rawBenefits = Some(Set(SCOTTISHCARERSALLOWANCE.toString, HIGHRATEDISABILITYBENEFITS.toString, DISABILITYBENEFITS.toString, INCOMEBENEFITS.toString))
        val mappedBenefits = Benefits.populateFromRawData(rawBenefits).get

        mappedBenefits.scottishCarersAllowance mustBe true
        mappedBenefits.highRateDisabilityBenefits mustBe true
        mappedBenefits.disabilityBenefits mustBe true
        mappedBenefits.incomeBenefits mustBe true
      }
    }
  }
}
