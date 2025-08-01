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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.models.EarningsEnum.EarningsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{
  freeHoursForEngland,
  freeHoursForNI,
  freeHoursForScotland,
  freeHoursForWales
}

case class ResultsViewModel(
    firstParagraph: List[String] = List.empty,
    tfc: Option[BigDecimal] = None,
    esc: Option[BigDecimal] = None,
    freeHours: Option[BigDecimal] = None,
    freeChildcareWorkingParents: Boolean = false,
    location: Location.Value,
    childrenAgeGroups: Set[ChildAgeGroup] = Set(NoneOfThese),
    tfcWarningMessage: Option[String] = None,
    hasChildcareCosts: Boolean,
    hasCostsWithApprovedProvider: Boolean,
    isAnyoneInPaidEmployment: Boolean,
    livesWithPartner: Boolean,
    yourEarnings: Option[EarningsEnum] = None,
    partnerEarnings: Option[EarningsEnum] = None,
    freeChildcareWorkingParentsEligibilityMsg: Option[String] = None,
    taxFreeChildcareEligibilityMsg: Option[String] = None
) {

  def noOfEligibleSchemes: Int = List(tfc, esc, freeHours).flatten.size

  def isEligibleOnlyToMinimumFreeHours: Boolean =
    esc.isEmpty && tfc.isEmpty && (freeHours.contains(BigDecimal(freeHoursForEngland)) || freeHours.contains(
      BigDecimal(freeHoursForWales)
    ) || freeHours.contains(BigDecimal(freeHoursForScotland)) || freeHours.contains(BigDecimal(freeHoursForNI)))

  def isEligibleToMaximumFreeHours: Boolean = freeHours.contains(BigDecimal(30))

  def hasIneligibleMessages: Boolean =
    (freeChildcareWorkingParentsEligibilityMsg.nonEmpty && !freeChildcareWorkingParents) ||
      (taxFreeChildcareEligibilityMsg.nonEmpty && tfc.isEmpty)

  def isEligibleToAllSchemes: Boolean = noOfEligibleSchemes == 3

  def showTwoYearOldInfo: Boolean =
    if (childrenAgeGroups.contains(TwoYears)) {
      location match {
        case Location.NORTHERN_IRELAND => false
        case Location.WALES            => false
        case _ =>
          if (noOfEligibleSchemes == 0) {
            if (childrenAgeGroups.contains(ThreeYears) || childrenAgeGroups.contains(FourYears)) false else true
          } else {
            true
          }
      }
    } else {
      false
    }

  def showNonEnglandFreeHoursLinks: Boolean =
    location != Location.ENGLAND &&
      !childrenAgeGroups.contains(NoneOfThese) &&
      !(location == Location.WALES && yourEarnings.exists(_ != EarningsEnum.BetweenMinimumAndMaximum))

}

object ResultsViewModel {
  implicit val format: OFormat[ResultsViewModel] = Json.format[ResultsViewModel]
}
