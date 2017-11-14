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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.{SelfEmployedOrApprenticeOrNeitherEnum, WhichBenefitsEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class ModelFactory @Inject() () {

  def apply(answers: UserAnswers): Option[Household] = {
    answers.doYouLiveWithPartner.flatMap {

      case true =>
        for {
          youOrPartnerInPaidWork  <- answers.paidEmployment

          parentMinEarnings <- if (youOrPartnerInPaidWork) {
            answers.whoIsInPaidEmployment.flatMap {
              case str if str != YouPartnerBothEnum.PARTNER.toString =>
                answers.yourMinimumEarnings
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          partnerMinEarnings <- if (youOrPartnerInPaidWork) {
            answers.whoIsInPaidEmployment.flatMap {
              case str if str != YouPartnerBothEnum.YOU.toString =>
                answers.partnerMinimumEarnings
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          parentApprentice <- if (!parentMinEarnings) {
            answers.areYouSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString =>
                Some(true)
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          partnerApprentice <- if (!partnerMinEarnings) {
            answers.partnerSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString =>
                Some(true)
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          parentSelfEmployed <- if (!parentMinEarnings) {
            answers.areYouSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString =>
                answers.yourSelfEmployed
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          partnerSelfEmployed <- if (!partnerMinEarnings) {
            answers.partnerSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString =>
                answers.partnerSelfEmployed
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          parentMaxEarnings <- if (parentMinEarnings) {
            answers.yourMaximumEarnings
          } else {
            Some(false)
          }

          partnerMaxEarnings <- if (partnerMinEarnings) {
            answers.partnerMaximumEarnings
          } else {
            Some(false)
          }

          anyBenefits <- answers.doYouOrYourPartnerGetAnyBenefits
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
          Parent(parentMinEarnings, !parentMaxEarnings, parentSelfEmployed, parentApprentice, parentBenefits.map(WhichBenefitsEnum.withName)),
          Parent(partnerMinEarnings, !partnerMaxEarnings, partnerSelfEmployed, partnerApprentice, partnerBenefits.map(WhichBenefitsEnum.withName))
        )
      case false =>
        for {
          areYouInPaidWork <- answers.areYouInPaidWork
          minEarnings <- if (areYouInPaidWork) {
            answers.yourMinimumEarnings
          } else {
            Some(false)
          }

          apprentice <- if (!minEarnings) {
            answers.areYouSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString =>
                Some(true)
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          selfEmployed <- if (!minEarnings) {
            answers.areYouSelfEmployedOrApprentice.flatMap {
              case str if str == SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString =>
                answers.yourSelfEmployed
              case _ =>
                Some(false)
            }
          } else {
            Some(false)
          }

          maxEarnings <- if (minEarnings) {
            answers.yourMaximumEarnings
          } else {
            Some(false)
          }

          doYouGetAnyBenefits <- answers.doYouGetAnyBenefits
          benefits            <- if (doYouGetAnyBenefits) {
            answers.whichBenefitsYouGet
          } else {
            Some(Set.empty)
          }
        } yield SingleHousehold(Parent(minEarnings, !maxEarnings, selfEmployed, apprentice, benefits.map(WhichBenefitsEnum.withName)))
    }
  }
}

