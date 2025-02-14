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

import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.Inject

class ModelFactory @Inject() {

  val TRUE: Boolean = true
  val FALSE: Boolean = false
  val You: String = YouPartnerBothEnum.YOU.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString

  def apply(answers: UserAnswers): Option[Household] = {
    answers.doYouLiveWithPartner.flatMap {
      case true => createJointHousehold(answers)
      case false => createSingleHousehold(answers)
    }
  }

  private def createJointHousehold(answers: UserAnswers): Option[JointHousehold] = {
    for {
      parentMinEarnings <- answers.whoIsInPaidEmployment.flatMap {
        case str if str == You => answers.yourMinimumEarnings
        case str if str == Both => answers.yourMinimumEarnings
        case _ => Some(false)
      }

      partnerMinEarnings <- answers.whoIsInPaidEmployment.flatMap {
        case str if str == Partner => answers.partnerMinimumEarnings
        case str if str == Both => answers.partnerMinimumEarnings
        case _ => Some(false)
      }

      parentApprentice = checkMinEarnings(parentMinEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false), FALSE).getOrElse(false)

      partnerApprentice = checkMinEarnings(partnerMinEarnings, answers.partnerSelfEmployedOrApprentice, answers.partnerSelfEmployed.getOrElse(false), FALSE).getOrElse(false)

      parentSelfEmployed = checkMinEarnings(parentMinEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false), TRUE).getOrElse(false)

      partnerSelfEmployed = checkMinEarnings(partnerMinEarnings, answers.partnerSelfEmployedOrApprentice, answers.partnerSelfEmployed.getOrElse(false), TRUE).getOrElse(false)


      parentMaxEarnings <- if (parentMinEarnings) {
        answers.eitherOfYouMaximumEarnings.fold(answers.yourMaximumEarnings)(x => Some(x))
      } else {
        Some(false)
      }

      partnerMaxEarnings <- if (partnerMinEarnings) {
        answers.eitherOfYouMaximumEarnings.fold(answers.partnerMaximumEarnings)(x => Some(x))

      } else {
        Some(false)
      }

      parentBenefits = answers.doYouGetAnyBenefits.getOrElse(Set.empty)
      partnerBenefits = answers.doesYourPartnerGetAnyBenefits.getOrElse(Set.empty)

    } yield JointHousehold(
      Parent(parentMinEarnings, !parentMaxEarnings, parentSelfEmployed, parentApprentice, parentBenefits),
      Parent(partnerMinEarnings, !partnerMaxEarnings, partnerSelfEmployed, partnerApprentice, partnerBenefits)
    )
  }

  private def createSingleHousehold(answers: UserAnswers): Option[SingleHousehold] =
    for {
      areYouInPaidWork <- answers.areYouInPaidWork
      minEarnings <- if (areYouInPaidWork) {
        answers.yourMinimumEarnings
      } else {
        Some(false)
      }

      apprentice = checkMinEarnings(minEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false), FALSE).getOrElse(false)

      selfEmployed = checkMinEarnings(minEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false), TRUE).getOrElse(false)

      maxEarnings <- if (minEarnings) {
        answers.yourMaximumEarnings
      } else {
        Some(false)
      }

      doYouGetAnyBenefits <- answers.doYouGetAnyBenefits
      benefits <- if (doYouGetAnyBenefits.contains(ParentsBenefits.NoneOfThese)) {
        Some(Set.empty[ParentsBenefits])
      } else {
        answers.doYouGetAnyBenefits
      }
    } yield SingleHousehold(Parent(minEarnings, !maxEarnings, selfEmployed, apprentice, benefits))

  private def checkMinEarnings(
    minEarnings: Boolean,
    selfOrApprentice: Option[String],
    employedLessThan12Months: Boolean ,
    self: Boolean
  ): Option[Boolean] = {
    val strAppOrSelf = if(self) {
      SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString
    } else {
      SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
    }

    if (!minEarnings) {
      selfOrApprentice.flatMap {
        case str if str == strAppOrSelf => Some(employedLessThan12MonthsCheck(str, employedLessThan12Months))
        case _ => Some(false)
      }
    } else {
      Some(false)
    }
  }

  private def employedLessThan12MonthsCheck(selfEmployedOrApprentice: String, employedLessThan12Months: Boolean): Boolean = {
    if (selfEmployedOrApprentice == SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString) {
      true
    } else if (selfEmployedOrApprentice == SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString && !employedLessThan12Months) {
      false
    } else {
      true
    }
  }

}

