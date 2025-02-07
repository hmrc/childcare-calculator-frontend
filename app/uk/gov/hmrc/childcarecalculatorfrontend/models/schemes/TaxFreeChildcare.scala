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

import javax.inject.Inject
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum.{CARERSALLOWANCE, SCOTTISHCARERSALLOWANCE}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotDetermined, NotEligible, WhichBenefitsEnum, Location}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.{JointHousehold, ModelFactory, Parent, SingleHousehold}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxFreeChildcare @Inject() (household: ModelFactory) extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {
    val location = answers.location.get
    household(answers).map {
      case SingleHousehold(parent) => singleEligibility(parent)
      case JointHousehold(parent, partner) => jointEligibility(parent, partner, location)
    }
  }.getOrElse(NotDetermined)

  private def singleEligibility(parent: Parent): Eligibility = {
    if (isEligible(parent)) {
      Eligible
    } else {
      NotEligible
    }
  }

  private def jointEligibility(parent: Parent, partner: Parent, location: Location.Value): Eligibility =
    if ((isEligible(parent) && (isEligible(partner) || partner.benefits.intersect(applicableBenefitsByLocation(location)).nonEmpty)) ||
      (isEligible(partner) && (isEligible(parent) || parent.benefits.intersect(applicableBenefitsByLocation(location)).nonEmpty))) {
      Eligible
    } else {
      NotEligible
    }

  private def isEligible(parent: Parent): Boolean =
    (parent.minEarnings && parent.maxEarnings) || (!parent.minEarnings && (parent.apprentice || parent.selfEmployed))

  private def applicableBenefitsByLocation(location: Location.Value): Set[WhichBenefitsEnum.Value] = {
    location match {
      case Location.SCOTLAND => Set(SCOTTISHCARERSALLOWANCE)
      case _ => Set(CARERSALLOWANCE)
    }
  }

}
