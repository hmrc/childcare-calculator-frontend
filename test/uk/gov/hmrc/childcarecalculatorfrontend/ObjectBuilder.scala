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

package uk.gov.hmrc.childcarecalculatorfrontend

import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoUnsureEnum, YouPartnerBothEnum, _}

trait ObjectBuilder {

  val defaultChildAgedTwo = Some(true)
  val defaultChildAgedThreeOrFour = Some(false)
  val defaultExpectChildcareCosts = Some(true)
  val defaultLivingWithPartner = Some(true)
  val defaultPaidOrSelfEmployed = Some(false)
  val defaultWhichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH)
  val defaultGetVouchers = Some(YesNoUnsureEnum.NOTSURE)
  val defaultWhoGetsVouchers = Some(YouPartnerBothEnum.YOU)
  val defaultGetBenefits = Some(false)

  val defaultCredits = Some(CreditsEnum.NONE)
  val defaultLocation = LocationEnum.ENGLAND
  val defaultChildrenList = List.empty

  val defaultAgeRange = Some(AgeRangeEnum.OVERTWENTYFOUR)
  val defaultHours = Some(BigDecimal(12))

  val buildStatutoryIncome = StatutoryIncome(
    statutoryWeeks = 0.00,
    statutoryAmount = Some(BigDecimal(100))
  )

  val buildBenefits = Benefits()

  val buildIncome = Income(
    employmentIncome = Some(BigDecimal(100)),
    pension = Some(BigDecimal(100)),
    otherIncome = Some(BigDecimal(100)),
    benefits = Some(BigDecimal(100)),
    statutoryIncome = Some(buildStatutoryIncome))

  val buildMinimumEarnings = MinimumEarnings()

  val buildClaimant = Claimant(
    ageRange = defaultAgeRange,
    benefits = Some(buildBenefits),
    lastYearlyIncome = Some(buildIncome),
    currentYearlyIncome = Some(buildIncome),
    hours = defaultHours,
    minimumEarnings = Some(buildMinimumEarnings),
    escVouchers = defaultGetVouchers
  )

  val buildHousehold = Household(
    credits = defaultCredits,
    location = defaultLocation,
    children = defaultChildrenList,
    parent = buildClaimant,
    partner = Some(buildClaimant))

  val buildPageObjects = PageObjects(
    household = buildHousehold,
    childAgedTwo = defaultChildAgedTwo,
    childAgedThreeOrFour = defaultChildAgedThreeOrFour,
    expectChildcareCosts = defaultExpectChildcareCosts,
    livingWithPartner = defaultLivingWithPartner,
    paidOrSelfEmployed = defaultPaidOrSelfEmployed,
    whichOfYouInPaidEmployment = defaultWhichOfYouInPaidEmployment,
    getVouchers = defaultGetVouchers,
    whoGetsVouchers = defaultWhoGetsVouchers,
    getBenefits = defaultGetBenefits
  )

}
