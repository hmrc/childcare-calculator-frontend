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

import javax.inject.Inject
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum

import scala.collection.immutable.ListMap

class IncomeSummary @Inject()(utils: Utils) {

  def load(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    userAnswers.doYouLiveWithPartner match {

      case Some(true) =>
        loadBothIncomeSection(userAnswers) ++
          loadBothPension(userAnswers) ++
          loadBothBenefits(userAnswers) ++
          loadBothOtherIncome(userAnswers)

      case Some(false) =>
        loadParentIncome(userAnswers) ++
          loadHowMuchYouPayPension(userAnswers) ++
          loadYourBenefitsIncome(userAnswers) ++
          loadYourOtherIncome(userAnswers)

      case _ => ListMap.empty
    }

  private def loadBothIncomeSection(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] = {
    def partnerDidNotWorkThisYear: Boolean = !(userAnswers.partnerPaidWorkCY.getOrElse(false))
    def parentDidNotWorkThisYear: Boolean = !(userAnswers.parentPaidWorkCY.getOrElse(false))

    userAnswers.whoIsInPaidEmployment match {
      case Some(ChildcareConstants.you) if partnerDidNotWorkThisYear    => loadParentIncome(userAnswers)
      case Some(ChildcareConstants.partner) if parentDidNotWorkThisYear => loadPartnerIncome(userAnswers)
      case Some(_)                                                      => loadBothIncome(userAnswers)

      case _ => ListMap.empty
    }
  }

  private def loadPartnerIncome(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    userAnswers.partnerEmploymentIncomeCY.fold(ListMap.empty[String, String]) { income =>
      ListMap(Messages("incomeSummary.partnersIncome") -> s"£${income.format}")
  }

  private def loadBothIncome(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    userAnswers.employmentIncomeCY.fold(ListMap.empty[String, String]) { incomes =>
      ListMap(
        Messages("incomeSummary.yourIncome") -> s"£${incomes.parentEmploymentIncomeCY.format}",
        Messages("incomeSummary.partnersIncome") -> s"£${incomes.partnerEmploymentIncomeCY.format}"
      )
    }

  private def loadBothPension(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] = {
    def parentPaysIntoPension: Boolean = userAnswers.YouPaidPensionCY.getOrElse(false)
    def partnerPaysIntoPension: Boolean = userAnswers.PartnerPaidPensionCY.getOrElse(false)

    userAnswers.whoIsInPaidEmployment match {

      case Some(ChildcareConstants.you) if parentPaysIntoPension =>
        userAnswers.howMuchYouPayPension.fold(ListMap.empty[String, String]) { pension =>
          ListMap(Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${pension.format}")
        }

      case Some(ChildcareConstants.partner) if partnerPaysIntoPension =>
        userAnswers.howMuchPartnerPayPension.fold(ListMap.empty[String, String]) { pension =>
          ListMap(Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${pension.format}")
        }

      case Some(ChildcareConstants.both) =>
        loadBothPensionWhenBothAreInPaidEmployment(userAnswers)

      case Some(_) =>
        ListMap(Messages("incomeSummary.paidIntoPension") -> Messages("site.no"))

      case _ => ListMap.empty
    }
  }

  private def loadBothPensionWhenBothAreInPaidEmployment(userAnswers: UserAnswers)(implicit message: Messages): ListMap[String, String] =
    (userAnswers.bothPaidPensionCY, userAnswers.whoPaysIntoPension) match {

      case (Some(true), Some(ChildcareConstants.you)) =>
        userAnswers.howMuchYouPayPension.fold(ListMap.empty[String, String]) { pension =>
          ListMap(Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${pension.format}")
        }

      case (Some(true), Some(ChildcareConstants.partner)) =>
        userAnswers.howMuchPartnerPayPension.fold(ListMap.empty[String, String]) { pension =>
          ListMap(Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${pension.format}")
        }

      case (Some(true), Some(ChildcareConstants.both)) =>
        userAnswers.howMuchBothPayPension.fold(ListMap.empty[String, String]) { pensions =>
          ListMap(
            Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${pensions.howMuchYouPayPension.format}",
            Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${pensions.howMuchPartnerPayPension.format}"
          )
        }

      case (Some(false), _) =>
        ListMap(Messages("incomeSummary.paidIntoPension") -> Messages("site.no"))

      case _ => ListMap.empty
    }

  private def loadBothBenefits(userAnswers: UserAnswers)(implicit message: Messages): ListMap[String, String] =
    (userAnswers.bothAnyTheseBenefitsCY, userAnswers.whosHadBenefits) match {

      case (Some(true), Some(YouPartnerBothEnum.YOU)) =>
        userAnswers.youBenefitsIncomeCY.fold(ListMap.empty[String, String]) { benefitAmount =>
          ListMap(Messages("incomeSummary.yourBenefitsIncome") -> s"£${benefitAmount.format}")
        }

      case (Some(true), Some(YouPartnerBothEnum.PARTNER)) =>
        userAnswers.partnerBenefitsIncomeCY.fold(ListMap.empty[String, String]) { benefitAmount =>
          ListMap(Messages("incomeSummary.partnerBenefitsIncome") -> s"£${benefitAmount.format}")
        }

      case (Some(true), Some(YouPartnerBothEnum.BOTH)) =>
        userAnswers.benefitsIncomeCY.fold(ListMap.empty[String, String]) { benefits =>
          ListMap(
            Messages("incomeSummary.yourBenefitsIncome") -> s"£${benefits.parentBenefitsIncome.format}",
            Messages("incomeSummary.partnerBenefitsIncome") -> s"£${benefits.partnerBenefitsIncome.format}"
          )
        }

      case (Some(false), _) =>
        ListMap(Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"))

      case _ => ListMap.empty
    }

  private def loadBothOtherIncome(userAnswers: UserAnswers)(implicit message: Messages): ListMap[String, String] = {
    (userAnswers.bothOtherIncomeThisYear, userAnswers.whoGetsOtherIncomeCY) match {

      case (Some(true), Some(ChildcareConstants.you)) =>
        userAnswers.yourOtherIncomeAmountCY.fold(ListMap.empty[String, String]) { otherIncome =>
          ListMap(Messages("incomeSummary.yourOtherIncome") -> s"£${otherIncome.format}")
        }

      case (Some(true), Some(ChildcareConstants.partner)) =>
        userAnswers.partnerOtherIncomeAmountCY.fold(ListMap.empty[String, String]) { otherIncome =>
          ListMap(Messages("incomeSummary.partnerOtherIncome") -> s"£${otherIncome.format}")
        }

      case (Some(true), Some(ChildcareConstants.both)) =>
        userAnswers.otherIncomeAmountCY.fold(ListMap.empty[String, String]) { otherIncomes =>
          ListMap(
            Messages("incomeSummary.yourOtherIncome") -> s"£${otherIncomes.parentOtherIncome.format}",
            Messages("incomeSummary.partnerOtherIncome") -> s"£${otherIncomes.partnerOtherIncome.format}"
          )
        }

      case (Some(false), _) =>
        ListMap(Messages("incomeSummary.otherIncome") -> Messages("site.no"))

      case _ => ListMap.empty
    }
  }

  private def loadParentIncome(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    userAnswers.parentEmploymentIncomeCY.fold(ListMap.empty[String, String]) { income =>
      ListMap(Messages("incomeSummary.yourIncome") -> s"£${income.format}")
    }

  private def loadHowMuchYouPayPension(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    loadSectionAmount(
      userAnswers.YouPaidPensionCY,
      (Messages("incomeSummary.paidIntoPension") -> Messages("site.no")),
      Messages("incomeSummary.pensionPaymentsAmonth"),
      userAnswers.howMuchYouPayPension
    )

  private def loadYourOtherIncome(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    loadSectionAmount(
      userAnswers.yourOtherIncomeThisYear,
      (Messages("incomeSummary.otherIncome") -> Messages("site.no")),
      Messages("incomeSummary.yourOtherIncome"),
      userAnswers.yourOtherIncomeAmountCY
    )

  private def loadYourBenefitsIncome(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] =
    loadSectionAmount(
      userAnswers.youAnyTheseBenefits,
      (Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")),
      Messages("incomeSummary.yourBenefitsIncome"),
      userAnswers.youBenefitsIncomeCY
    )

  private def loadSectionAmount(
    conditionToCheckAmount: Option[Boolean],
    conditionNotMet: (String, String),
    textForIncome: String,
    incomeSection: Option[BigDecimal]
  ): ListMap[String, String] =
    conditionToCheckAmount match {

      case Some(true) =>
        incomeSection.fold(ListMap.empty[String, String]) { income =>
          ListMap(textForIncome -> s"£${income.format}")
        }

      case Some(false) => ListMap(conditionNotMet)

      case _ => ListMap(conditionNotMet)
    }

  implicit private class BigDecimalValueFormatter(bigDecimal: BigDecimal) {
    def format: String = utils.valueFormatter(bigDecimal)
  }
}
