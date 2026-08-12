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

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.config.NmwConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit.{CarersAllowance, IncapacityBenefit}
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.child.{Child, ChildCareCost, Disability, Period}
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.claimant.{
  BackendEmploymentStatus,
  Claimant,
  Income,
  MinimumEarnings
}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, TaxYearInfo, UserAnswers}
import uk.gov.hmrc.time.TaxYear

import java.time.LocalDate

class UserAnswerToHouseholdSpec extends SchemeSpec with MockitoSugar with BeforeAndAfterEach {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers*)))

  val nmwConfig: NmwConfig = mock[NmwConfig]

  val mockTaxYearInfo: TaxYearInfo = mock[TaxYearInfo]

  val currentTaxYear: Int = TaxYear.current.startYear

  val previousTaxYear: Int = currentTaxYear - 1

  def userAnswerToHousehold: UserAnswerToHousehold = new UserAnswerToHousehold(nmwConfig)

  val today: LocalDate = LocalDate.now()

  override def beforeEach(): Unit = {
    reset(nmwConfig)
    reset(mockTaxYearInfo)
    super.beforeEach()
  }

  "UserAnswerToHousehold" should {

    "convert UserAnswers to Household object" when {

      "user input contains only location" in {
        val claimant =
          Claimant(escVouchers = Some(YesNoNotSure.No), minimumEarnings = Some(MinimumEarnings(0.0, None, None)))
        val household = Household(location = Location.England, parent = claimant)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "user input has 1 child" in {
        val child1 = Child(
          id = 0,
          name = "Patrick",
          dob = today.minusYears(7),
          disability = Some(Disability(disabled = true, severelyDisabled = true, blind = true)),
          childcareCost = Some(ChildCareCost(Some(200.0), Some(Period.Monthly))),
          education = None
        )
        val claimant =
          Claimant(escVouchers = Some(YesNoNotSure.No), minimumEarnings = Some(MinimumEarnings(0.0, None, None)))

        val household = Household(location = Location.England, children = List(child1), parent = claimant)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.expectedChildcareCosts(0)).thenReturn(Some(BigDecimal(200.0)))
        when(answers.childcarePayFrequency(0)).thenReturn(Some(ChildcarePayFrequency.Monthly))
        when(answers.aboutYourChild(0)).thenReturn(Some(AboutYourChild("Patrick", today.minusYears(7))))

        when(answers.whichChildrenDisability).thenReturn(Some(Set(0)))
        when(answers.whichDisabilityBenefits).thenReturn(
          Some(Map(0 -> Set(DisabilityBenefit.HigherDisabilityBenefits, DisabilityBenefit.DisabilityBenefits)))
        )
        when(answers.registeredBlind).thenReturn(Some(true))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has 2 children" in {
        val claimant =
          Claimant(escVouchers = Some(YesNoNotSure.No), minimumEarnings = Some(MinimumEarnings(0.0, None, None)))

        val child1 = Child(
          id = 0,
          name = "Kamal",
          dob = today.minusYears(7),
          disability = Some(Disability(disabled = true, severelyDisabled = true)),
          childcareCost = None,
          education = None
        )

        val child2 = Child(
          id = 1,
          name = "Jagan",
          dob = today.minusYears(2),
          disability = Some(Disability(disabled = true, blind = true)),
          childcareCost = None,
          education = None
        )

        val household = Household(location = Location.England, children = List(child1, child2), parent = claimant)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.aboutYourChild(0)).thenReturn(Some(AboutYourChild("Kamal", today.minusYears(7))))
        when(answers.aboutYourChild(1)).thenReturn(Some(AboutYourChild("Jagan", today.minusYears(2))))

        when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1)))
        when(answers.whichDisabilityBenefits).thenReturn(
          Some(
            Map(
              0 -> Set(DisabilityBenefit.HigherDisabilityBenefits, DisabilityBenefit.DisabilityBenefits),
              1 -> Set(DisabilityBenefit.DisabilityBenefits)
            )
          )
        )
        when(answers.whichChildrenBlind).thenReturn(Some(Set(1)))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has location and universal credit for non-Scottish users" in {
        val parent = Claimant(
          benefits = Set(CarersAllowance, IncapacityBenefit),
          escVouchers = Some(YesNoNotSure.No),
          minimumEarnings = Some(MinimumEarnings(0.0, None, None))
        )
        val household = Household(
          credits = Some(Credits.UniversalCredit),
          location = Location.England,
          parent = parent
        )

        val answers = spy(userAnswers())
        when(answers.location).thenReturn(Some(Location.England))
        when(answers.doYouGetAnyBenefits).thenReturn(Some(Set(IncapacityBenefit, CarersAllowance)))
        when(answers.universalCredit).thenReturn(Some(true))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has location and UNIVERSAL CREDIT for Scottish users" in {
        val parent = Claimant(
          benefits = Set(CarersAllowance, IncapacityBenefit),
          escVouchers = Some(YesNoNotSure.No),
          minimumEarnings = Some(MinimumEarnings(0.0, None, None))
        )
        val household = Household(
          credits = Some(Credits.UniversalCredit),
          location = Location.Scotland,
          parent = parent
        )

        val answers = spy(userAnswers())
        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouGetAnyBenefits).thenReturn(Some(Set(IncapacityBenefit, CarersAllowance)))
        when(answers.universalCredit).thenReturn(Some(true))

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with minimum earnings" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is neither self employed nor apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Neither))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Neither)
        )
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is Apprentice " in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings =
            Some(MinimumEarnings(amount = 120, employmentStatus = Some(BackendEmploymentStatus.Apprentice))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Apprentice)
        )
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is self employed for less than 12 months " in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(
            MinimumEarnings(
              amount = 120,
              employmentStatus = Some(BackendEmploymentStatus.SelfEmployed),
              selfEmployedIn12Months = Some(true)
            )
          ),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.SelfEmployed)
        )
        when(answers.yourSelfEmployed).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with no minimum earnings and employment status is self employed for more than 12 months " in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(
            MinimumEarnings(
              employmentStatus = Some(BackendEmploymentStatus.SelfEmployed),
              selfEmployedIn12Months = Some(false)
            )
          ),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.SelfEmployed)
        )
        when(answers.yourSelfEmployed).thenReturn(Some(false))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay falling within previous tax year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false)
        )
        val household = Household(location = Location.England, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay falling within current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.England, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay split between last and current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.England, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with statutory pay split across invalid year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(120.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.England, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.England))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(120)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have no minimum earnings and employment status is neither" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Neither))),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Neither))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Neither)
        )
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(false))
        when(answers.partnerSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Neither)
        )
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have no minimum earnings and employment status Apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(
            MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Apprentice), amount = BigDecimal(112))
          ),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings =
            Some(MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Apprentice), amount = BigDecimal(89))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Apprentice)
        )
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(false))
        when(answers.partnerSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.Apprentice)
        )
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent who is self employed for less than 12 months and partner has minimum earnings" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(
            MinimumEarnings(
              employmentStatus = Some(BackendEmploymentStatus.SelfEmployed),
              selfEmployedIn12Months = Some(true),
              amount = BigDecimal(112)
            )
          ),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(amount = 89)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(
          Some(EmploymentStatus.SelfEmployed)
        )
        when(answers.yourSelfEmployed).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and both have minimum earnings and either of maximum earnings is true" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(answers.eitherOfYouMaximumEarnings).thenReturn(Some(true))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where only partner has statutory pay in previous year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where only partner has statutory pay in current year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where only partner has statutory pay split across years" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where both have statutory pay within previous year" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner where both have statutory pay split across years" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )

        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(32000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.UnderEighteen),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = Some(BackendEmploymentStatus.Apprentice))),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              pension = Some(BigDecimal(200.0))
            )
          )
        )
        val household = Household(location = Location.NorthernIreland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.NorthernIreland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.UnderEighteen))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(Some(EmploymentStatus.Apprentice))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(answers.howMuchYouPayPension).thenReturn(Some(BigDecimal(200.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent with self employed" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(
            MinimumEarnings(
              employmentStatus = Some(BackendEmploymentStatus.SelfEmployed),
              selfEmployedIn12Months = Some(true)
            )
          ),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Scotland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(Some(EmploymentStatus.SelfEmployed))
        when(answers.yourSelfEmployed).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a single parent who gets vouchers" in {

        val answers = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Scotland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(false))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(Some(EmploymentStatus.SelfEmployed))
        when(answers.yourSelfEmployed).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers).parent.escVouchers.get mustBe YesNoNotSure.No
      }

      "has a single parent with neither self employed or apprentice" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.UnderEighteen),
          minimumEarnings = Some(MinimumEarnings(employmentStatus = None)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.NorthernIreland, parent = parent)
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.NorthernIreland))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.yourChildcareVouchers).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.UnderEighteen))
        when(answers.yourMinimumEarnings).thenReturn(Some(false))
        when(answers.areYouSelfEmployedOrApprentice).thenReturn(None)
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.parentEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(0)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner and only partner works and get vouchers" in {
        val answers = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Partner))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.partnerChildcareVouchers).thenReturn(Some(true))
        when(answers.partnerEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        val result = userAnswerToHousehold.convert(answers)
        result.parent.escVouchers.get mustEqual YesNoNotSure.No
        result.partner.get.escVouchers.get mustEqual YesNoNotSure.Yes
      }

      "has a parent and partner and only partner works and doesn't get vouchers" in {
        val answers = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Partner))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.partnerChildcareVouchers).thenReturn(Some(false))
        when(answers.partnerEmploymentIncomeCY).thenReturn(Some(BigDecimal(32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        val result = userAnswerToHousehold.convert(answers)
        result.parent.escVouchers.get mustEqual YesNoNotSure.No
        result.partner.get.escVouchers.get mustEqual YesNoNotSure.No
      }

      "has a parent and partner containing only current year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Both))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true)
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false)
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and partner containing previous year incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false)
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Both))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              pension = Some(BigDecimal(250.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              pension = Some(BigDecimal(200.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.You))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.howMuchBothPayPension).thenReturn(Some(HowMuchBothPayPension(250.0, 200.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              pension = Some(BigDecimal(250.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              pension = Some(BigDecimal(200.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.howMuchBothPayPension).thenReturn(Some(HowMuchBothPayPension(250.0, 200.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and a partner containing previous year pensions" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              pension = Some(BigDecimal(250.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.howMuchBothPayPension).thenReturn(Some(HowMuchBothPayPension(250.0, 0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))

        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              otherIncome = Some(BigDecimal(150.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.NotSure),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              otherIncome = Some(BigDecimal(1000.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.NotSure))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.otherIncomeAmountCY).thenReturn(Some(OtherIncomeAmountCY(150.0, 1000.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only previous year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.otherIncomeAmountCY).thenReturn(None)
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              otherIncome = Some(BigDecimal(7500.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              otherIncome = Some(BigDecimal(1350.0))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.otherIncomeAmountCY).thenReturn(Some(OtherIncomeAmountCY(7500.0, 1350.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing current year and a partner containing previous year additional incomes" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              otherIncome = Some(BigDecimal(150.0))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(32000.0))))
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Partner))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.otherIncomeAmountCY).thenReturn(Some(OtherIncomeAmountCY(150.0, 0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing both previous and current benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              benefits = Some(BigDecimal(250))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              benefits = Some(BigDecimal(200))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.You))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.benefitsIncomeCY).thenReturn(Some(BenefitsIncomeCY(250.0, 200.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent and partner containing only current year benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(72000.0)),
              benefits = Some(BigDecimal(250))
            )
          )
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.Yes),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              benefits = Some(BigDecimal(200))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Both))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.benefitsIncomeCY).thenReturn(Some(BenefitsIncomeCY(250.0, 200.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

      "has a parent containing previous year and partner containing current year benefits" in {
        val parent = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.TwentyOneOrOver),
          minimumEarnings = Some(MinimumEarnings(112.0)),
          maximumEarnings = Some(true),
          currentYearlyIncome = Some(Income(employmentIncome = Some(BigDecimal(72000.0))))
        )
        val partner = Claimant(
          escVouchers = Some(YesNoNotSure.No),
          ageRange = Some(Age.EighteenToTwenty),
          minimumEarnings = Some(MinimumEarnings(89.0)),
          maximumEarnings = Some(false),
          currentYearlyIncome = Some(
            Income(
              employmentIncome = Some(BigDecimal(32000.0)),
              benefits = Some(BigDecimal(200))
            )
          )
        )
        val household = Household(location = Location.Wales, parent = parent, partner = Some(partner))
        val answers   = spy(userAnswers())

        when(answers.location).thenReturn(Some(Location.Wales))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
        when(answers.whoGetsVouchers).thenReturn(Some(YouPartnerBothNeitherNotSure.Neither))
        when(answers.yourAge).thenReturn(Some(Age.TwentyOneOrOver))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(true))
        when(answers.employmentIncomeCY).thenReturn(Some(EmploymentIncomeCY(72000.0, 32000.0)))
        when(answers.benefitsIncomeCY).thenReturn(Some(BenefitsIncomeCY(0, 200.0)))
        when(answers.yourPartnersAge).thenReturn(Some(Age.EighteenToTwenty))
        when(answers.partnerMinimumEarnings).thenReturn(Some(true))
        when(answers.partnerMaximumEarnings).thenReturn(Some(false))
        when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(89).thenReturn(112)

        userAnswerToHousehold.convert(answers) mustEqual household
      }

    }

  }

}
