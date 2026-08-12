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

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Location, YouPartnerBoth}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class IncomeCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Parent Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = CacheMap.of(LocationId.withValue(Location.NorthernIreland))

        val result = cascadeUpsert(ParentPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.withValue(true),
          LocationId.withValue(Location.NorthernIreland)
        )
      }

      "save the data and remove PartnerEmploymentIncomeCY, BothPaidPensionCY, WhoPaysIntoPension  page data when user selects yes" in {
        val originalCacheMap = CacheMap.of(
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          PartnerEmploymentIncomeCYId.withValue(1200),
          BothPaidPensionCYId.withValue(true),
          ParentPaidWorkCYId.withValue(false),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.You)
        )

        val result = cascadeUpsert(ParentPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20))
        )
      }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = CacheMap.of(LocationId.withValue(Location.NorthernIreland))

        val result = cascadeUpsert(ParentPaidWorkCYId, false, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.withValue(false),
          LocationId.withValue(Location.NorthernIreland)
        )
      }

      "clear EmploymentIncomeCY, PartnerPaidPensionCY, HowMuchPartnerPayPension, HowMuchYouPayPensionId, HowMuchBothPayPensionId" +
        " page data when user change the selection from yes to no" in {
          val originalCacheMap = CacheMap.of(
            EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
            ParentPaidWorkCYId.withValue(true),
            HowMuchYouPayPensionId.withValue(2300),
            HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(23, 23))
          )

          val result = cascadeUpsert(ParentPaidWorkCYId, false, originalCacheMap)

          result.data mustBe Map(ParentPaidWorkCYId.withValue(false))
        }
    }
  }

  "Partner Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = CacheMap.of(LocationId.withValue(Location.NorthernIreland))

        val result = cascadeUpsert(PartnerPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.withValue(true),
          LocationId.withValue(Location.NorthernIreland)
        )
      }

      "save the data and remove ParentEmploymentIncomeCY, EmploymentIncomeCY, YouPaidPensionCYId page data when user changes" +
        "the selection from no to yes" in {
          val originalCacheMap = CacheMap.of(
            ParentEmploymentIncomeCYId.withValue(1200),
            YouPaidPensionCYId.withValue(true),
            PartnerPaidWorkCYId.withValue(false)
          )

          val result = cascadeUpsert(PartnerPaidWorkCYId, true, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.withValue(true))
        }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = CacheMap.of(LocationId.withValue(Location.NorthernIreland))

        val result = cascadeUpsert(PartnerPaidWorkCYId, false, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.withValue(false),
          LocationId.withValue(Location.NorthernIreland)
        )
      }

      "clear EmploymentIncomeCY,BothPaidPensionCY, WhoPaysIntoPension, HowMuchPartnerPayPension, HowMuchBothPayPension" +
        " page data when user changes the selection from yes to no " in {
          val originalCacheMap = CacheMap.of(
            EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.withValue(true),
            WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
            HowMuchPartnerPayPensionId.withValue(230),
            HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(230, 230)),
            PartnerPaidWorkCYId.withValue(true)
          )

          val result = cascadeUpsert(PartnerPaidWorkCYId, false, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.withValue(false))
        }
    }
  }

  "Other Income CY" when {
    "Save YourOtherIncomeThisYear data " must {
      "remove yourOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = CacheMap.of(YourOtherIncomeAmountCYId.withValue(20))

        val result = cascadeUpsert(YourOtherIncomeThisYearId, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.withValue(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(YourOtherIncomeAmountCYId.withValue(20))

        val result = cascadeUpsert(YourOtherIncomeThisYearId, true, originalCacheMap)

        result.data mustBe Map(
          YourOtherIncomeThisYearId.withValue(true),
          YourOtherIncomeAmountCYId.withValue(20)
        )
      }
    }

    "Save BothOtherIncomeThisYear data " must {
      "remove whoGetsOtherIncomeCY, yourOtherIncomeAmountCY, partnerOtherIncomeAmountCY and otherIncomeAmountCY pages data" +
        " when user selects no option" in {
          val originalCacheMap = CacheMap.of(
            YourOtherIncomeAmountCYId.withValue(20),
            PartnerOtherIncomeAmountCYId.withValue(20),
            OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
            WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.You)
          )

          val result = cascadeUpsert(BothOtherIncomeThisYearId, false, originalCacheMap)

          result.data mustBe Map(BothOtherIncomeThisYearId.withValue(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.You),
          YourOtherIncomeAmountCYId.withValue(20)
        )

        val result = cascadeUpsert(BothOtherIncomeThisYearId, true, originalCacheMap)

        result.data mustBe Map(
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.You),
          YourOtherIncomeAmountCYId.withValue(20)
        )
      }
    }

    "Save WhoGetsOtherIncomeCY data " must {
      "remove PartnerOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects you option" in {
        val originalCacheMap = CacheMap.of(
          YourOtherIncomeAmountCYId.withValue(20),
          PartnerOtherIncomeAmountCYId.withValue(20),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20))
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.You, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.You),
          YourOtherIncomeAmountCYId.withValue(20)
        )
      }

      "remove YourOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects partner option" in {
        val originalCacheMap = CacheMap.of(
          YourOtherIncomeAmountCYId.withValue(20),
          PartnerOtherIncomeAmountCYId.withValue(20),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20))
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.Partner, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Partner),
          PartnerOtherIncomeAmountCYId.withValue(20)
        )
      }

      "remove PartnerOtherIncomeAmountCY and YourOtherIncomeAmountCY page data when user selects both option" in {
        val originalCacheMap = CacheMap.of(
          YourOtherIncomeAmountCYId.withValue(20),
          PartnerOtherIncomeAmountCYId.withValue(20),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20))
        )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.Both, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20))
        )
      }
    }

  }

}
