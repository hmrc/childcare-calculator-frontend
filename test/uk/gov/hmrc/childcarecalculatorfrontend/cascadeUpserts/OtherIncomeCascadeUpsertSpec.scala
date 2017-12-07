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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{OtherIncomeAmountCY, OtherIncomeAmountPY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}
import uk.gov.hmrc.http.cache.client.CacheMap

class OtherIncomeCascadeUpsertSpec extends SpecBase with CascadeUpsertBase{

  "Other Income PY" when {
    "Save YourOtherIncomeLY data " must {
      "remove yourOtherIncomeAmountPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YourOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YourOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeLYId.toString.toString -> JsBoolean(true),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyOtherIncomeLY data " must {
      "remove partnerOtherIncomeAmountPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeLYId.toString.toString -> JsBoolean(true),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothOtherIncomeLY data " must {
      "remove whoOtherIncomePY, yourOtherIncomeAmountPY, partnerOtherIncomeAmountPY and otherIncomeAmountPY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          WhoOtherIncomePYId.toString -> JsString(You)))

        val result = cascadeUpsert(BothOtherIncomeLYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeLYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(BothOtherIncomeLYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeLYId.toString.toString -> JsBoolean(true),
          WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoOtherIncomePY data " must {
      "remove PartnerOtherIncomeAmountPY and OtherIncomeAmountPY page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(WhoOtherIncomePYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(You),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove YourOtherIncomeAmountPY and OtherIncomeAmountPY page data when user selects partner option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val result = cascadeUpsert(WhoOtherIncomePYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(Partner),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove PartnerOtherIncomeAmountPY and YourOtherIncomeAmountPY page data when user selects both option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val result = cascadeUpsert(WhoOtherIncomePYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString(Both),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20"))))

        val result = cascadeUpsert(WhoOtherIncomePYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoOtherIncomePYId.toString -> JsString("invalidvalue"),
          YourOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountPYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20", "20")))
      }
    }

  }

  "Other Income CY" when {
    "Save YourOtherIncomeThisYear data " must {
      "remove yourOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YourOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(YourOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerAnyOtherIncomeThisYear data " must {
      "remove partnerOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothOtherIncomeThisYear data " must {
      "remove whoGetsOtherIncomeCY, yourOtherIncomeAmountCY, partnerOtherIncomeAmountCY and otherIncomeAmountCY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          WhoGetsOtherIncomeCYId.toString -> JsString(You)))

        val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(BothOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoGetsOtherIncomeCY data " must {
      "remove PartnerOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(You),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove YourOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects partner option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY(20, 20))))

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(Partner),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove PartnerOtherIncomeAmountCY and YourOtherIncomeAmountCY page data when user selects both option" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY(20, 20))))

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString(Both),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY(20, 20)))
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY(20, 20))))

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoGetsOtherIncomeCYId.toString -> JsString("invalidvalue"),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY(20, 20)))
      }
    }

  }

}
