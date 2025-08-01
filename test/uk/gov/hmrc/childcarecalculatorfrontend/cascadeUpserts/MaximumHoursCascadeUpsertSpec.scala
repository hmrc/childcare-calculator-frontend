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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits.IncapacityBenefit
import uk.gov.hmrc.childcarecalculatorfrontend.models.SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}

class MaximumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  private val yes: String = YesNoUnsureEnum.YES.toString

  private val under18: String          = AgeEnum.UNDER18.toString
  private val eighteenToTwenty: String = AgeEnum.EIGHTEENTOTWENTY.toString
  private val twentyOneOrOver: String  = AgeEnum.TWENTYONEOROVER.toString

  "saving the doYouLiveWithPartner" when {

    "doYouLiveWithPartner is false" must {

      "remove data related to both parents in employment" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            WhoIsInPaidEmploymentId.toString                -> JsString(both),
            HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
            HasYourTaxCodeBeenAdjustedId.toString           -> JsString(yes),
            DoYouKnowYourAdjustedTaxCodeId.toString         -> JsBoolean(true),
            WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
            WhatIsYourTaxCodeId.toString                    -> JsString("1100L"),
            WhoGetsVouchersId.toString                      -> JsString("you"),
            PartnerChildcareVouchersId.toString             -> JsString("yes"),
            YourChildcareVouchersId.toString                -> JsString("yes"),
            DoYouGetAnyBenefitsId.toString                  -> JsArray(Seq(JsString(IncapacityBenefit.toString))),
            DoesYourPartnerGetAnyBenefitsId.toString        -> JsArray(Seq(JsString(IncapacityBenefit.toString))),
            YourPartnersAgeId.toString                      -> JsString("under18"),
            YourAgeId.toString                              -> JsString("under18"),
            PartnerMinimumEarningsId.toString               -> JsBoolean(true),
            YourMinimumEarningsId.toString                  -> JsBoolean(false),
            AreYouSelfEmployedOrApprenticeId.toString       -> JsString(SELFEMPLOYED.toString),
            PartnerMaximumEarningsId.toString               -> JsBoolean(true)
          )
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap)
        result.data mustBe Map(
          DoYouLiveWithPartnerId.toString           -> JsBoolean(false),
          DoYouKnowYourAdjustedTaxCodeId.toString   -> JsBoolean(true),
          HasYourTaxCodeBeenAdjustedId.toString     -> JsString(yes),
          WhatIsYourTaxCodeId.toString              -> JsString("1100L"),
          YourChildcareVouchersId.toString          -> JsString("yes"),
          YourAgeId.toString                        -> JsString("under18"),
          YourMinimumEarningsId.toString            -> JsBoolean(false),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString)
        )
      }
    }

    "doYouLiveWithPartner is true" must {
      "remove an existing paid employment and who is in paid employment" in {
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            AreYouInPaidWorkId.toString    -> JsBoolean(true),
            DoYouGetAnyBenefitsId.toString -> JsBoolean(true)
          )
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))
      }
    }
  }

  "saving the areYouInPaidWork" must {
    "remove all the relevant data for you pages when are you in paid work is no" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          HasYourTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString            -> JsString("1100L"),
          YourChildcareVouchersId.toString        -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString          -> JsBoolean(false),
          YourAgeId.toString                      -> JsString("under18"),
          YourMinimumEarningsId.toString          -> JsBoolean(true),
          YourMaximumEarningsId.toString          -> JsBoolean(true),
          UniversalCreditId.toString              -> JsString("uc"),
          PartnerPaidWorkCYId.toString            -> JsBoolean(true),
          ParentEmploymentIncomeCYId.toString     -> JsBoolean(true),
          YouPaidPensionCYId.toString             -> JsBoolean(true),
          HowMuchYouPayPensionId.toString         -> JsNumber(BigDecimal(20)),
          YourOtherIncomeThisYearId.toString      -> JsBoolean(true),
          YouAnyTheseBenefitsIdCY.toString        -> JsBoolean(true),
          YouBenefitsIncomeCYId.toString          -> JsNumber(BigDecimal(20))
        )
      )

      val result = cascadeUpsert(AreYouInPaidWorkId.toString, false, originalCacheMap)
      result.data mustBe Map(AreYouInPaidWorkId.toString -> JsBoolean(false))
    }
  }

  "saving the whoIsInPaidEmployment" must {

    "Do data clearance for Neither" in {
      val originalCacheMap1 = new CacheMap(
        "id",
        Map(
          HasYourTaxCodeBeenAdjustedId.toString           -> JsString(yes),
          HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourAdjustedTaxCodeId.toString         -> JsBoolean(true),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString                    -> JsString("1100L"),
          WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
          WhoGetsVouchersId.toString                      -> JsString("both"),
          YourChildcareVouchersId.toString                -> JsString("yes"),
          PartnerChildcareVouchersId.toString             -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString                  -> JsBoolean(false),
          YourAgeId.toString                              -> JsString("under18"),
          YourPartnersAgeId.toString                      -> JsString("under18"),
          YourMinimumEarningsId.toString                  -> JsBoolean(true),
          PartnerMinimumEarningsId.toString               -> JsBoolean(true),
          EitherOfYouMaximumEarningsId.toString           -> JsBoolean(true),
          UniversalCreditId.toString                      -> JsString("uc"),
          EmploymentIncomeCYId.toString                   -> Json.toJson(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.toString                    -> JsBoolean(true),
          WhoPaysIntoPensionId.toString                   -> JsString(both),
          HowMuchBothPayPensionId.toString                -> JsNumber(BigDecimal(20)),
          BothOtherIncomeThisYearId.toString              -> JsBoolean(true),
          WhoGetsOtherIncomeCYId.toString                 -> JsString(both),
          OtherIncomeAmountCYId.toString                  -> Json.toJson(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.toString               -> JsBoolean(true),
          WhosHadBenefitsId.toString                      -> JsString(both),
          BenefitsIncomeCYId.toString                     -> Json.toJson(BenefitsIncomeCY(20, 20))
        )
      )

      // Partner In Paid Employment
      val originalCacheMap2 = new CacheMap(
        "id",
        Map(
          HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
          PartnerChildcareVouchersId.toString             -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString                  -> JsBoolean(false),
          YourPartnersAgeId.toString                      -> JsString("under18"),
          PartnerMinimumEarningsId.toString               -> JsBoolean(false),
          PartnerSelfEmployedOrApprenticeId.toString      -> JsString(SELFEMPLOYED.toString),
          UniversalCreditId.toString                      -> JsString("uc"),
          ParentPaidWorkCYId.toString                     -> JsBoolean(true),
          PartnerEmploymentIncomeCYId.toString            -> JsBoolean(true),
          PartnerPaidPensionCYId.toString                 -> JsBoolean(true),
          HowMuchPartnerPayPensionId.toString             -> JsNumber(BigDecimal(20)),
          PartnerAnyOtherIncomeThisYearId.toString        -> JsBoolean(true),
          PartnerBenefitsIncomeCYId.toString              -> JsNumber(BigDecimal(20))
        )
      )

      // You In Paid Employment
      val originalCacheMap3 = new CacheMap(
        "id",
        Map(
          HasYourTaxCodeBeenAdjustedId.toString     -> JsString(yes),
          DoYouKnowYourAdjustedTaxCodeId.toString   -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString              -> JsString("1100L"),
          YourChildcareVouchersId.toString          -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString            -> JsBoolean(false),
          YourAgeId.toString                        -> JsString("under18"),
          YourMinimumEarningsId.toString            -> JsBoolean(false),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
          YourMaximumEarningsId.toString            -> JsBoolean(true),
          UniversalCreditId.toString                -> JsString("uc"),
          PartnerPaidWorkCYId.toString              -> JsBoolean(true),
          ParentEmploymentIncomeCYId.toString       -> JsBoolean(true),
          YouPaidPensionCYId.toString               -> JsBoolean(true),
          HowMuchYouPayPensionId.toString           -> JsNumber(BigDecimal(20)),
          YourOtherIncomeThisYearId.toString        -> JsBoolean(true),
          YouAnyTheseBenefitsIdCY.toString          -> JsBoolean(true),
          YouBenefitsIncomeCYId.toString            -> JsNumber(BigDecimal(20))
        )
      )

      val result1 =
        cascadeUpsert(WhoIsInPaidEmploymentId.toString, YouPartnerBothNeitherEnum.NEITHER.toString, originalCacheMap1)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothNeitherEnum.NEITHER.toString))

      val result2 =
        cascadeUpsert(WhoIsInPaidEmploymentId.toString, YouPartnerBothNeitherEnum.NEITHER.toString, originalCacheMap2)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothNeitherEnum.NEITHER.toString))

      val result3 =
        cascadeUpsert(WhoIsInPaidEmploymentId.toString, YouPartnerBothNeitherEnum.NEITHER.toString, originalCacheMap3)
      result3.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothNeitherEnum.NEITHER.toString))
    }

    "remove an existing partner work hours, partner min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is you" in {

        // Partner earning less than minimum earnings
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            YourPartnersAgeId.toString                 -> JsString("under18"),
            PartnerMinimumEarningsId.toString          -> JsBoolean(false),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
            UniversalCreditId.toString                 -> JsString("uc"),
            ParentPaidWorkCYId.toString                -> JsBoolean(true),
            PartnerEmploymentIncomeCYId.toString       -> JsBoolean(true),
            PartnerPaidPensionCYId.toString            -> JsBoolean(true),
            HowMuchPartnerPayPensionId.toString        -> JsNumber(BigDecimal(20)),
            PartnerAnyOtherIncomeThisYearId.toString   -> JsBoolean(true),
            PartnerBenefitsIncomeCYId.toString         -> JsNumber(BigDecimal(20))
          )
        ) // TODO Add in Statutory Data

        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap)
        result.data mustBe Map(
          WhoIsInPaidEmploymentId.toString -> JsString(you),
          UniversalCreditId.toString       -> JsString("uc")
        )
      }

    "remove an existing partner work hours, partner vouchers partner and both min and max earnings, " +
      "both employment, both pension, both benefits CY when whoIsInPaidEmployment is you" in {

        // Parent earning more than minimum earnings and Partner earning less than minimum earnings
        val originalCacheMap1 = new CacheMap(
          "id",
          Map(
            WhoGetsVouchersId.toString                 -> JsString("you"),
            YourAgeId.toString                         -> JsString("under18"),
            YourPartnersAgeId.toString                 -> JsString("under18"),
            YourMinimumEarningsId.toString             -> JsBoolean(true),
            PartnerMinimumEarningsId.toString          -> JsBoolean(false),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
            YourMaximumEarningsId.toString             -> JsBoolean(true),
            EmploymentIncomeCYId.toString              -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString               -> JsBoolean(true),
            WhoPaysIntoPensionId.toString              -> JsString(both),
            HowMuchBothPayPensionId.toString           -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString         -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString            -> JsString(both),
            OtherIncomeAmountCYId.toString             -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString          -> JsBoolean(true),
            WhosHadBenefitsId.toString                 -> JsString(both),
            BenefitsIncomeCYId.toString                -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        // Parent and Partner earning more than minimum earnings
        val originalCacheMap2 = new CacheMap(
          "id",
          Map(
            WhoGetsVouchersId.toString            -> JsString("you"),
            YourAgeId.toString                    -> JsString("under18"),
            YourPartnersAgeId.toString            -> JsString("under18"),
            YourMinimumEarningsId.toString        -> JsBoolean(true),
            PartnerMinimumEarningsId.toString     -> JsBoolean(true),
            EitherOfYouMaximumEarningsId.toString -> JsBoolean(true),
            EmploymentIncomeCYId.toString         -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString          -> JsBoolean(true),
            WhoPaysIntoPensionId.toString         -> JsString(both),
            HowMuchBothPayPensionId.toString      -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString    -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString       -> JsString(both),
            OtherIncomeAmountCYId.toString        -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString     -> JsBoolean(true),
            WhosHadBenefitsId.toString            -> JsString(both),
            BenefitsIncomeCYId.toString           -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = new CacheMap(
          "id",
          Map(
            WhoGetsVouchersId.toString                -> JsString("you"),
            YourAgeId.toString                        -> JsString("under18"),
            YourPartnersAgeId.toString                -> JsString("under18"),
            YourMinimumEarningsId.toString            -> JsBoolean(false),
            PartnerMinimumEarningsId.toString         -> JsBoolean(true),
            AreYouSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
            PartnerMaximumEarningsId.toString         -> JsBoolean(true),
            EmploymentIncomeCYId.toString             -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString              -> JsBoolean(true),
            WhoPaysIntoPensionId.toString             -> JsString(both),
            HowMuchBothPayPensionId.toString          -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString        -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString           -> JsString(both),
            OtherIncomeAmountCYId.toString            -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString         -> JsBoolean(true),
            WhosHadBenefitsId.toString                -> JsString(both),
            BenefitsIncomeCYId.toString               -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.toString -> JsString(you),
          YourAgeId.toString               -> JsString("under18"),
          YourMinimumEarningsId.toString   -> JsBoolean(true),
          YourMaximumEarningsId.toString   -> JsBoolean(true)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.toString -> JsString(you),
          YourAgeId.toString               -> JsString("under18"),
          YourMinimumEarningsId.toString   -> JsBoolean(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.toString          -> JsString(you),
          YourAgeId.toString                        -> JsString("under18"),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
          YourMinimumEarningsId.toString            -> JsBoolean(false)
        )
      }

    "remove an existing your work hours, your min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is partner" in {

        // Parent earning less than minimum earnings
        val originalCacheMap = new CacheMap(
          "id",
          Map(
            HasYourTaxCodeBeenAdjustedId.toString     -> JsString(yes),
            DoYouKnowYourAdjustedTaxCodeId.toString   -> JsBoolean(true),
            WhatIsYourTaxCodeId.toString              -> JsString("1100L"),
            YourChildcareVouchersId.toString          -> Json.toJson(YesNoUnsureEnum.YES),
            YourAgeId.toString                        -> JsString("under18"),
            YourMinimumEarningsId.toString            -> JsBoolean(false),
            AreYouSelfEmployedOrApprenticeId.toString -> JsString(SELFEMPLOYED.toString),
            PartnerPaidWorkCYId.toString              -> JsBoolean(true),
            ParentEmploymentIncomeCYId.toString       -> JsBoolean(true),
            YouPaidPensionCYId.toString               -> JsBoolean(true),
            HowMuchYouPayPensionId.toString           -> JsNumber(BigDecimal(20)),
            YourOtherIncomeThisYearId.toString        -> JsBoolean(true),
            YouAnyTheseBenefitsIdCY.toString          -> JsBoolean(true),
            YouBenefitsIncomeCYId.toString            -> JsNumber(BigDecimal(20))
          )
        )

        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner))
      }

    "remove an existing your work hours,  your vouchers your and both min and max earnings, " +
      "both employment,both pension,both benefits CY when whoIsInPaidEmployment is partner" in {

        // Partner earning less than minimum earnings and Parent earning more than minimum earnings
        val originalCacheMap1 = new CacheMap(
          "id",
          Map(
            HasYourTaxCodeBeenAdjustedId.toString           -> JsString(yes),
            HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
            DoYouKnowYourAdjustedTaxCodeId.toString         -> JsBoolean(true),
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
            WhatIsYourTaxCodeId.toString                    -> JsString("1100L"),
            WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
            WhoGetsVouchersId.toString                      -> JsString("you"),
            YourAgeId.toString                              -> JsString("under18"),
            YourPartnersAgeId.toString                      -> JsString("under18"),
            YourMinimumEarningsId.toString                  -> JsBoolean(true),
            PartnerMinimumEarningsId.toString               -> JsBoolean(false),
            PartnerSelfEmployedOrApprenticeId.toString      -> JsString(SELFEMPLOYED.toString),
            YourMaximumEarningsId.toString                  -> JsBoolean(true),
            EmploymentIncomeCYId.toString                   -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString                    -> JsBoolean(true),
            WhoPaysIntoPensionId.toString                   -> JsString(both),
            HowMuchBothPayPensionId.toString                -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString              -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString                 -> JsString(both),
            OtherIncomeAmountCYId.toString                  -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString               -> JsBoolean(true),
            WhosHadBenefitsId.toString                      -> JsString(both),
            BenefitsIncomeCYId.toString                     -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        // Partner and Parent earning more than minimum earnings
        val originalCacheMap2 = new CacheMap(
          "id",
          Map(
            HasYourTaxCodeBeenAdjustedId.toString           -> JsString(yes),
            HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
            DoYouKnowYourAdjustedTaxCodeId.toString         -> JsBoolean(true),
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
            WhatIsYourTaxCodeId.toString                    -> JsString("1100L"),
            WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
            WhoGetsVouchersId.toString                      -> JsString("you"),
            YourAgeId.toString                              -> JsString("under18"),
            YourPartnersAgeId.toString                      -> JsString("under18"),
            YourMinimumEarningsId.toString                  -> JsBoolean(true),
            PartnerMinimumEarningsId.toString               -> JsBoolean(true),
            EitherOfYouMaximumEarningsId.toString           -> JsBoolean(true),
            EmploymentIncomeCYId.toString                   -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString                    -> JsBoolean(true),
            WhoPaysIntoPensionId.toString                   -> JsString(both),
            HowMuchBothPayPensionId.toString                -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString              -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString                 -> JsString(both),
            OtherIncomeAmountCYId.toString                  -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString               -> JsBoolean(true),
            WhosHadBenefitsId.toString                      -> JsString(both),
            BenefitsIncomeCYId.toString                     -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = new CacheMap(
          "id",
          Map(
            HasYourTaxCodeBeenAdjustedId.toString           -> JsString(yes),
            HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
            DoYouKnowYourAdjustedTaxCodeId.toString         -> JsBoolean(true),
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
            WhatIsYourTaxCodeId.toString                    -> JsString("1100L"),
            WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
            WhoGetsVouchersId.toString                      -> JsString("you"),
            YourAgeId.toString                              -> JsString("under18"),
            YourPartnersAgeId.toString                      -> JsString("under18"),
            YourMinimumEarningsId.toString                  -> JsBoolean(false),
            PartnerMinimumEarningsId.toString               -> JsBoolean(true),
            AreYouSelfEmployedOrApprenticeId.toString       -> JsString(SELFEMPLOYED.toString),
            PartnerMaximumEarningsId.toString               -> JsBoolean(true),
            EmploymentIncomeCYId.toString                   -> Json.toJson(EmploymentIncomeCY(20, 20)),
            BothPaidPensionCYId.toString                    -> JsBoolean(true),
            WhoPaysIntoPensionId.toString                   -> JsString(both),
            HowMuchBothPayPensionId.toString                -> JsNumber(BigDecimal(20)),
            BothOtherIncomeThisYearId.toString              -> JsBoolean(true),
            WhoGetsOtherIncomeCYId.toString                 -> JsString(both),
            OtherIncomeAmountCYId.toString                  -> Json.toJson(OtherIncomeAmountCY(20, 20)),
            BothAnyTheseBenefitsCYId.toString               -> JsBoolean(true),
            WhosHadBenefitsId.toString                      -> JsString(both),
            BenefitsIncomeCYId.toString                     -> Json.toJson(BenefitsIncomeCY(20, 20))
          )
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.toString                -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          YourPartnersAgeId.toString                      -> JsString("under18"),
          WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
          PartnerMinimumEarningsId.toString               -> JsBoolean(false),
          PartnerSelfEmployedOrApprenticeId.toString      -> JsString(SELFEMPLOYED.toString)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.toString                -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          YourPartnersAgeId.toString                      -> JsString("under18"),
          WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
          PartnerMinimumEarningsId.toString               -> JsBoolean(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.toString                -> JsString(partner),
          HasYourPartnersTaxCodeBeenAdjustedId.toString   -> JsString(yes),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          YourPartnersAgeId.toString                      -> JsString("under18"),
          WhatIsYourPartnersTaxCodeId.toString            -> JsString("1100L"),
          PartnerMinimumEarningsId.toString               -> JsBoolean(true),
          PartnerMaximumEarningsId.toString               -> JsBoolean(true)
        )
      }

    "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourChildcareVouchersId.toString    -> JsString("yes"),
          PartnerPaidWorkCYId.toString        -> JsBoolean(true),
          ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
          YouPaidPensionCYId.toString         -> JsBoolean(true),
          HowMuchYouPayPensionId.toString     -> JsNumber(BigDecimal(20)),
          YourOtherIncomeThisYearId.toString  -> JsBoolean(true),
          YouAnyTheseBenefitsIdCY.toString    -> JsBoolean(true),
          YouBenefitsIncomeCYId.toString      -> JsNumber(BigDecimal(20))
        )
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both)) // TODO Add in Statutory Data
    }

    "remove partner childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          PartnerChildcareVouchersId.toString      -> JsString("yes"),
          ParentPaidWorkCYId.toString              -> JsBoolean(true),
          PartnerEmploymentIncomeCYId.toString     -> JsBoolean(true),
          PartnerPaidPensionCYId.toString          -> JsBoolean(true),
          HowMuchPartnerPayPensionId.toString      -> JsNumber(BigDecimal(20)),
          PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(true),
          PartnerBenefitsIncomeCYId.toString       -> JsNumber(BigDecimal(20))
        )
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both)) // TODO Add in Statutory Data
    }
  }

  "saving the your age" must {
    "removing an existing yourMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(YourAgeId.toString -> JsString(eighteenToTwenty), YourMinimumEarningsId.toString -> JsBoolean(true))
      )

      val result = cascadeUpsert(YourAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString -> JsString(under18))
    }

    "removing an existing yourMinimumEarnings and areYouSelfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourAgeId.toString                        -> JsString(under18),
          YourMinimumEarningsId.toString            -> JsBoolean(false),
          AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(false)
        )
      )

      val result = cascadeUpsert(YourAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString -> JsString(eighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings areYouSelfEmployedOrApprentice and yourSelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourAgeId.toString                        -> JsString(under18),
          YourMinimumEarningsId.toString            -> JsBoolean(false),
          AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true),
          YourSelfEmployedId.toString               -> JsBoolean(true)
        )
      )

      val result = cascadeUpsert(YourAgeId.toString, twentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString -> JsString(twentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings  when user change the selection to age over 25" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(YourAgeId.toString -> JsString(under18), YourMinimumEarningsId.toString -> JsBoolean(true))
      )

      val result = cascadeUpsert(YourAgeId.toString, twentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString -> JsString(twentyOneOrOver))
    }

    " not removing an existing your minimumEarnings  when user change the selection to age 18-20 again" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(YourAgeId.toString -> JsString(eighteenToTwenty), YourMinimumEarningsId.toString -> JsBoolean(true))
      )

      val result = cascadeUpsert(YourAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(
        YourAgeId.toString             -> JsString(eighteenToTwenty),
        YourMinimumEarningsId.toString -> JsBoolean(true)
      )
    }
  }

  "saving the partner age" must {
    "removing an existing partnerMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourPartnersAgeId.toString        -> JsString(eighteenToTwenty),
          PartnerMinimumEarningsId.toString -> JsBoolean(true)
        )
      )

      val result = cascadeUpsert(YourPartnersAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString -> JsString(under18))
    }

    "removing an existing yourMinimumEarnings ,selfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourPartnersAgeId.toString                 -> JsString(under18),
          PartnerMinimumEarningsId.toString          -> JsBoolean(false),
          PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(false)
        )
      )

      val result = cascadeUpsert(YourPartnersAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString -> JsString(eighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings selfEmployedOrApprentice and SelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          YourPartnersAgeId.toString                 -> JsString(under18),
          PartnerMinimumEarningsId.toString          -> JsBoolean(false),
          PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true),
          PartnerSelfEmployedId.toString             -> JsBoolean(true)
        )
      )

      val result = cascadeUpsert(YourPartnersAgeId.toString, twentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString -> JsString(twentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings, maximumEarnings when user change the selection to age over 25" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(YourPartnersAgeId.toString -> JsString(under18), PartnerMinimumEarningsId.toString -> JsBoolean(true))
      )

      val result = cascadeUpsert(YourPartnersAgeId.toString, twentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString -> JsString(twentyOneOrOver))
    }

    "not removing an existing yourMinimumEarnings maximum earnings when user change the selection to age under18 again" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(YourPartnersAgeId.toString -> JsString(under18), PartnerMinimumEarningsId.toString -> JsBoolean(true))
      )

      val result = cascadeUpsert(YourPartnersAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(
        YourPartnersAgeId.toString        -> JsString(under18),
        PartnerMinimumEarningsId.toString -> JsBoolean(true)
      )
    }
  }

  "saving the your minimumEarnings" must {
    "remove your maximum earnings and either of you max earnings whenparent in paid employment and your minimum earnings is no" in {
      val originalCacheMap = new CacheMap("id", Map(YourMaximumEarningsId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(YourMinimumEarningsId.toString, false, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(false))
    }

    "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true),
          YourSelfEmployedId.toString               -> JsBoolean(true)
        )
      )

      val result = cascadeUpsert(YourMinimumEarningsId.toString, true, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(true))
    }
  }

  "saving the your partners minimumEarnings" must {
    "remove partners and either of you maximum earnings when partners minimum earnings is no" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerMaximumEarningsId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(PartnerMinimumEarningsId.toString, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(false))
    }

    "remove your either of you max earnings when both in paid employment  and your minimum earnings is no" in {
      val originalCacheMap = new CacheMap("id", Map(EitherOfYouMaximumEarningsId.toString -> JsBoolean(true)))

      val result = cascadeUpsert(PartnerMinimumEarningsId.toString, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(false))
    }

    "remove your partners self employed or apprentice and partners self employed less than 12 months when partners minimum earnings is yes" in {
      val originalCacheMap = new CacheMap(
        "id",
        Map(
          PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true),
          PartnerSelfEmployedId.toString             -> JsBoolean(true)
        )
      )

      val result = cascadeUpsert(PartnerMinimumEarningsId.toString, true, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(true))
    }
  }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId.toString,
        SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
      )
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId.toString,
        SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)
      )
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId.toString,
        SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(
          SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
        )
      )
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId.toString,
        SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)
      )
    }
  }

  // Need to work on clearence for maximum earnings 'no' to clear noOfChildren data and further

  "session management" must {
    "clear all the cache Map data" in {

      val originalCacheMap = new CacheMap(
        "id",
        Map(
          LocationId.toString            -> JsString(Location.ENGLAND.toString),
          PartnerSelfEmployedId.toString -> JsBoolean(false)
        )
      )
      val result = cascadeUpsert(SessionDataClearId.toString, "sessionData", originalCacheMap)

      result.data mustBe Map(SessionDataClearId.toString -> JsString("sessionData"))

    }
  }

}
