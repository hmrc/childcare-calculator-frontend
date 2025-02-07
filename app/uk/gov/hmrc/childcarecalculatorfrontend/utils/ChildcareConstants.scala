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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.models._

object ChildcareConstants {

  val maxFreeHours = BigDecimal(30)

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

  val YES: String = YesNoUnsureEnum.YES.toString
  val NO: String = YesNoUnsureEnum.NO.toString
  val NOTSURE: String = YesNoUnsureEnum.NOTSURE.toString

  val no = YesNoNotYetEnum.NO.toString
  val yes = YesNoNotYetEnum.YES.toString
  val notYet = YesNoNotYetEnum.NOTYET.toString

  val incomeBenefits: String = WhichBenefitsEnum.INCOMEBENEFITS.toString
  val disabilityBenefits: String = WhichBenefitsEnum.DISABILITYBENEFITS.toString
  val highRatedDisabilityBenefits: String = WhichBenefitsEnum.HIGHRATEDISABILITYBENEFITS.toString
  val severelyDisabledPremium: String = WhichBenefitsEnum.SEVEREDISABILITYPREMIUM.toString
  val carersAllowanceBenefits: String = WhichBenefitsEnum.CARERSALLOWANCE.toString
  val scottishCarersAllowanceBenefits: String = WhichBenefitsEnum.SCOTTISHCARERSALLOWANCE.toString

  val firstMonthOfTaxYear = 4
  val startDayOfTaxYear = 6

  val lastMonthOfTaxYear = 4
  val lastDayOfTaxYear = 5

  val freeHoursForEngland = 15
  val freeHoursForScotland = 22
  val freeHoursForWales = 10
  val freeHoursForNI = 12.5
  val eligibleMaxFreeHours = 30
  val sixteenHours = 16
  val twentyFoursHours = 24

  val totalNoOfHoursAYear = 570
  val noOfFreeHours30 = 30
  val noOfFreeHours15 = 15

  val nineteen = 19
  val sixteen = 16


  val ucSchemeGuidanceLinkUrl = "https://www.gov.uk/guidance/universal-credit-childcare-costs"

  val extraHelpTFCLink = "https://www.gov.uk/apply-for-tax-free-childcare"

  val scotlandExtraHelpLink1 = "https://www.parentclub.scot/articles/whats-your-child-entitled-to"
  val scotlandExtraHelpLink2 = "https://www.parentclub.scot/articles/get-grips-childcare-costs-and-benefits"

  val walesExtraHelpLink1a = "https://gov.wales/childcare-support-parents-whilst-training-or-looking-work"
  val walesExtraHelpLink1b = "https://gov.wales/childcare-3-and-4-year-olds"

  val northernIrelandExtraHelpLink = "https://www.nidirect.gov.uk/articles/pre-school-education-places"

  val universalCredits = "uc"
  val taxCredits = "tc"
  val unknownErrorKey = "error.unknown"

  val locationErrorKey = "location.error.notCompleted"

  val areYouInPaidWorkErrorKey = "areYouInPaidWork.error.notCompleted"
  val doYouLiveWithPartnerErrorKey = "doYouLiveWithPartner.error.notCompleted"
  val whoIsInPaidEmploymentErrorKey = "whoIsInPaidEmployment.error.notCompleted"
  val paidEmploymentErrorKey = "paidEmployment.error.notCompleted"

  val whichBenefitsYouGetErrorKey = "whichBenefitsYouGet.error.notCompleted"
  val whichBenefitsPartnerGetErrorKey = "whichBenefitsPartnerGet.error.notCompleted"

  val doYouKnowYourPartnerAdjustedTaxCodeErrorKey = "doYouKnowYourPartnersAdjustedTaxCode.error.notCompleted"
  val taxCodeLength_six = 6
  val taxCodeLength_four = 4
  val taxCodeLength_five = 5
  val two = 2
  val one = 1
  val taxCode0T = "0T"

  val yourAgeErrorKey = "yourAge.error.notCompleted"
  val yourPartnersAgeErrorKey = "yourPartnersAge.error.notCompleted"

  val yourMinimumEarningsErrorKey = "yourMinimumEarnings.error.notCompleted"
  val partnerMinimumEarningsErrorKey = "partnerMinimumEarnings.error.notCompleted"
  val yourMaximumEarningsErrorKey = "yourMaximumEarnings.error.notCompleted"

  val partnerMaximumEarningsErrorKey = "partnerMaximumEarnings.error.notCompleted"
  val eitherOfYouMaximumEarningsErrorKey = "eitherOfYouMaximumEarnings.error.notCompleted"

  val yourSelfEmployedErrorKey = "yourSelfEmployed.error.notCompleted"

  val ruleDateConfigParam = "rule-date"
  val ccDateFormat = "dd-MM-yyyy"
  val nmwConfigFileAbbreviation = "nmw"

  val taxCodeRegularExpression = """([K][1-9][0-9]{2,3})|([1-9][0-9]{2,3}(L|M|N|BR|D0|D1|NT|S|0T|W1|M1|X)+)"""

  val partnerSelfEmployedErrorKey = "partnerSelfEmployed.error.notCompleted"

  val parentPaidWorkCYErrorKey = "parentPaidWorkCY.error.notCompleted"
  val partnerPaidWorkCYErrorKey = "partnerPaidWorkCY.error.notCompleted"
  val bothPaidWorkCYErrorKey = "bothPaidWorkCY.error.notCompleted"

  val whoPaysIntoPensionErrorKey = "whoPaysIntoPension.error.notCompleted"
  val whoGetsOtherIncomeCYErrorKey = "whoGetsOtherIncomeCY.error.notCompleted"
  val employmentIncomeInvalidErrorKey = "employmentIncome.error.invalid"

  val parentEmploymentIncomeBlankErrorKey = "parentEmploymentIncomeCY.error.blank"
  val parentEmploymentIncomeInvalidErrorKey = "parentEmploymentIncomeCY.error.invalid"
  val parentEmploymentIncomeInvalidMaxEarningsErrorKey = "parentEmploymentIncomeCY.maxEarnings.error.invalid"
  val parentEmploymentIncomeBothInvalidMaxEarningsErrorKey = "parentEmploymentIncomeCY.both.maxEarnings.error.invalid"
  val partnerEmploymentIncomeBlankErrorKey = "partnerEmploymentIncomeCY.error.blank"
  val partnerEmploymentIncomeInvalidErrorKey = "partnerEmploymentIncomeCY.error.invalid"
  val partnerEmploymentIncomeInvalidMaxEarningsErrorKey = "partnerEmploymentIncomeCY.maxEarnings.error.invalid"
  val partnerEmploymentIncomeBothInvalidMaxEarningsErrorKey = "partnerEmploymentIncomeCY.both.maxEarnings.error.invalid"
  val whoOtherIncomePYErrorKey = "whoOtherIncomePY.error.notCompleted"

  val youAnyTheseBenefitsCYErrorKey = "youAnyTheseBenefitsCY.error.notCompleted"
  val youAnyTheseBenefitsCYCarerAllowanceErrorKey = "youAnyTheseBenefitsCY.error.carers.allowance"
  val youAnyTheseBenefitsCYScottishCarerAllowanceErrorKey = "youAnyTheseBenefitsCY.error.scottishCarers.allowance"
  val bothAnyTheseBenefitsCYErrorKey = "bothAnyTheseBenefitsCY.error.notCompleted"
  val bothAnyTheseBenefitsCYCarerAllowanceErrorKey = "bothAnyTheseBenefitsCY.error.carers.allowance"
  val bothAnyTheseBenefitsCYScottishCarerAllowanceErrorKey = "bothAnyTheseBenefitsCY.error.scottishCarers.allowance"
  val youAnyTheseBenefitsPYErrorKey = "youAnyTheseBenefitsPY.error.notCompleted"
  val partnerAnyTheseBenefitsPYErrorKey = "partnerAnyTheseBenefitsPY.error.notCompleted"
  val bothAnyTheseBenefitsPYErrorKey = "bothAnyTheseBenefitsPY.error.notCompleted"
  val whosHadBenefitsErrorKey = "whosHadBenefits.error.notCompleted"
  val whosHadBenefitsPYErrorKey = "whosHadBenefitsPY.error.notCompleted"

  val parentPaidWorkPYErrorKey = "parentPaidWorkPY.error.notCompleted"
  val partnerPaidWorkPYErrorKey = "partnerPaidWorkPY.error.notCompleted"
  val bothPaidWorkPYErrorKey = "bothPaidWorkPY.error.notCompleted"

  val bothBenefitsIncomeCYErrorKey = "bothBenefitsIncomeCY.error.notCompleted"

  val howMuchYouPayPensionInvalidErrorKey = "howMuchYouPayPension.error.invalid"
  val howMuchYouPayPensionRequiredErrorKey = "howMuchYouPayPension.error.required"
  val howMuchPartnerPayPensionInvalidErrorKey = "howMuchPartnerPayPension.error.invalid"
  val howMuchPartnerPayPensionRequiredErrorKey = "howMuchPartnerPayPension.error.required"
  val howMuchBothPayPensionInvalidErrorKey = "howMuchBothPayPension.error.invalid"
  val howMuchBothPayPensionRequiredErrorKey = "howMuchBothPayPension.error.required"

  val howMuchYouPayPensionPYInvalidErrorKey = "howMuchYouPayPensionPY.error.invalid"
  val howMuchYouPayPensionPYRequiredErrorKey = "howMuchYouPayPensionPY.error.required"
  val howMuchPartnerPayPensionPYInvalidErrorKey = "howMuchPartnerPayPensionPY.error.invalid"
  val howMuchPartnerPayPensionPYRequiredErrorKey = "howMuchPartnerPayPensionPY.error.required"

  val parentBenefitsIncomeCYRequiredErrorKey = "parentBenefitsIncome.error.required"
  val partnerBenefitsIncomeCYRequiredErrorKey = "partnerBenefitsIncome.error.required"
  val partnerBenefitsIncomeCYInvalidErrorKey = "partnerBenefitsIncome.error.invalid"
  val parentBenefitsIncomePYRequiredErrorKey = "parentBenefitsIncomePY.error.required"
  val partnerBenefitsIncomePYRequiredErrorKey = "partnerBenefitsIncomePY.error.required"
  val parentBenefitsIncomePYInvalidErrorKey = "parentBenefitsIncomePY.error.invalid"
  val partnerBenefitsIncomePYInvalidErrorKey = "partnerBenefitsIncomePY.error.invalid"
  val parentBenefitsIncomeInvalidErrorKey = "parentBenefitsIncome.error.invalid"
  val partnerBenefitsIncomeInvalidErrorKey = "partnerBenefitsIncome.error.invalid"

  val whoPaidIntoPensionErrorKey = "whoPaidIntoPensionPY.error.notCompleted"

  val parentOtherIncomeRequiredErrorKey = "parentOtherIncome.error.required"
  val parentOtherIncomeInvalidErrorKey = "parentOtherIncome.error.invalid"
  val partnerOtherIncomeRequiredErrorKey = "partnerOtherIncome.error.required"
  val partnerOtherIncomeInvalidErrorKey = "partnerOtherIncome.error.invalid"
  val parentOtherIncomePYRequiredErrorKey = "parentOtherIncomePY.error.required"
  val parentOtherIncomePYInvalidErrorKey = "parentOtherIncomePY.error.invalid"
  val partnerOtherIncomePYRequiredErrorKey = "partnerOtherIncomePY.error.required"
  val partnerOtherIncomePYInvalidErrorKey = "partnerOtherIncomePY.error.invalid"

  val childcareCostsErrorKey = "childcareCosts.error.notCompleted"

  val eitherGetsVouchersErrorKey = "eitherGetsVouchers.error.notCompleted"
  val approvedProviderErrorKey = "approvedProvider.error.notCompleted"

  val selfEmployedOrApprenticeErrorKey = "areYouSelfEmployedOrApprentice.error.notCompleted"
  val noOfChildrenErrorKey = "noOfChildren.error.notCompleted"
  val noOfChildrenRequiredErrorKey = "noOfChildren.error.required"
  val noOfChildrenNotInteger = "noOfChildren.error.non_numeric"
  val partnerChildcareVouchersErrorKey = "partnerChildcareVouchers.error.notCompleted"

  val partnerSelfEmployedOrApprenticeErrorKey = "partnerSelfEmployedOrApprentice.error.notCompleted"
  val whatIsYourPartnersTaxCodeBlankErrorKey = "whatIsYourPartnersTaxCode.error.blank"
  val whatIsYourPartnersTaxCodeInvalidErrorKey = "whatIsYourPartnersTaxCode.error.invalid"
  val whatIsYourTaxCodeInvalidErrorKey = "whatIsYourTaxCode.error.invalid"
  val whatIsYourTaxCodeBlankErrorKey = "whatIsYourTaxCode.error.blank"
  val whoGetsVouchersErrorKey = "whoGetsVouchers.error.notCompleted"
  val yourChildcareVoucherErrorKey = "yourChildcareVouchers.error.notCompleted"

  val youBenefitsIncomeCYInvalidErrorKey = "youBenefitsIncomeCY.error.invalid"
  val youBenefitsIncomeCYRequiredErrorKey = "youBenefitsIncomeCY.error.required"

  val parentOtherIncomeAmountPYRequiredErrorKey = "parentOtherIncomeAmountPY.error.required"
  val parentOtherIncomeAmountPYInvalidErrorKey = "parentOtherIncomeAmountPY.error.invalid"
  val partnerOtherIncomeAmountPYRequiredErrorKey = "partnerOtherIncomeAmountPY.error.required"
  val partnerOtherIncomeAmountPYInvalidErrorKey = "partnerOtherIncomeAmountPY.error.invalid"

  val parentEmploymentIncomePYRequiredErrorKey = "parentEmploymentIncomePY.error.required"
  val parentEmploymentIncomePYInvalidErrorKey = "parentEmploymentIncomePY.error.invalid"
  val partnerEmploymentIncomePYRequiredErrorKey = "partnerEmploymentIncomePY.error.required"
  val partnerEmploymentIncomePYInvalidErrorKey = "partnerEmploymentIncomePY.error.invalid"

  val whoGotStatutoryPayErrorKey = "whoGotStatutoryPay.error.notCompleted"

  val yourStatutoryPayTypeErrorKey = "yourStatutoryPayType.error.notCompleted"
  val partnerStatutoryPayTypeErrorKey = "partnerStatutoryPayType.error.notCompleted"

  val whoWasInPaidWorkErrorKey = "whoWasInPaidWorkPY.error.notCompleted"

  val surveyChildcareSupportErrorKey = "surveyChildcareSupport.error.notCompleted"

  val parentEmpIncomeCYFormField = "parentEmploymentIncomeCY"
  val partnerEmpIncomeCYFormField = "partnerEmploymentIncomeCY"
  val defaultFormValueField = "value"

  val whoGetsTheBenefitsErrorKey = "whoGetsTheBenefits.error.notCompleted"
}
