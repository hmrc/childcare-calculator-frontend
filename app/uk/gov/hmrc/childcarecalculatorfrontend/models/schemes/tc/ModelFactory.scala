/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.{WhichBenefitsEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class ModelFactory @Inject() () {

  def apply(answers: UserAnswers): Option[Household] = {
    answers.doYouLiveWithPartner.flatMap {
      case true =>

        for {

          anyBenefits <- answers.doYouOrYourPartnerGetAnyBenefits

          parentHours <- answers.whoIsInPaidEmployment.flatMap {
              case str if str == YouPartnerBothEnum.YOU.toString => answers.parentWorkHours
              case str if str == YouPartnerBothEnum.BOTH.toString => answers.parentWorkHours
              case _ => Some(BigDecimal(0))
            }


          partnerHours <- answers.whoIsInPaidEmployment.flatMap {
            case str if str == YouPartnerBothEnum.PARTNER.toString => answers.partnerWorkHours
            case str if str == YouPartnerBothEnum.BOTH.toString => answers.partnerWorkHours
            case _ => Some(BigDecimal(0))
          }

          parentBenefits <- if (anyBenefits) {
            answers.whoGetsBenefits.flatMap {
              case str if str != YouPartnerBothEnum.PARTNER.toString =>
                answers.whichBenefitsYouGet
              case _ =>
                Some(Set.empty)
            }
          } else {
            Some(Set.empty)
          }

          partnerBenefits <- if (anyBenefits) {
            answers.whoGetsBenefits.flatMap {
              case str if str != YouPartnerBothEnum.YOU.toString =>
                answers.whichBenefitsPartnerGet
              case _ =>
                Some(Set.empty)
            }
          } else {
            Some(Set.empty)
          }
        } yield JointHousehold(
          Parent(parentHours, parentBenefits.map(WhichBenefitsEnum.withName)),
          Parent(partnerHours, partnerBenefits.map(WhichBenefitsEnum.withName))
        )
      case false =>
        for {

          areYouInPaidWork <- answers.areYouInPaidWork
          hours            <- if (areYouInPaidWork) {
            answers.parentWorkHours
          } else {
            Some(BigDecimal(0))
          }

          doYouGetAnyBenefits <- answers.doYouGetAnyBenefits
          benefits            <- if (doYouGetAnyBenefits) {
            answers.whichBenefitsYouGet
          } else {
            Some(Set.empty)
          }
        } yield SingleHousehold(Parent(hours, benefits.map(WhichBenefitsEnum.withName)))
    }
  }
}
