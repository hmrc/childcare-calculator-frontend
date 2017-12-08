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

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.hmrc.childcarecalculatorfrontend.models.CreditsEnum.CreditsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum.PeriodEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum

//Note :- The order of these classes need to preserved to ensure json formatters are prepared in the correct order
case class StatutoryIncome(
                            statutoryWeeks: Double = 0.00,
                            statutoryAmount: Option[BigDecimal] = None
                          )

object StatutoryIncome {
  implicit val formatStatutoryIncome = Json.format[StatutoryIncome]
}

case class Income(
                   employmentIncome: Option[BigDecimal] = None,
                   pension: Option[BigDecimal] = None,
                   otherIncome: Option[BigDecimal] = None,
                   benefits: Option[BigDecimal] = None,
                   statutoryIncome: Option[StatutoryIncome] = None,
                   taxCode: Option[String] = None
                 )

object Income {
  implicit val formatIncome = Json.format[Income]
}

case class Benefits(
                     disabilityBenefits: Boolean = false,
                     highRateDisabilityBenefits: Boolean = false,
                     incomeBenefits: Boolean = false,
                     carersAllowance: Boolean = false
                   )

object Benefits {
  implicit val formatBenefits = Json.format[Benefits]

  def populateFromRawData(data: Option[Set[String]]): Option[Benefits] = {
    data.map(benefits => benefits.foldLeft(Benefits())((benefits, currentBenefit) => {
      currentBenefit match {
        case "incomeBenefits" => benefits.copy(incomeBenefits = true)
        case "disabilityBenefits" => benefits.copy(disabilityBenefits = true)
        case "highRateDisabilityBenefits" => benefits.copy(highRateDisabilityBenefits = true)
        case "carersAllowance" => benefits.copy(carersAllowance = true)
        case _ => benefits
      }
    }))
  }
}

case class MinimumEarnings(
                            amount: BigDecimal = 0.00,
                            employmentStatus: Option[String] = None, //TODO - covert to EmploymentStatusEnum type
                            selfEmployedIn12Months: Option[Boolean] = None
                          )

object MinimumEarnings {
  implicit val formatMinimumEarnings = Json.format[MinimumEarnings]
}

case class Disability(
                       disabled: Boolean = false,
                       severelyDisabled: Boolean = false,
                       blind: Boolean = false
                     )

object Disability {
  implicit val formatDisability = Json.format[Disability]

  def populateFromRawData(currentIndex: Int,disabilities: Option[Map[Int, Set[DisabilityBenefits.Value]]], blindChildren: Option[Set[Int]] = None) : Option[Disability] = {
    disabilities.map(_.get(currentIndex).fold(Disability())(disabilities => {
      disabilities.foldLeft(Disability())((disabilities,currentDisability) => {
        val childrenDisabilities = currentDisability match {
          case DisabilityBenefits.DISABILITY_BENEFITS => disabilities.copy(disabled = true)
          case DisabilityBenefits.HIGHER_DISABILITY_BENEFITS => disabilities.copy(severelyDisabled = true)
        }

        blindChildren.fold(childrenDisabilities)(childrenWithBlindDisability => {
          childrenWithBlindDisability.find(childIndex=> childIndex == currentIndex).fold(childrenDisabilities)(_ => childrenDisabilities.copy(blind = true))
        })
      })
    })) match {
      case Some(Disability(false,false,false)) => None
      case disabilities => disabilities
    }
  }
}

case class ChildCareCost(
                          amount: Option[BigDecimal] = None,
                          period: Option[PeriodEnum] = None
                        )

object ChildCareCost {
  implicit val formatChildCareCost = Json.format[ChildCareCost]
}

case class Education(
                      inEducation: Boolean = false,
                      startDate: Option[LocalDate] = None
                    )

object Education {
  implicit val formatEducation = Json.format[Education]
}

case class Child(
                  id: Short,
                  name: String,
                  dob: LocalDate,
                  disability: Option[Disability] = None,
                  childcareCost: Option[ChildCareCost] = None,
                  education: Option[Education] = None
                )

object Child {
  implicit val formatChild = Json.format[Child]
}

case class Claimant(
                     ageRange: Option[String] = None, //TODO - convert to AgeRangeEnum type
                     benefits: Option[Benefits] = None,
                     lastYearlyIncome: Option[Income] = None,
                     currentYearlyIncome: Option[Income] = None,
                     hours: Option[BigDecimal] = None,
                     minimumEarnings: Option[MinimumEarnings] = None,
                     escVouchers: Option[YesNoUnsureEnum] = None,
                     maximumEarnings: Option[Boolean] = None
                   )

object Claimant {
  implicit val formatClaimant = Json.format[Claimant]
}

case class Household(
                      credits: Option[CreditsEnum] = None,
                      location: Location,
                      children: List[Child] = List.empty,
                      parent: Claimant = Claimant(),
                      partner: Option[Claimant] = None
                    )

object Household {
  implicit val formatHousehold = Json.format[Household]
}
