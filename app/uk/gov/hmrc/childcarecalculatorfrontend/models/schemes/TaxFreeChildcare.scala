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

import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, ParentsBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.Inject

class TaxFreeChildcare @Inject() (freeChildcareEligibilityCalculator: FreeChildcareEligibilityCalculator)
    extends Scheme {

  private val eligibleBenefits: Set[ParentsBenefits] = Set(
    CarersAllowance,
    IncapacityBenefit,
    SevereDisablementAllowance,
    ContributionBasedEmploymentAndSupportAllowance
  )

  override def eligibility(answers: UserAnswers): Eligibility =
    freeChildcareEligibilityCalculator.calculateEligibility(answers, eligibleBenefits)

}
