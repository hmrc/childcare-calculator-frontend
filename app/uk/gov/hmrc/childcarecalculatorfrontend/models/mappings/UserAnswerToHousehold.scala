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

import uk.gov.hmrc.childcarecalculatorfrontend.config.NmwConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.child.{Child, ChildCareCost, Disability, Period}
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.claimant.{
  BackendEmploymentStatus,
  Claimant,
  Income,
  MinimumEarnings
}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class UserAnswerToHousehold @Inject() (
    nmwConfig: NmwConfig
) extends OverallIncome {

  private def booleanToCredits(x: Option[Boolean]): Option[Credits] = x match {
    case Some(true)  => Some(Credits.UniversalCredit)
    case Some(false) => Some(Credits.None)
    case _           => None
  }

  def convert(answers: UserAnswers): Household = {
    val children = if (answers.noOfChildren.isDefined) createChildren(answers) else List.empty
    val partner = Option.when(answers.doYouLiveWithPartner.contains(true)) {
      createPartnerClaimant(answers)
    }
    Household(
      credits = booleanToCredits(answers.universalCredit),
      location = answers.location.getOrElse(Location.England),
      parent = createParentClaimant(answers),
      partner = partner,
      children = children
    )
  }

  private def ccFrequencyToPeriod(x: Option[ChildcarePayFrequency]): Option[Period] = x match {
    case Some(ChildcarePayFrequency.Monthly) => Some(Period.Monthly)
    case Some(ChildcarePayFrequency.Weekly)  => Some(Period.Weekly)
    case _                                   => None
  }

  private def createChild(answers: UserAnswers, index: Int, totalChildren: Int): Option[Child] =
    for {
      aboutChild <- answers.aboutYourChild(index)
      childcareAmt    = answers.expectedChildcareCosts(index)
      childcarePeriod = ccFrequencyToPeriod(answers.childcarePayFrequency(index))
      childcareCost = Option.when(childcareAmt.isDefined) {
        ChildCareCost(childcareAmt, childcarePeriod)
      }
      childIsBlindValue = childIsBlind(answers, totalChildren, index)
    } yield Child(
      id = index.toShort,
      name = aboutChild.name,
      dob = aboutChild.dob,
      disability = Disability.populateFromRawData(index, answers.whichDisabilityBenefits, childIsBlindValue),
      childcareCost = childcareCost
    )

  private def createChildren(answers: UserAnswers): List[Child] = {
    val totalChildren: Int = answers.noOfChildren
      .getOrElse(0)

    val childList = for {
      index <- 0 until totalChildren
      child <- createChild(answers, index, totalChildren)
    } yield child

    childList.toList
  }

  private def childIsBlind(answers: UserAnswers, count: Int, key: Int): Option[Boolean] = count match {
    case 1 => answers.registeredBlind
    case _ => answers.whichChildrenBlind.map(blindChildren => blindChildren.exists(Set(key)))
  }

  private def checkMinEarnings(
      age: Option[Age],
      selfEmployedOrApprentice: Option[EmploymentStatus],
      selfEmployedLessThan12Months: Option[Boolean]
  ): Option[MinimumEarnings] = {

    def selfEmployedOrApprenticeCheck =
      selfEmployedOrApprentice.isDefined && selfEmployedOrApprentice.contains(
        EmploymentStatus.Apprentice
      )
        || selfEmployedLessThan12Months.contains(true)

    val amt: Option[BigDecimal] = Option.when(age.isDefined) {
      nmwConfig.getEarningsForAgeRange(LocalDate.now, age)
    }

    val backendEmploymentStatus = selfEmployedOrApprentice.map(BackendEmploymentStatus.from)

    if (selfEmployedOrApprentice.isEmpty) {
      Some(MinimumEarnings(amount = amt.getOrElse(0.0)))

    } else if (selfEmployedOrApprentice.contains(EmploymentStatus.Neither)) {

      Some(MinimumEarnings(employmentStatus = backendEmploymentStatus))

    } else if (selfEmployedOrApprenticeCheck) {
      Some(
        MinimumEarnings(
          amount = amt.getOrElse(0.0),
          employmentStatus = backendEmploymentStatus,
          selfEmployedIn12Months = selfEmployedLessThan12Months
        )
      )
    } else {
      Some(
        MinimumEarnings(
          employmentStatus = backendEmploymentStatus,
          selfEmployedIn12Months = selfEmployedLessThan12Months
        )
      )
    }
  }

  private def getVoucherValue(
      vouchers: Option[YouPartnerBothNeitherNotSure],
      isPartner: Boolean = false
  ): Some[YesNoNotSure] = {
    import YouPartnerBothNeitherNotSure.*

    val whichParent: YouPartnerBothNeitherNotSure = if (isPartner) {
      Partner
    } else {
      You
    }
    vouchers.fold[Some[YesNoNotSure]](Some(YesNoNotSure.No)) {
      case Both | `whichParent` => Some(YesNoNotSure.Yes)
      case NotSure              => Some(YesNoNotSure.NotSure)
      case _                    => Some(YesNoNotSure.No)
    }
  }

  private def createParentClaimant(answers: UserAnswers): Claimant = {
    val benefits = answers.doYouGetAnyBenefits.getOrElse(Set.empty)
    val vouchers = if (answers.yourChildcareVouchers.isDefined) {
      answers.yourChildcareVouchers.map(YesNoNotSure.fromBoolean)
    } else {
      getVoucherValue(answers.whoGetsVouchers)
    }
    val selfEmployedOrApprentice     = answers.areYouSelfEmployedOrApprentice
    val selfEmployedLessThan12Months = answers.yourSelfEmployed
    val maxEarnings =
      if (answers.eitherOfYouMaximumEarnings.isDefined) answers.eitherOfYouMaximumEarnings
      else answers.yourMaximumEarnings
    val age         = answers.yourAge
    val minEarnings = checkMinEarnings(age, selfEmployedOrApprentice, selfEmployedLessThan12Months)
    val taxCode     = answers.whatIsYourTaxCode

    val currentYearIncome = getParentCurrentYearIncome(answers, taxCode)

    Claimant(
      benefits = benefits,
      escVouchers = vouchers,
      currentYearlyIncome = currentYearIncome,
      ageRange = age,
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )
  }

  private def createPartnerClaimant(answers: UserAnswers): Claimant = {
    val benefits = answers.doesYourPartnerGetAnyBenefits.getOrElse(Set.empty)
    val vouchers = if (answers.partnerChildcareVouchers.isDefined) {
      answers.partnerChildcareVouchers.map(YesNoNotSure.fromBoolean)
    } else {
      getVoucherValue(answers.whoGetsVouchers, isPartner = true)
    }
    val selfEmployedOrApprentice     = answers.partnerSelfEmployedOrApprentice
    val selfEmployedLessThan12Months = answers.partnerSelfEmployed
    val maxEarnings =
      if (answers.eitherOfYouMaximumEarnings.isDefined) answers.eitherOfYouMaximumEarnings
      else answers.partnerMaximumEarnings
    val age         = answers.yourPartnersAge
    val minEarnings = checkMinEarnings(age, selfEmployedOrApprentice, selfEmployedLessThan12Months)
    val taxCode     = answers.whatIsYourPartnersTaxCode

    val currentYearIncome = getPartnerCurrentYearIncome(answers, taxCode)

    Claimant(
      benefits = benefits,
      escVouchers = vouchers,
      currentYearlyIncome = currentYearIncome,
      ageRange = age,
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )

  }

}

sealed trait OverallIncome {

  def getParentCurrentYearIncome(answers: UserAnswers, taxCode: Option[String]): Option[Income] = {
    val incomeValue =
      determineIncomeValue(answers.parentEmploymentIncomeCY, answers.employmentIncomeCY, parentEmploymentIncomeCY)

    val pensionValue =
      determineIncomeValue(answers.howMuchYouPayPension, answers.howMuchBothPayPension, parentPensionCY)

    val otherIncome =
      determineIncomeValue(answers.yourOtherIncomeAmountCY, answers.otherIncomeAmountCY, parentOtherIncomeCY)

    val benefits = determineIncomeValue(answers.youBenefitsIncomeCY, answers.benefitsIncomeCY, parentBenefitsCY)

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(
          Income(
            employmentIncome = incomeValue,
            pension = pensionValue,
            otherIncome = otherIncome,
            benefits = benefits,
            taxCode = taxCode
          )
        )
      case _ =>
        None
    }

  }

  def getPartnerCurrentYearIncome(answers: UserAnswers, taxCode: Option[String]): Option[Income] = {
    val incomeValue =
      determineIncomeValue(answers.partnerEmploymentIncomeCY, answers.employmentIncomeCY, partnerEmploymentIncomeCY)

    val pensionValue =
      determineIncomeValue(answers.howMuchPartnerPayPension, answers.howMuchBothPayPension, partnerPensionCY)

    val otherIncome =
      determineIncomeValue(answers.partnerOtherIncomeAmountCY, answers.otherIncomeAmountCY, partnerOtherIncomeCY)

    val benefits = determineIncomeValue(answers.partnerBenefitsIncomeCY, answers.benefitsIncomeCY, partnerBenefitsCY)

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(
          Income(
            employmentIncome = incomeValue,
            pension = pensionValue,
            otherIncome = otherIncome,
            benefits = benefits,
            taxCode = taxCode
          )
        )
      case _ =>
        None
    }

  }

  private def parentBenefitsCY(x: BenefitsIncomeCY): BigDecimal =
    x.parentBenefitsIncome

  private def partnerBenefitsCY(x: BenefitsIncomeCY): BigDecimal =
    x.partnerBenefitsIncome

  private def parentOtherIncomeCY(x: OtherIncomeAmountCY): BigDecimal =
    x.parentOtherIncome

  private def partnerOtherIncomeCY(x: OtherIncomeAmountCY): BigDecimal =
    x.partnerOtherIncome

  private def partnerPensionCY(x: HowMuchBothPayPension): BigDecimal =
    x.howMuchPartnerPayPension

  private def parentPensionCY(x: HowMuchBothPayPension): BigDecimal =
    x.howMuchYouPayPension

  private def parentEmploymentIncomeCY(x: EmploymentIncomeCY): BigDecimal =
    x.parentEmploymentIncomeCY

  private def partnerEmploymentIncomeCY(x: EmploymentIncomeCY): BigDecimal =
    x.partnerEmploymentIncomeCY

  private def determineIncomeValue[A](
      s: Option[BigDecimal],
      multipleIncome: Option[A],
      f: A => BigDecimal
  ): Option[BigDecimal] =
    if (s.isDefined) {
      s
    } else {
      multipleIncome.fold(Option.empty[BigDecimal]) { income =>
        val value = f(income)
        Option.when(value > 0) {
          value
        }
      }
    }

}
