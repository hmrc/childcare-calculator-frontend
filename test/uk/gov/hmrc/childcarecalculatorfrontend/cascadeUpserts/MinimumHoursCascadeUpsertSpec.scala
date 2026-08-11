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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup.ThreeYears
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class MinimumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "MinimumHoursCascadeUpsert" when {

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.withValue(true))

        val result = cascadeUpsert(LocationId, Location.NorthernIreland, originalCacheMap)
        result.data mustBe Map(LocationId.withValue(Location.NorthernIreland))
      }
    }

    "saving a location of wales" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.withValue(true))

        val result = cascadeUpsert(LocationId, Location.Wales, originalCacheMap)
        result.data mustBe Map(LocationId.withValue(Location.Wales))
      }
    }

    "saving a location of scotland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.withValue(true))

        val result = cascadeUpsert(LocationId, Location.Scotland, originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.withValue(true),
          LocationId.withValue(Location.Scotland)
        )
      }
    }

    "saving a location of england" must {
      "save the location and remove existing childAgedTwo and childAgedThreeOrFour answers" in {
        val originalCacheMap = CacheMap.of(
          ChildAgedTwoId.withValue(true),
          ChildAgedThreeOrFourId.withValue(true)
        )

        val result = cascadeUpsert(LocationId, Location.England, originalCacheMap)
        result.data mustBe Map(
          LocationId.withValue(Location.England)
        )
      }
    }

    "saving childcareCosts with an england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ThreeYears))
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.withValue(YesNoNotYet.No),
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ThreeYears))
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ThreeYears)),
          ChildcareCostsId.withValue(YesNoNotYet.Yes),
          ApprovedProviderId.withValue(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.withValue(false),
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerChildcareVouchersId.withValue(true),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(true),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ThreeYears)),
          ChildcareCostsId.withValue(YesNoNotYet.No)
        )
      }
    }

    "saving childcareCosts with a non england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.withValue(YesNoNotYet.No),
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ChildcareCostsId.withValue(YesNoNotYet.Yes),
          ApprovedProviderId.withValue(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.withValue(false),
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerChildcareVouchersId.withValue(true),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(true),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ChildcareCostsId.withValue(YesNoNotYet.No)
        )
      }
    }

    "saving ApprovedProvider with an england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.withValue(YesNoNotSure.No),
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.withValue(YesNoNotYet.Yes),
          ApprovedProviderId.withValue(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.withValue(false),
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerChildcareVouchersId.withValue(true),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(true),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.withValue(Location.England),
          ChildrenAgeGroupsId.withValue(Set(ChildAgeGroup.ThreeYears)),
          ApprovedProviderId.withValue(YesNoNotSure.No),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      }
    }

    "saving ApprovedProvider with a non england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.withValue(YesNoNotSure.No),
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ChildcareCostsId.withValue(YesNoNotYet.Yes),
          ApprovedProviderId.withValue(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.withValue(false),
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerChildcareVouchersId.withValue(true),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(true),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.withValue(Location.Scotland),
          ChildAgedTwoId.withValue(false),
          ChildAgedThreeOrFourId.withValue(true),
          ApprovedProviderId.withValue(YesNoNotSure.No),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      }
    }
  }

}
