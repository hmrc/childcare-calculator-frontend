package uk.gov.hmrc.childcarecalculatorfrontend.models

object BenefitsEnum extends Enumeration {
  type BenefitsEnum = Value
  val TAXCREDITS,
    UNIVERSALCREDITS,
    INCOMESUPPORT,
    INCOMEBASEDJOBSEEKER,
    EMPLOYMENTSUPPORTALLOWANCE,
    PENSIONCREDIT,
    DISABILITY,
    ATTENDANCE,
    PERSONALINDEPENDENCE = Value
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
  val EMPLOYED, SELFEMPLOYED, APPRENTICE, UNEMPLOYED = Value
}

object YesNoUnsureEnum extends Enumeration {
  type YesNoUnsureEnum = Value
  val YES, NO, NOTSURE = Value
  }

object PeriodEnum extends Enumeration {
  type PeriodEnum = Value
  val DAILY, WEEKLY, FORTNIGHTLY, MONTHLY, QUARTERLY, YEARLY, INVALID = Value
}