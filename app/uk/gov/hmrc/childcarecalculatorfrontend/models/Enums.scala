package uk.gov.hmrc.childcarecalculatorfrontend.models

object TcUcBenefitsEnum extends Enumeration {
  type TcUcBenefitsEnum = Value
  val TAXCREDITS,
    UNIVERSALCREDITS = Value
}

object LocationEnum extends Enumeration {
  type LocationEnum = Value
  val ENGLAND, SCOTLAND, WALES, NORTHERNIRELAND = Value
}

object AgeRangeEnum extends Enumeration {
  type AgeRangeEnum = Value
  val UNDER18, EIGHTEENTOTWENTY, TWENTYONETOTWENTYFOUR, OVERTWENTYFOUR = Value
}

object EmploymentStatusEnum extends Enumeration {
  type EmploymentStatusEnum = Value
  val SELFEMPLOYED, APPRENTICE = Value
}

object YesNoUnsureBothEnum extends Enumeration {
  type YesNoUnsureBothEnum = Value
  val YES, NO, NOTSURE, BOTH = Value
  }

object PeriodEnum extends Enumeration {
  type PeriodEnum = Value
  val DAILY, WEEKLY, FORTNIGHTLY, MONTHLY, QUARTERLY, YEARLY, INVALID = Value
}