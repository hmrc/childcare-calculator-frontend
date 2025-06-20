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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{both, partner, you}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class IncomeCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Parent Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = new CacheMap("id", Map(LocationId.toString -> JsString("northern-ireland")))

        val result = cascadeUpsert(ParentPaidWorkCYId.toString, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.toString -> JsBoolean(true),
          LocationId.toString         -> JsString("northern-ireland")
        )
      }

      "save the data and remove PartnerEmploymentIncomeCY, BothPaidPensionCY, WhoPaysIntoPension  page data when user selects yes" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            EmploymentIncomeCYId.toString        -> Json.toJson(EmploymentIncomeCY(20, 20)),
            PartnerEmploymentIncomeCYId.toString -> JsNumber(1200),
            BothPaidPensionCYId.toString         -> JsBoolean(true),
            ParentPaidWorkCYId.toString          -> JsBoolean(false),
            WhoPaysIntoPensionId.toString        -> JsString(you)
          )
        )

        val result = cascadeUpsert(ParentPaidWorkCYId.toString, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.toString   -> JsBoolean(true),
          EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY(20, 20))
        )
      }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = new CacheMap("id", Map(LocationId.toString -> JsString("northern-ireland")))

        val result = cascadeUpsert(ParentPaidWorkCYId.toString, false, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.toString -> JsBoolean(false),
          LocationId.toString         -> JsString("northern-ireland")
        )
      }

      "clear EmploymentIncomeCY, PartnerPaidPensionCY, HowMuchPartnerPayPension, HowMuchYouPayPensionId, HowMuchBothPayPensionId" +
        " page data when user change the selection from yes to no" in {
          val originalCacheMap = new CacheMap(
            "id",
            Map(
              EmploymentIncomeCYId.toString    -> Json.toJson(EmploymentIncomeCY(20, 20)),
              ParentPaidWorkCYId.toString      -> JsBoolean(true),
              HowMuchYouPayPensionId.toString  -> JsNumber(2300),
              HowMuchBothPayPensionId.toString -> Json.toJson(HowMuchBothPayPension(23, 23))
            )
          )

          val result = cascadeUpsert(ParentPaidWorkCYId.toString, false, originalCacheMap)

          result.data mustBe Map(ParentPaidWorkCYId.toString -> JsBoolean(false))
        }
    }
  }

  "Partner Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = new CacheMap("id", Map(LocationId.toString -> JsString("northern-ireland")))

        val result = cascadeUpsert(PartnerPaidWorkCYId.toString, true, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.toString -> JsBoolean(true),
          LocationId.toString          -> JsString("northern-ireland")
        )
      }

      "save the data and remove ParentEmploymentIncomeCY, EmploymentIncomeCY, YouPaidPensionCYId page data when user changes" +
        "the selection from no to yes" in {
          val originalCacheMap = new CacheMap(
            "id",
            Map(
              ParentEmploymentIncomeCYId.toString -> JsNumber(1200),
              YouPaidPensionCYId.toString         -> JsBoolean(true),
              PartnerPaidWorkCYId.toString        -> JsBoolean(false)
            )
          )

          val result = cascadeUpsert(PartnerPaidWorkCYId.toString, true, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.toString -> JsBoolean(true))
        }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = new CacheMap("id", Map(LocationId.toString -> JsString("northern-ireland")))

        val result = cascadeUpsert(PartnerPaidWorkCYId.toString, false, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.toString -> JsBoolean(false),
          LocationId.toString          -> JsString("northern-ireland")
        )
      }

      "clear EmploymentIncomeCY,BothPaidPensionCY, WhoPaysIntoPension, HowMuchPartnerPayPension, HowMuchBothPayPension" +
        " page data when user changes the selection from yes to no " in {
          val originalCacheMap = new CacheMap(
            "id",
            Map(
              EmploymentIncomeCYId.toString       -> Json.toJson(EmploymentIncomeCY(20, 20)),
              BothPaidPensionCYId.toString        -> JsBoolean(true),
              WhoPaysIntoPensionId.toString       -> JsString(both),
              HowMuchPartnerPayPensionId.toString -> JsNumber(230),
              HowMuchBothPayPensionId.toString    -> Json.toJson(HowMuchBothPayPension(230, 230)),
              PartnerPaidWorkCYId.toString        -> JsBoolean(true)
            )
          )

          val result = cascadeUpsert(PartnerPaidWorkCYId.toString, false, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.toString -> JsBoolean(false))
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

        result.data mustBe Map(
          YourOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          YourOtherIncomeAmountCYId.toString          -> JsNumber(BigDecimal(20))
        )
      }
    }

    "Save PartnerAnyOtherIncomeThisYear data " must {
      "remove partnerOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap =
          new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, false, originalCacheMap)

        result.data mustBe Map(PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap =
          new CacheMap("id", Map(PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))))

        val result = cascadeUpsert(PartnerAnyOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(
          PartnerAnyOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          PartnerOtherIncomeAmountCYId.toString             -> JsNumber(BigDecimal(20))
        )
      }
    }

    "Save BothOtherIncomeThisYear data " must {
      "remove whoGetsOtherIncomeCY, yourOtherIncomeAmountCY, partnerOtherIncomeAmountCY and otherIncomeAmountCY pages data" +
        " when user selects no option" in {
          val originalCacheMap = new CacheMap(
            "id",
            Map(
              YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
              PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
              OtherIncomeAmountCYId.toString        -> JsNumber(BigDecimal(20)),
              WhoGetsOtherIncomeCYId.toString       -> JsString(you)
            )
          )

          val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, false, originalCacheMap)

          result.data mustBe Map(BothOtherIncomeThisYearId.toString -> JsBoolean(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            WhoGetsOtherIncomeCYId.toString    -> JsString(you),
            YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))
          )
        )

        val result = cascadeUpsert(BothOtherIncomeThisYearId.toString, true, originalCacheMap)

        result.data mustBe Map(
          BothOtherIncomeThisYearId.toString.toString -> JsBoolean(true),
          WhoGetsOtherIncomeCYId.toString             -> JsString(you),
          YourOtherIncomeAmountCYId.toString          -> JsNumber(BigDecimal(20))
        )
      }
    }

    "Save WhoGetsOtherIncomeCY data " must {
      "remove PartnerOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects you option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
            PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
            OtherIncomeAmountCYId.toString        -> JsNumber(BigDecimal(20))
          )
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, you, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.toString    -> JsString(you),
          YourOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))
        )
      }

      "remove YourOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects partner option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
            PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
            OtherIncomeAmountCYId.toString        -> Json.toJson(OtherIncomeAmountCY(20, 20))
          )
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, partner, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.toString       -> JsString(partner),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20))
        )
      }

      "remove PartnerOtherIncomeAmountCY and YourOtherIncomeAmountCY page data when user selects both option" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
            PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
            OtherIncomeAmountCYId.toString        -> Json.toJson(OtherIncomeAmountCY(20, 20))
          )
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, both, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.toString -> JsString(both),
          OtherIncomeAmountCYId.toString  -> Json.toJson(OtherIncomeAmountCY(20, 20))
        )
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
            PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
            OtherIncomeAmountCYId.toString        -> Json.toJson(OtherIncomeAmountCY(20, 20))
          )
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.toString       -> JsString("invalidvalue"),
          YourOtherIncomeAmountCYId.toString    -> JsNumber(BigDecimal(20)),
          PartnerOtherIncomeAmountCYId.toString -> JsNumber(BigDecimal(20)),
          OtherIncomeAmountCYId.toString        -> Json.toJson(OtherIncomeAmountCY(20, 20))
        )
      }
    }

  }

}
