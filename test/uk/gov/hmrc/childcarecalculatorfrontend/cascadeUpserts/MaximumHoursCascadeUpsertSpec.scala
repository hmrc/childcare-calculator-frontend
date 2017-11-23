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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{PartnerPaidWorkPYId, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase, identifiers}
import uk.gov.hmrc.http.cache.client.CacheMap

class MaximumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {
  lazy val yes: String = YesNoUnsureEnum.YES.toString
  lazy val no: String = YesNoUnsureEnum.NO.toString
  lazy val notSure: String = YesNoUnsureEnum.NOTSURE.toString

  lazy val disabilityBenefits=WhichBenefitsEnum.DISABILITYBENEFITS.toString
  lazy val incomeBenefits=WhichBenefitsEnum.INCOMEBENEFITS.toString


  lazy val under18=AgeEnum.UNDER18.toString
  lazy val eighteenToTwenty=AgeEnum.EIGHTEENTOTWENTY.toString
  lazy val twentyToTwentyFour=AgeEnum.TWENTYONETOTWENTYFOUR.toString
  lazy val overTwentyFive=AgeEnum.OVERTWENTYFOUR.toString



  "saving the doYouLiveWithPartner" must {
    "remove partner and both pages related data wherever applicable when doYouLiveWithPartner is no " in {

      val originalCacheMap1 = new CacheMap("id", Map(
        WhoIsInPaidEmploymentId.toString -> JsString(partner), PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),PartnerChildcareVouchersId.toString -> JsString("yes"),
         WhoGetsBenefitsId.toString -> JsString("you"), YourPartnersAgeId.toString -> JsString("under18"),
        PartnerMinimumEarningsId.toString -> JsBoolean(true),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        PartnerMaximumEarningsId.toString -> JsBoolean(true)))

      val originalCacheMap2 = new CacheMap("id", Map(
        WhoIsInPaidEmploymentId.toString -> JsString(both), PartnerWorkHoursId.toString -> JsString("12"),ParentWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(yes), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        HasYourTaxCodeBeenAdjustedId.toString -> JsString(yes), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),  WhatIsYourTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), PartnerChildcareVouchersId.toString -> JsString("yes"), YourChildcareVouchersId.toString -> JsString("yes"),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString(both),
        WhichBenefitsYouGetId.toString-> JsArray(Seq(JsString(WhichBenefitsEnum.DISABILITYBENEFITS.toString))),
        WhichBenefitsPartnerGetId.toString-> JsArray(Seq(JsString(WhichBenefitsEnum.DISABILITYBENEFITS.toString))),
        YourPartnersAgeId.toString -> JsString("under18"), YourAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        YourMinimumEarningsId.toString -> JsBoolean(false),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        PartnerMaximumEarningsId.toString -> JsBoolean(true)))


      val result1 = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap1)
      result1.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))

      val result2 = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap2)
      result2.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false),ParentWorkHoursId.toString -> JsString("12"),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),
        YourChildcareVouchersId.toString -> JsString("yes"),
        WhichBenefitsYouGetId.toString-> JsArray(Seq(JsString(WhichBenefitsEnum.DISABILITYBENEFITS.toString))),
        YourAgeId.toString -> JsString("under18"), YourMinimumEarningsId.toString -> JsBoolean(false),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString))
    }

    "remove an existing paid employment, who is in paid employment when doYouLiveWithpartner is Yes" in {
      val originalCacheMap = new CacheMap("id", Map(AreYouInPaidWorkId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(true)))

      val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, true, originalCacheMap)
      result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))
    }
  }

  "saving the areYouInPaidWork" must {
    "remove all the relevant data for you pages when are you in paid work is no" in {
      val originalCacheMap = new CacheMap("id", Map(
        ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"), YourChildcareVouchersId.toString -> JsString("yes"),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), YourMaximumEarningsId.toString -> JsBoolean(true),
        TaxOrUniversalCreditsId.toString -> JsString("tc"),

        PartnerPaidWorkCYId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
        YouPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeThisYearId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsIdCY.toString ->JsBoolean(true),
        YouBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        PartnerPaidWorkPYId .toString -> JsBoolean(true),
        ParentEmploymentIncomePYId.toString -> JsBoolean(true),
        YouPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeLYId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        YouBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)) //TODO Add in Statutory Data

      ))

      val result = cascadeUpsert(AreYouInPaidWorkId.toString, false, originalCacheMap)
      result.data mustBe Map(AreYouInPaidWorkId.toString -> JsBoolean(false))
    }
  }

  "saving the are you your partner, or both of you in paid work" must {
    "remove an existing who's in paid work, parent work hours, partner work hours, parents adjusted tax code, partners adjusted tax code," +
      "either child care vouchers, who gets childcare vouchers, your childcare vouchers, partner childcare vouchers, do you get benefits, " +
      "your age, partners age and all you, partner,both pages data in employment, pensions, other income, and " +
      " benefits flow when paid employment is no" in {

      // Both In Paid Employment
      val originalCacheMap1 = new CacheMap("id", Map(WhoIsInPaidEmploymentId.toString -> JsString("both"), ParentWorkHoursId.toString -> JsString("12"),
        PartnerWorkHoursId.toString -> JsString("12"), HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes), HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("both"), YourChildcareVouchersId.toString -> JsString("yes"), PartnerChildcareVouchersId.toString -> JsString("yes"),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"), YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        EitherOfYouMaximumEarningsId.toString -> JsBoolean(true), TaxOrUniversalCreditsId.toString-> JsString("tc"),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data

      // Partner In Paid Employment
      val originalCacheMap2 = new CacheMap("id", Map(WhoIsInPaidEmploymentId.toString -> JsString("partner"),
        PartnerWorkHoursId.toString -> JsString("12"), HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
       DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
       WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L") , PartnerChildcareVouchersId.toString->JsString("yes"),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false),  YourPartnersAgeId.toString -> JsString("under18"),
        PartnerMinimumEarningsId.toString -> JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        TaxOrUniversalCreditsId.toString-> JsString("tc"),


        ParentPaidWorkCYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomeCYId.toString -> JsBoolean(true),
        PartnerPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsCYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        ParentPaidWorkPYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomePYId.toString -> JsBoolean(true),
        PartnerPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeLYId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsPYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data

      // You In Paid Employment
      val originalCacheMap3 = new CacheMap("id", Map(WhoIsInPaidEmploymentId.toString -> JsString("you"), ParentWorkHoursId.toString -> JsString("12"),
         HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),   YourChildcareVouchersId.toString -> JsString("yes"),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(false),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        YourMaximumEarningsId.toString -> JsBoolean(true), TaxOrUniversalCreditsId.toString-> JsString("tc"),

        PartnerPaidWorkCYId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
        YouPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeThisYearId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsIdCY.toString ->JsBoolean(true),
        YouBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        PartnerPaidWorkPYId .toString -> JsBoolean(true),
        ParentEmploymentIncomePYId.toString -> JsBoolean(true),
        YouPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeLYId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        YouBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data


      val result1 = cascadeUpsert(PaidEmploymentId.toString, false, originalCacheMap1)
      result1.data mustBe Map(PaidEmploymentId.toString -> JsBoolean(false))

      val result2 = cascadeUpsert(PaidEmploymentId.toString, false, originalCacheMap2)
      result2.data mustBe Map(PaidEmploymentId.toString -> JsBoolean(false))

      val result3 = cascadeUpsert(PaidEmploymentId.toString, false, originalCacheMap3)
      result3.data mustBe Map(PaidEmploymentId.toString -> JsBoolean(false))
    }
  }

  "saving the whoIsInPaidEmployment" must {
    "remove an existing partner work hours, partners adjusted tax code, partner min and max earnings, employment," +
      " pension,benefits CY and PY when whoIsInPaidEmployment is you" in {

      // Partner earning less than minimum earnings
      val originalCacheMap = new CacheMap("id", Map(PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), PartnerChildcareVouchersId.toString -> Json.toJson(YesNoUnsureEnum.YES),
        YourPartnersAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        TaxOrUniversalCreditsId.toString-> JsString("tc"),

        ParentPaidWorkCYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomeCYId.toString -> JsBoolean(true),
        PartnerPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsCYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        ParentPaidWorkPYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomePYId.toString -> JsBoolean(true),
        PartnerPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeLYId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsPYId.toString -> JsBoolean(true),
       PartnerBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data


      // Partner earning more than minimum earnings
      val originalCacheMap2 = new CacheMap("id", Map(PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), PartnerChildcareVouchersId.toString -> Json.toJson(YesNoUnsureEnum.YES),
        YourPartnersAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        PartnerMaximumEarningsId.toString -> JsBoolean(true),

        ParentPaidWorkCYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomeCYId.toString -> JsBoolean(true),
        PartnerPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsCYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        ParentPaidWorkPYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomePYId.toString -> JsBoolean(true),
        PartnerPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeLYId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsPYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data

      val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you),TaxOrUniversalCreditsId.toString-> JsString("tc"))

      val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you),TaxOrUniversalCreditsId.toString-> JsString("tc"))
    }

    "remove an existing partner work hours, partners adjusted tax code, partner vouchers partner and both min and max earnings, " +
      "both employment,both pension,both benefits CY and PY when whoIsInPaidEmployment is you" in {


      // Parent earning more than minimum earnings and Partner earning less than minimum earnings
      val originalCacheMap1 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
         DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        YourMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data


      // Parent and Partner earning more than minimum earnings
      val originalCacheMap2 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        EitherOfYouMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data

      // Partner earning more than minimum earnings and Parent earning less than minimum earnings
      val originalCacheMap3 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(false), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        PartnerMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data


      val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap1)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you),ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourAgeId.toString -> JsString("under18"), WhatIsYourTaxCodeId.toString -> JsString("1100L"),
        YourMinimumEarningsId.toString -> JsBoolean(true),YourMaximumEarningsId.toString -> JsBoolean(true))

      val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap2)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you),ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourAgeId.toString -> JsString("under18"), WhatIsYourTaxCodeId.toString -> JsString("1100L"),YourMinimumEarningsId.toString -> JsBoolean(true))

      val result3 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap3)
      result3.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you),ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourAgeId.toString -> JsString("under18"), AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),YourMinimumEarningsId.toString -> JsBoolean(false))

    }


    "remove an existing your work hours, your adjusted tax code, your min and max earnings, employment," +
      " pension,benefits CY and PY when whoIsInPaidEmployment is partner" in {

      // Parent earning less than minimum earnings
      val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"), YourChildcareVouchersId.toString -> Json.toJson(YesNoUnsureEnum.YES),
        YourAgeId.toString -> JsString("under18"), YourMinimumEarningsId.toString -> JsBoolean(false),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),


        PartnerPaidWorkCYId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
        YouPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeThisYearId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsIdCY.toString ->JsBoolean(true),
        YouBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        PartnerPaidWorkPYId .toString -> JsBoolean(true),
        ParentEmploymentIncomePYId.toString -> JsBoolean(true),
        YouPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeLYId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        YouBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data


      // Parent earning less than minimum earnings
      val originalCacheMap2 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"), YourChildcareVouchersId.toString -> Json.toJson(YesNoUnsureEnum.YES),
        YourAgeId.toString -> JsString("under18"), YourMinimumEarningsId.toString -> JsBoolean(true),
        YourMaximumEarningsId.toString -> JsBoolean(true),
        TaxOrUniversalCreditsId.toString-> JsString("tc"),

        PartnerPaidWorkCYId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
        YouPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeThisYearId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsIdCY.toString ->JsBoolean(true),
        YouBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        PartnerPaidWorkPYId .toString -> JsBoolean(true),
        ParentEmploymentIncomePYId.toString -> JsBoolean(true),
        YouPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeLYId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        YouBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20)))) //TODO Add in Statutory Data

      val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner))

      val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner))
    }

    "remove an existing your work hours, your adjusted tax code, your vouchers your and both min and max earnings, " +
      "both employment,both pension,both benefits CY and PY when whoIsInPaidEmployment is partner" in {

      // Partner earning less than minimum earnings and Parent earning more than minimum earnings
      val originalCacheMap1 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        YourMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data

      // Partner and Parent earning more than minimum earnings
      val originalCacheMap2 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(true), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        EitherOfYouMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data

      // Partner earning more than minimum earnings and Parent earning less than minimum earnings
      val originalCacheMap3 = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourTaxCodeBeenAdjustedId.toString ->  JsString(yes),HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),
        DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        WhatIsYourTaxCodeId.toString -> JsString("1100L"),WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        EitherGetsVouchersId.toString -> JsString("yes"),
        WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"),YourPartnersAgeId.toString -> JsString("under18"),
        YourMinimumEarningsId.toString -> JsBoolean(false), PartnerMinimumEarningsId.toString -> JsBoolean(true),
        AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
        PartnerMaximumEarningsId.toString -> JsBoolean(true),

        EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionCYId.toString -> JsBoolean(true),
        WhoPaysIntoPensionId.toString -> JsString(both),
        HowMuchBothPayPensionId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeThisYearId.toString -> JsBoolean(true),
        WhoGetsOtherIncomeCYId.toString -> JsString(both),
        OtherIncomeAmountCYId.toString -> Json.toJson(OtherIncomeAmountCY("20","20")),
        BothAnyTheseBenefitsCYId.toString ->JsBoolean(true),
        WhosHadBenefitsId.toString -> JsString(both),
        BenefitsIncomeCYId.toString ->Json.toJson(BenefitsIncomeCY("20","20")),

        EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomeCY("20", "20")),
        BothPaidPensionPYId.toString -> JsBoolean(true),
        WhoPaidIntoPensionPYId.toString -> JsString(both),
        HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        BothOtherIncomeLYId.toString -> JsBoolean(true),
        WhoOtherIncomePYId.toString -> JsString(both),//
        OtherIncomeAmountPYId.toString -> Json.toJson(OtherIncomeAmountPY("20","20")),
        BothAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        WhosHadBenefitsPYId.toString -> JsString(both),
        BothBenefitsIncomePYId.toString -> Json.toJson(BothBenefitsIncomePY("20","20")))) //TODO Add in Statutory Data


      val result1 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap1)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourPartnersAgeId.toString -> JsString("under18"), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        PartnerMinimumEarningsId.toString -> JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString))

      val result2 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap2)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourPartnersAgeId.toString -> JsString("under18"), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),
        PartnerMinimumEarningsId.toString -> JsBoolean(true))

      val result3 = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap3)
      result3.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner),PartnerWorkHoursId.toString -> JsString("12"),
        HasYourPartnersTaxCodeBeenAdjustedId.toString ->  JsString(yes),DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
        YourPartnersAgeId.toString -> JsString("under18"),
        WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"),PartnerMinimumEarningsId.toString -> JsBoolean(true),
        PartnerMaximumEarningsId.toString -> JsBoolean(true))

    }

    "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = new CacheMap("id", Map(YourChildcareVouchersId.toString -> JsString("yes"),

        PartnerPaidWorkCYId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString -> JsBoolean(true),
        YouPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeThisYearId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsIdCY.toString ->JsBoolean(true),
        YouBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        PartnerPaidWorkPYId .toString -> JsBoolean(true),
        ParentEmploymentIncomePYId.toString -> JsBoolean(true),
        YouPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        YourOtherIncomeLYId.toString -> JsBoolean(true),
        YouAnyTheseBenefitsPYId.toString ->JsBoolean(true),
        YouBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20))))

      val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both)) //TODO Add in Statutory Data
    }

    "remove partner childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerChildcareVouchersId.toString -> JsString("yes"),

        ParentPaidWorkCYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomeCYId.toString -> JsBoolean(true),
        PartnerPaidPensionCYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeThisYearId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsCYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomeCYId.toString ->JsNumber(BigDecimal(20)),

        ParentPaidWorkPYId.toString -> JsBoolean(true),
        PartnerEmploymentIncomePYId.toString -> JsBoolean(true),
        PartnerPaidPensionPYId.toString -> JsBoolean(true),
        HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
        PartnerAnyOtherIncomeLYId.toString -> JsBoolean(true),
        PartnerAnyTheseBenefitsPYId.toString -> JsBoolean(true),
        PartnerBenefitsIncomePYId.toString ->JsNumber(BigDecimal(20))))

      val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both)) //TODO Add in Statutory Data
    }
  }


  "saving has your tax code been adjusted" must {
    "remove an existing do you know your adjusted tax code and your tax code when has your tax code been adjusted is no" in {

      val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(HasYourTaxCodeBeenAdjustedId.toString, no, originalCacheMap)
      result.data mustBe Map(HasYourTaxCodeBeenAdjustedId.toString -> JsString(no))
    }

    "remove an existing do you know your adjusted tax code and your tax code when has your tax code been adjusted is not sure" in {

      val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(HasYourTaxCodeBeenAdjustedId.toString, notSure, originalCacheMap)
      result.data mustBe Map(HasYourTaxCodeBeenAdjustedId.toString -> JsString(notSure))
    }

  }

  "saving do you know your adjusted tax code" must {
    "remove an existing your tax code when do you know adjusted tax code is no" in {
      val originalCacheMap = new CacheMap("id", Map(WhatIsYourTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(DoYouKnowYourAdjustedTaxCodeId.toString, false, originalCacheMap)
      result.data mustBe Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(false))
    }
  }

  "saving has your partner's tax code been adjusted" must {
    "remove an existing do you know your partner's adjusted tax code and your partner's tax code when has your partner's tax code been adjusted is no" in {
      val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(HasYourPartnersTaxCodeBeenAdjustedId.toString, no, originalCacheMap)
      result.data mustBe Map(HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(no))
    }

    "remove an existing do you know your partner's adjusted tax code and your partner's tax code when has your partner's tax code been adjusted is not sure" in {
      val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(HasYourPartnersTaxCodeBeenAdjustedId.toString, notSure, originalCacheMap)
      result.data mustBe Map(HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsString(notSure))
    }
  }

  "saving do you know your partners adjusted tax code" must {
    "remove an existing your partners tax code when do you know your partners adjusted tax code is no" in {
      val originalCacheMap = new CacheMap("id", Map(WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(DoYouKnowYourPartnersAdjustedTaxCodeId.toString, false, originalCacheMap)
      result.data mustBe Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(false))
    }
  }

  "saving the either childcare vouchers" must {
    "remove an existing who gets vouchers when either childcare vouchers is no" in {
      val originalCacheMap = new CacheMap("id", Map(WhoGetsVouchersId.toString -> JsString("you")))

      val result = cascadeUpsert(EitherGetsVouchersId.toString, no, originalCacheMap)
      result.data mustBe Map(EitherGetsVouchersId.toString -> JsString(no))
    }


      "remove an existing who gets vouchers when either childcare vouchers is no sure" in {
        val originalCacheMap = new CacheMap("id", Map(WhoGetsVouchersId.toString -> JsString("you")))

        val result = cascadeUpsert(EitherGetsVouchersId.toString, notSure, originalCacheMap)
        result.data mustBe Map(EitherGetsVouchersId.toString -> JsString(notSure))
      }
  }


  "saving the your or your partner benefits" must {
    "remove an existing who gets benefits, which benefits you get,which benefits does your partner get accordingly when you or your partner benefits is no" in {

      val originalCacheMap1 = new CacheMap("id", Map(WhoGetsBenefitsId.toString -> JsString("you"),
        WhichBenefitsYouGetId.toString->
          JsArray(Seq(JsString(disabilityBenefits),JsString(incomeBenefits)))))

      val originalCacheMap2 = new CacheMap("id", Map(WhoGetsBenefitsId.toString -> JsString("you"),
        WhichBenefitsPartnerGetId.toString-> JsArray(Seq(JsString(disabilityBenefits)))))

      val originalCacheMap3 = new CacheMap("id", Map(WhoGetsBenefitsId.toString -> JsString("you"),
        WhichBenefitsYouGetId.toString-> JsArray(Seq(JsString(disabilityBenefits))),
        WhichBenefitsYouGetId.toString-> JsArray(Seq(JsString(disabilityBenefits)))))

      val result1 = cascadeUpsert(DoYouOrYourPartnerGetAnyBenefitsId.toString, false, originalCacheMap1)
      result1.data mustBe Map(DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false))

      val result2 = cascadeUpsert(DoYouOrYourPartnerGetAnyBenefitsId.toString, false, originalCacheMap2)
      result2.data mustBe Map(DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false))

      val result3 = cascadeUpsert(DoYouOrYourPartnerGetAnyBenefitsId.toString, false, originalCacheMap3)
      result3.data mustBe Map(DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false))
    }
  }

  "saving the who gets benefits" must {
    "removing an existing which benefits does your partner get  when who gets benefits is 'you'" in {
      val originalCacheMap = new CacheMap("id", Map(
        WhichBenefitsPartnerGetId.toString ->
          JsArray(Seq(JsString(disabilityBenefits), JsString(incomeBenefits)))))

      val result = cascadeUpsert(WhoGetsBenefitsId.toString, you, originalCacheMap)
      result.data mustBe Map(WhoGetsBenefitsId.toString -> JsString(you))
    }

    "removing an existing which benefits you partner get  when who gets benefits is 'partner'" in {
      val originalCacheMap = new CacheMap("id", Map(
        WhichBenefitsYouGetId.toString ->
          JsArray(Seq(JsString(disabilityBenefits), JsString(incomeBenefits)))))

      val result = cascadeUpsert(WhoGetsBenefitsId.toString, partner, originalCacheMap)
      result.data mustBe Map(WhoGetsBenefitsId.toString -> JsString(partner))
    }
  }


  "saving the do you get any benefits" must {
    "removing an existing which benefits do you get when do you get any benefits is 'no'" in {
      val originalCacheMap = new CacheMap("id", Map(
        WhichBenefitsYouGetId.toString ->
          JsArray(Seq(JsString(disabilityBenefits), JsString(incomeBenefits)))))

      val result = cascadeUpsert(DoYouGetAnyBenefitsId.toString, false, originalCacheMap)
      result.data mustBe Map(DoYouGetAnyBenefitsId.toString -> JsBoolean(false))
    }
  }

  "saving the your age" must{
    "removing an existing yourMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = new CacheMap("id", Map(YourAgeId.toString->JsString(eighteenToTwenty),
        YourMinimumEarningsId.toString->JsBoolean(true) ))

      val result = cascadeUpsert(YourAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString->JsString(under18))
    }

    "removing an existing yourMinimumEarnings and areYouSelfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = new CacheMap("id", Map(YourAgeId.toString->JsString(under18),
        YourMinimumEarningsId.toString->JsBoolean(false),AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(false) ))

      val result = cascadeUpsert(YourAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString->JsString(eighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings areYouSelfEmployedOrApprentice and yourSelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = new CacheMap("id", Map(YourAgeId.toString->JsString(under18),
        YourMinimumEarningsId.toString->JsBoolean(false),AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true),YourSelfEmployedId.toString ->JsBoolean(true) ))

      val result = cascadeUpsert(YourAgeId.toString, twentyToTwentyFour, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString->JsString(twentyToTwentyFour))
    }

    "removing an existing yourMinimumEarnings  when user change the selection to age over 25" in {
      val originalCacheMap = new CacheMap("id", Map(YourAgeId.toString->JsString(under18),
        YourMinimumEarningsId.toString->JsBoolean(true)))

      val result = cascadeUpsert(YourAgeId.toString, overTwentyFive, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString->JsString(overTwentyFive))
    }

    " not removing an existing your minimumEarnings  when user change the selection to age 18-20 again" in {
      val originalCacheMap = new CacheMap("id", Map(YourAgeId.toString->JsString(eighteenToTwenty),
        YourMinimumEarningsId.toString->JsBoolean(true)))

      val result = cascadeUpsert(YourAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourAgeId.toString->JsString(eighteenToTwenty),
        YourMinimumEarningsId.toString->JsBoolean(true))
    }
  }


  "saving the partner age" must{
    "removing an existing partnerMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = new CacheMap("id", Map(YourPartnersAgeId.toString->JsString(eighteenToTwenty),
        PartnerMinimumEarningsId.toString->JsBoolean(true)))

      val result = cascadeUpsert(YourPartnersAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString->JsString(under18))
    }

    "removing an existing yourMinimumEarnings ,selfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = new CacheMap("id", Map(YourPartnersAgeId.toString->JsString(under18),
        PartnerMinimumEarningsId.toString->JsBoolean(false),PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(false) ))

      val result = cascadeUpsert(YourPartnersAgeId.toString, eighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString->JsString(eighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings selfEmployedOrApprentice and SelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = new CacheMap("id", Map(YourPartnersAgeId.toString->JsString(under18), PartnerMinimumEarningsId.toString->JsBoolean(false),
        PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true),PartnerSelfEmployedId.toString-> JsBoolean(true) ))

      val result = cascadeUpsert(YourPartnersAgeId.toString, twentyToTwentyFour, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString->JsString(twentyToTwentyFour))
    }

    "removing an existing yourMinimumEarnings, maximumEarnings when user change the selection to age over 25" in {
      val originalCacheMap = new CacheMap("id", Map(YourPartnersAgeId.toString->JsString(under18),
        PartnerMinimumEarningsId.toString->JsBoolean(true)))

      val result = cascadeUpsert(YourPartnersAgeId.toString, overTwentyFive, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString->JsString(overTwentyFive))
    }

    "not removing an existing yourMinimumEarnings maximum earnings when user change the selection to age under18 again" in {
      val originalCacheMap = new CacheMap("id", Map(YourPartnersAgeId.toString->JsString(under18),
        PartnerMinimumEarningsId.toString->JsBoolean(true)))

      val result = cascadeUpsert(YourPartnersAgeId.toString, under18, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.toString->JsString(under18),
        PartnerMinimumEarningsId.toString->JsBoolean(true))
    }
  }

  "saving the your minimumEarnings" must {
    "remove your maximum earnings and either of you max earnings whenparent in paid employment and your minimum earnings is no" in {
      val originalCacheMap = new CacheMap("id", Map(YourMaximumEarningsId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(YourMinimumEarningsId.toString, false, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(false))
    }


    "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
      val originalCacheMap = new CacheMap("id", Map(AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true),YourSelfEmployedId.toString->JsBoolean(true)))

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
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true),PartnerSelfEmployedId.toString->JsBoolean(true)))

      val result = cascadeUpsert(PartnerMinimumEarningsId.toString, true, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(true))
    }
  }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))

      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  // Need to work on clearence for maximum earnings 'no' to clear noOfChildren data and further

}
