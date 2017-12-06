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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import play.api.libs.json.{Format, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.EnumUtils

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

object YouPartnerBothEnum extends Enumeration {
  type YouPartnerBothEnum = Value

  val YOU = Value("you")
  val PARTNER = Value("partner")
  val BOTH = Value("both")

  val enumReads: Reads[YouPartnerBothEnum] = EnumUtils.enumReads(YouPartnerBothEnum)
  val enumWrites: Writes[YouPartnerBothEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YouPartnerBothEnum] = EnumUtils.enumFormat(YouPartnerBothEnum)
}

object YesNoUnsureEnum extends Enumeration {
  type YesNoUnsureEnum = Value
  val YES = Value("yes")
  val NO = Value("no")
  val NOTSURE = Value("notSure")

  val enumReads: Reads[YesNoUnsureEnum] = EnumUtils.enumReads(YesNoUnsureEnum)
  val enumWrites: Writes[YesNoUnsureEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YesNoUnsureEnum] = EnumUtils.enumFormat(YesNoUnsureEnum)
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
  val TWENTYONETOTWENTYFOUR = Value("TWENTYONETOTWENTYFOUR")
  val OVERTWENTYFOUR = Value("OVERTWENTYFOUR")

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

object WhichBenefitsEnum extends Enumeration {
  type WhichBenefitsEnum = Value

  val INCOMEBENEFITS = Value("incomeBenefits")
  val DISABILITYBENEFITS = Value("disabilityBenefits")
  val HIGHRATEDISABILITYBENEFITS = Value("highRateDisabilityBenefits")
  val CARERSALLOWANCE = Value("carersAllowance")

  val enumReads: Reads[WhichBenefitsEnum] = EnumUtils.enumReads(WhichBenefitsEnum)
  val enumWrites: Writes[WhichBenefitsEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[WhichBenefitsEnum] = EnumUtils.enumFormat(WhichBenefitsEnum)
}

object DisabilityBenefits extends Enumeration {

  val DISABILITY_BENEFITS = Value("disability-benefits")
  val HIGHER_DISABILITY_BENEFITS = Value("higher-disability-benefit")

  val reads: Reads[Value] = EnumUtils.enumReads(DisabilityBenefits)
  val writes: Writes[Value] = EnumUtils.enumWrites

  implicit def enumFormats: Format[Value] = EnumUtils.enumFormat(DisabilityBenefits)
}

object ChildcarePayFrequency extends Enumeration {

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
