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

import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._

import java.time.LocalDate

// scalastyle:off number.of.methods

class UserAnswers(val cacheMap: CacheMap) extends MapFormats with DateTimeUtils {
  def bothGetSameIncomePreviousYear: Option[Boolean] = cacheMap.getEntry[Boolean](BothGetSameIncomePreviousYearId.toString)

  def youGetSameIncomePreviousYear: Option[Boolean] = cacheMap.getEntry[Boolean](YouGetSameIncomePreviousYearId.toString)

  def surveyDoNotUnderstand: Option[String] = cacheMap.getEntry[String](SurveyDoNotUnderstandId.toString)

  def surveyChildcareSupport: Option[Boolean] = cacheMap.getEntry[Boolean](SurveyChildcareSupportId.toString)

  def whoWasInPaidWorkPY: Option[String] = cacheMap.getEntry[String](WhoWasInPaidWorkPYId.toString)

  def partnerStatutoryStartDate: Option[LocalDate] = cacheMap.getEntry[LocalDate](PartnerStatutoryStartDateId.toString)

  def yourStatutoryStartDate: Option[LocalDate] = cacheMap.getEntry[LocalDate](YourStatutoryStartDateId.toString)

  def partnerStatutoryPayPerWeek: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerStatutoryPayPerWeekId.toString)

  def yourStatutoryPayPerWeek: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourStatutoryPayPerWeekId.toString)

  def partnerStatutoryPayBeforeTax: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayBeforeTaxId.toString)

  def yourStatutoryPayBeforeTax: Option[Boolean] = cacheMap.getEntry[Boolean](YourStatutoryPayBeforeTaxId.toString)

  def partnerStatutoryWeeks: Option[Int] = cacheMap.getEntry[Int](PartnerStatutoryWeeksId.toString)

  def yourStatutoryWeeks: Option[Int] = cacheMap.getEntry[Int](YourStatutoryWeeksId.toString)

  def yourStatutoryPayType: Option[StatutoryPayTypeEnum.Value] = cacheMap.getEntry[StatutoryPayTypeEnum.Value](YourStatutoryPayTypeId.toString)

  def partnerStatutoryPayType: Option[StatutoryPayTypeEnum.Value] = cacheMap.getEntry[StatutoryPayTypeEnum.Value](PartnerStatutoryPayTypeId.toString)

  def whoGotStatutoryPay: Option[YouPartnerBothEnum.Value] = cacheMap.getEntry[YouPartnerBothEnum.Value](WhoGotStatutoryPayId.toString)

  def partnerStatutoryPay: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerStatutoryPayId.toString)

  def bothStatutoryPay: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayId.toString)

  def youStatutoryPay: Option[Boolean] = cacheMap.getEntry[Boolean](YouStatutoryPayId.toString)

  def expectedChildcareCosts(index: Int): Option[BigDecimal] =
    expectedChildcareCosts.flatMap(_.get(index))

  def expectedChildcareCosts: Option[Map[Int, BigDecimal]] =
    cacheMap.getEntry[Map[Int, BigDecimal]](ExpectedChildcareCostsId.toString)

  def whichDisabilityBenefits: Option[Map[Int, Set[DisabilityBenefits.Value]]] =
    cacheMap.getEntry[Map[Int, Set[DisabilityBenefits.Value]]](WhichDisabilityBenefitsId.toString)

  def whichDisabilityBenefits(index: Int): Option[Set[DisabilityBenefits.Value]] =
    whichDisabilityBenefits.flatMap(_.get(index))

  def whoHasChildcareCosts: Option[Set[Int]] = cacheMap.getEntry[Set[Int]](WhoHasChildcareCostsId.toString)

  def whichChildrenBlind: Option[Set[Int]] = cacheMap.getEntry[Set[Int]](WhichChildrenBlindId.toString)

  def whichChildrenDisability: Option[Set[Int]] = cacheMap.getEntry[Set[Int]](WhichChildrenDisabilityId.toString)

  def childRegisteredBlind: Option[Boolean] = cacheMap.getEntry[Boolean](ChildRegisteredBlindId.toString)

  def childrenDisabilityBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](ChildrenDisabilityBenefitsId.toString)

  def childcarePayFrequency: Option[Map[Int, ChildcarePayFrequency.Value]] =
    cacheMap.getEntry[Map[Int, ChildcarePayFrequency.Value]](ChildcarePayFrequencyId.toString)

  def childcarePayFrequency(index: Int): Option[ChildcarePayFrequency.Value] =
    childcarePayFrequency.flatMap(_.get(index))

  def employmentIncomePY: Option[EmploymentIncomePY] = cacheMap.getEntry[EmploymentIncomePY](EmploymentIncomePYId.toString)

  def partnerEmploymentIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerEmploymentIncomePYId.toString)

  def parentEmploymentIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentEmploymentIncomePYId.toString)

  def howMuchPartnerPayPensionPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchPartnerPayPensionPYId.toString)

  def howMuchYouPayPensionPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchYouPayPensionPYId.toString)

  def howMuchBothPayPensionPY: Option[HowMuchBothPayPensionPY] = cacheMap.getEntry[HowMuchBothPayPensionPY](HowMuchBothPayPensionPYId.toString)

  def childDisabilityBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](ChildDisabilityBenefitsId.toString)

  def otherIncomeAmountPY: Option[OtherIncomeAmountPY] = cacheMap.getEntry[OtherIncomeAmountPY](OtherIncomeAmountPYId.toString)

  def partnerOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountPYId.toString)

  def yourOtherIncomeAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountPYId.toString)

  def youBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomePYId.toString)

  def partnerBenefitsIncomePY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomePYId.toString)

  def bothBenefitsIncomePY: Option[BothBenefitsIncomePY] = cacheMap.getEntry[BothBenefitsIncomePY](BothBenefitsIncomePYId.toString)

  def howMuchBothPayPension: Option[HowMuchBothPayPension] = cacheMap.getEntry[HowMuchBothPayPension](HowMuchBothPayPensionId.toString)

  def howMuchPartnerPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchPartnerPayPensionId.toString)

  def howMuchYouPayPension: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](HowMuchYouPayPensionId.toString)

  def registeredBlind: Option[Boolean] = cacheMap.getEntry[Boolean](RegisteredBlindId.toString)

  def benefitsIncomeCY: Option[BenefitsIncomeCY] = cacheMap.getEntry[BenefitsIncomeCY](BenefitsIncomeCYId.toString)

  def employmentIncomeCY: Option[EmploymentIncomeCY] = cacheMap.getEntry[EmploymentIncomeCY](EmploymentIncomeCYId.toString)

  def partnerOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerOtherIncomeAmountCYId.toString)

  def yourOtherIncomeAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourOtherIncomeAmountCYId.toString)

  def otherIncomeAmountCY: Option[OtherIncomeAmountCY] = cacheMap.getEntry[OtherIncomeAmountCY](OtherIncomeAmountCYId.toString)

  def bothOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeLYId.toString)

  def whosHadBenefitsPY: Option[YouPartnerBothEnum.Value] = cacheMap.getEntry[YouPartnerBothEnum.Value](WhosHadBenefitsPYId.toString)

  def partnerAnyOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyOtherIncomeLYId.toString)

  def bothAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsPYId.toString)

  def partnerAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyTheseBenefitsPYId.toString)

  def youAnyTheseBenefitsPY: Option[Boolean] = cacheMap.getEntry[Boolean](YouAnyTheseBenefitsPYId.toString)

  def whoPaidIntoPensionPY: Option[String] = cacheMap.getEntry[String](WhoPaidIntoPensionPYId.toString)

  def whoOtherIncomePY: Option[String] = cacheMap.getEntry[String](WhoOtherIncomePYId.toString)

  def youBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YouBenefitsIncomeCYId.toString)

  def partnerBenefitsIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerBenefitsIncomeCYId.toString)

  def yourOtherIncomeLY: Option[Boolean] = cacheMap.getEntry[Boolean](YourOtherIncomeLYId.toString)

  def aboutYourChild(index: Int): Option[AboutYourChild] = aboutYourChild.flatMap(_.get(index))

  def aboutYourChild: Option[Map[Int, AboutYourChild]] = cacheMap.getEntry[Map[Int, AboutYourChild]](AboutYourChildId.toString)

  def bothPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionPYId.toString)

  def partnerPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionPYId.toString)

  def youPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionPYId.toString)

  def bothPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkPYId.toString)

  def partnerPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkPYId.toString)

  def parentPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkPYId.toString)

  def bothOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](BothOtherIncomeThisYearId.toString)

  def bothPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionCYId.toString)

  def PartnerPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidPensionCYId.toString)

  def YouPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](YouPaidPensionCYId.toString)

  def whosHadBenefits: Option[YouPartnerBothEnum.Value] = cacheMap.getEntry[YouPartnerBothEnum.Value](WhosHadBenefitsId.toString)

  def bothAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsCYId.toString)

  def youAnyTheseBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](YouAnyTheseBenefitsIdCY.toString)

  def partnerEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](PartnerEmploymentIncomeCYId.toString)

  def parentEmploymentIncomeCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](ParentEmploymentIncomeCYId.toString)

  def whoGetsOtherIncomeCY: Option[String] = cacheMap.getEntry[String](WhoGetsOtherIncomeCYId.toString)

  def bothPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkCYId.toString)

  def partnerPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerPaidWorkCYId.toString)

  def parentPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](ParentPaidWorkCYId.toString)

  def whoPaysIntoPension: Option[String] = cacheMap.getEntry[String](WhoPaysIntoPensionId.toString)

  def partnerAnyOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerAnyOtherIncomeThisYearId.toString)

  def yourOtherIncomeThisYear: Option[Boolean] = cacheMap.getEntry[Boolean](YourOtherIncomeThisYearId.toString)

  def eitherOfYouMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](EitherOfYouMaximumEarningsId.toString)

  def noOfChildren: Option[Int] = cacheMap.getEntry[Int](NoOfChildrenId.toString)

  def taxOrUniversalCredits: Option[String] = cacheMap.getEntry[String](TaxOrUniversalCreditsId.toString)

  def partnerMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMaximumEarningsId.toString)

  def yourMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMaximumEarningsId.toString)

  def yourSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](YourSelfEmployedId.toString)

  def partnerSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerSelfEmployedId.toString)

  def partnerSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](PartnerSelfEmployedOrApprenticeId.toString)

  def areYouSelfEmployedOrApprentice: Option[String] = cacheMap.getEntry[String](AreYouSelfEmployedOrApprenticeId.toString)

  def partnerMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerMinimumEarningsId.toString)

  def yourMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMinimumEarningsId.toString)

  def yourAge: Option[String] = cacheMap.getEntry[String](YourAgeId.toString)

  def yourPartnersAge: Option[String] = cacheMap.getEntry[String](YourPartnersAgeId.toString)

  def whichBenefitsYouGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsYouGetId.toString)

  def whichBenefitsPartnerGet: Option[Set[String]] = cacheMap.getEntry[Set[String]](WhichBenefitsPartnerGetId.toString)

  def doYouGetAnyBenefits: Option[Set[ParentsBenefits]] = cacheMap.getEntry[Set[ParentsBenefits]](DoYouGetAnyBenefitsId.toString)

  def doesYourPartnerGetAnyBenefits: Option[Set[ParentsBenefits]] = cacheMap.getEntry[Set[ParentsBenefits]](DoesYourPartnerGetAnyBenefitsId.toString)

  def whoGetsVouchers: Option[String] = cacheMap.getEntry[String](WhoGetsVouchersId.toString)

  def yourChildcareVouchers: Option[Boolean] = cacheMap.getEntry[Boolean](YourChildcareVouchersId.toString)

  def partnerChildcareVouchers: Option[Boolean] = cacheMap.getEntry[Boolean](PartnerChildcareVouchersId.toString)

  def whatIsYourTaxCode: Option[String] = cacheMap.getEntry[String](WhatIsYourTaxCodeId.toString)

  def whatIsYourPartnersTaxCode: Option[String] = cacheMap.getEntry[String](WhatIsYourPartnersTaxCodeId.toString)

  def whoIsInPaidEmployment: Option[String] = cacheMap.getEntry[String](WhoIsInPaidEmploymentId.toString)

  def areYouInPaidWork: Option[Boolean] = cacheMap.getEntry[Boolean](AreYouInPaidWorkId.toString)

  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouLiveWithPartnerId.toString)

  def approvedProvider: Option[String] = cacheMap.getEntry[String](ApprovedProviderId.toString)

  def childcareCosts: Option[String] = cacheMap.getEntry[String](ChildcareCostsId.toString)

  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)

  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)

  def childrenAgeGroups: Option[Set[ChildAgeGroup]] = cacheMap.getEntry[Set[ChildAgeGroup]](ChildrenAgeGroupsId.toString) match {
    case None =>
      (childAgedTwo, childAgedThreeOrFour) match {
        case (Some(true), Some(true)) => Some(Set(TwoYears, ThreeYears, FourYears))
        case (_, Some(true)) => Some(Set(ThreeYears, FourYears))
        case (Some(true), _) => Some(Set(TwoYears))
        case (_, Some(false)) => Some(Set(NoneOfThese))
        case _ => None
      }
    case option => option
  }

  def isChildAgedTwo: Option[Boolean] = childrenAgeGroups.map(_.contains(TwoYears))

  def isChildAgedThreeOrFour: Option[Boolean] = childrenAgeGroups.map(_.exists(Set[ChildAgeGroup](ThreeYears, FourYears).contains))

  def isChildAgedNineTo23Months: Option[Boolean] = childrenAgeGroups.map(_.contains(NineTo23Months))

  def location: Option[Location.Value] = cacheMap.getEntry[Location.Value](LocationId.toString)

  def isYouPartnerOrBoth(who: Option[String]): String = {
    val You: String = YouPartnerBothEnum.YOU.toString
    val Partner: String = YouPartnerBothEnum.PARTNER.toString
    val Both: String = YouPartnerBothEnum.BOTH.toString
    val Neither: String = YouPartnerBothNeitherEnum.NEITHER.toString

    who match {
      case Some(You) => You
      case Some(Partner) => Partner
      case Some(Both) => Both
      case Some(Neither) => Neither
      case _ => You
    }
  }

  // scalastyle:off cyclomatic.complexity
  def hasChildEligibleForTfc: Boolean = {
    // Day of birth is before 1st September and age is 16 or under
    def after16yoCutoff(dob: LocalDate) = dob.isAfter(LocalDate.of(now.getYear - 17, 8, 31))

    // Day of birth is before 1st September and age is 11 or under
    def after11yoCutoff(dob: LocalDate) = dob.isAfter(LocalDate.of(now.getYear - 12, 8, 31))

    aboutYourChild.exists { children =>
      children.map {
        case (_, AboutYourChild(_, dob)) if after11yoCutoff(dob) =>
          true
        case (childId, AboutYourChild(_, dob)) if children.size > 1 && after16yoCutoff(dob) &&
          (whichChildrenDisability.exists(_.contains(childId)) || whichChildrenBlind.exists(_.contains(childId))) =>
          true
        case (_, AboutYourChild(_, dob)) if children.size == 1 && after16yoCutoff(dob) &&
          (childrenDisabilityBenefits.contains(true) || registeredBlind.contains(true)) =>
          true
        case _ =>
          false
      }.toSeq.contains(true)
    }
  }

  def childrenOver16: Option[Map[Int, AboutYourChild]] = {
    val children16OrOlder = get16YearOldsAndOlder
    val childrenBetween16And17 = extract16YearOldsWithBirthdayBefore31stAugust(children16OrOlder)
    children16OrOlder.map {
      children =>
        children.filterNot {
          case (x, _) => childrenBetween16And17.getOrElse(Map()).keys.exists(_ == x)
        }
    }
  }

  def extract16YearOldsWithBirthdayBefore31stAugust(children: Option[Map[Int, AboutYourChild]]): Option[Map[Int, AboutYourChild]] = {
    children.map {
      children16OrOlder =>
        children16OrOlder.filter {
          case (_, model) => model.dob.plusYears(16).isAfter(LocalDate.parse(s"${now.getYear - 1}-08-31")) &&
            model.dob.plusYears(16).isBefore(LocalDate.parse(s"${now.getYear}-09-01"))
        }
    }
  }

  private def get16YearOldsAndOlder: Option[Map[Int, AboutYourChild]] = {
    aboutYourChild.map {
      children =>
        children.filter {
          case (_, model) => model.dob.isBefore(now.minusYears(16))
        }
    }
  }

  def numberOfChildrenOver16: Int = childrenOver16.fold(0)(_.size)

  def childrenIdsForAgeExactly16: List[Int] =
    extract16YearOldsWithBirthdayBefore31stAugust(aboutYourChild).getOrElse(Map()).keys.toList

  def childrenBelow16AndExactly16Disabled: List[Int] = {
    (childrenIdsForAgeExactly16AndDisabled ++ childrenBelow16).sorted
  }

  def childrenBelow16: List[Int] =
    aboutYourChild.getOrElse(Map()).filter(_._2.dob.isAfter(now.minusYears(16))).keys.toList

  def childrenIdsForAgeExactly16AndDisabled: List[Int] = {

    childrenIdsForAgeExactly16.filter {

      identity =>
        if (noOfChildren.getOrElse(0) == 1) {
          childrenDisabilityBenefits.contains(true) || registeredBlind.contains(true)
        } else {
          whichChildrenDisability.getOrElse(Set()).contains(identity) || whichChildrenBlind.getOrElse(Set()).contains(identity)
        }
    }
  }

  def childrenWithDisabilityBenefits: Option[Set[Int]] = {
    whichChildrenDisability.orElse {
      noOfChildren.flatMap {
        noOfChildren =>
          if (noOfChildren == 1) {
            childrenDisabilityBenefits.map {
              case true => Set(0)
              case false => Set.empty
            }
          } else {
            childrenDisabilityBenefits.flatMap {
              case true => None
              case false => Some(Set.empty)
            }
          }
      }
    }
  }

  def childrenWithCosts: Option[Set[Int]] = {
    whoHasChildcareCosts.orElse {
      noOfChildren.flatMap {
        noOfChildren =>
          if (noOfChildren == 1) {
            childcareCosts.map {
              value =>
                if (value == YesNoNotYetEnum.YES.toString || value == YesNoNotYetEnum.NOTYET.toString) {
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
  }

  def hasApprovedCosts: Option[Boolean] = {
    for {
      costs <- childcareCosts.map(_ != YesNoNotYetEnum.NO.toString)
      approved <- if (costs) {
        approvedProvider.map(_ != YesNoUnsureEnum.NO.toString)
      } else {
        Some(false)
      }
    } yield approved
  }


  def hasVouchers: Boolean = Seq(yourChildcareVouchers, partnerChildcareVouchers, checkVouchersForBoth).flatten.contains(true)

  def checkVouchersForBoth: Option[Boolean] = whoGetsVouchers match {
    case None => None
    case Some("neither") => Some(false)
    case _ => Some(true)
  }

  def max30HoursEnglandContent: Option[Boolean] = {
    (location, hasVouchers) match {
      case (Some(Location.ENGLAND), true) => Some(true)
      case (Some(Location.ENGLAND), false) => Some(false)
      case (_, _) => None
    }
  }

  def isOnSevereDisabilityPremium: Boolean =
    whichBenefitsYouGet.getOrElse(Set()) ++ whichBenefitsPartnerGet.getOrElse(Set()) contains WhichBenefitsEnum.SEVEREDISABILITYPREMIUM.toString

  def isAlreadyReceivingTaxCredits: Boolean =
    taxOrUniversalCredits match {
      case Some(credit) if credit == TaxOrUniversalCreditsEnum.TC.toString => true
      case None => false
      case _ => false
    }

}
