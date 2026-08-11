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
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class MaximumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "saving the doYouLiveWithPartner" when {

    "doYouLiveWithPartner is false" must {

      "remove data related to both parents in employment" in {
        val originalCacheMap = CacheMap.of(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Both),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          WhatIsYourTaxCodeId.withValue("1100L"),
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          PartnerChildcareVouchersId.withValue(true),
          YourChildcareVouchersId.withValue(true),
          DoYouGetAnyBenefitsId.withValue(Set(ParentsBenefit.IncapacityBenefit)),
          DoesYourPartnerGetAnyBenefitsId.withValue(Set(ParentsBenefit.IncapacityBenefit)),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(true),
          YourMinimumEarningsId.withValue(false),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true)
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId, false, originalCacheMap)
        result.data mustBe Map(
          DoYouLiveWithPartnerId.withValue(false),
          WhatIsYourTaxCodeId.withValue("1100L"),
          YourChildcareVouchersId.withValue(true),
          YourAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(false),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed)
        )
      }
    }

    "doYouLiveWithPartner is true" must {
      "remove an existing paid employment and who is in paid employment" in {
        val originalCacheMap = CacheMap.of(
          AreYouInPaidWorkId.withValue(true),
          DoYouGetAnyBenefitsId.withValue(Set.empty)
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.withValue(true))
      }
    }
  }

  "saving the areYouInPaidWork" must {
    "remove all the relevant data for you pages when are you in paid work is no" in {
      val originalCacheMap = CacheMap.of(
        WhatIsYourTaxCodeId.withValue("1100L"),
        YourChildcareVouchersId.withValue(true),
        DoYouGetAnyBenefitsId.withValue(Set.empty),
        YourAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(true),
        YourMaximumEarningsId.withValue(true),
        UniversalCreditId.withValue(true),
        PartnerPaidWorkCYId.withValue(true),
        ParentEmploymentIncomeCYId.withValue(20),
        YouPaidPensionCYId.withValue(true),
        HowMuchYouPayPensionId.withValue(20),
        YourOtherIncomeThisYearId.withValue(true),
        YouAnyTheseBenefitsCYId.withValue(true),
        YouBenefitsIncomeCYId.withValue(20)
      )

      val result = cascadeUpsert(AreYouInPaidWorkId, false, originalCacheMap)
      result.data mustBe Map(AreYouInPaidWorkId.withValue(false))
    }
  }

  "saving the whoIsInPaidEmployment" must {

    "Do data clearance for Neither" in {
      val originalCacheMap1 = CacheMap.of(
        WhatIsYourTaxCodeId.withValue("1100L"),
        WhatIsYourPartnersTaxCodeId.withValue("1100L"),
        WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.Both),
        YourChildcareVouchersId.withValue(true),
        PartnerChildcareVouchersId.withValue(true),
        DoYouGetAnyBenefitsId.withValue(Set.empty),
        YourAgeId.withValue(Age.UnderEighteen),
        YourPartnersAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(true),
        PartnerMinimumEarningsId.withValue(true),
        EitherOfYouMaximumEarningsId.withValue(true),
        UniversalCreditId.withValue(true),
        EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
        BothPaidPensionCYId.withValue(true),
        WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
        HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(10, 10)),
        BothOtherIncomeThisYearId.withValue(true),
        WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
        OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
        BothAnyTheseBenefitsCYId.withValue(true),
        WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
        BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
      )

      // Partner In Paid Employment
      val originalCacheMap2 = CacheMap.of(
        WhatIsYourPartnersTaxCodeId.withValue("1100L"),
        PartnerChildcareVouchersId.withValue(true),
        DoYouGetAnyBenefitsId.withValue(Set.empty),
        YourPartnersAgeId.withValue(Age.UnderEighteen),
        PartnerMinimumEarningsId.withValue(false),
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        UniversalCreditId.withValue(true),
        ParentPaidWorkCYId.withValue(true),
        PartnerEmploymentIncomeCYId.withValue(20),
        PartnerPaidPensionCYId.withValue(true),
        HowMuchPartnerPayPensionId.withValue(20),
        PartnerBenefitsIncomeCYId.withValue(20)
      )

      // You In Paid Employment
      val originalCacheMap3 = CacheMap.of(
        WhatIsYourTaxCodeId.withValue("1100L"),
        YourChildcareVouchersId.withValue(true),
        DoYouGetAnyBenefitsId.withValue(Set.empty),
        YourAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(false),
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        YourMaximumEarningsId.withValue(true),
        UniversalCreditId.withValue(true),
        PartnerPaidWorkCYId.withValue(true),
        ParentEmploymentIncomeCYId.withValue(20),
        YouPaidPensionCYId.withValue(true),
        HowMuchYouPayPensionId.withValue(20),
        YourOtherIncomeThisYearId.withValue(true),
        YouAnyTheseBenefitsCYId.withValue(true),
        YouBenefitsIncomeCYId.withValue(20)
      )

      val result1 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap1)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Neither))

      val result2 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap2)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Neither))

      val result3 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap3)
      result3.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Neither))
    }

    "remove an existing partner work hours, partner min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is you" in {

        // Partner earning less than minimum earnings
        val originalCacheMap = CacheMap.of(
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          PartnerMinimumEarningsId.withValue(false),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          UniversalCreditId.withValue(true),
          ParentPaidWorkCYId.withValue(true),
          PartnerEmploymentIncomeCYId.withValue(20),
          PartnerPaidPensionCYId.withValue(true),
          HowMuchPartnerPayPensionId.withValue(20),
          PartnerBenefitsIncomeCYId.withValue(20)
        ) // TODO Add in Statutory Data

        val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap)
        result.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.You),
          UniversalCreditId.withValue(true)
        )
      }

    "remove an existing partner work hours, partner vouchers partner and both min and max earnings, " +
      "both employment, both pension, both benefits CY when whoIsInPaidEmployment is you" in {

        // Parent earning more than minimum earnings and Partner earning less than minimum earnings
        val originalCacheMap1 = CacheMap.of(
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true),
          PartnerMinimumEarningsId.withValue(false),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          YourMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        // Parent and Partner earning more than minimum earnings
        val originalCacheMap2 = CacheMap.of(
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true),
          PartnerMinimumEarningsId.withValue(true),
          EitherOfYouMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = CacheMap.of(
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(false),
          PartnerMinimumEarningsId.withValue(true),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true),
          YourMaximumEarningsId.withValue(true)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.You),
          YourAgeId.withValue(Age.UnderEighteen),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          YourMinimumEarningsId.withValue(false)
        )
      }

    "remove an existing your work hours, your min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is partner" in {

        // Parent earning less than minimum earnings
        val originalCacheMap = CacheMap.of(
          WhatIsYourTaxCodeId.withValue("1100L"),
          YourChildcareVouchersId.withValue(true),
          YourAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(false),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerPaidWorkCYId.withValue(true),
          ParentEmploymentIncomeCYId.withValue(20),
          YouPaidPensionCYId.withValue(true),
          HowMuchYouPayPensionId.withValue(20),
          YourOtherIncomeThisYearId.withValue(true),
          YouAnyTheseBenefitsCYId.withValue(true),
          YouBenefitsIncomeCYId.withValue(20)
        )

        val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner))
      }

    "remove an existing your work hours,  your vouchers your and both min and max earnings, " +
      "both employment,both pension,both benefits CY when whoIsInPaidEmployment is partner" in {

        // Partner earning less than minimum earnings and Parent earning more than minimum earnings
        val originalCacheMap1 = CacheMap.of(
          WhatIsYourTaxCodeId.withValue("1100L"),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true),
          PartnerMinimumEarningsId.withValue(false),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          YourMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        // Partner and Parent earning more than minimum earnings
        val originalCacheMap2 = CacheMap.of(
          WhatIsYourTaxCodeId.withValue("1100L"),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(true),
          PartnerMinimumEarningsId.withValue(true),
          EitherOfYouMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = CacheMap.of(
          WhatIsYourTaxCodeId.withValue("1100L"),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You),
          YourAgeId.withValue(Age.UnderEighteen),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          YourMinimumEarningsId.withValue(false),
          PartnerMinimumEarningsId.withValue(true),
          AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.withValue(true),
          EmploymentIncomeCYId.withValue(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.withValue(true),
          WhoPaysIntoPensionId.withValue(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.withValue(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.withValue(true),
          WhoGetsOtherIncomeCYId.withValue(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.withValue(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.withValue(true),
          WhosHadBenefitsId.withValue(YouPartnerBoth.Both),
          BenefitsIncomeCYId.withValue(BenefitsIncomeCY(20, 20))
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerMinimumEarningsId.withValue(false),
          PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerMinimumEarningsId.withValue(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.withValue(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.withValue("1100L"),
          PartnerMinimumEarningsId.withValue(true),
          PartnerMaximumEarningsId.withValue(true)
        )
      }

    "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = CacheMap.of(
        YourChildcareVouchersId.withValue(true),
        PartnerPaidWorkCYId.withValue(true),
        ParentEmploymentIncomeCYId.withValue(20),
        YouPaidPensionCYId.withValue(true),
        HowMuchYouPayPensionId.withValue(20),
        YourOtherIncomeThisYearId.withValue(true),
        YouAnyTheseBenefitsCYId.withValue(true),
        YouBenefitsIncomeCYId.withValue(20)
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Both)) // TODO Add in Statutory Data
    }

    "remove partner childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = CacheMap.of(
        PartnerChildcareVouchersId.withValue(true),
        ParentPaidWorkCYId.withValue(true),
        PartnerEmploymentIncomeCYId.withValue(20),
        PartnerPaidPensionCYId.withValue(true),
        HowMuchPartnerPayPensionId.withValue(20),
        PartnerBenefitsIncomeCYId.withValue(20)
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.withValue(YouPartnerBothNeither.Both)) // TODO Add in Statutory Data
    }
  }

  "saving the your age" must {
    "removing an existing yourMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap =
        CacheMap.of(YourAgeId.withValue(Age.EighteenToTwenty), YourMinimumEarningsId.withValue(true))

      val result = cascadeUpsert(YourAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(YourAgeId.withValue(Age.UnderEighteen))
    }

    "removing an existing yourMinimumEarnings and areYouSelfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = CacheMap.of(
        YourAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(false),
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Neither)
      )

      val result = cascadeUpsert(YourAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourAgeId.withValue(Age.EighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings areYouSelfEmployedOrApprentice and yourSelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = CacheMap.of(
        YourAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(false),
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        YourSelfEmployedId.withValue(true)
      )

      val result = cascadeUpsert(YourAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.withValue(Age.TwentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings  when user change the selection to age over 25" in {
      val originalCacheMap = CacheMap.of(YourAgeId.withValue(Age.UnderEighteen), YourMinimumEarningsId.withValue(true))

      val result = cascadeUpsert(YourAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.withValue(Age.TwentyOneOrOver))
    }

    " not removing an existing your minimumEarnings  when user change the selection to age 18-20 again" in {
      val originalCacheMap =
        CacheMap.of(YourAgeId.withValue(Age.EighteenToTwenty), YourMinimumEarningsId.withValue(true))

      val result = cascadeUpsert(YourAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(
        YourAgeId.withValue(Age.EighteenToTwenty),
        YourMinimumEarningsId.withValue(true)
      )
    }
  }

  "saving the partner age" must {
    "removing an existing partnerMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.withValue(Age.EighteenToTwenty),
        PartnerMinimumEarningsId.withValue(true)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.withValue(Age.UnderEighteen))
    }

    "removing an existing yourMinimumEarnings ,selfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.withValue(Age.UnderEighteen),
        PartnerMinimumEarningsId.withValue(false),
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Neither)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.withValue(Age.EighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings selfEmployedOrApprentice and SelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.withValue(Age.UnderEighteen),
        PartnerMinimumEarningsId.withValue(false),
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        PartnerSelfEmployedId.withValue(true)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.withValue(Age.TwentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings, maximumEarnings when user change the selection to age over 25" in {
      val originalCacheMap =
        CacheMap.of(YourPartnersAgeId.withValue(Age.UnderEighteen), PartnerMinimumEarningsId.withValue(true))

      val result = cascadeUpsert(YourPartnersAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.withValue(Age.TwentyOneOrOver))
    }

    "not removing an existing yourMinimumEarnings maximum earnings when user change the selection to age under18 again" in {
      val originalCacheMap =
        CacheMap.of(YourPartnersAgeId.withValue(Age.UnderEighteen), PartnerMinimumEarningsId.withValue(true))

      val result = cascadeUpsert(YourPartnersAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(
        YourPartnersAgeId.withValue(Age.UnderEighteen),
        PartnerMinimumEarningsId.withValue(true)
      )
    }
  }

  "saving the your minimumEarnings" must {
    "remove your maximum earnings and either of you max earnings whenparent in paid employment and your minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(YourMaximumEarningsId.withValue(false))

      val result = cascadeUpsert(YourMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.withValue(false))
    }

    "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
      val originalCacheMap = CacheMap.of(
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        YourSelfEmployedId.withValue(true)
      )

      val result = cascadeUpsert(YourMinimumEarningsId, true, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.withValue(true))
    }
  }

  "saving the your partners minimumEarnings" must {
    "remove partners and either of you maximum earnings when partners minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(PartnerMaximumEarningsId.withValue(false))

      val result = cascadeUpsert(PartnerMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.withValue(false))
    }

    "remove your either of you max earnings when both in paid employment  and your minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(EitherOfYouMaximumEarningsId.withValue(true))

      val result = cascadeUpsert(PartnerMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.withValue(false))
    }

    "remove your partners self employed or apprentice and partners self employed less than 12 months when partners minimum earnings is yes" in {
      val originalCacheMap = CacheMap.of(
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.SelfEmployed),
        PartnerSelfEmployedId.withValue(true)
      )

      val result = cascadeUpsert(PartnerMinimumEarningsId, true, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.withValue(true))
    }
  }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = CacheMap.of(YourSelfEmployedId.withValue(false))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId,
        EmploymentStatus.Apprentice,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Apprentice)
      )
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = CacheMap.of(YourSelfEmployedId.withValue(false))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId,
        EmploymentStatus.Neither,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Neither)
      )
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = CacheMap.of(PartnerSelfEmployedId.withValue(false))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId,
        EmploymentStatus.Apprentice,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Apprentice)
      )
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = CacheMap.of(PartnerSelfEmployedId.withValue(false))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId,
        EmploymentStatus.Neither,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.withValue(EmploymentStatus.Neither)
      )
    }
  }

  // Need to work on clearence for maximum earnings 'no' to clear noOfChildren data and further

  "session management" must {
    "clear all the cache Map data" in {

      val originalCacheMap = CacheMap.of(
        LocationId.withValue(Location.England),
        PartnerSelfEmployedId.withValue(false)
      )
      val result = cascadeUpsert(SessionDataClearId, "sessionData", originalCacheMap)

      result.data mustBe Map(SessionDataClearId.withValue("sessionData"))

    }
  }

}
