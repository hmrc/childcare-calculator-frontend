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

import uk.gov.hmrc.childcarecalculatorfrontend.models.Eligibility
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{
  YesNoNotYet,
  YouPartnerBothNeither,
  YouPartnerBothNeitherNotSure
}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import javax.inject.{Inject, Singleton}

@Singleton
class EmploymentSupportedChildcare @Inject() extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {

    val hasParentChildcareCosts: Boolean = answers.childcareCosts.contains(YesNoNotYet.Yes)
    val childcareCostsNotYet: Boolean    = answers.childcareCosts.contains(YesNoNotYet.NotYet)
    val hasPartnerChildcareVouchers      = answers.partnerChildcareVouchers.getOrElse(false)
    val hasParentChildcareVouchers       = answers.yourChildcareVouchers.getOrElse(false)

    val hasPartner            = answers.doYouLiveWithPartner.getOrElse(false)
    val whoInPaidEmployment   = answers.whoIsInPaidEmployment
    val bothChildcareVouchers = answers.whoGetsVouchers

    if (hasPartner) {
      whoInPaidEmployment match {
        case Some(YouPartnerBothNeither.You) =>
          Eligibility.fromBoolean((hasParentChildcareCosts || childcareCostsNotYet) && hasParentChildcareVouchers)
        case Some(YouPartnerBothNeither.Partner) =>
          Eligibility.fromBoolean((hasParentChildcareCosts || childcareCostsNotYet) && hasPartnerChildcareVouchers)
        case Some(_) =>
          Eligibility.fromBoolean(
            (hasParentChildcareCosts || childcareCostsNotYet) &&
              (bothChildcareVouchers.contains(YouPartnerBothNeitherNotSure.Both) || bothChildcareVouchers.contains(
                YouPartnerBothNeitherNotSure.You
              ) || bothChildcareVouchers
                .contains(YouPartnerBothNeitherNotSure.Partner))
          )
        case _ => Eligibility.NotEligible
      }
    } else {

      Eligibility.fromBoolean((hasParentChildcareCosts || childcareCostsNotYet) && hasParentChildcareVouchers)
    }
  }

}
