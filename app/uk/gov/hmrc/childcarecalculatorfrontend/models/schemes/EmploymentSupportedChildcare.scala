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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotEligible, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}

class EmploymentSupportedChildcare extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {

    val hasParentChildcareCosts: Boolean = answers.childcareCosts.contains(YesNoNotYetEnum.YES.toString)
    val childcareCostsNotYet: Boolean    = answers.childcareCosts.contains(YesNoNotYetEnum.NOTYET.toString)
    val hasPartnerChildcareVouchers      = answers.partnerChildcareVouchers.getOrElse(false)
    val hasParentChildcareVouchers       = answers.yourChildcareVouchers.getOrElse(false)

    val hasPartner            = answers.doYouLiveWithPartner.getOrElse(false)
    val whoInPaidEmployment   = answers.whoIsInPaidEmployment
    val bothChildcareVouchers = answers.whoGetsVouchers

    if (hasPartner) {
      whoInPaidEmployment match {
        case Some(You) =>
          getEligibility((hasParentChildcareCosts || childcareCostsNotYet) && hasParentChildcareVouchers)
        case Some(Partner) =>
          getEligibility((hasParentChildcareCosts || childcareCostsNotYet) && hasPartnerChildcareVouchers)
        case Some(_) =>
          getEligibility(
            (hasParentChildcareCosts || childcareCostsNotYet) &&
              (bothChildcareVouchers.contains(Both) || bothChildcareVouchers.contains(You) || bothChildcareVouchers
                .contains(Partner))
          )
        case _ => NotEligible
      }
    } else {

      getEligibility((hasParentChildcareCosts || childcareCostsNotYet) && hasParentChildcareVouchers)
    }
  }

  private def getEligibility(f: Boolean): Eligibility = f match {
    case true  => Eligible
    case false => NotEligible
  }

}
