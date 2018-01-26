/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.models._

object ChildcareConstants {

  val YouSelected: String = YouPartnerBothNeitherEnum.YOU.toString
  val PartnerSelected: String = YouPartnerBothNeitherEnum.PARTNER.toString
  val BothSelected: String = YouPartnerBothNeitherEnum.BOTH.toString
  val NeitherSelected: String = YouPartnerBothNeitherEnum.NEITHER.toString

  val you: String = YouPartnerBothEnum.YOU.toString
  val partner: String = YouPartnerBothEnum.PARTNER.toString
  val both: String = YouPartnerBothEnum.BOTH.toString
  val neither: String = YouPartnerBothNeitherEnum.NEITHER.toString
  val notSure: String = YouPartnerBothNeitherNotSureEnum.NOTSURE.toString

  // First letter is capital in naming for below constants as these are being used in case statements
  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString

  val Yes: String = YesNoUnsureEnum.YES.toString
  val No: String = YesNoUnsureEnum.NO.toString
  val NotSure: String = YesNoUnsureEnum.NOTSURE.toString

  val no = YesNoNotYetEnum.NO.toString
  val yes = YesNoNotYetEnum.YES.toString
  val notYet = YesNoNotYetEnum.NOTYET.toString

  val IncomeBenefits: String = WhichBenefitsEnum.INCOMEBENEFITS.toString
  val DisabilityBenefits: String = WhichBenefitsEnum.DISABILITYBENEFITS.toString
  val HighRatedDisabilityBenefits: String = WhichBenefitsEnum.HIGHRATEDISABILITYBENEFITS.toString
  val CarersAllowanceBenefits: String = WhichBenefitsEnum.CARERSALLOWANCE.toString

  val firstMonthOfTaxYear = 4
  val startDayOfTaxYear = 6

  val lastMonthOfTaxYear = 4
  val lastDayOfTaxYear = 5

  val freeHoursForEngland = 15
  val freeHoursForScotland = 16
  val freeHoursForWales = 10
  val freeHoursForNI = 12.5
  val eligibleMaxFreeHours = 30

  val totalNoOfHoursAYear = 570
  val noOfFreeHours15 = 15

  val universalCredits = "uc"
  val taxCredits = "tc"
  val unknownErrorKey = "error.unknown"

  val locationErrorKey = "location.error"
  val northernIreland = "northernIreland"

  val areYouInPaidWorkErrorKey = "areYouInPaidWork.error"
  val doYouLiveWithPartnerErrorKey = "doYouLiveWithPartner.error"
  val whoIsInPaidEmploymentErrorKey = "whoIsInPaidEmployment.error"
  val paidEmploymentErrorKey = "paidEmployment.error"

  val parentWorkHoursBlankErrorKey = "parentWorkHours.blank"
  val parentWorkHoursInvalidErrorKey = "parentWorkHours.invalid"
  val partnerWorkHoursBlankErrorKey = "partnerWorkHours.blank"
  val partnerWorkHoursInvalidErrorKey = "partnerWorkHours.invalid"

  val doYouGetAnyBenefitsErrorKey = "doYouGetAnyBenefits.error"
  val doYouOrYourPartnerGetAnyBenefitsErrorKey = "doYouOrYourPartnerGetAnyBenefits.error"

  val whoGetsBenefitsErrorKey = "whoGetsBenefits.error"
  val whichBenefitsYouGetErrorKey = "whichBenefitsYouGet.error"
  val whichBenefitsPartnerGetErrorKey = "whichBenefitsPartnerGet.error"

  val doYouKnowYourPartnerAdjustedTaxCodeErrorKey = "doYouKnowYourPartnersAdjustedTaxCode.error"
  val taxCodeLength_six = 6
  val taxCodeLength_four = 4
  val taxCodeLength_five = 5
  val two = 2
  val one = 1
  val taxCode0T = "0T"

  val yourAgeErrorKey = "yourAge.error"
  val yourPartnersAgeErrorKey = "yourPartnersAge.error"

  val yourMinimumEarningsErrorKey = "yourMinimumEarnings.error"
  val partnerMinimumEarningsErrorKey = "partnerMinimumEarnings.error"
  val yourMaximumEarningsErrorKey = "yourMaximumEarnings.error"

  val partnerMaximumEarningsErrorKey = "partnerMaximumEarnings.error"
  val eitherOfYouMaximumEarningsErrorKey = "eitherOfYouMaximumEarnings.error"

  val yourSelfEmployedErrorKey = "yourSelfEmployed.error"

  val ruleDateConfigParam = "rule-date"
  val ccDateFormat = "dd-MM-yyyy"
  val nmwConfigFileAbbreviation = "nmw"

  val taxCodeRegularExpression = """([K][1-9][0-9]{2,3})|([1-9][0-9]{2,3}(L|M|N|BR|D0|D1|NT|S|0T|W1|M1|X)+)"""

  val partnerSelfEmployedErrorKey = "partnerSelfEmployed.error"

  val parentPaidWorkCYErrorKey = "parentPaidWorkCY.error"
  val partnerPaidWorkCYErrorKey = "partnerPaidWorkCY.error"
  val bothPaidWorkCYErrorKey = "bothPaidWorkCY.error"

  val whoPaysIntoPensionErrorKey = "whoPaysIntoPension.error"
  val whoGetsOtherIncomeCYErrorKey = "whoGetsOtherIncomeCY.error"
  val employmentIncomeInvalidErrorKey = "employmentIncome.invalid"

  val parentEmploymentIncomeBlankErrorKey = "parentEmploymentIncomeCY.blank"
  val parentEmploymentIncomeInvalidErrorKey = "parentEmploymentIncomeCY.invalid"
  val partnerEmploymentIncomeBlankErrorKey = "partnerEmploymentIncomeCY.blank"
  val partnerEmploymentIncomeInvalidErrorKey = "partnerEmploymentIncomeCY.invalid"
  val whoOtherIncomePYErrorKey = "whoOtherIncomePY.error"

  val youAnyTheseBenefitsCYErrorKey = "youAnyTheseBenefitsCY.error"
  val youAnyTheseBenefitsCYCarerAllowanceErrorKey = "youAnyTheseBenefitsCY.error.carers.allowance"
  val partnerAnyTheseBenefitsCYErrorKey = "partnerAnyTheseBenefitsCY.error"
  val partnerAnyTheseBenefitsCYCarerAllowanceErrorKey = "partnerAnyTheseBenefitsCY.error.carers.allowance"
  val bothAnyTheseBenefitsCYErrorKey = "bothAnyTheseBenefitsCY.error"
  val bothAnyTheseBenefitsCYCarerAllowanceErrorKey = "bothAnyTheseBenefitsCY.error.carers.allowance"
  val youAnyTheseBenefitsPYErrorKey = "youAnyTheseBenefitsPY.error"
  val partnerAnyTheseBenefitsPYErrorKey = "partnerAnyTheseBenefitsPY.error"
  val bothAnyTheseBenefitsPYErrorKey = "bothAnyTheseBenefitsPY.error"
  val whosHadBenefitsErrorKey = "whosHadBenefits.error"
  val whosHadBenefitsPYErrorKey = "whosHadBenefitsPY.error"

  val parentPaidWorkPYErrorKey = "parentPaidWorkPY.error"
  val partnerPaidWorkPYErrorKey = "partnerPaidWorkPY.error"
  val bothPaidWorkPYErrorKey = "bothPaidWorkPY.error"

  val bothBenefitsIncomeCYErrorKey = "bothBenefitsIncomeCY.error"

  val howMuchYouPayPensionInvalidErrorKey = "howMuchYouPayPension.invalid"
  val howMuchYouPayPensionRequiredErrorKey = "howMuchYouPayPension.required"
  val howMuchPartnerPayPensionInvalidErrorKey = "howMuchPartnerPayPension.invalid"
  val howMuchPartnerPayPensionRequiredErrorKey = "howMuchPartnerPayPension.required"
  val howMuchBothPayPensionInvalidErrorKey = "howMuchBothPayPension.invalid"
  val howMuchBothPayPensionRequiredErrorKey = "howMuchBothPayPension.required"

  val howMuchYouPayPensionPYInvalidErrorKey = "howMuchYouPayPensionPY.invalid"
  val howMuchYouPayPensionPYRequiredErrorKey = "howMuchYouPayPensionPY.required"
  val howMuchPartnerPayPensionPYInvalidErrorKey = "howMuchPartnerPayPensionPY.invalid"
  val howMuchPartnerPayPensionPYRequiredErrorKey = "howMuchPartnerPayPensionPY.required"

  val parentBenefitsIncomeCYRequiredErrorKey = "parentBenefitsIncome.required"
  val partnerBenefitsIncomeCYRequiredErrorKey = "partnerBenefitsIncome.required"
  val partnerBenefitsIncomeCYInvalidErrorKey = "partnerBenefitsIncome.invalid"
  val parentBenefitsIncomePYRequiredErrorKey = "parentBenefitsIncomePY.required"
  val partnerBenefitsIncomePYRequiredErrorKey = "partnerBenefitsIncomePY.required"
  val parentBenefitsIncomePYInvalidErrorKey = "parentBenefitsIncomePY.invalid"
  val partnerBenefitsIncomePYInvalidErrorKey = "partnerBenefitsIncomePY.invalid"
  val parentBenefitsIncomeInvalidErrorKey = "parentBenefitsIncome.invalid"
  val partnerBenefitsIncomeInvalidErrorKey = "partnerBenefitsIncome.invalid"

  val whoPaidIntoPensionErrorKey = "whoPaidIntoPensionPY.error"

  val parentOtherIncomeRequiredErrorKey = "parentOtherIncome.required"
  val parentOtherIncomeInvalidErrorKey = "parentOtherIncome.invalid"
  val partnerOtherIncomeRequiredErrorKey = "partnerOtherIncome.required"
  val partnerOtherIncomeInvalidErrorKey = "partnerOtherIncome.invalid"
  val parentOtherIncomePYRequiredErrorKey = "parentOtherIncomePY.required"
  val parentOtherIncomePYInvalidErrorKey = "parentOtherIncomePY.invalid"
  val partnerOtherIncomePYRequiredErrorKey = "partnerOtherIncomePY.required"
  val partnerOtherIncomePYInvalidErrorKey = "partnerOtherIncomePY.invalid"

  val childcareCostsErrorKey = "childcareCosts.error"

  val eitherGetsVouchersErrorKey = "eitherGetsVouchers.error"
  val approvedProviderErrorKey = "approvedProvider.error"

  val selfEmployedOrApprenticeErrorKey = "areYouSelfEmployedOrApprentice.error"
  val noOfChildrenErrorKey = "noOfChildren.error"
  val noOfChildrenRequiredErrorKey = "noOfChildren.required"
  val partnerChildcareVouchersErrorKey = "partnerChildcareVouchers.error"

  val partnerSelfEmployedOrApprenticeErrorKey = "partnerSelfEmployedOrApprentice.error"
  val whatIsYourPartnersTaxCodeBlankErrorKey = "whatIsYourPartnersTaxCode.blank"
  val whatIsYourPartnersTaxCodeInvalidErrorKey = "whatIsYourPartnersTaxCode.invalid"
  val whatIsYourTaxCodeInvalidErrorKey = "whatIsYourTaxCode.invalid"
  val whatIsYourTaxCodeBlankErrorKey = "whatIsYourTaxCode.blank"
  val whoGetsVouchersErrorKey = "whoGetsVouchers.error"
  val yourChildcareVoucherErrorKey = "yourChildcareVouchers.error"

  val youBenefitsIncomeCYInvalidErrorKey = "youBenefitsIncomeCY.invalid"
  val youBenefitsIncomeCYRequiredErrorKey = "youBenefitsIncomeCY.required"

  val parentOtherIncomeAmountPYRequiredErrorKey = "parentOtherIncomeAmountPY.required"
  val parentOtherIncomeAmountPYInvalidErrorKey = "parentOtherIncomeAmountPY.invalid"
  val partnerOtherIncomeAmountPYRequiredErrorKey = "partnerOtherIncomeAmountPY.required"
  val partnerOtherIncomeAmountPYInvalidErrorKey = "partnerOtherIncomeAmountPY.invalid"

  val parentEmploymentIncomePYRequiredErrorKey = "parentEmploymentIncomePY.required"
  val parentEmploymentIncomePYInvalidErrorKey = "parentEmploymentIncomePY.invalid"
  val partnerEmploymentIncomePYRequiredErrorKey = "partnerEmploymentIncomePY.required"
  val partnerEmploymentIncomePYInvalidErrorKey = "partnerEmploymentIncomePY.invalid"

  val whoGotStatutoryPayErrorKey = "whoGotStatutoryPay.error"

  val yourStatutoryPayTypeErrorKey = "yourStatutoryPayType.error"
  val partnerStatutoryPayTypeErrorKey = "partnerStatutoryPayType.error"

  val whoWasInPaidWorkErrorKey = "whoWasInPaidWorkPY.error"

}
