/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import com.google.inject.Inject
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

private[schemes] class FreeChildcareEligibilityCalculator @Inject() (modelFactory: ModelFactory) {

  def calculateEligibility(answers: UserAnswers, eligibleBenefits: Set[ParentsBenefits]): Eligibility =
    modelFactory(answers)
      .map {
        case SingleHousehold(parent) => singleEligibility(parent)
        case JointHousehold(parent, partner) =>
          jointEligibility(parent, partner, eligibleBenefits: Set[ParentsBenefits])
      }
      .getOrElse(NotDetermined)

  private def singleEligibility(parent: Parent): Eligibility =
    if (isEligibleBasedOnEarnings(parent)) Eligible
    else NotEligible

  private def jointEligibility(parent: Parent, partner: Parent, eligibleBenefits: Set[ParentsBenefits]): Eligibility =
    if (isJointHouseholdEligible(parent, partner, eligibleBenefits)) Eligible
    else NotEligible

  private def isJointHouseholdEligible(
      parent: Parent,
      partner: Parent,
      eligibleBenefits: Set[ParentsBenefits]
  ): Boolean =
    isEligibleBasedOnEarnings(parent) && (isEligibleBasedOnEarnings(partner) || isEligibleBasedOnBenefits(
      partner,
      eligibleBenefits
    )) ||
      isEligibleBasedOnEarnings(partner) && (isEligibleBasedOnEarnings(parent) || isEligibleBasedOnBenefits(
        parent,
        eligibleBenefits
      ))

  private def isEligibleBasedOnEarnings(parent: Parent): Boolean =
    earnsBetweenMinAndMaxEarnings(parent) || earnsBelowMinEarningsButIsApprenticeOrSelfEmployed(parent)

  private def earnsBetweenMinAndMaxEarnings(parent: Parent): Boolean =
    parent.earnsAboveMinEarnings && !parent.earnsAboveMaxEarnings

  private def earnsBelowMinEarningsButIsApprenticeOrSelfEmployed(parent: Parent): Boolean =
    !parent.earnsAboveMinEarnings && (parent.apprentice || parent.selfEmployed)

  private def isEligibleBasedOnBenefits(parent: Parent, eligibleBenefits: Set[ParentsBenefits]): Boolean =
    parent.benefits.intersect(eligibleBenefits).nonEmpty

}
