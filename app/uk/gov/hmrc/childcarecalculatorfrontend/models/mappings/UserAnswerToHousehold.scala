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

import javax.inject.Inject

import org.joda.time.LocalDate
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeEnum.AgeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.CreditsEnum.CreditsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum.PeriodEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

import scala.math.BigDecimal.RoundingMode

class UserAnswerToHousehold @Inject()(appConfig: FrontendAppConfig, utils: Utils, tc: TaxCredits)
  extends OverallIncome {

  private def stringToCreditsEnum(x: Option[String]): Option[CreditsEnum] = x match {
    case Some(x) =>
      x.toUpperCase match {
        case "TC" => Some(CreditsEnum.TAXCREDITS)
        case "UC" => Some(CreditsEnum.UNIVERSALCREDIT)
        case _ => Some(CreditsEnum.NONE)
      }

    case _ => None
  }

  def convert(answers: UserAnswers): Household = {
    val children = if (answers.noOfChildren.isDefined) createChildren(answers) else List.empty
    val partner = if (answers.doYouLiveWithPartner.contains(true)) {
      Some(createPartnerClaimant(answers))
    } else {
      None
    }
    Household(credits = stringToCreditsEnum(answers.taxOrUniversalCredits), location = answers.location.getOrElse(Location.ENGLAND),
      parent = createParentClaimant(answers), partner = partner, children = children)
  }

  private def ccFrequencyToPeriod(x: Option[ChildcarePayFrequency.Value]): Option[PeriodEnum] = x match {
    case Some(ChildcarePayFrequency.MONTHLY) => Some(PeriodEnum.MONTHLY)
    case Some(ChildcarePayFrequency.WEEKLY) => Some(PeriodEnum.WEEKLY)
    case _ => None
  }

  private def createChildren(answers: UserAnswers): List[Child] = {
    val totalChildren: Int = answers.noOfChildren.getOrElse(0)
    var childList: List[Child] = List()

    for (i <- 0 until totalChildren) {
      val (childName, childDob): (String, LocalDate) =
        if (answers.aboutYourChild(i).isDefined) {
          (answers.aboutYourChild(i).get.name, answers.aboutYourChild(i).get.dob)
        } else {
          ("", null)
        }

      val childcareAmt: Option[BigDecimal] = answers.expectedChildcareCosts(i)
      val childcarePeriod: Option[PeriodEnum] = ccFrequencyToPeriod(answers.childcarePayFrequency(i))
      val childcareCost = if (childcareAmt.isDefined) {
        Some(ChildCareCost(childcareAmt, childcarePeriod))
      } else {
        None
      }

      val childInEducation = answers.childApprovedEducation(i).getOrElse(false)
      val childStartDate = answers.childStartEducation(i)
      val childEducation = if (childInEducation) {
        Some(Education(childInEducation, childStartDate))
      } else {
        None
      }
      val child = Child(
        id = i.toShort,
        name = childName,
        dob = childDob,
        disability = Disability.populateFromRawData(i, answers.whichDisabilityBenefits, answers.whichChildrenBlind),
        childcareCost = childcareCost,
        education = childEducation
      )

      childList ::= child
    }

    childList.sortWith(_.id < _.id)
  }

  private def checkMinEarnings(age: Option[String], selfEmployedOrApprentice: Option[String], selfEmployed: Option[Boolean]): Option[MinimumEarnings] = {
    val amt: Option[BigDecimal] = if (age.isDefined) {
      Some(utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, age))
    } else {
      None
    }

    if (amt.isDefined || selfEmployedOrApprentice.isDefined || selfEmployed.isDefined) {
      Some(MinimumEarnings(
        amount = amt.getOrElse(0.0),
        employmentStatus = stringToEmploymentStatusEnum(selfEmployedOrApprentice),
        selfEmployedIn12Months = selfEmployed)
      )
    } else {
      None
    }
  }

  private def stringToYesNoUnsureEnum(x: Option[String]): Option[YesNoUnsureEnum] = x match {
    case Some(x) =>
      x.toLowerCase match {
        case "yes" => Some(YesNoUnsureEnum.YES)
        case "no" => Some(YesNoUnsureEnum.NO)
        case _ => Some(YesNoUnsureEnum.NOTSURE)
      }
    case _ => None
  }

  private def stringToAgeEnum(x: Option[String]): Option[AgeEnum] = x match {
    case Some(x) =>
      x.toUpperCase match {
        case "UNDER18" => Some(AgeEnum.UNDER18)
        case "EIGHTEENTOTWENTY" => Some(AgeEnum.EIGHTEENTOTWENTY)
        case "TWENTYONETOTWENTYFOUR" => Some(AgeEnum.TWENTYONETOTWENTYFOUR)
        case "OVERTWENTYFOUR" => Some(AgeEnum.OVERTWENTYFOUR)
      }
    case _ => None
  }

  private def stringToEmploymentStatusEnum(x: Option[String]): Option[EmploymentStatusEnum] = x match {
    case Some(x) =>
      x.toLowerCase match {
        case "selfemployed" => Some(EmploymentStatusEnum.SELFEMPLOYED)
        case "apprentice" => Some(EmploymentStatusEnum.APPRENTICE)
        case "neither" => Some(EmploymentStatusEnum.NEITHER)
      }
    case _ => None
  }

  private def checkEitherVouchers(eitherVouchers: Option[String]) = {
    if(eitherVouchers.contains(YesNoUnsureEnum.NOTSURE.toString) || eitherVouchers.contains(YesNoUnsureEnum.YES.toString)) {
      true
    } else {
      false
    }
  }

  private def getVoucherValue(eitherGetsVouchers: Option[String], whoGetsVouchers: Option[String], who: String, default: Option[String]): Option[YesNoUnsureEnum] = {

    val vouchers: Option[String] =

      if(eitherGetsVouchers.contains(YesNoUnsureEnum.YES.toString)) {

        if(!whoGetsVouchers.contains(who)) Some("yes") else Some("no")

      } else if(eitherGetsVouchers.contains(YesNoUnsureEnum.NOTSURE.toString) || eitherGetsVouchers.contains(YesNoUnsureEnum.NO.toString)) {
        eitherGetsVouchers
      }
      else
      {
        default
      }

    stringToYesNoUnsureEnum(vouchers)
  }

  private def createParentClaimant(answers: UserAnswers): Claimant = {
    val hours = answers.parentWorkHours
    val benefits = answers.whichBenefitsYouGet
    val getBenefits = Benefits.populateFromRawData(benefits)
    val vouchers = getVoucherValue(answers.eitherGetsVouchers, answers.whoGetsVouchers, "partner", answers.yourChildcareVouchers)
    val selfEmployedOrApprentice = answers.areYouSelfEmployedOrApprentice
    val selfEmployed = answers.yourSelfEmployed
    val maxEarnings = answers.yourMaximumEarnings
    val age = answers.yourAge
    val minEarnings = checkMinEarnings(age, selfEmployedOrApprentice, selfEmployed)

    val tcEligibility = if (tc.eligibility(answers) == Eligible) true else false
    val taxCode = answers.whatIsYourTaxCode
    val statPay = None //TODO - to be implemented

    val currentYearIncome = getParentCurrentYearIncome(answers, taxCode, statPay)
    val previousYearIncome = getParentPreviousYearIncome(answers, taxCode, statPay)

    Claimant(
      hours = hours,
      benefits = getBenefits,
      escVouchers = vouchers,
      lastYearlyIncome = previousYearIncome,
      currentYearlyIncome = currentYearIncome,
      ageRange = stringToAgeEnum(age),
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )
  }

  private def createPartnerClaimant(answers: UserAnswers, isParent: Boolean = true): Claimant = {
    val hours = answers.partnerWorkHours
    val benefits = answers.whichBenefitsPartnerGet
    val getBenefits = Benefits.populateFromRawData(benefits)
    val vouchers = getVoucherValue(answers.eitherGetsVouchers, answers.whoGetsVouchers, "parent", answers.partnerChildcareVouchers)
    val selfEmployedOrApprentice = answers.partnerSelfEmployedOrApprentice
    val selfEmployed = answers.partnerSelfEmployed
    val maxEarnings = answers.partnerMaximumEarnings
    val age = answers.yourPartnersAge
    val minEarnings = checkMinEarnings(age, selfEmployedOrApprentice, selfEmployed)

    val tcEligibility = if (tc.eligibility(answers) == Eligible) true else false
    val taxCode = answers.whatIsYourPartnersTaxCode
    val statPay = None //TODO - to be implemented

    val currentYearIncome = getPartnerCurrentYearIncome(answers, taxCode, statPay)
    val previousYearIncome = getPartnerPreviousYearIncome(answers, taxCode, statPay)

    Claimant(
      hours = hours,
      benefits = getBenefits,
      escVouchers = vouchers,
      lastYearlyIncome = previousYearIncome,
      currentYearlyIncome = currentYearIncome,
      ageRange = stringToAgeEnum(age),
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )

  }

}

sealed trait OverallIncome {

  private val defaultStatutoryPay: Int = 100

  //TODO: Test this after statutory pay journey has been fixed
  private def buildStatutoryPay(value: Option[Int], weeks: Option[Int]): Option[StatutoryIncome] = (value, weeks) match {
    case (Some(v), Some(w)) =>
      Some(StatutoryIncome(
        statutoryWeeks = w.toDouble,
        statutoryAmount = if(w > 0) Some(BigDecimal(v)) else None
      ))
    case (None, Some(w)) =>
      Some(StatutoryIncome(
        statutoryWeeks = w.toDouble,
        statutoryAmount = if(w > 0) Some(BigDecimal(defaultStatutoryPay)) else None
      ))
    case _ =>
      None
  }

  private def yearToMonth(yearlyValue: Option[BigDecimal]): Option[BigDecimal] = yearlyValue match {
    case Some(yearlyValue) =>
    val monthlyValue: BigDecimal = yearlyValue/12
      Some(monthlyValue.setScale(2, RoundingMode.HALF_EVEN))
    case None => None
  }

  def getParentPreviousYearIncome(answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    val incomeValue = determineIncomeValue(answers.parentEmploymentIncomePY, answers.employmentIncomePY, parentEmploymentIncomePY)

    val pensionValue = determineIncomeValue(answers.howMuchYouPayPensionPY, answers.howMuchBothPayPensionPY, parentPensionPY)

    val otherIncome = determineIncomeValue(answers.yourOtherIncomeAmountPY, answers.otherIncomeAmountPY, parentOtherIncomePY)

    val benefits = yearToMonth(
      determineIncomeValue(answers.youBenefitsIncomePY, answers.bothBenefitsIncomePY, parentBenefitsPY)
    )

    //TODO: Implement statutoryPay
    //val statutoryPay = buildStatutoryPay(answers.yourStatutoryPayPerWeek, answers.yourStatutoryWeeks)

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(Income(
          employmentIncome = incomeValue,
          pension = pensionValue,
          otherIncome = otherIncome,
          benefits = benefits,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      case _ => None
    }

  }

  def getPartnerPreviousYearIncome(answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    val incomeValue = determineIncomeValue(answers.partnerEmploymentIncomePY, answers.employmentIncomePY, partnerEmploymentIncomePY)

    val pensionValue = determineIncomeValue(answers.howMuchPartnerPayPensionPY, answers.howMuchBothPayPensionPY, partnerPensionPY)

    val otherIncome = determineIncomeValue(answers.partnerOtherIncomeAmountPY, answers.otherIncomeAmountPY, partnerOtherIncomePY)

    val benefits =  yearToMonth(
      determineIncomeValue(answers.partnerBenefitsIncomePY, answers.bothBenefitsIncomePY, partnerBenefitsPY)
    )

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(Income(
          employmentIncome = incomeValue,
          pension = pensionValue,
          otherIncome = otherIncome,
          benefits = benefits,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      case _ =>
        None
    }

  }

  def getParentCurrentYearIncome(answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    val incomeValue = determineIncomeValue(answers.parentEmploymentIncomeCY, answers.employmentIncomeCY, parentEmploymentIncomeCY)

    val pensionValue = determineIncomeValue(answers.howMuchYouPayPension, answers.howMuchBothPayPension, parentPensionCY)

    val otherIncome = determineIncomeValue(answers.yourOtherIncomeAmountCY, answers.otherIncomeAmountCY, parentOtherIncomeCY)

    val benefits =  yearToMonth(
      determineIncomeValue(answers.youBenefitsIncomeCY, answers.benefitsIncomeCY, parentBenefitsCY)
    )

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(Income(
          employmentIncome = incomeValue,
          pension = pensionValue,
          otherIncome = otherIncome,
          benefits = benefits,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      case _ =>
        None
    }

  }

  def getPartnerCurrentYearIncome(answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    val incomeValue = determineIncomeValue(answers.partnerEmploymentIncomeCY, answers.employmentIncomeCY, partnerEmploymentIncomeCY)

    val pensionValue = determineIncomeValue(answers.howMuchPartnerPayPension, answers.howMuchBothPayPension, partnerPensionCY)

    val otherIncome = determineIncomeValue(answers.partnerOtherIncomeAmountCY, answers.otherIncomeAmountCY, partnerOtherIncomeCY)

    val benefits = yearToMonth(
      determineIncomeValue(answers.partnerBenefitsIncomeCY, answers.benefitsIncomeCY, partnerBenefitsCY)
    )

    incomeValue match {
      case Some(x) if x > 0 =>
        Some(Income(
          employmentIncome = incomeValue,
          pension = pensionValue,
          otherIncome = otherIncome,
          benefits = benefits,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      case _ =>
        None
    }

  }

  private def parentBenefitsPY(x: BothBenefitsIncomePY) : BigDecimal = {
    x.parentBenefitsIncomePY
  }

  private def partnerBenefitsPY(x: BothBenefitsIncomePY) : BigDecimal = {
    x.partnerBenefitsIncomePY
  }

  private def parentBenefitsCY(x: BenefitsIncomeCY) : BigDecimal = {
    x.parentBenefitsIncome
  }

  private def partnerBenefitsCY(x: BenefitsIncomeCY) : BigDecimal = {
    x.partnerBenefitsIncome
  }

  private def parentOtherIncomePY(x: OtherIncomeAmountPY) : BigDecimal = {
    x.parentOtherIncomeAmountPY
  }

  private def partnerOtherIncomePY(x: OtherIncomeAmountPY) : BigDecimal = {
    x.partnerOtherIncomeAmountPY
  }

  private def parentOtherIncomeCY(x: OtherIncomeAmountCY) : BigDecimal = {
    x.parentOtherIncome
  }

  private def partnerOtherIncomeCY(x: OtherIncomeAmountCY) : BigDecimal = {
    x.partnerOtherIncome
  }

  private def partnerPensionCY(x: HowMuchBothPayPension) : BigDecimal = {
    x.howMuchPartnerPayPension
  }

  private def partnerPensionPY(x: HowMuchBothPayPensionPY) : BigDecimal = {
    x.howMuchPartnerPayPensionPY
  }

  private def parentPensionPY(x: HowMuchBothPayPensionPY) : BigDecimal = {
    x.howMuchYouPayPensionPY
  }

  private def parentPensionCY(x: HowMuchBothPayPension) : BigDecimal = {
    x.howMuchYouPayPension
  }

  private def parentEmploymentIncomePY(x: EmploymentIncomePY): BigDecimal = {
    x.parentEmploymentIncomePY
  }

  private def parentEmploymentIncomeCY(x: EmploymentIncomeCY): BigDecimal = {
    x.parentEmploymentIncomeCY
  }

  private def partnerEmploymentIncomeCY(x: EmploymentIncomeCY): BigDecimal = {
    x.partnerEmploymentIncomeCY
  }

  private def partnerEmploymentIncomePY(x: EmploymentIncomePY): BigDecimal = {
    x.partnerEmploymentIncomePY
  }

  private def determineIncomeValue[A](s: Option[BigDecimal], multipleIncome: Option[A], f: A => BigDecimal): Option[BigDecimal] = s match {
    case Some(x) => s
    case None =>
      multipleIncome.fold(Option.empty[BigDecimal]) {
        income =>
          val value = f(income)
          if(value > 0) Some(value) else None
      }
  }

}