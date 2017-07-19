package uk.gov.hmrc.childcarecalculatorfrontend.models

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum.AgeRangeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum.PeriodEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.TcUcBenefitsEnum.TcUcBenefitsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureBothEnum.{YesNoUnsureBothEnum}

case class Household(
                     tcUcBenefits: Option[TcUcBenefitsEnum] =   None,
                     location: Option[LocationEnum] =   None,
                     hasPartner: Boolean =   false,
                     children: List[Child],
                     parent: Claimant,
                     partner: Option[Claimant]
                    )
object Household {
  implicit val formatHousehold = Json.format[Household]
}

case class Claimant(
                    ageRange: Option[AgeRangeEnum] = None,
                    benefits: Option[Benefits] = None,
                    lastYearlyIncome: Option[Income]  =   None,
                    currentYearlyIncome: Option[Income]  = None,
                    hours: Option[BigDecimal] =   None,
                    minimumEarnings: Option[MinimumEarnings]= None,
                    escVouchers: Option[YesNoUnsureBothEnum] =   None
                   )
object Claimant {
  implicit val formatClaimant = Json.format[Claimant]
}

case class Benefits(
                    disabilityBenefits: Boolean = false,
                    highRateDisabilityBenefits: Boolean =   false,
                    incomeBenefits: Boolean =   false,
                    carersAllowance: Boolean = false
                   )
object Benefits {
  implicit val formatBenefits = Json.format[Benefits]
}

case class Income(
                  employmentIncome: Option[BigDecimal] = None,
                  pension: Option[BigDecimal] =   None,
                  otherIncome: Option[BigDecimal] = None,
                  benefits: Option[BigDecimal] =   None,
                  statutoryIncome: Option[StatutoryIncome]=None
                 )
object Income {
  implicit val formatIncome = Json.format[Income]
}

case class StatutoryIncome(
                           statutoryWeeks: Double = 0.00,
                           statutoryAmount: BigDecimal =   0.00
                          )
object StatutoryIncome {
  implicit val formatStatutoryIncome = Json.format[StatutoryIncome]
}

case class MinimumEarnings(
                           amount: BigDecimal =   0.00,
                           employmentStatus: Option[EmploymentStatusEnum] =   None,
                           selfEmployedIn12Months: Option[Boolean] =   None
                          )
object MinimumEarnings {
  implicit val formatMinimumEarnings = Json.format[MinimumEarnings]
}

case class Child(
                 id: Short,
                 name: String,
                 dob: Option[LocalDate]=None,
                 disability: Option[Disability]=None,
                 childcareCost: Option[ChildCareCost]=None,
                 education: Option[Education]= None
                )
object Child {
  implicit val formatChild = Json.format[Child]
}

case class Disability(
                      disabled: Boolean,
                      severelyDisabled: Boolean,
                      blind: Boolean
                     )
object Disability {
  implicit val formatDisability = Json.format[Disability]
}

case class ChildCareCost(
                         amount: Option[BigDecimal] =   None,
                         period: Option[PeriodEnum] =   None
                        )
object ChildCareCost {
  implicit val formatChildCareCost = Json.format[ChildCareCost]
}

case class Education(
                      inEducation: Boolean = false,
                      startDate: Option[LocalDate] =   None
                    )
object Education {
  implicit val formatEducation = Json.format[Education]
}