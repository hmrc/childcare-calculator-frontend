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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum.CARERSALLOWANCE
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.{JointHousehold, ModelFactory, Parent, SingleHousehold}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxFreeChildcare @Inject() (household: ModelFactory) extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {
    household(answers).map {
      case SingleHousehold(parent) =>
        singleEligibility(parent)
      case JointHousehold(parent, partner) =>
        jointEligibility(parent, partner)
    }
  }.getOrElse(NotDetermined)

  private def singleEligibility(parent: Parent): Eligibility = {
    if ((parent.minEarnings && parent.maxEarnings) || (!parent.minEarnings && (parent.apprentice || parent.selfEmployed))) {
      Eligible
    } else {
      NotEligible
    }
  }

  private def jointEligibility(parent: Parent, partner: Parent): Eligibility = {

    val parentEligibility: Boolean =
      (parent.minEarnings && parent.maxEarnings) || (!parent.minEarnings && (parent.apprentice || parent.selfEmployed))

    val partnerEligibility: Boolean =
      (partner.minEarnings && partner.maxEarnings) || (!partner.minEarnings && (partner.apprentice || partner.selfEmployed))

    if ((parentEligibility && (partnerEligibility || partner.benefits.intersect(applicableBenefits).nonEmpty)) ||
      (partnerEligibility && (parentEligibility || parent.benefits.intersect(applicableBenefits).nonEmpty))) {
      Eligible
    } else {
      NotEligible
    }
  }

  //Only carer's allowance is considered as benefit to eligible
  private val applicableBenefits: Set[WhichBenefitsEnum.Value] =
    Set(CARERSALLOWANCE)


}
