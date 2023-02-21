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

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.{SelfEmployedOrApprenticeOrNeitherEnum, WhichBenefitsEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class ModelFactory @Inject() () {

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

  private def createJointHousehold(answers: UserAnswers) = {
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

      parentApprentice = checkMinEarnings(parentMinEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false),FALSE).getOrElse(false)

      partnerApprentice = checkMinEarnings(partnerMinEarnings, answers.partnerSelfEmployedOrApprentice, answers.partnerSelfEmployed.getOrElse(false), FALSE).getOrElse(false)

      parentSelfEmployed = checkMinEarnings(parentMinEarnings, answers.areYouSelfEmployedOrApprentice, answers.yourSelfEmployed.getOrElse(false),TRUE).getOrElse(false)

      partnerSelfEmployed = checkMinEarnings(partnerMinEarnings, answers.partnerSelfEmployedOrApprentice, answers.partnerSelfEmployed.getOrElse(false),TRUE).getOrElse(false)


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

      anyBenefits <- answers.doYouOrYourPartnerGetAnyBenefits
      parentBenefits <- if (anyBenefits) {
        answers.whoGetsBenefits.flatMap {
          case str if str != Partner => answers.whichBenefitsYouGet
          case _ => Some(Set.empty)
        }
      } else {
        Some(Set.empty)
      }

      partnerBenefits <- if (anyBenefits) {
        answers.whoGetsBenefits.flatMap {
          case str if str != You =>
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
  }

  private def createSingleHousehold(answers: UserAnswers)  =
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
      benefits <- if (doYouGetAnyBenefits) {
        answers.whichBenefitsYouGet
      } else {
        Some(Set.empty)
      }
    } yield SingleHousehold(Parent(minEarnings, !maxEarnings, selfEmployed, apprentice, benefits.map(WhichBenefitsEnum.withName)))

  private def checkMinEarnings(minEarnings: Boolean,
                       selfOrApprentice: Option[String],
                       employedLessThan12Months: Boolean ,
                       self: Boolean): Option[Boolean] = {
    val strAppOrSelf = if(self) {
      SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString
    } else {
      SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
    }

    if (!minEarnings) {
      selfOrApprentice.flatMap {
        case str if str == strAppOrSelf => employedLessThan12MonthsCheck(str, employedLessThan12Months)
        case _ => Some(false)
      }
    } else {
      Some(false)
    }
  }

  private def employedLessThan12MonthsCheck(selfEmployedOrApprentice: String, employedLessThan12Months:Boolean) = {
    if(selfEmployedOrApprentice.equals(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)){
      Some(true)
    } else if (selfEmployedOrApprentice.equals(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString) && !employedLessThan12Months){
      Some(false)
    } else {
      Some(true)
    }
  }

}

