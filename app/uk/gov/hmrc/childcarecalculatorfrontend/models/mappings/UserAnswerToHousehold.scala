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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import javax.inject.{Inject, Singleton}

import org.joda.time.LocalDate
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

class UserAnswerToHousehold @Inject()(appConfig: FrontendAppConfig, utils: Utils) {

  def convert(answers: UserAnswers): Household = {
    //    val children = createChildren(answers)
    val parent = createClaimant(answers)
    Household(credits = answers.taxOrUniversalCredits, location = answers.location.getOrElse(Location.ENGLAND),
      parent = parent, children = Nil, partner = None)
  }

  private def createClaimant(answers: UserAnswers): Claimant = {
    val isParent = !answers.doYouLiveWithPartner.getOrElse(false)
    val hours = if(isParent) answers.parentWorkHours else answers.partnerWorkHours
    val benefits = if(isParent) answers.whichBenefitsYouGet else answers.whichBenefitsPartnerGet
    val vouchers = if(isParent) answers.yourChildcareVouchers else answers.partnerChildcareVouchers
    val selfEmployedOrApprentice = if(isParent) answers.areYouSelfEmployedOrApprentice else answers.partnerSelfEmployedOrApprentice
    val selfEmployed = if(isParent) answers.yourSelfEmployed else answers.partnerSelfEmployed
    val age = if(isParent) answers.yourAge else answers.yourPartnersAge
    val amt = utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, age)
    val minEarnings = if(amt > 0) Some(createMinEarnings(amt, selfEmployedOrApprentice, selfEmployed)) else None
    val maxEarnings = if(isParent) answers.yourMaximumEarnings else answers.partnerMaximumEarnings

    println(s"*********************isParent>>>>>>>$isParent")
    println(s"*********************selfEmployedOrApprentice>>>>>>>$selfEmployedOrApprentice")
    println(s"*********************selfEmployed>>>>>>>$selfEmployed")
    println(s"*********************age>>>>>>>$age")
    println(s"*********************amt>>>>>>>$amt")
    val claimant = Claimant(
      ageRange = age,
      benefits = None,
//      lastYearlyIncome =   None,
//      currentYearlyIncome = None,
      hours = hours,
      minimumEarnings = minEarnings,
      escVouchers = vouchers,
      maximumEarnings = maxEarnings
    )
    println(s"*******claimant>>>>>>$claimant")
    claimant
  }

  private def createMinEarnings(amt: Int, employedOrAppren: Option[String], selfEmployed: Option[Boolean]): MinimumEarnings = {
    if(amt > 0) {
      MinimumEarnings(
        amount = amt,
        employmentStatus = employedOrAppren,
        selfEmployedIn12Months = selfEmployed
      )
    } else {
      MinimumEarnings(
        amount = 0.0
      )
    }

  }

}
