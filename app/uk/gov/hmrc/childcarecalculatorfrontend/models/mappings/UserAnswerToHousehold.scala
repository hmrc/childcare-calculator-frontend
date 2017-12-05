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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{DisabilityBenefits, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

class UserAnswerToHousehold @Inject()(appConfig: FrontendAppConfig, utils: Utils, tc: TaxCredits) {

  def convert(answers: UserAnswers): Household = {
    val children = if(answers.noOfChildren.isDefined) createChildren(answers) else List.empty
    val partner = if(answers.doYouLiveWithPartner.contains(true)) {
      Some(createClaimant(answers, isParent = false))
    } else {
      None
    }
    Household(credits = answers.taxOrUniversalCredits, location = answers.location.getOrElse(Location.ENGLAND),
      parent = createClaimant(answers), partner = partner, children = children)
  }

  private def createChildren(answers: UserAnswers): List[Child] = {

    val totalChildren: Int = answers.noOfChildren.getOrElse(0)
    var childList: List[Child] = List()

    for(i <- 0 to totalChildren-1) {
      val (childName, childDob): (String, LocalDate) = if(answers.aboutYourChild.isDefined || answers.aboutYourChild(i).isDefined) {
        (answers.aboutYourChild(i).get.name, answers.aboutYourChild(i).get.dob)
      } else {
        ("", null)
      }
      val childcareAmt: Option[BigDecimal] = answers.expectedChildcareCosts(i)
      val childcarePeriod: Option[ChildcarePayFrequency.Value] = answers.childcarePayFrequency(i)
      val childcareCost = if(childcareAmt.isDefined) {
        Some(ChildCareCost(childcareAmt, childcarePeriod))
      } else {
        None
      }

      val childInEducation = answers.childApprovedEducation(i).getOrElse(false)
      val childStartDate = answers.childStartEducation(i)
      val childEducation = if(childInEducation) {
        Some(Education(childInEducation, childStartDate))
      } else {
        None
      }
      val child = Child(
        id = i.toShort,
        name = childName,
        dob = childDob,
        disability = Disability.populateFromRawData(i,answers.whichChildrenDisability,answers.whichDisabilityBenefits,answers.whichChildrenBlind),
        childcareCost = childcareCost,
        education = childEducation
      )

      childList ::= child
    }

    childList.sortWith(_.id < _.id)
  }

  private def checkMinEarnings(age: Option[String], selfEmployedOrApprentice: Option[String], selfEmployed: Option[Boolean]): Option[MinimumEarnings] = {
    val amt: Option[BigDecimal] = if(age.isDefined) {
      Some(utils.getEarningsForAgeRange(appConfig.configuration, LocalDate.now, age))
    } else { None }

    if (amt.isDefined || selfEmployedOrApprentice.isDefined || selfEmployed.isDefined) {
      Some(MinimumEarnings(
        amount = amt.getOrElse(0.0),
        employmentStatus = selfEmployedOrApprentice,
        selfEmployedIn12Months = selfEmployed)
      )
    } else { None }
  }

  private def createClaimant(answers: UserAnswers, isParent: Boolean = true): Claimant = {
    val hours = if (isParent) answers.parentWorkHours else answers.partnerWorkHours
    val benefits = if (isParent) answers.whichBenefitsYouGet else answers.whichBenefitsPartnerGet
    val getBenefits = Benefits.populateFromRawData(benefits)
    val vouchers = if (isParent) answers.yourChildcareVouchers else answers.partnerChildcareVouchers
    val selfEmployedOrApprentice = if (isParent) answers.areYouSelfEmployedOrApprentice else answers.partnerSelfEmployedOrApprentice
    val selfEmployed = if (isParent) answers.yourSelfEmployed else answers.partnerSelfEmployed
    val maxEarnings = if (isParent) answers.yourMaximumEarnings else answers.partnerMaximumEarnings
    val age = if (isParent) answers.yourAge else answers.yourPartnersAge
    val minEarnings = checkMinEarnings(age, selfEmployedOrApprentice, selfEmployed)

    val tcEligibility = if (tc.eligibility(answers) == Eligible) true else false
    val taxCode = if (isParent) answers.whatIsYourTaxCode else answers.whatIsYourPartnersTaxCode
    val statPay = None //TODO - to be implemented

    Claimant(
      hours = hours,
      benefits = getBenefits,
      escVouchers = vouchers,
      lastYearlyIncome = getLastYearIncome(isParent, answers, taxCode, statPay),
      currentYearlyIncome = getCurrentYearIncome(isParent, answers, taxCode, statPay),
      ageRange = age,
      minimumEarnings = minEarnings,
      maximumEarnings = maxEarnings
    )

  }

  private def getCurrentYearIncome(isParent: Boolean, answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    if (isParent) {
      if (answers.parentEmploymentIncomeCY.isDefined) {
        Some(Income(
          employmentIncome = answers.parentEmploymentIncomeCY,
          pension = answers.howMuchYouPayPension,
          otherIncome = answers.yourOtherIncomeAmountCY,
          benefits = answers.youBenefitsIncomeCY,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      } else {
        None
      }
    } else {
      if(answers.partnerEmploymentIncomeCY.isDefined) {
        Some(Income(
          employmentIncome = answers.partnerEmploymentIncomeCY,
          pension = answers.howMuchPartnerPayPension,
          otherIncome = answers.partnerOtherIncomeAmountCY,
          benefits = answers.partnerBenefitsIncomeCY,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      } else {
        None
      }
    }

  }

  private def getLastYearIncome(isParent: Boolean, answers: UserAnswers, taxCode: Option[String], statPay: Option[StatutoryIncome]): Option[Income] = {
    if (isParent) {
      if (answers.parentEmploymentIncomePY.isDefined) {
        Some(Income(
          employmentIncome = answers.parentEmploymentIncomePY,
          pension = answers.howMuchYouPayPensionPY,
          otherIncome = answers.yourOtherIncomeAmountPY,
          benefits = answers.youBenefitsIncomePY,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      } else {
        None
      }

    } else {
      if(answers.partnerEmploymentIncomePY.isDefined) {
        Some(Income(
          employmentIncome = answers.partnerEmploymentIncomePY,
          pension = answers.howMuchPartnerPayPensionPY,
          otherIncome = answers.partnerOtherIncomeAmountPY,
          benefits = answers.partnerBenefitsIncomePY,
          statutoryIncome = statPay,
          taxCode = taxCode)
        )
      } else {
        None
      }

    }

  }

}

