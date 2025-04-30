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

package uk.gov.hmrc.childcarecalculatorfrontend.models.integration

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.models.DisabilityBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.DisabilityBenefits.{
  DISABILITY_BENEFITS,
  HIGHER_DISABILITY_BENEFITS
}
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum.PeriodEnum

case class Disability(
    disabled: Boolean = false,
    severelyDisabled: Boolean = false,
    blind: Boolean = false
)

object Disability {
  implicit val formatDisability: OFormat[Disability] = Json.format[Disability]

  def populateFromRawData(
      currentChildIndex: Int,
      disabilities: Option[Map[Int, Set[DisabilityBenefits.Value]]],
      blindChildren: Option[Boolean] = None
  ): Option[Disability] =
    disabilities match {
      case None =>
        blindChildren match {
          case Some(true) => Some(Disability(false, false, true))
          case _          => None
        }
      case Some(_) =>
        disabilities.map(childrenWithDisabilities =>
          checkIfChildHasDisabilities(currentChildIndex, blindChildren, childrenWithDisabilities)
        ) match {
          case Some(Disability(false, false, false)) => None
          case childDisabilities                     => childDisabilities
        }
    }

  private def checkIfChildHasDisabilities(
      currentChildIndex: Int,
      blindChildren: Option[Boolean],
      childrenWithDisabilities: Map[Int, Set[DisabilityBenefits.Value]]
  ) =
    childrenWithDisabilities.get(currentChildIndex) match {
      case Some(disabilities) => checkDisabilities(disabilities, blindChildren, currentChildIndex)
      case _                  => Disability()
    }

  private def checkDisabilities(
      disabilities: Set[DisabilityBenefits.Value],
      blindChildren: Option[Boolean],
      currentChildIndex: Int
  ) =
    disabilities.foldLeft(Disability())((disabilities, currentDisability) =>
      checkDisabilityType(currentDisability, disabilities, blindChildren)
    )

  private def checkDisabilityType(
      disabilityType: DisabilityBenefits.Value,
      childDisabilities: Disability,
      blindChildren: Option[Boolean]
  ): Disability = {
    val disabilities = disabilityType match {
      case DISABILITY_BENEFITS        => childDisabilities.copy(disabled = true)
      case HIGHER_DISABILITY_BENEFITS => childDisabilities.copy(severelyDisabled = true)
    }

    blindChildren match {
      case Some(true)  => disabilities.copy(blind = true)
      case Some(false) => disabilities
      case None        => disabilities
    }
  }

}

case class ChildCareCost(
    amount: Option[BigDecimal] = None,
    period: Option[PeriodEnum] = None
)

object ChildCareCost {
  implicit val formatChildCareCost: OFormat[ChildCareCost] = Json.format[ChildCareCost]
}

case class Education(
    inEducation: Boolean = false,
    startDate: Option[LocalDate] = None
)

object Education {
  implicit val formatEducation: OFormat[Education] = Json.format[Education]
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
  implicit val formatChild: OFormat[Child] = Json.format[Child]
}
