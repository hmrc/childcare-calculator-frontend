/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class TCSchemeInEligibilityMsgBuilder {

  val defaultInEligibilityMsg = "result.tc.not.eligible.para1"

  def getMessage(answers: UserAnswers)(implicit messages: Messages): String = {
   if (answers.doYouLiveWithPartner.getOrElse(false)) {
      getMessageForPartnerJourney(answers)
    } else {
      getMessageForSingleUser(answers)
    }
  }

  private def getMessageForSingleUser(answers: UserAnswers)(implicit messages: Messages) = {
    val parentHours = answers.parentWorkHours.getOrElse(BigDecimal(0))

    if(parentHours < sixteenHours) {
      messages("result.tc.not.eligible.single.user.hours.less.than.16")

    } else {
      messageForChildrenBelow16(answers)
    }
  }

  private def getMessageForPartnerJourney(answers: UserAnswers)(implicit messages: Messages) =
    answers.whoIsInPaidEmployment match {
      case Some(Both) => messageForPartnerJourneyWithBothInWork(answers)
      case Some(Partner) => messageForPartnerJourneyOnlyPartnerInWork(answers)
      case Some(You) =>  messageForPartnerJourneyOnlyParentInWork(answers)
      case _ => messages(defaultInEligibilityMsg)
    }

  private def messageForPartnerJourneyWithBothInWork(answers: UserAnswers)(implicit messages: Messages) = {

    val parentHours = answers.parentWorkHours.getOrElse(BigDecimal(0))
    val partnerHours = answers.partnerWorkHours.getOrElse(BigDecimal(0))
    val totalHours = parentHours + partnerHours

    val haveBothLessThan16HoursEach = parentHours < sixteenHours && partnerHours < sixteenHours
    val haveLessThan24HoursCombined = totalHours < twentyFoursHours

    if(haveBothLessThan16HoursEach || haveLessThan24HoursCombined) {
      messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
    } else {
      messageForChildrenBelow16(answers)
    }
  }

  private def messageForPartnerJourneyOnlyPartnerInWork(answers: UserAnswers)(implicit messages: Messages) = {
    val parentBenefits = answers.whichBenefitsYouGet
    if(answers.partnerWorkHours.getOrElse(BigDecimal(0)) < twentyFoursHours) {
      if(parentBenefits.isDefined) {
        messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum.parent.receiving.benefits")
      } else {
        messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }
    } else {
      messageForChildrenBelow16(answers)
    }
  }

  private def messageForPartnerJourneyOnlyParentInWork(answers: UserAnswers)(implicit messages: Messages) = {
    val partnerBenefits = answers.whichBenefitsPartnerGet
    if(answers.parentWorkHours.getOrElse(BigDecimal(0)) < twentyFoursHours) {
      if(partnerBenefits.isDefined) {
        messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum.partner.receiving.benefits")
      } else {
        messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }
    } else {
      messageForChildrenBelow16(answers)
    }
  }

  private def messageForChildrenBelow16(answers: UserAnswers)(implicit messages: Messages) = {

    if(answers.childrenBelow16AndExactly16Disabled.isEmpty){
      messages("result.tc.not.eligible.user.no.child.below.16")
    } else {
      messages(defaultInEligibilityMsg)
    }
  }
}
