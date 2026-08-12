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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.{Inject, Singleton}

@Singleton
class FreeChildcareWorkingParents @Inject() (freeChildcareEligibilityCalculator: FreeChildcareEligibilityCalculator)
    extends Scheme {

  private val eligibleBenefits: Set[ParentsBenefit] = Set(
    CarersAllowance,
    IncapacityBenefit,
    SevereDisablementAllowance,
    ContributionBasedEmploymentAndSupportAllowance,
    NICreditsForIncapacityOrLimitedCapabilityForWork,
    CarersCredit
  )

  override def eligibility(answers: UserAnswers): Eligibility =
    answers.location match {
      case Some(Location.England) =>
        if (hasChildrenInAgeGroups(answers))
          freeChildcareEligibilityCalculator.calculateEligibility(answers, eligibleBenefits)
        else
          Eligibility.NotEligible

      case Some(_) => Eligibility.NotEligible
      case _       => Eligibility.NotDetermined
    }

  private def hasChildrenInAgeGroups(answers: UserAnswers): Boolean =
    answers.isChildAgedNineTo23Months.getOrElse(false) ||
      answers.isChildAgedTwo.getOrElse(false) ||
      answers.isChildAgedThreeOrFour.getOrElse(false)

}
