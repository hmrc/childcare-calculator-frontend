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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import play.api.libs.json.{Format, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.EnumUtils

trait YouPartnerBothBaseEnumeration extends Enumeration{
  val YOU = Value("you")
  val PARTNER = Value("partner")
  val BOTH = Value("both")
}

trait NeitherBaseEnumeration extends Enumeration{
  val NEITHER = Value("neither")
}

object Location extends Enumeration {
  type Location = Value
  val ENGLAND = Value("england")
  val SCOTLAND = Value("scotland")
  val WALES = Value("wales")
  val NORTHERN_IRELAND = Value("northern-ireland")

  val enumReads: Reads[Location] = EnumUtils.enumReads(Location)

  val enumWrites: Writes[Location] = EnumUtils.enumWrites

  implicit def enumFormats: Format[Location] = EnumUtils.enumFormat(Location)
}

object YouPartnerBothEnum extends YouPartnerBothBaseEnumeration  {
  type YouPartnerBothEnum = Value

  val enumReads: Reads[YouPartnerBothEnum] = EnumUtils.enumReads(YouPartnerBothEnum)
  val enumWrites: Writes[YouPartnerBothEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YouPartnerBothEnum] = EnumUtils.enumFormat(YouPartnerBothEnum)
}

object YouPartnerBothNeitherEnum extends YouPartnerBothBaseEnumeration with NeitherBaseEnumeration {
  type YouPartnerBothNeitherEnum = Value

  val enumReads: Reads[YouPartnerBothNeitherEnum] = EnumUtils.enumReads(YouPartnerBothNeitherEnum)
  val enumWrites: Writes[YouPartnerBothNeitherEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YouPartnerBothNeitherEnum] = EnumUtils.enumFormat(YouPartnerBothNeitherEnum)
}

object YouPartnerBothNeitherNotSureEnum extends YouPartnerBothBaseEnumeration with NeitherBaseEnumeration {
  type YouPartnerBothNeitherNotSureEnum = Value

  val NOTSURE = Value("notSure")

  val enumReads: Reads[YouPartnerBothNeitherNotSureEnum] = EnumUtils.enumReads(YouPartnerBothNeitherNotSureEnum)
  val enumWrites: Writes[YouPartnerBothNeitherNotSureEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YouPartnerBothNeitherNotSureEnum] = EnumUtils.enumFormat(YouPartnerBothNeitherNotSureEnum)
}

object YesNoUnsureEnum extends Enumeration {
  type YesNoUnsureEnum = Value
  val YES, NO, NOTSURE = Value
  val enumReads: Reads[YesNoUnsureEnum] = EnumUtils.enumReads(YesNoUnsureEnum)

  val enumWrites: Writes[YesNoUnsureEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YesNoUnsureEnum] = EnumUtils.enumFormat(YesNoUnsureEnum)
}

object YesNoEnum extends Enumeration {
  type YesNoEnum = Value
  val YES, NO = Value
  val enumReads: Reads[YesNoEnum] = EnumUtils.enumReads(YesNoEnum)

  val enumWrites: Writes[YesNoEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YesNoEnum] = EnumUtils.enumFormat(YesNoEnum)
}

object YesNoNotYetEnum extends Enumeration {
  type YesNoNotYetEnum = Value
  val YES = Value("yes")
  val NO = Value("no")
  val NOTYET = Value("notYet")

  val enumReads: Reads[YesNoNotYetEnum] = EnumUtils.enumReads(YesNoNotYetEnum)
  val enumWrites: Writes[YesNoNotYetEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YesNoNotYetEnum] = EnumUtils.enumFormat(YesNoNotYetEnum)
}

object AgeEnum extends Enumeration {
  type AgeEnum = Value

  val UNDER18 = Value("UNDER18")
  val EIGHTEENTOTWENTY = Value("EIGHTEENTOTWENTY")
  val TWENTYONEOROVER = Value("TWENTYONEOROVER")

  val enumReads: Reads[AgeEnum] = EnumUtils.enumReads(AgeEnum)
  val enumWrites: Writes[AgeEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[AgeEnum] = EnumUtils.enumFormat(AgeEnum)
}

object SelfEmployedOrApprenticeOrNeitherEnum extends Enumeration {
  type SelfEmployedOrApprenticeOrNeitherEnum = Value

  val SELFEMPLOYED = Value("selfEmployed")
  val APPRENTICE = Value("apprentice")
  val NEITHER = Value("neither")

  val enumReads: Reads[SelfEmployedOrApprenticeOrNeitherEnum] = EnumUtils.enumReads(SelfEmployedOrApprenticeOrNeitherEnum)
  val enumWrites: Writes[SelfEmployedOrApprenticeOrNeitherEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[SelfEmployedOrApprenticeOrNeitherEnum] = EnumUtils.enumFormat(SelfEmployedOrApprenticeOrNeitherEnum)
}

object DisabilityBenefits extends Enumeration {

  val DISABILITY_BENEFITS = Value("disability-benefits")
  val HIGHER_DISABILITY_BENEFITS = Value("higher-disability-benefit")

  val reads: Reads[Value] = EnumUtils.enumReads(DisabilityBenefits)
  val writes: Writes[Value] = EnumUtils.enumWrites

  implicit def enumFormats: Format[Value] = EnumUtils.enumFormat(DisabilityBenefits)

  val sortedDisabilityBenefits = Seq(DISABILITY_BENEFITS, HIGHER_DISABILITY_BENEFITS)
}

object ChildcarePayFrequency extends Enumeration {

  val WEEKLY_KEY = "value"
  val MONTHLY_KEY = "value-2"

  val WEEKLY = Value("weekly")
  val MONTHLY = Value("monthly")

  val reads: Reads[Value] = EnumUtils.enumReads(ChildcarePayFrequency)
  val writes: Writes[Value] = EnumUtils.enumWrites

  implicit def enumFormats: Format[Value] = EnumUtils.enumFormat(ChildcarePayFrequency)
}

object CreditsEnum extends Enumeration {
  type CreditsEnum = Value
  val TAXCREDITS, UNIVERSALCREDIT, NONE = Value

  val enumReads: Reads[CreditsEnum] = EnumUtils.enumReads(CreditsEnum)

  val enumWrites: Writes[CreditsEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[CreditsEnum] = EnumUtils.enumFormat(CreditsEnum)
}

object EmploymentStatusEnum extends Enumeration {
  type EmploymentStatusEnum = Value
  val SELFEMPLOYED, APPRENTICE, NEITHER = Value

  val enumReads: Reads[EmploymentStatusEnum] = EnumUtils.enumReads(EmploymentStatusEnum)

  val enumWrites: Writes[EmploymentStatusEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[EmploymentStatusEnum] = EnumUtils.enumFormat(EmploymentStatusEnum)
}

object SchemeEnum extends Enumeration {
  type SchemeEnum = Value
  val TFCELIGIBILITY = Value("tfcEligibility")
  val TCELIGIBILITY = Value("tcEligibility")
  val ESCELIGIBILITY = Value("escEligibility")

  val enumReads: Reads[SchemeEnum] = EnumUtils.enumReads(SchemeEnum)

  val enumWrites: Writes[SchemeEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[SchemeEnum] = EnumUtils.enumFormat(SchemeEnum)
}

object PeriodEnum extends Enumeration {
  type PeriodEnum = Value
  val WEEKLY, FORTNIGHTLY, MONTHLY, QUARTERLY, YEARLY, INVALID = Value
  val enumReads: Reads[PeriodEnum] = EnumUtils.enumReads(PeriodEnum)

  val enumWrites: Writes[PeriodEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[PeriodEnum] = EnumUtils.enumFormat(PeriodEnum)
}

object UniversalCreditEnum extends Enumeration {
  type UniversalCreditEnum = Value

  val TC = Value("tc")
  val NONE = Value("none")

  val enumReads: Reads[UniversalCreditEnum]   = EnumUtils.enumReads(UniversalCreditEnum)
  val enumWrites: Writes[UniversalCreditEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[UniversalCreditEnum] = EnumUtils.enumFormat(UniversalCreditEnum)
}

object EarningsEnum extends Enumeration {
  type EarningsEnum = Value

  val LessThanMinimum = Value("lessThanMinimum")
  val BetweenMinimumAndMaximum = Value("betweenMinimumAndMaximum")
  val GreaterThanMaximum = Value("greaterThanMaximum")

  val enumReads: Reads[EarningsEnum]   = EnumUtils.enumReads(EarningsEnum)
  val enumWrites: Writes[EarningsEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[EarningsEnum] = EnumUtils.enumFormat(EarningsEnum)
}
