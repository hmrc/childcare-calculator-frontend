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

import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{both, partner, you}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class BenefitsCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Benefits CY" when {
    "Save YouAnyTheseBenefitsCY data " must {
      "remove YouBenefitsIncomeCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YouAnyTheseBenefitsIdCY.toString, false, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsIdCY.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YouAnyTheseBenefitsIdCY.toString, true, originalCacheMap)

        result.data mustBe Map(
          YouAnyTheseBenefitsIdCY.toString.toString -> JsBoolean(true),
          YouBenefitsIncomeCYId.toString            -> JsNumber(BigDecimal(20))
        )
      }

    }

    "Save BothAnyTheseBenefitsCY data " must {
      "remove whosHadBenefits, youBenefitsIncomeCY, partnerBenefitsIncomeCY and BenefitsIncomeCY pages data" +
        " when user selects no option" in {
          val originalCacheMap = new CacheMap(
            "id",
            Map(
              YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(20)),
              PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
              BenefitsIncomeCYId.toString        -> JsNumber(BigDecimal(20)),
              WhosHadBenefitsId.toString         -> JsString(you)
            )
          )

          val result = cascadeUpsert(BothAnyTheseBenefitsCYId.toString, false, originalCacheMap)

          result.data mustBe Map(BothAnyTheseBenefitsCYId.toString -> JsBoolean(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(WhosHadBenefitsId.toString -> JsString(you), YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)))
        )

        val result = cascadeUpsert(BothAnyTheseBenefitsCYId.toString, true, originalCacheMap)

        result.data mustBe Map(
          BothAnyTheseBenefitsCYId.toString.toString -> JsBoolean(true),
          WhosHadBenefitsId.toString                 -> JsString(you),
          YouBenefitsIncomeCYId.toString             -> JsNumber(BigDecimal(20))
        )
      }

    }

    "Save WhosHadBenefits CY data " must {
      "remove PartnerBenefitsIncomeCY and BenefitsIncomeCY page data when user selects you option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(10)),
            PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
            BenefitsIncomeCYId.toString        -> Json.toJson(BenefitsIncomeCY(10, 20))
          )
        )

        val result = cascadeUpsert(WhosHadBenefitsId.toString, you, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.toString     -> JsString(you),
          YouBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(10))
        )
      }

      "remove youBenefitsIncomeCY and BenefitsIncomeCY page data when user selects partner option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(10)),
            PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
            BenefitsIncomeCYId.toString        -> Json.toJson(BenefitsIncomeCY(10, 20))
          )
        )

        val result = cascadeUpsert(WhosHadBenefitsId.toString, partner, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.toString         -> JsString(partner),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20))
        )
      }

      "remove PartnerBenefitsIncomeCY and youBenefitsIncomeCY page data when user selects both option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(10)),
            PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
            BenefitsIncomeCYId.toString        -> Json.toJson(BenefitsIncomeCY(10, 20))
          )
        )

        val result = cascadeUpsert(WhosHadBenefitsId.toString, both, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.toString  -> JsString(both),
          BenefitsIncomeCYId.toString -> Json.toJson(BenefitsIncomeCY(10, 20))
        )
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(10)),
            PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
            BenefitsIncomeCYId.toString        -> Json.toJson(BenefitsIncomeCY(10, 20))
          )
        )

        val result = cascadeUpsert(WhosHadBenefitsId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.toString         -> JsString("invalidvalue"),
          YouBenefitsIncomeCYId.toString     -> JsNumber(BigDecimal(10)),
          PartnerBenefitsIncomeCYId.toString -> JsNumber(BigDecimal(20)),
          BenefitsIncomeCYId.toString        -> Json.toJson(BenefitsIncomeCY(10, 20))
        )
      }
    }
  }

}
