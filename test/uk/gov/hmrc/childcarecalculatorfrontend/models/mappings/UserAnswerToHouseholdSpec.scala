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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import java.time.LocalDate
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum.{CARERSALLOWANCE, HIGHRATEDISABILITYBENEFITS, SCOTTISHCARERSALLOWANCE}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.time.TaxYear

class UserAnswerToHouseholdSpec extends SchemeSpec with MockitoSugar with BeforeAndAfterEach {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val utils: Utils = mock[Utils]

  val mockTaxYearInfo: TaxYearInfo = mock[TaxYearInfo]

  val currentTaxYear: Int =  TaxYear.current.startYear

  val previousTaxYear: Int = currentTaxYear - 1

  def userAnswerToHousehold: UserAnswerToHousehold = new UserAnswerToHousehold(frontendAppConfig, utils)

  val todaysDate: LocalDate = LocalDate.now()

  override def beforeEach(): Unit = {
    reset(frontendAppConfig, utils, mockTaxYearInfo)
    super.beforeEach()
  }

  "UserAnswerToHousehold" should {

    "convert UserAnswers to Household object" when {

      "user input contains only location" in {
        val claimant = Claimant(escVouchers = Some(YesNoUnsureEnum.NO), minimumEarnings = Some(MinimumEarnings(0.0,None,None)))
        val household = Household(location = Location.ENGLAND, parent = claimant)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "user input has 1 child" in {
        val child1 = Child(
          id = 0,
          name = "Patrick",
          dob = todaysDate.minusYears(7),
          disability = Some(Disability(disabled = true, severelyDisabled = true, blind = true)),
          childcareCost = Some(ChildCareCost(Some(200.0), Some(PeriodEnum.MONTHLY))),
          education = None)
        val claimant = Claimant(escVouchers = Some(YesNoUnsureEnum.NO), minimumEarnings = Some(MinimumEarnings(0.0,None,None)))

        val household = Household(location = Location.ENGLAND, children = List(child1), parent = claimant)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.noOfChildren) thenReturn Some(1)
        when(answers.expectedChildcareCosts(0)) thenReturn Some(BigDecimal(200.0))
        when(answers.childcarePayFrequency(0)) thenReturn Some(ChildcarePayFrequency.MONTHLY)
        when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("Patrick", todaysDate.minusYears(7)))

        when(answers.whichChildrenDisability) thenReturn Some(Set(0))
        when(answers.whichDisabilityBenefits) thenReturn Some(Map(0 -> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS, DisabilityBenefits.DISABILITY_BENEFITS)))
        when(answers.registeredBlind) thenReturn Some(true)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has 2 children" in {
        val claimant = Claimant(escVouchers = Some(YesNoUnsureEnum.NO), minimumEarnings = Some(MinimumEarnings(0.0,None,None)))

        val child1 = Child(
          id = 0,
          name = "Kamal",
          dob = todaysDate.minusYears(7),
          disability = Some(Disability(disabled = true, severelyDisabled = true)),
          childcareCost = None,
          education = None)

        val child2 = Child(
          id = 1,
          name = "Jagan",
          dob = todaysDate.minusYears(2),
          disability = Some(Disability(disabled = true, blind = true)),
          childcareCost = None,
          education = None)

        val household = Household(location = Location.ENGLAND, children = List(child1, child2),parent = claimant)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.noOfChildren) thenReturn Some(2)
        when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("Kamal", todaysDate.minusYears(7)))
        when(answers.aboutYourChild(1)) thenReturn Some(AboutYourChild("Jagan", todaysDate.minusYears(2)))

        when(answers.whichChildrenDisability) thenReturn Some(Set(0, 1))
        when(answers.whichDisabilityBenefits) thenReturn Some(Map(0 -> Set(DisabilityBenefits.HIGHER_DISABILITY_BENEFITS, DisabilityBenefits.DISABILITY_BENEFITS),
          1 -> Set(DisabilityBenefits.DISABILITY_BENEFITS)))
        when(answers.whichChildrenBlind) thenReturn Some(Set(1))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has location and tax credits for non-scottish users" in {
        val parent = Claimant(
          benefits = Some(Benefits(highRateDisabilityBenefits = true, carersAllowance = true)),
          escVouchers = Some(YesNoUnsureEnum.NO),
          minimumEarnings = Some(MinimumEarnings(0.0,None,None))
        )

        val household = Household(credits = Some(CreditsEnum.TAXCREDITS), location = Location.ENGLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(HIGHRATEDISABILITYBENEFITS.toString, CARERSALLOWANCE.toString))
        when(answers.taxOrUniversalCredits) thenReturn Some("tc")

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has location and tax credits for scottish users" in {
        val parent = Claimant(
          benefits = Some(Benefits(highRateDisabilityBenefits = true, carersAllowance = true)),
          escVouchers = Some(YesNoUnsureEnum.NO),
          minimumEarnings = Some(MinimumEarnings(0.0,None,None))
        )

        val household = Household(credits = Some(CreditsEnum.TAXCREDITS), location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(HIGHRATEDISABILITYBENEFITS.toString, SCOTTISHCARERSALLOWANCE.toString))
        when(answers.taxOrUniversalCredits) thenReturn Some("tc")

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with minimum earnings" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is neither self employed nor apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.NEITHER))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is Apprentice " in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(amount = 120, employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is self employed for less than 12 months " in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(amount = 120, employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED),
                                                 selfEmployedIn12Months = Some(true))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)
        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is self employed for more than 12 months " in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED),
            selfEmployedIn12Months = Some(false))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)
        when(answers.yourSelfEmployed) thenReturn Some(false)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay falling within previous tax year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200)))))
          )
        )
        val household = Household(location = Location.ENGLAND, parent = parent)
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(200))
        when(answers.yourStatutoryWeeks) thenReturn Some(4)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomePY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay falling within current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 5.0, statutoryAmount = Some(BigDecimal(300)))))
          )
        )
        val household = Household(location = Location.ENGLAND, parent = parent)
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(currentTaxYear, 5, 1)

        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.yourStatutoryWeeks) thenReturn Some(5)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay split between last and current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 3.0, statutoryAmount = Some(BigDecimal(300)))))
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 2.0, statutoryAmount = Some(BigDecimal(300)))))
          )
        )
        val household = Household(location = Location.ENGLAND, parent = parent)
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(currentTaxYear, 3, 21)


        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.yourStatutoryWeeks) thenReturn Some(5)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(answers.parentEmploymentIncomePY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay split across invalid year and previous year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = None)),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 3.0, statutoryAmount = Some(BigDecimal(300)))))
          )
        )
        val household = Household(location = Location.ENGLAND, parent = parent)
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 3, 21)

        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.yourStatutoryWeeks) thenReturn Some(5)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(answers.parentEmploymentIncomePY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have no minimum earnings and employment status is neither" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.NEITHER))),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.NEITHER))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200.0)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200.0))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)
        when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have no minimum earnings and employment status Apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.APPRENTICE),
                                                 amount = BigDecimal(112))),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.APPRENTICE),
            amount = BigDecimal(89))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200.0)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200.0))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)
        when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent who is self employed for less than 12 months and partner has minimum earnings" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED),
            selfEmployedIn12Months = Some(true), amount = BigDecimal(112))),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(amount = 89)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200.0)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200.0))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)
        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have minimum earnings and either of maximum earnings is true" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200.0)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200.0))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(answers.eitherOfYouMaximumEarnings) thenReturn Some(true)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }


      "has a parent and partner where only partner has statutory pay in previous year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200.0)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200.0))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where only partner has statutory pay in current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200)))))),
                lastYearlyIncome = Some(Income(
                employmentIncome = Some(BigDecimal(21000.0))
            )))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(currentTaxYear, 5, 1)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where only partner has statutory pay split across years" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 3.0, statutoryAmount = Some(BigDecimal(300)))))),
            lastYearlyIncome = Some(Income(
              employmentIncome = Some(BigDecimal(21000.0)),
              statutoryIncome = Some( StatutoryIncome(statutoryWeeks = 2.0, statutoryAmount = Some(BigDecimal(300)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(currentTaxYear, 3, 21)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.partnerStatutoryWeeks) thenReturn Some(5)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }


      "has a parent and partner where both have statutory pay within previous year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 2.0, statutoryAmount = Some(BigDecimal(250)))))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 4.0, statutoryAmount = Some(BigDecimal(200)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(previousTaxYear, 4, 6)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(200))
        when(answers.partnerStatutoryWeeks) thenReturn Some(4)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(250))
        when(answers.yourStatutoryWeeks) thenReturn Some(2)
        when(answers.yourStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where both have statutory pay split across years" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 3.0, statutoryAmount = Some(BigDecimal(300)))))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some( StatutoryIncome(statutoryWeeks = 2.0, statutoryAmount = Some(BigDecimal(300)))))))
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            statutoryIncome = Some(
              StatutoryIncome(statutoryWeeks = 3.0, statutoryAmount = Some(BigDecimal(300)))))),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            statutoryIncome = Some( StatutoryIncome(statutoryWeeks = 2.0, statutoryAmount = Some(BigDecimal(300)))))))

        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        val statutoryStartDate = LocalDate.of(currentTaxYear, 3, 21)

        when(answers.partnerStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.partnerStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.partnerStatutoryWeeks) thenReturn Some(5)
        when(answers.partnerStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.yourStatutoryStartDate) thenReturn Some(statutoryStartDate)
        when(answers.yourStatutoryPayPerWeek) thenReturn Some(BigDecimal(300))
        when(answers.yourStatutoryWeeks) thenReturn Some(5)
        when(answers.yourStatutoryPayBeforeTax) thenReturn Some(false)

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(32000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.UNDER18),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.APPRENTICE))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            pension = Some(BigDecimal(200.0))
          )
          )
        )
        val household = Household(location = Location.NORTHERN_IRELAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.UNDER18.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(EmploymentStatusEnum.APPRENTICE.toString)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(200.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with self employed" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(EmploymentStatusEnum.SELFEMPLOYED), selfEmployedIn12Months = Some(true))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.SCOTLAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(EmploymentStatusEnum.SELFEMPLOYED.toString)
        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent who gets vouchers" in {

        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(EmploymentStatusEnum.SELFEMPLOYED.toString)
        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers).parent.escVouchers.get mustBe YesNoUnsureEnum.NO
      }

      "has a single parent with neither self employed or apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.UNDER18),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = None)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.NORTHERN_IRELAND, parent = parent)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourChildcareVouchers) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.UNDER18.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn None
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(utils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0)))),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0)))),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and only partner works and get vouchers" in {
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.partnerChildcareVouchers) thenReturn Some(true)
        when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(answers.partnerEmploymentIncomePY) thenReturn Some(BigDecimal(21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        val result = userAnswerToHousehold.convert(answers)
        result.parent.escVouchers.get mustEqual YesNoUnsureEnum.NO
        result.partner.get.escVouchers.get mustEqual YesNoUnsureEnum.YES
      }

      "has a parent and partner and only partner works and doesn't get vouchers" in {
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.partnerChildcareVouchers) thenReturn Some(false)
        when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(32000.0))
        when(answers.partnerEmploymentIncomePY) thenReturn Some(BigDecimal(21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        val result = userAnswerToHousehold.convert(answers)
        result.parent.escVouchers.get mustEqual YesNoUnsureEnum.NO
        result.partner.get.escVouchers.get mustEqual YesNoUnsureEnum.NO
      }

      "has a parent and partner containing only current year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(23000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(20000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(23000.0, 20000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and partner containing previous year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          lastYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(21000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(0, 21000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            pension = Some(BigDecimal(250.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            pension = Some(BigDecimal(300.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            pension = Some(BigDecimal(200.0))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            pension = Some(BigDecimal(100.0))
          ))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension(250.0, 200.0))
        when(answers.howMuchBothPayPensionPY) thenReturn Some(HowMuchBothPayPensionPY(300.0, 100.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            pension = Some(BigDecimal(250.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            pension = Some(BigDecimal(200.0))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))
          ))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension(250.0, 200.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            pension = Some(BigDecimal(300.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            pension = Some(BigDecimal(100.0))
          ))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.howMuchBothPayPensionPY) thenReturn Some(HowMuchBothPayPensionPY(300.0, 100.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and a partner containing previous year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            pension = Some(BigDecimal(250.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            pension = Some(BigDecimal(100.0))
          ))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension(250.0, 0))
        when(answers.howMuchBothPayPensionPY) thenReturn Some(HowMuchBothPayPensionPY(0, 100.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            otherIncome = Some(BigDecimal(150.0))
            )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            otherIncome = Some(BigDecimal(175.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NOTSURE),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            otherIncome = Some(BigDecimal(1000.0))
            )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            otherIncome = Some(BigDecimal(2000.0))
           )
          )
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YesNoUnsureEnum.NOTSURE.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(150.0, 1000.0))
        when(answers.otherIncomeAmountPY) thenReturn Some(OtherIncomeAmountPY(175.0, 2000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            otherIncome = Some(BigDecimal(200.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            otherIncome = Some(BigDecimal(1100.0))
          )
          )
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.otherIncomeAmountCY) thenReturn None
        when(answers.otherIncomeAmountPY) thenReturn Some(OtherIncomeAmountPY(200.0, 1100.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            otherIncome = Some(BigDecimal(7500.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            otherIncome = Some(BigDecimal(1350.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))
          )
          )
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(7500.0, 1350.0))
        when(answers.otherIncomeAmountPY) thenReturn None
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and a partner containing previous year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            otherIncome = Some(BigDecimal(150.0))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)))
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)))
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            otherIncome = Some(BigDecimal(2000.0))
          )
          )
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(150.0, 0))
        when(answers.otherIncomeAmountPY) thenReturn Some(OtherIncomeAmountPY(0, 2000.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            benefits = Some(BigDecimal(250))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            benefits = Some(BigDecimal(300))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            benefits = Some(BigDecimal(200))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            benefits = Some(BigDecimal(100))
          ))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY(250.0, 200.0))
        when(answers.bothBenefitsIncomePY) thenReturn Some(BothBenefitsIncomePY(300.0, 100.0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)),
            benefits = Some(BigDecimal(250))
          )
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)))
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.YES),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            benefits = Some(BigDecimal(200))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY(250.0, 200.0))
        when(answers.bothBenefitsIncomePY) thenReturn None
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing previous year and partner containing current year benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.TWENTYONEOROVER),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(72000.0)))
          ),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0)),
            benefits = Some(BigDecimal(300))
          )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoUnsureEnum.NO),
          ageRange = Some(AgeEnum.EIGHTEENTOTWENTY),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(32000.0)),
            benefits = Some(BigDecimal(200))
          )),
          lastYearlyIncome = Some(Income(
            employmentIncome = Some(BigDecimal(21000.0))))
        )
        val household = Household(location = Location.WALES, parent = parent, partner = Some(partner))
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.WALES)
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothNeitherNotSureEnum.NEITHER.toString)
        when(answers.yourAge) thenReturn Some(AgeEnum.TWENTYONEOROVER.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true)
        when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(72000.0, 32000.0))
        when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(21000.0, 21000.0))
        when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY(0, 200.0))
        when(answers.bothBenefitsIncomePY) thenReturn Some(BothBenefitsIncomePY(300.0, 0))
        when(answers.yourPartnersAge) thenReturn Some(AgeEnum.EIGHTEENTOTWENTY.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)
        when(utils.getEarningsForAgeRange(any(), any(), any())) thenReturn 89 thenReturn 112

        userAnswerToHousehold.convert(answers) mustEqual household
      }

    }

  }

}
