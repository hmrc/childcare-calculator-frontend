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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPension, HowMuchBothPayPensionPY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class PensionsCascadeUpsertSpec extends SpecBase with CascadeUpsertBase{

  "Paid Pension CY" when {
    "Save  YouPaidPensionCY data " must {
      "remove howMuchYouPayPension page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(YouPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(YouPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(YouPaidPensionCYId.toString, true, originalCacheMap)

        result.data mustBe Map(YouPaidPensionCYId.toString.toString -> JsBoolean(true),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }
    }

    "Save PartnerPaidPensionCY data " must {
      "remove howMuchPartnerPayPension page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(PartnerPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(PartnerPaidPensionCYId.toString, true, originalCacheMap)

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
          WhoPaysIntoPensionId.toString -> JsString(You)))

        val result = getCascadeUpsert(BothPaidPensionCYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothPaidPensionCYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoPaysIntoPensionId.toString -> JsString(You),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(BothPaidPensionCYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothPaidPensionCYId.toString.toString -> JsBoolean(true),
          WhoPaysIntoPensionId.toString -> JsString(You),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoPaysIntoPension data " must {
      "remove HowMuchPartnerPayPension and HowMuchBothPayPension page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(WhoPaysIntoPensionId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(You),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove HowMuchYouPayPension and HowMuchBothPayPension page data when user selects partner option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(WhoPaysIntoPensionId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(Partner),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove HowMuchPartnerPayPension and HowMuchYouPayPension page data when user selects both option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20"))))

        val result = getCascadeUpsert(WhoPaysIntoPensionId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString(Both),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20")))
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20"))))

        val result = getCascadeUpsert(WhoPaysIntoPensionId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoPaysIntoPensionId.toString -> JsString("invalidvalue"),
          HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension("20", "20")))
      }
    }

  }

  "Paid Pensions PY" when {
    "Save YouPaidPensionPY data " must {
      "remove howMuchBothPayPensionPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(YouPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(YouPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(YouPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(YouPaidPensionPYId.toString.toString -> JsBoolean(true),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save PartnerPaidPensionPY data " must {
      "remove howMuchPartnerPayPensionPY page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(PartnerPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(PartnerPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionPYId.toString.toString -> JsBoolean(true),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save BothPaidPensionPY data " must {
      "remove whoPaidIntoPensionPY, howMuchBothPayPensionPY, howMuchPartnerPayPensionPY and howMuchBothPayPensionPY pages data" +
        " when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          WhoPaidIntoPensionPYId.toString -> JsString(You)))

        val result = getCascadeUpsert(BothPaidPensionPYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothPaidPensionPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))))

        val result = getCascadeUpsert(BothPaidPensionPYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothPaidPensionPYId.toString.toString -> JsBoolean(true),
          WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

    }

    "Save WhoPaidIntoPensionPY data " must {
      "remove  howMuchPartnerPayPensionPY and howMuchBothPayPensionPY page data when user selects you option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val result = getCascadeUpsert(WhoPaidIntoPensionPYId.toString, You, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(You),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove howMuchPartnerPayPensionPY and howMuchBothPayPensionPY page data when user selects partner option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val result = getCascadeUpsert(WhoPaidIntoPensionPYId.toString, Partner, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(Partner),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)))
      }

      "remove howMuchBothPayPensionPY and howMuchPartnerPayPensionPY page data when user selects both option" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val result = getCascadeUpsert(WhoPaidIntoPensionPYId.toString, Both, originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString(Both),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20")))
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20"))))

        val result = getCascadeUpsert(WhoPaidIntoPensionPYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(WhoPaidIntoPensionPYId.toString -> JsString("invalidvalue"),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> Json.toJson(HowMuchBothPayPensionPY("20", "20")))
      }
    }

  }

}
