/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.integration.child

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.DisabilityBenefit

case class Disability(
    disabled: Boolean = false,
    severelyDisabled: Boolean = false,
    blind: Boolean = false
)

object Disability {
  given formatDisability: OFormat[Disability] = Json.format[Disability]

  def populateFromRawData(
      currentChildIndex: Int,
      disabilities: Option[Map[Int, Set[DisabilityBenefit]]],
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
      childrenWithDisabilities: Map[Int, Set[DisabilityBenefit]]
  ) =
    childrenWithDisabilities.get(currentChildIndex) match {
      case Some(disabilities) => checkDisabilities(disabilities, blindChildren)
      case _                  => Disability()
    }

  private def checkDisabilities(
      disabilities: Set[DisabilityBenefit],
      blindChildren: Option[Boolean]
  ) =
    disabilities.foldLeft(Disability())((disabilities, currentDisability) =>
      checkDisabilityType(currentDisability, disabilities, blindChildren)
    )

  private def checkDisabilityType(
      disabilityType: DisabilityBenefit,
      childDisabilities: Disability,
      blindChildren: Option[Boolean]
  ): Disability = {
    val disabilities = disabilityType match {
      case DisabilityBenefit.DisabilityBenefits       => childDisabilities.copy(disabled = true)
      case DisabilityBenefit.HigherDisabilityBenefits => childDisabilities.copy(severelyDisabled = true)
    }

    blindChildren match {
      case Some(true) => disabilities.copy(blind = true)
      case _          => disabilities
    }
  }

}
