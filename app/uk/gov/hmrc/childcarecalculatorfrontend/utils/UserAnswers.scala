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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPension, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BenefitsIncomeCY, EmploymentIncomeCY, OtherIncomeAmountCY, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models._

class UserAnswers(val cacheMap: CacheMap) extends EligibilityChecks {
  def partnerStatutoryPayAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerStatutoryPayAmountPYId.toString)

  def yourStatutoryPayAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourStatutoryPayAmountPYId.toString)

  def childDisabilityBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](ChildDisabilityBenefitsId.toString)

  def otherIncomeAmountPY: Option[OtherIncomeAmountPY] = cacheMap.getEntry[OtherIncomeAmountPY](OtherIncomeAmountPYId.toString)

  def partnerOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountPYId.toString)

  def yourOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountPYId.toString)

  def statutoryPayAmountCY: Option[StatutoryPayAmountCY] = cacheMap.getEntry[StatutoryPayAmountCY](StatutoryPayAmountCYId.toString)

  def yourStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](YourStatutoryPayPYId.toString)

  def bothStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayPYId.toString)

  def partnerStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayPYId.toString)

  def youBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomePYId.toString)

  def partnerBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomePYId.toString)

  def bothBenefitsIncomePY: Option[BothBenefitsIncomePY] = cacheMap.getEntry[BothBenefitsIncomePY](BothBenefitsIncomePYId.toString)

  def howMuchBothPayPension: Option[HowMuchBothPayPension] = cacheMap.getEntry[HowMuchBothPayPension](HowMuchBothPayPensionId.toString)

  def howMuchPartnerPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchPartnerPayPensionId.toString)

  def howMuchYouPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchYouPayPensionId.toString)

  def registeredBlind: Option[Boolean] = cacheMap.getEntry[Boolean](RegisteredBlindId.toString)

  def statutoryPayAWeekLY: Option[Boolean] = cacheMap.getEntry[Boolean](StatutoryPayAWeekLYId.toString)

  def benefitsIncomeCY: Option[BenefitsIncomeCY] = cacheMap.getEntry[BenefitsIncomeCY](BenefitsIncomeCYId.toString)

  def partnerStatutoryPayAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerStatutoryPayAmountCYId.toString)

  def yourStatutoryPayAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourStatutoryPayAmountCYId.toString)

  def employmentIncomeCY: Option[EmploymentIncomeCY] = cacheMap.getEntry[EmploymentIncomeCY](EmploymentIncomeCYId.toString)

  def partnerOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountCYId.toString)

  def yourOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountCYId.toString)

  def otherIncomeAmountCY: Option[OtherIncomeAmountCY] = cacheMap.getEntry[OtherIncomeAmountCY](OtherIncomeAmountCYId.toString)

  def bothOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeLYId.toString)

  def whosHadBenefitsPY: Option[String] = cacheMap.getEntry[String](WhosHadBenefitsPYId.toString)

  def partnerAnyOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyOtherIncomeLYId.toString)

  def bothAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsPYId.toString)

  def partnerAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyTheseBenefitsPYId.toString)

  def youAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](YouAnyTheseBenefitsPYId.toString)

  def whoPaidIntoPensionPY: Option[String] = cacheMap.getEntry[String](WhoPaidIntoPensionPYId.toString)

  def whoOtherIncomePY: Option[String] = cacheMap.getEntry[String](WhoOtherIncomePYId.toString)

  def whoGetsStatutoryPY: Option[String] = cacheMap.getEntry[String](WhoGetsStatutoryPYId.toString)

  def youBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomeCYId.toString)

  def partnerBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomeCYId.toString)

  def yourOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](YourOtherIncomeLYId.toString)

  def bothPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionPYId.toString)

  def partnerPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionPYId.toString)

  def youPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionPYId.toString)

  def statutoryPayAWeek: Option[Boolean] = cacheMap.getEntry[Boolean](StatutoryPayAWeekId.toString)

  def bothPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkPYId.toString)

  def partnerPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkPYId.toString)

  def parentPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkPYId.toString)

  def bothStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayCYId.toString)

  def partnerStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayCYId.toString)

  def yourStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](YourStatutoryPayCYId.toString)

  def bothOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeThisYearId.toString)

  def bothPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionCYId.toString)

  def PartnerPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionCYId.toString)

  def YouPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionCYId.toString)

  def whosHadBenefits: Option[String] = cacheMap.getEntry[String](WhosHadBenefitsId.toString)

  def whoGetsStatutoryCY: Option[String] = cacheMap.getEntry[String](WhoGetsStatutoryCYId.toString)

  def bothAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsCYId.toString)

  def partnerAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyTheseBenefitsCYId.toString)

  def youAnyTheseBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](YouAnyTheseBenefitsIdCY.toString)

  def partnerEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerEmploymentIncomeCYId.toString)

  def parentEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentEmploymentIncomeCYId.toString)

  def whoGetsOtherIncomeCY: Option[String] = cacheMap.getEntry[String](WhoGetsOtherIncomeCYId.toString)

  def bothPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkCYId.toString)

  def partnerPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkCYId.toString)

  def parentPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkCYId.toString)

  def whoPaysIntoPension: Option[String] = cacheMap.getEntry[String](WhoPaysIntoPensionId.toString)

  def partnerAnyOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyOtherIncomeThisYearId.toString)

  def yourOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](YourOtherIncomeThisYearId.toString)

  def eitherOfYouMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](EitherOfYouMaximumEarningsId.toString)

  def noOfChildren: Option[Int] = cacheMap.getEntry[Int](NoOfChildrenId.toString)

  def taxOrUniversalCredits: Option[String] = cacheMap.getEntry[String](TaxOrUniversalCreditsId.toString)

  def partnerMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMaximumEarningsId.toString)

  def yourMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMaximumEarningsId.toString)

  def yourSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](YourSelfEmployedId.toString)

  def partnerSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerSelfEmployedId.toString)

  def partnerSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](PartnerSelfEmployedOrApprenticeId.toString)

  def areYouSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](AreYouSelfEmployedOrApprenticeId.toString)

  def partnerMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMinimumEarningsId.toString)

  def yourMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMinimumEarningsId.toString)
    
  def yourAge: Option[String] = cacheMap.getEntry[String](YourAgeId.toString)

  def yourPartnersAge: Option[String] = cacheMap.getEntry[String](YourPartnersAgeId.toString)

  def whichBenefitsYouGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsYouGetId.toString)

  def whichBenefitsPartnerGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsPartnerGetId.toString)

  def whoGetsBenefits: Option[String] = cacheMap.getEntry[String](WhoGetsBenefitsId.toString)

  def doYouOrYourPartnerGetAnyBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouOrYourPartnerGetAnyBenefitsId.toString)

  def doYouGetAnyBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouGetAnyBenefitsId.toString)

  def whoGetsVouchers: Option[String] = cacheMap.getEntry[String](WhoGetsVouchersId.toString)

  def eitherGetsVouchers: Option[String] = cacheMap.getEntry[String](EitherGetsVouchersId.toString)

  def yourChildcareVouchers: Option[String] = cacheMap.getEntry[String](YourChildcareVouchersId.toString)

  def partnerChildcareVouchers: Option[String] = cacheMap.getEntry[String](PartnerChildcareVouchersId.toString)

  def whatIsYourTaxCode: Option[String] = cacheMap.getEntry[String](WhatIsYourTaxCodeId.toString)

  def whatIsYourPartnersTaxCode: Option[String] = cacheMap.getEntry[String](WhatIsYourPartnersTaxCodeId.toString)

  def hasYourPartnersTaxCodeBeenAdjusted: Option[Boolean] = cacheMap.getEntry[Boolean](HasYourPartnersTaxCodeBeenAdjustedId.toString)

  def hasYourTaxCodeBeenAdjusted: Option[Boolean] = cacheMap.getEntry[Boolean](HasYourTaxCodeBeenAdjustedId.toString)

  def doYouKnowYourPartnersAdjustedTaxCode: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouKnowYourPartnersAdjustedTaxCodeId.toString)

  def doYouKnowYourAdjustedTaxCode: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouKnowYourAdjustedTaxCodeId.toString)

  def partnerWorkHours: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerWorkHoursId.toString)

  def parentWorkHours: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentWorkHoursId.toString)

  def whoIsInPaidEmployment: Option[String] = cacheMap.getEntry[String](WhoIsInPaidEmploymentId.toString)

  def paidEmployment: Option[Boolean] = cacheMap.getEntry[Boolean](PaidEmploymentId.toString)

  def areYouInPaidWork: Option[Boolean] = cacheMap.getEntry[Boolean](AreYouInPaidWorkId.toString)

  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouLiveWithPartnerId.toString)

  def approvedProvider: Option[String] = cacheMap.getEntry[String](ApprovedProviderId.toString)

  def childcareCosts: Option[String] = cacheMap.getEntry[String](ChildcareCostsId.toString)

  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)

  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)

  def location: Option[String] = cacheMap.getEntry[String](LocationId.toString)

  def hasBothInPaidWork: Boolean = {
    doYouLiveWithPartner.contains(true) && whoIsInPaidEmployment.contains(YouPartnerBothEnum.BOTH.toString)
  }

  def hasPartnerInPaidWork: Boolean = {
    doYouLiveWithPartner.contains(true) && whoIsInPaidEmployment.contains(YouPartnerBothEnum.PARTNER.toString)
  }
}
