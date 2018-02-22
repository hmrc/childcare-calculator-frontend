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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothNeitherNotSureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{NotSure, _}

class TCSchemeInEligibilityMsgBuilder {

  val defaultInEligibilityMsg = "result.tc.not.eligible.para1"

  def getMessage(answers: UserAnswers)(implicit messages: Messages): String =
    if (answers.doYouLiveWithPartner.getOrElse(false)) {
      getMessageForPartnerJourney(answers)
    } else {
      getMessageForSingleUser(answers)
    }


  private def getMessageForSingleUser(answers: UserAnswers)(implicit messages: Messages) = {
    val parentHours = answers.parentWorkHours.getOrElse(BigDecimal(0))
    if(parentHours < sixteenHours) {
      messages("result.tc.not.eligible.single.user.hours.less.than.16")
    } else {messages(defaultInEligibilityMsg)}
  }

  private def getMessageForPartnerJourney(answers: UserAnswers)(implicit messages: Messages) =
    answers.whoIsInPaidEmployment match {
      case Some(Both) => messageForPartnerJourneyWithBothInWork(answers)
      case Some(Partner) => messageForPartnerJourneyOnlyPartnerInWork(answers)
      case Some(You) => messages(defaultInEligibilityMsg)
    }

  private def messageForPartnerJourneyWithBothInWork(answers: UserAnswers)(implicit messages: Messages) = {

    val parentHours = answers.parentWorkHours.getOrElse(BigDecimal(0))
    val partnerHours = answers.partnerWorkHours.getOrElse(BigDecimal(0))

    if(parentHours < sixteenHours && partnerHours < sixteenHours) {
      messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
    } else if(parentHours >= sixteenHours) {

    }


  }

  private def messageForPartnerJourneyOnlyPartnerInWork(answers: UserAnswers)(implicit messages: Messages) =
    answers.partnerChildcareVouchers match {
      case Some(No) => messages("result.esc.not.eligible.with.partner.partner.work.no.childCare.voucher")
      case Some(NotSure) => messages("result.esc.not.eligible.with.partner.partner.work.childCare.voucher.notSure")
      case _ => messages(defaultInEligibilityMsg)
    }
}