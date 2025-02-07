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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{SelfEmployedOrApprenticeOrNeitherEnum, YesNoNotYetEnum, YesNoUnsureEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}

import java.time.LocalDate

class MinimumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {
  lazy val no: String = YesNoNotYetEnum.NO.toString
  lazy val No: String = YesNoUnsureEnum.NO.toString

  "MinimumHoursCascadeUpsert" when {

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(LocationId.toString, "northern-ireland", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("northern-ireland"))
      }
    }

    "saving a location of wales" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(LocationId.toString, "wales", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("wales"))
      }
    }

    "saving a location of scotland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(LocationId.toString, "scotland", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("scotland")
        )
      }
    }

    "saving a location of england" must {
      "save the location and remove existing childAgedTwo and childAgedThreeOrFour answers" in {
        val originalCacheMap = new CacheMap("id", Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true)
        ))

        val result = cascadeUpsert(LocationId.toString, "england", originalCacheMap)
        result.data mustBe Map(
          LocationId.toString -> JsString("england")
        )
      }
    }

    "saving childcareCosts with an england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears")))
        ))

        val result = cascadeUpsert(ChildcareCostsId.toString, no, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.toString -> JsString(no),
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears")))
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.toString -> JsString(yes),
          ApprovedProviderId.toString -> JsString(yes),
          DoYouLiveWithPartnerId.toString -> JsBoolean(false),
          WhoIsInPaidEmploymentId.toString -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
          PartnerChildcareVouchersId.toString -> JsString("yes"),
          YourPartnersAgeId.toString -> JsString("under18"),
          PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true),
          BothStatutoryPayId.toString -> JsBoolean(true),
          WhoGotStatutoryPayId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString),
          PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          PartnerStatutoryWeeksId.toString -> JsNumber(200),
          PartnerStatutoryPayBeforeTaxId.toString -> JsString("true"),
          PartnerStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))
        ))

        val result = cascadeUpsert(ChildcareCostsId.toString, no, originalCacheMap)
        result.data mustBe Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.toString -> JsString(no)
        )
      }
    }

    "saving childcareCosts with a non england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = new CacheMap("id", Map(LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(ChildcareCostsId.toString, no, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.toString -> JsString(no),
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ChildcareCostsId.toString -> JsString(yes),
          ApprovedProviderId.toString -> JsString(yes),
          DoYouLiveWithPartnerId.toString -> JsBoolean(false),
          WhoIsInPaidEmploymentId.toString -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
          PartnerChildcareVouchersId.toString -> JsString("yes"),
          YourPartnersAgeId.toString -> JsString("under18"),
          PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true),
          BothStatutoryPayId.toString -> JsBoolean(true),
          WhoGotStatutoryPayId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString),
          PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          PartnerStatutoryWeeksId.toString -> JsNumber(200),
          PartnerStatutoryPayBeforeTaxId.toString -> JsString("true"),
          PartnerStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))
        ))

        val result = cascadeUpsert(ChildcareCostsId.toString, no, originalCacheMap)
        result.data mustBe Map(
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ChildcareCostsId.toString -> JsString(no)
        )
      }
    }

    "saving ApprovedProvider with an england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.toString -> JsString(yes)
        ))

        val result = cascadeUpsert(ApprovedProviderId.toString, No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.toString -> JsString(No),
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.toString -> JsString(yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.toString -> JsString(yes),
          ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.YES.toString),
          DoYouLiveWithPartnerId.toString -> JsBoolean(false),
          WhoIsInPaidEmploymentId.toString -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
          PartnerChildcareVouchersId.toString -> JsString("yes"),
          YourPartnersAgeId.toString -> JsString("under18"),
          PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true),
          BothStatutoryPayId.toString -> JsBoolean(true),
          WhoGotStatutoryPayId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString),
          PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          PartnerStatutoryWeeksId.toString -> JsNumber(200),
          PartnerStatutoryPayBeforeTaxId.toString -> JsString("true"),
          PartnerStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))
        ))

        val result = cascadeUpsert(ApprovedProviderId.toString, No, originalCacheMap)
        result.data mustBe Map(
          LocationId.toString -> JsString("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ApprovedProviderId.toString -> JsString(No),
          ChildcareCostsId.toString -> JsString(yes)
        )
      }
    }

    "saving ApprovedProvider with a non england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ChildcareCostsId.toString -> JsString(yes)
        ))

        val result = cascadeUpsert(ApprovedProviderId.toString, No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.toString -> JsString(No),
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ChildcareCostsId.toString -> JsString(yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = new CacheMap("id", Map(
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ChildcareCostsId.toString -> JsString(yes),
          ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.YES.toString),
          DoYouLiveWithPartnerId.toString -> JsBoolean(false),
          WhoIsInPaidEmploymentId.toString -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
          PartnerChildcareVouchersId.toString -> JsString("yes"),
          YourPartnersAgeId.toString -> JsString("under18"),
          PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerMaximumEarningsId.toString -> JsBoolean(true),
          BothStatutoryPayId.toString -> JsBoolean(true),
          WhoGotStatutoryPayId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString),
          PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          PartnerStatutoryWeeksId.toString -> JsNumber(200),
          PartnerStatutoryPayBeforeTaxId.toString -> JsString("true"),
          PartnerStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))
        ))

        val result = cascadeUpsert(ApprovedProviderId.toString, No, originalCacheMap)
        result.data mustBe Map(
          LocationId.toString -> JsString("scotland"),
          ChildAgedTwoId.toString -> JsBoolean(false),
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          ApprovedProviderId.toString -> JsString(No),
          ChildcareCostsId.toString -> JsString(yes)
        )
      }
    }
  }

}
