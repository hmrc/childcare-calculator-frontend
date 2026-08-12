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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*

import java.time.LocalDate

// scalastyle:off number.of.methods

class UserAnswers(val cacheMap: CacheMap) extends MapFormats with DateTimeUtils {

  def surveyDoNotUnderstand: Option[String] = cacheMap.getEntry(SurveyDoNotUnderstandId)

  def surveyChildcareSupport: Option[Boolean] = cacheMap.getEntry(SurveyChildcareSupportId)

  def expectedChildcareCosts(index: Int): Option[BigDecimal] =
    expectedChildcareCosts.flatMap(_.get(index))

  def expectedChildcareCosts: Option[Map[Int, BigDecimal]] =
    cacheMap.getEntry(ExpectedChildcareCostsId)

  def whichDisabilityBenefits: Option[Map[Int, Set[DisabilityBenefit]]] =
    cacheMap.getEntry(WhichDisabilityBenefitsId)

  def whichDisabilityBenefits(index: Int): Option[Set[DisabilityBenefit]] =
    whichDisabilityBenefits.flatMap(_.get(index))

  def whoHasChildcareCosts: Option[Set[Int]] = cacheMap.getEntry(WhoHasChildcareCostsId)

  def whichChildrenBlind: Option[Set[Int]] = cacheMap.getEntry(WhichChildrenBlindId)

  def whichChildrenDisability: Option[Set[Int]] = cacheMap.getEntry(WhichChildrenDisabilityId)

  def childRegisteredBlind: Option[Boolean] = cacheMap.getEntry(ChildRegisteredBlindId)

  def childrenDisabilityBenefits: Option[Boolean] = cacheMap.getEntry(ChildrenDisabilityBenefitsId)

  def childcarePayFrequency: Option[Map[Int, ChildcarePayFrequency]] =
    cacheMap.getEntry(ChildcarePayFrequencyId)

  def childcarePayFrequency(index: Int): Option[ChildcarePayFrequency] =
    childcarePayFrequency.flatMap(_.get(index))

  def childDisabilityBenefits: Option[Boolean] = cacheMap.getEntry(ChildDisabilityBenefitsId)

  def howMuchBothPayPension: Option[HowMuchBothPayPension] =
    cacheMap.getEntry(HowMuchBothPayPensionId)

  def howMuchPartnerPayPension: Option[BigDecimal] = cacheMap.getEntry(HowMuchPartnerPayPensionId)

  def howMuchYouPayPension: Option[BigDecimal] = cacheMap.getEntry(HowMuchYouPayPensionId)

  def registeredBlind: Option[Boolean] = cacheMap.getEntry(RegisteredBlindId)

  def benefitsIncomeCY: Option[BenefitsIncomeCY] = cacheMap.getEntry(BenefitsIncomeCYId)

  def employmentIncomeCY: Option[EmploymentIncomeCY] =
    cacheMap.getEntry(EmploymentIncomeCYId)

  def partnerOtherIncomeAmountCY: Option[BigDecimal] =
    cacheMap.getEntry(PartnerOtherIncomeAmountCYId)

  def yourOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry(YourOtherIncomeAmountCYId)

  def otherIncomeAmountCY: Option[OtherIncomeAmountCY] =
    cacheMap.getEntry(OtherIncomeAmountCYId)

  def youBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry(YouBenefitsIncomeCYId)

  def partnerBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry(PartnerBenefitsIncomeCYId)

  def aboutYourChild(index: Int): Option[AboutYourChild] = aboutYourChild.flatMap(_.get(index))

  def aboutYourChild: Option[Map[Int, AboutYourChild]] =
    cacheMap.getEntry(AboutYourChildId)

  def bothOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry(BothOtherIncomeThisYearId)

  def bothPaidPensionCY: Option[Boolean] = cacheMap.getEntry(BothPaidPensionCYId)

  def PartnerPaidPensionCY: Option[Boolean] = cacheMap.getEntry(PartnerPaidPensionCYId)

  def YouPaidPensionCY: Option[Boolean] = cacheMap.getEntry(YouPaidPensionCYId)

  def whosHadBenefits: Option[YouPartnerBoth] =
    cacheMap.getEntry(WhosHadBenefitsId)

  def bothAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry(BothAnyTheseBenefitsCYId)

  def youAnyTheseBenefits: Option[Boolean] = cacheMap.getEntry(YouAnyTheseBenefitsCYId)

  def partnerEmploymentIncomeCY: Option[BigDecimal] =
    cacheMap.getEntry(PartnerEmploymentIncomeCYId)

  def parentEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry(ParentEmploymentIncomeCYId)

  def whoGetsOtherIncomeCY: Option[YouPartnerBoth] = cacheMap.getEntry(WhoGetsOtherIncomeCYId)

  def partnerPaidWorkCY: Option[Boolean] = cacheMap.getEntry(PartnerPaidWorkCYId)

  def parentPaidWorkCY: Option[Boolean] = cacheMap.getEntry(ParentPaidWorkCYId)

  def whoPaysIntoPension: Option[YouPartnerBoth] = cacheMap.getEntry(WhoPaysIntoPensionId)

  def yourOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry(YourOtherIncomeThisYearId)

  def eitherOfYouMaximumEarnings: Option[Boolean] = cacheMap.getEntry(EitherOfYouMaximumEarningsId)

  def noOfChildren: Option[Int] = cacheMap.getEntry(NoOfChildrenId)

  def universalCredit: Option[Boolean] = cacheMap.getEntry(UniversalCreditId)

  def partnerMaximumEarnings: Option[Boolean] = cacheMap.getEntry(PartnerMaximumEarningsId)

  def yourMaximumEarnings: Option[Boolean] = cacheMap.getEntry(YourMaximumEarningsId)

  def yourSelfEmployed: Option[Boolean] = cacheMap.getEntry(YourSelfEmployedId)

  def partnerSelfEmployed: Option[Boolean] = cacheMap.getEntry(PartnerSelfEmployedId)

  def partnerSelfEmployedOrApprentice: Option[EmploymentStatus] =
    cacheMap.getEntry(PartnerSelfEmployedOrApprenticeId)

  def areYouSelfEmployedOrApprentice: Option[EmploymentStatus] =
    cacheMap.getEntry(AreYouSelfEmployedOrApprenticeId)

  def partnerMinimumEarnings: Option[Boolean] = cacheMap.getEntry(PartnerMinimumEarningsId)

  def yourMinimumEarnings: Option[Boolean] = cacheMap.getEntry(YourMinimumEarningsId)

  def yourAge: Option[Age] = cacheMap.getEntry(YourAgeId)

  def yourPartnersAge: Option[Age] = cacheMap.getEntry(YourPartnersAgeId)

  def doYouGetAnyBenefits: Option[Set[ParentsBenefit]] =
    cacheMap.getEntry(DoYouGetAnyBenefitsId)

  def doesYourPartnerGetAnyBenefits: Option[Set[ParentsBenefit]] =
    cacheMap.getEntry(DoesYourPartnerGetAnyBenefitsId)

  def whoGetsVouchers: Option[YouPartnerBothNeitherNotSure] =
    cacheMap.getEntry(WhoGetsVouchersId)

  def yourChildcareVouchers: Option[Boolean] = cacheMap.getEntry(YourChildcareVouchersId)

  def partnerChildcareVouchers: Option[Boolean] = cacheMap.getEntry(PartnerChildcareVouchersId)

  def whatIsYourTaxCode: Option[String] = cacheMap.getEntry(WhatIsYourTaxCodeId)

  def whatIsYourPartnersTaxCode: Option[String] = cacheMap.getEntry(WhatIsYourPartnersTaxCodeId)

  def whoIsInPaidEmployment: Option[YouPartnerBothNeither] =
    cacheMap.getEntry(WhoIsInPaidEmploymentId)

  def areYouInPaidWork: Option[Boolean] = cacheMap.getEntry(AreYouInPaidWorkId)

  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry(DoYouLiveWithPartnerId)

  def approvedProvider: Option[YesNoNotSure] = cacheMap.getEntry(ApprovedProviderId)

  def childcareCosts: Option[YesNoNotYet] = cacheMap.getEntry(ChildcareCostsId)

  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry(ChildAgedThreeOrFourId)

  def childAgedTwo: Option[Boolean] = cacheMap.getEntry(ChildAgedTwoId)

  def childrenAgeGroups: Option[Set[ChildAgeGroup]] =
    cacheMap.getEntry(ChildrenAgeGroupsId) match {
      case None =>
        (childAgedTwo, childAgedThreeOrFour) match {
          case (Some(true), Some(true)) =>
            Some(Set(ChildAgeGroup.TwoYears, ChildAgeGroup.ThreeYears, ChildAgeGroup.FourYears))
          case (_, Some(true))  => Some(Set(ChildAgeGroup.ThreeYears, ChildAgeGroup.FourYears))
          case (Some(true), _)  => Some(Set(ChildAgeGroup.TwoYears))
          case (_, Some(false)) => Some(Set(ChildAgeGroup.NoneOfThese))
          case _                => None
        }
      case option => option
    }

  def isChildAgedTwo: Option[Boolean] = childrenAgeGroups.map(_.contains(ChildAgeGroup.TwoYears))

  def isChildAgedThreeOrFour: Option[Boolean] =
    childrenAgeGroups.map(_.exists(Set[ChildAgeGroup](ChildAgeGroup.ThreeYears, ChildAgeGroup.FourYears).contains))

  def isChildAgedNineTo23Months: Option[Boolean] = childrenAgeGroups.map(_.contains(ChildAgeGroup.NineTo23Months))

  def location: Option[Location] = cacheMap.getEntry(LocationId)

  def whoIsInPaidEmploymentDefaultYou: YouPartnerBothNeither =
    whoIsInPaidEmployment.getOrElse(YouPartnerBothNeither.You)

  // scalastyle:off cyclomatic.complexity
  def hasChildEligibleForTfc: Boolean = {
    // Day of birth is before 1st September and age is 16 or under
    def after16yoCutoff(dob: LocalDate) = dob.isAfter(LocalDate.of(now.getYear - 17, 8, 31))

    // Day of birth is before 1st September and age is 11 or under
    def after11yoCutoff(dob: LocalDate) = dob.isAfter(LocalDate.of(now.getYear - 12, 8, 31))

    aboutYourChild.exists { children =>
      children
        .map {
          case (_, AboutYourChild(_, dob)) if after11yoCutoff(dob) =>
            true
          case (childId, AboutYourChild(_, dob))
              if children.size > 1 && after16yoCutoff(dob) &&
                (whichChildrenDisability
                  .exists(_.contains(childId)) || whichChildrenBlind.exists(_.contains(childId))) =>
            true
          case (_, AboutYourChild(_, dob))
              if children.size == 1 && after16yoCutoff(dob) &&
                (childrenDisabilityBenefits.contains(true) || registeredBlind.contains(true)) =>
            true
          case _ =>
            false
        }
        .toSeq
        .contains(true)
    }
  }

  def childrenOver16: Option[Map[Int, AboutYourChild]] = {
    val children16OrOlder      = get16YearOldsAndOlder
    val childrenBetween16And17 = extract16YearOldsWithBirthdayBefore31stAugust(children16OrOlder)
    children16OrOlder.map { children =>
      children.filterNot { case (x, _) => childrenBetween16And17.getOrElse(Map()).keys.exists(_ == x) }
    }
  }

  def extract16YearOldsWithBirthdayBefore31stAugust(
      children: Option[Map[Int, AboutYourChild]]
  ): Option[Map[Int, AboutYourChild]] =
    children.map { children16OrOlder =>
      children16OrOlder.filter { case (_, model) =>
        model.dob.plusYears(16).isAfter(LocalDate.parse(s"${now.getYear - 1}-08-31")) &&
        model.dob.plusYears(16).isBefore(LocalDate.parse(s"${now.getYear}-09-01"))
      }
    }

  private def get16YearOldsAndOlder: Option[Map[Int, AboutYourChild]] =
    aboutYourChild.map(children => children.filter { case (_, model) => model.dob.isBefore(now.minusYears(16)) })

  def numberOfChildrenOver16: Int = childrenOver16.fold(0)(_.size)

  def childrenIdsForAgeExactly16: List[Int] =
    extract16YearOldsWithBirthdayBefore31stAugust(aboutYourChild).getOrElse(Map()).keys.toList

  def childrenBelow16AndExactly16Disabled: List[Int] =
    (childrenIdsForAgeExactly16AndDisabled ++ childrenBelow16).sorted

  def childrenBelow16: List[Int] =
    aboutYourChild.getOrElse(Map()).filter(_._2.dob.isAfter(now.minusYears(16))).keys.toList

  def childrenIdsForAgeExactly16AndDisabled: List[Int] =

    childrenIdsForAgeExactly16.filter { identity =>
      if (noOfChildren.getOrElse(0) == 1) {
        childrenDisabilityBenefits.contains(true) || registeredBlind.contains(true)
      } else {
        whichChildrenDisability.getOrElse(Set()).contains(identity) || whichChildrenBlind
          .getOrElse(Set())
          .contains(identity)
      }
    }

  def childrenWithDisabilityBenefits: Option[Set[Int]] =
    whichChildrenDisability.orElse {
      noOfChildren.flatMap { noOfChildren =>
        if (noOfChildren == 1) {
          childrenDisabilityBenefits.map {
            case true  => Set(0)
            case false => Set.empty
          }
        } else {
          childrenDisabilityBenefits.flatMap {
            case true  => None
            case false => Some(Set.empty)
          }
        }
      }
    }

  def childrenWithCosts: Option[Set[Int]] =
    whoHasChildcareCosts.orElse {
      noOfChildren.flatMap { noOfChildren =>
        if (noOfChildren == 1) {
          childcareCosts.map { value =>
            if (value == YesNoNotYet.Yes || value == YesNoNotYet.NotYet) {
              Set(0)
            } else {
              Set.empty
            }
          }
        } else {
          None
        }
      }
    }

  def hasApprovedCosts: Option[Boolean] =
    for {
      costs <- childcareCosts.map(_ != YesNoNotYet.No)
      approved <-
        if (costs) {
          approvedProvider.map(_ != YesNoNotSure.No)
        } else {
          Some(false)
        }
    } yield approved

  def hasVouchers: Boolean =
    Seq(yourChildcareVouchers, partnerChildcareVouchers, checkVouchersForBoth).flatten.contains(true)

  def checkVouchersForBoth: Option[Boolean] = whoGetsVouchers match {
    case None                                       => None
    case Some(YouPartnerBothNeitherNotSure.Neither) => Some(false)
    case _                                          => Some(true)
  }

  def max30HoursEnglandContent: Option[Boolean] =
    (location, hasVouchers) match {
      case (Some(Location.England), true)  => Some(true)
      case (Some(Location.England), false) => Some(false)
      case (_, _)                          => None
    }

}
