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

import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeEnum.AgeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.CreditsEnum.CreditsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum.PeriodEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{ChildcareConstants, UserAnswers, Utils}

import java.time.LocalDate
import javax.inject.Inject

class UserAnswerToHousehold @Inject() (appConfig: FrontendAppConfig, utils: Utils) extends OverallIncome {

  private def stringToCreditsEnum(x: Option[Boolean]): Option[CreditsEnum] = x match {
    case Some(x) =>
      x match {
        case true => Some(CreditsEnum.UNIVERSALCREDIT)
        case _    => Some(CreditsEnum.NONE)
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
    Household(
      credits = stringToCreditsEnum(answers.universalCredit),
      location = answers.location.getOrElse(Location.ENGLAND),
      parent = createParentClaimant(answers),
      partner = partner,
      children = children
    )
  }

  private def ccFrequencyToPeriod(x: Option[ChildcarePayFrequency.Value]): Option[PeriodEnum] = x match {
    case Some(ChildcarePayFrequency.MONTHLY) => Some(PeriodEnum.MONTHLY)
    case Some(ChildcarePayFrequency.WEEKLY)  => Some(PeriodEnum.WEEKLY)
    case _                                   => None
  }

  private def childDOBFromChildData(answers: UserAnswers, index: Int): Option[(String, LocalDate)] =
    if (answers.aboutYourChild(index).isDefined) {
      Some((answers.aboutYourChild(index).get.name, answers.aboutYourChild(index).get.dob))
    } else {
      None
    }

  private def createChildren(answers: UserAnswers): List[Child] = {
    val totalChildren: Int     = answers.noOfChildren.getOrElse(0)
    var childList: List[Child] = List()

    for (i <- 0 until totalChildren) {
      val childDOB: Option[(String, LocalDate)] = childDOBFromChildData(answers, i)

      if (childDOB.nonEmpty) {
        val childcareAmt: Option[BigDecimal]    = answers.expectedChildcareCosts(i)
        val childcarePeriod: Option[PeriodEnum] = ccFrequencyToPeriod(answers.childcarePayFrequency(i))
        val childcareCost = if (childcareAmt.isDefined) {
          Some(ChildCareCost(childcareAmt, childcarePeriod))
        } else {
          None
        }

        val childIsBlindValue = childIsBlind(answers, totalChildren, i)

        val child = Child(
          id = i.toShort,
          name = childDOB.get._1,
          dob = childDOB.get._2,
          disability = Disability.populateFromRawData(i, answers.whichDisabilityBenefits, childIsBlindValue),
          childcareCost = childcareCost
        )

        childList ::= child
      }
    }

    childList.sortWith(_.id < _.id)
  }

  private def childIsBlind(answers: UserAnswers, count: Int, key: Int): Option[Boolean] = count match {
    case 1 => answers.registeredBlind
    case _ => answers.whichChildrenBlind.map(blindChildren => blindChildren.exists(Set(key)))
  }

  private def checkMinEarnings(
      age: Option[String],
      selfEmployedOrApprentice: Option[String],
      selfEmployedLessThan12Months: Option[Boolean]
  ): Option[MinimumEarnings] = {

    def selfEmployedOrApprenticeCheck =

      (selfEmployedOrApprentice.isDefined && selfEmployedOrApprentice.contains(
        SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
      )
        || selfEmployedLessThan12Months.contains(true))

    val amt: Option[BigDecimal] = if (age.isDefined) {
      Some(utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, age))
    } else {
      None
    }

    if (selfEmployedOrApprentice.isEmpty) {
      Some(MinimumEarnings(amount = amt.getOrElse(0.0)))

    } else if (selfEmployedOrApprentice.contains(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString)) {

      Some(MinimumEarnings(employmentStatus = stringToEmploymentStatusEnum(selfEmployedOrApprentice)))

    } else if (selfEmployedOrApprenticeCheck) {
      Some(
        MinimumEarnings(
          amount = amt.getOrElse(0.0),
          employmentStatus = stringToEmploymentStatusEnum(selfEmployedOrApprentice),
          selfEmployedIn12Months = selfEmployedLessThan12Months
        )
      )
    } else {
      Some(
        MinimumEarnings(
          employmentStatus = stringToEmploymentStatusEnum(selfEmployedOrApprentice),
          selfEmployedIn12Months = selfEmployedLessThan12Months
        )
      )
    }
  }

  private def stringToAgeEnum(x: Option[String]): Option[AgeEnum] = x match {
    case Some(x) =>
      x.toUpperCase match {
        case "UNDER18"          => Some(AgeEnum.UNDER18)
        case "EIGHTEENTOTWENTY" => Some(AgeEnum.EIGHTEENTOTWENTY)
        case "TWENTYONEOROVER"  => Some(AgeEnum.TWENTYONEOROVER)
      }
    case _ => None
  }

  private def stringToEmploymentStatusEnum(x: Option[String]): Option[EmploymentStatusEnum] = x match {
    case Some(x) =>
      x.toLowerCase match {
        case "selfemployed" => Some(EmploymentStatusEnum.SELFEMPLOYED)
        case "apprentice"   => Some(EmploymentStatusEnum.APPRENTICE)
        case "neither"      => Some(EmploymentStatusEnum.NEITHER)
      }
    case _ => None
  }

  private def getVoucherValue(vouchers: Option[String], isPartner: Boolean = false): Option[YesNoUnsureEnum] = {
    val whichParent: String = if (isPartner) {
      ChildcareConstants.Partner
    } else {
      ChildcareConstants.You
    }
    vouchers.fold(Some(YesNoUnsureEnum.NO)) {
      case ChildcareConstants.Both | `whichParent` => Some(YesNoUnsureEnum.YES)
      case ChildcareConstants.NOTSURE              => Some(YesNoUnsureEnum.NOTSURE)
      case ChildcareConstants.YES                  => Some(YesNoUnsureEnum.YES)
      case _                                       => Some(YesNoUnsureEnum.NO)
    }
  }

  private def createParentClaimant(answers: UserAnswers): Claimant = {
    val benefits = answers.doYouGetAnyBenefits.getOrElse(Set.empty)
    val vouchers = if (answers.yourChildcareVouchers.isDefined) {
      answers.yourChildcareVouchers.map {
        case true  => YesNoUnsureEnum.YES
        case false => YesNoUnsureEnum.NO
      }
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
      ageRange = stringToAgeEnum(age),
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )
  }

  private def createPartnerClaimant(answers: UserAnswers): Claimant = {
    val benefits = answers.doesYourPartnerGetAnyBenefits.getOrElse(Set.empty)
    val vouchers = if (answers.partnerChildcareVouchers.isDefined) {
      answers.partnerChildcareVouchers.map {
        case true  => YesNoUnsureEnum.YES
        case false => YesNoUnsureEnum.NO
      }
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
      ageRange = stringToAgeEnum(age),
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
  ): Option[BigDecimal] = s match {
    case Some(x) => s
    case None =>
      multipleIncome.fold(Option.empty[BigDecimal]) { income =>
        val value = f(income)
        if (value > 0) Some(value) else None
      }
  }

}
