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

import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.youNoWeeksStatPayCYInvalidErrorKey

object ChildcareConstants {
  val you = "you"
  val partner = "partner"
  val both = "both"
  val no = "no"
  val yes = "yes"
  val unknownErrorKey = "error.unknown"
  val locationErrorKey = "location.error"
  val northernIreland = "northernIreland"
  val areYouInPaidWorkErrorKey = "areYouInPaidWork.error"
  val doYouLiveWithPartnerErrorKey = "doYouLiveWithPartner.error"
  val whoIsInPaidEmploymentErrorKey = "whoIsInPaidEmployment.error"
  val paidEmploymentErrorKey = "paidEmployment.error"
  val workHoursBlankErrorKey = "workHours.blank"
  val workHoursInvalidErrorKey = "workHours.invalid"
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
  val partnerSelfEmployedErrorKey = "partnerSelfEmployed.error"
  val parentPaidWorkCYErrorKey = "parentPaidWorkCY.error"
  val partnerPaidWorkCYErrorKey = "partnerPaidWorkCY.error"
  val bothPaidWorkCYErrorKey = "bothPaidWorkCY.error"
  val whoPaysIntoPensionErrorKey = "whoPaysIntoPension.error"
  val whoGetsOtherIncomeCYErrorKey = "whoGetsOtherIncomeCY.error"
  val whoOtherIncomePYErrorKey = "whoOtherIncomePY.error"
  val youAnyTheseBenefitsCYErrorKey = "youAnyTheseBenefitsCY.error"
  val partnerAnyTheseBenefitsCYErrorKey = "partnerAnyTheseBenefitsCY.error"
  val bothAnyTheseBenefitsCYErrorKey = "bothAnyTheseBenefitsCY.error"
  val youAnyTheseBenefitsPYErrorKey = "youAnyTheseBenefitsPY.error"
  val partnerAnyTheseBenefitsPYErrorKey = "partnerAnyTheseBenefitsPY.error"
  val bothAnyTheseBenefitsPYErrorKey = "bothAnyTheseBenefitsPY.error"
  val whosHadBenefitsErrorKey = "whosHadBenefits.error"
  val whosHadBenefitsPYErrorKey = "whosHadBenefitsPY.error"
  val whoGetsStatutoryCYErrorKey = "whoGetsStatutoryCY.error"
  val whoGetsStatutoryPYErrorKey = "whoGetsStatutoryPY.error"
  val yourStatutoryPayCYErrorKey = "yourStatutoryPayCY.error"
  val partnerStatutoryPayCYErrorKey = "partnerStatutoryPayCY.error"
  val bothStatutoryPayCYErrorKey = "bothStatutoryPayCY.error"
  val parentPaidWorkPYErrorKey = "parentPaidWorkPY.error"
  val partnerPaidWorkPYErrorKey = "partnerPaidWorkPY.error"
  val bothPaidWorkPYErrorKey = "bothPaidWorkPY.error"
  val youNoWeeksStatPayCYErrorKey = "youNoWeeksStatPayCY.error"
  val youNoWeeksStatPayCYInvalidErrorKey = "youNoWeeksStatPayCY.invalid"
  val youNoWeeksStatPayCYNumericErrorKey = "youNoWeeksStatPayCY.numeric.error"
  val parentBenefitsIncomeCYRequiredErrorKey = "parentBenefitsIncome.required"
  val partnerBenefitsIncomeCYRequiredErrorKey = "partnerBenefitsIncome.required"
  val whoPaidIntoPensionErrorKey = "whoPaidIntoPensionPY.error"

  val parentOtherIncomeRequiredErrorKey = "parentOtherIncome.required"
  val parentOtherIncomeInvalidErrorKey = "parentOtherIncome.invalid"
  val partnerOtherIncomeRequiredErrorKey = "partnerOtherIncome.required"
  val partnerOtherIncomeInvalidErrorKey = "partnerOtherIncome.invalid"
}
