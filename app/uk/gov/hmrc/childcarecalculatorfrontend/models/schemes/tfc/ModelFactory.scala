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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc

import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{EmploymentStatus, YouPartnerBothNeither}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.Household.{JointHousehold, SingleHousehold}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.{Inject, Singleton}

@Singleton
class ModelFactory @Inject() {

  def apply(answers: UserAnswers): Option[Household] =
    answers.doYouLiveWithPartner.flatMap {
      case true  => createJointHousehold(answers)
      case false => createSingleHousehold(answers)
    }

  private def createJointHousehold(answers: UserAnswers): Option[JointHousehold] =
    for {
      parent  <- buildParent(answers)
      partner <- buildPartner(answers)
    } yield JointHousehold(parent, partner)

  private def buildParent(answers: UserAnswers): Option[Parent] =
    for {
      parentMinEarnings <- answers.whoIsInPaidEmployment.flatMap {
        case YouPartnerBothNeither.You  => answers.yourMinimumEarnings
        case YouPartnerBothNeither.Both => answers.yourMinimumEarnings
        case _                          => Some(false)
      }

      areYouSelfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice

      parentApprentice = isApprenticeAndNotEarningTheMinimum(parentMinEarnings, areYouSelfEmployedOrApprentice)
      parentSelfEmployed = isRecentlySelfEmployedAndNotEarningTheMinimum(
        parentMinEarnings,
        areYouSelfEmployedOrApprentice,
        answers.yourSelfEmployed.getOrElse(false)
      )

      parentMaxEarnings <-
        if (parentMinEarnings) {
          answers.eitherOfYouMaximumEarnings.fold(answers.yourMaximumEarnings)(x => Some(x))
        } else {
          Some(false)
        }

      parentBenefits = answers.doYouGetAnyBenefits.getOrElse(Set.empty)

    } yield Parent(parentMinEarnings, parentMaxEarnings, parentSelfEmployed, parentApprentice, parentBenefits)

  private def buildPartner(answers: UserAnswers): Option[Parent] =
    for {
      partnerMinEarnings <- answers.whoIsInPaidEmployment.flatMap {
        case YouPartnerBothNeither.Partner => answers.partnerMinimumEarnings
        case YouPartnerBothNeither.Both    => answers.partnerMinimumEarnings
        case _                             => Some(false)
      }

      partnerSelfEmployedOrApprentice = answers.partnerSelfEmployedOrApprentice

      partnerApprentice = isApprenticeAndNotEarningTheMinimum(partnerMinEarnings, partnerSelfEmployedOrApprentice)
      partnerSelfEmployed = isRecentlySelfEmployedAndNotEarningTheMinimum(
        partnerMinEarnings,
        partnerSelfEmployedOrApprentice,
        answers.partnerSelfEmployed.getOrElse(false)
      )

      partnerMaxEarnings <-
        if (partnerMinEarnings) {
          answers.eitherOfYouMaximumEarnings.fold(answers.partnerMaximumEarnings)(x => Some(x))
        } else {
          Some(false)
        }

      partnerBenefits = answers.doesYourPartnerGetAnyBenefits.getOrElse(Set.empty)
    } yield Parent(partnerMinEarnings, partnerMaxEarnings, partnerSelfEmployed, partnerApprentice, partnerBenefits)

  private def createSingleHousehold(answers: UserAnswers): Option[SingleHousehold] =
    for {
      areYouInPaidWork <- answers.areYouInPaidWork
      minEarnings <-
        if (areYouInPaidWork) {
          answers.yourMinimumEarnings
        } else {
          Some(false)
        }

      areYouSelfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice

      apprentice = isApprenticeAndNotEarningTheMinimum(minEarnings, areYouSelfEmployedOrApprentice)
      selfEmployed = isRecentlySelfEmployedAndNotEarningTheMinimum(
        minEarnings,
        areYouSelfEmployedOrApprentice,
        answers.yourSelfEmployed.getOrElse(false)
      )

      maxEarnings <-
        if (minEarnings) {
          answers.yourMaximumEarnings
        } else {
          Some(false)
        }

      doYouGetAnyBenefits <- answers.doYouGetAnyBenefits
      benefits <-
        if (doYouGetAnyBenefits.contains(ParentsBenefit.NoneOfThese)) {
          Some(Set.empty[ParentsBenefit])
        } else {
          answers.doYouGetAnyBenefits
        }
    } yield SingleHousehold(Parent(minEarnings, maxEarnings, selfEmployed, apprentice, benefits))

  private def isApprenticeAndNotEarningTheMinimum(
      minEarnings: Boolean,
      areYouSelfEmployedOrApprentice: Option[EmploymentStatus]
  ): Boolean =
    (minEarnings, areYouSelfEmployedOrApprentice) match {
      case (false, Some(EmploymentStatus.Apprentice)) => true
      case _                                          => false
    }

  private def isRecentlySelfEmployedAndNotEarningTheMinimum(
      minEarnings: Boolean,
      areYouSelfEmployedOrApprentice: Option[EmploymentStatus],
      isEmployedForLessThan12Months: Boolean
  ): Boolean =
    (minEarnings, areYouSelfEmployedOrApprentice) match {
      case (false, Some(EmploymentStatus.SelfEmployed)) => isEmployedForLessThan12Months
      case _                                            => false
    }

}
