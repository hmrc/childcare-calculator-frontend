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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPension}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{both, partner, you}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class PensionsCascadeUpsertSpec extends SpecBase with CascadeUpsertBase{

  "Paid Pension CY" when {
    "Save  YouPaidPensionCY data " must {
      "remove howMuchYouPayPension page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YouPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(YouPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YouPaidPensionCYId.toString, true, originalCacheMap)

        result.data mustBe Map(YouPaidPensionCYId.toString.toString -> JsBoolean(true),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }
    }

    "Save PartnerPaidPensionCY data " must {
      "remove howMuchPartnerPayPension page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerPaidPensionCYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionCYId.toString.toString -> JsBoolean(true),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }
    }

    "Save BothPaidPensionCY data " must {
      "remove WhoPaysIntoPension, howMuchYouPayPension, howMuchPartnerPayPension and howMuchBothPayPension pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
          WhoPaysIntoPensionId.toString -> JsString(you)))

        val result = cascadeUpsert(BothPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoPaysIntoPensionId.toString -> JsString(you),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(BothPaidPensionCYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothPaidPensionCYId.toString.toString -> JsBoolean(true),
          WhoPaysIntoPensionId.toString -> JsString(you),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoPaysIntoPension data " must {
      "remove HowMuchPartnerPayPension and HowMuchBothPayPension page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(WhoPaysIntoPensionId.toString, you, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(you),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove HowMuchYouPayPension and HowMuchBothPayPension page data when user selects partner option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(WhoPaysIntoPensionId.toString, partner, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(partner),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove HowMuchPartnerPayPension and HowMuchYouPayPension page data when user selects both option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension(20, 20))))

        val result = cascadeUpsert(WhoPaysIntoPensionId.toString, both, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(both),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension(20, 20)))
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension(20, 20))))

        val result = cascadeUpsert(WhoPaysIntoPensionId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString("invalidvalue"),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension(20, 20)))
      }
    }

  }

}
