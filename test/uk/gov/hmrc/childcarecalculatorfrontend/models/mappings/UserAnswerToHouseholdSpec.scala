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

import org.joda.time.LocalDate
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsNumber, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum.{CARERSALLOWANCE, HIGHRATEDISABILITYBENEFITS}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{SchemeSpec, TaxCredits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswerToHouseholdSpec extends SchemeSpec with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val utils = mock[Utils]
  val taxCredits = mock[TaxCredits]

  def userAnswerToHousehold = new UserAnswerToHousehold(frontendAppConfig, utils, taxCredits)
  val todaysDate = LocalDate.now()

  "UserAnswerToHousehold" must {

    "given a user input with location" in {
      val household = Household(location = Location.ENGLAND)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with 1 child" in {
      val child1 = Child(
        id = 0,
        name = "Patrick",
        dob = todaysDate.minusYears(7),
        disability = Some(Disability(true,true,true)),
        childcareCost = Some(ChildCareCost(Some(200.0), Some(ChildcarePayFrequency.MONTHLY))),
        education = Some(Education(inEducation = true, startDate = Some(todaysDate.minusMonths(6)))))

      val household = Household(location = Location.ENGLAND, children = List(child1))
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(answers.noOfChildren) thenReturn Some(1)
      when(answers.childApprovedEducation(0)) thenReturn Some(true)
      when(answers.childStartEducation(0)) thenReturn Some(todaysDate.minusMonths(6))
      when(answers.expectedChildcareCosts(0)) thenReturn Some(BigDecimal(200.0))
      when(answers.childcarePayFrequency(0)) thenReturn Some(ChildcarePayFrequency.MONTHLY)
      when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("Patrick", todaysDate.minusYears(7)))

      when(answers.whichChildrenDisability) thenReturn Some(Set(0))
      when(answers.whichDisabilityBenefits) thenReturn Some(Map(0-> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS,DisabilityBenefits.DISABILITY_BENEFITS)))
      when(answers.whichChildrenBlind) thenReturn Some(Set(0))


      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with 2 children" in {
      val child1 = Child(
        id = 0,
        name = "Kamal",
        dob = todaysDate.minusYears(7),
        disability = Some(Disability(true,true,false)),
        childcareCost = None,
        education = None)
      val child2 = Child(
        id = 1,
        name = "Jagan",
        dob = todaysDate.minusYears(2),
        disability = Some(Disability(true,false,true)),
        childcareCost = None,
        education = None)

      val household = Household(location = Location.ENGLAND, children = List(child1, child2))
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(answers.noOfChildren) thenReturn Some(2)
      when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("Kamal", todaysDate.minusYears(7)))
      when(answers.aboutYourChild(1)) thenReturn Some(AboutYourChild("Jagan", todaysDate.minusYears(2)))

      when(answers.whichChildrenDisability) thenReturn Some(Set(0,1))
      when(answers.whichDisabilityBenefits) thenReturn Some(Map(0-> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS,DisabilityBenefits.DISABILITY_BENEFITS),
        1-> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
      when(answers.whichChildrenBlind) thenReturn Some(Set(1))

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with location and tax credits" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        benefits = Some(Benefits(highRateDisabilityBenefits = true, carersAllowance = true))
      )

      val household = Household(credits = Some(CreditsEnum.TAXCREDITS.toString), location = Location.SCOTLAND, parent = parent)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.whichBenefitsYouGet) thenReturn Some(Set(HIGHRATEDISABILITYBENEFITS.toString, CARERSALLOWANCE.toString))
      when(answers.taxOrUniversalCredits) thenReturn Some(CreditsEnum.TAXCREDITS.toString)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent with minimum earnings" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        escVouchers = Some(YesNoUnsureEnum.NO),
        ageRange = Some(AgeEnum.OVERTWENTYFOUR.toString),
        minimumEarnings = Some(MinimumEarnings(120.0)),
        maximumEarnings = Some(false),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(32000.0))))
      )
      val household = Household(location = Location.SCOTLAND, parent = parent)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourChildcareVouchers) thenReturn Some(YesNoUnsureEnum.NO.toString)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.yourAge) thenReturn Some(AgeEnum.OVERTWENTYFOUR.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.yourMaximumEarnings) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
      when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent with apprentice" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        escVouchers = Some(YesNoUnsureEnum.YES),
        ageRange = Some(AgeEnum.UNDER18.toString),
        minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.APPRENTICE.toString))),
        maximumEarnings = Some(false),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(32000.0))))
      )
      val household = Household(location = Location.NORTHERN_IRELAND, parent = parent)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourChildcareVouchers) thenReturn Some(YesNoUnsureEnum.YES.toString)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.yourAge) thenReturn Some(AgeEnum.UNDER18.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(EmploymentStatusEnum.APPRENTICE.toString)
      when(answers.yourMaximumEarnings) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
      when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent with self employed" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        escVouchers = Some(YesNoUnsureEnum.YES),
        ageRange = Some(AgeEnum.OVERTWENTYFOUR.toString),
        minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED.toString), selfEmployedIn12Months = Some(true))),
        maximumEarnings = Some(false),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(32000.0))))
      )
      val household = Household(location = Location.SCOTLAND, parent = parent)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourChildcareVouchers) thenReturn Some(YesNoUnsureEnum.YES.toString)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.yourAge) thenReturn Some(AgeEnum.OVERTWENTYFOUR.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(EmploymentStatusEnum.SELFEMPLOYED.toString)
      when(answers.yourSelfEmployed) thenReturn Some(true)
      when(answers.yourMaximumEarnings) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
      when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent with neither self employed or apprentice" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        escVouchers = Some(YesNoUnsureEnum.YES),
        ageRange = Some(AgeEnum.UNDER18.toString),
        minimumEarnings = Some(MinimumEarnings(employmentStatus = None)),
        maximumEarnings = Some(false),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(32000.0))))
      )
      val household = Household(location = Location.NORTHERN_IRELAND, parent = parent)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourChildcareVouchers) thenReturn Some(YesNoUnsureEnum.YES.toString)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.yourAge) thenReturn Some(AgeEnum.UNDER18.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn None
      when(answers.yourMaximumEarnings) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
      when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent and partner" in {
      val parent = Claimant(
        hours = Some(BigDecimal(32.1)),
        escVouchers = Some(YesNoUnsureEnum.NOTSURE),
        ageRange = Some(AgeEnum.TWENTYONETOTWENTYFOUR.toString),
        minimumEarnings = Some(MinimumEarnings(112.0)),
        maximumEarnings = Some(true),
        lastYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(21000.0)))),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(72000.0))))
      )
      val partner = Claimant(
        hours = Some(BigDecimal(46.0)),
        escVouchers = Some(YesNoUnsureEnum.YES),
        ageRange = Some(AgeEnum.EIGHTEENTOTWENTY.toString),
        minimumEarnings = Some(MinimumEarnings(89.0)),
        maximumEarnings = Some(false),
        currentYearlyIncome = Some(Income(employmentIncome=Some(BigDecimal(32000.0))))
      )
      val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.WALES)
      when(answers.doYouLiveWithPartner) thenReturn Some(true)

      when(answers.yourChildcareVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(32.1))
      when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONETOTWENTYFOUR.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.yourMaximumEarnings) thenReturn Some(true)
      when(answers.parentEmploymentIncomePY) thenReturn Some(BigDecimal(21000.0))
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(72000.0))

      when(answers.partnerChildcareVouchers) thenReturn Some(YesNoUnsureEnum.YES.toString)
      when(answers.partnerWorkHours) thenReturn Some(BigDecimal(46.0))
      when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
      when(answers.partnerMinimumEarnings) thenReturn Some(true)
      when(answers.partnerMaximumEarnings) thenReturn Some(false)
      when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
      when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn(89) thenReturn(112)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

  }

}
