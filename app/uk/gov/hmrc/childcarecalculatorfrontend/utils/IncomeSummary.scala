/*
 * Copyright 2018 HM Revenue & Customs
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

class IncomeSummary @Inject()(utils: Utils) {
  def load(userAnswers: UserAnswers)(implicit messages: Messages): Map[String, String] = {
    val result: Map[String, String] = Map()
    lazy val parentIncome = loadParentIncome(userAnswers, _: Map[String, String])
    lazy val parentPension = loadHowMuchYouPayPension(userAnswers, _: Map[String, String])
    lazy val parentOtherIncome = loadYourOtherIncome(userAnswers, _: Map[String, String])
    lazy val parentBenefitsIncome = loadYourBenefitsIncome(userAnswers, _: Map[String, String])
    lazy val partnerIncome = loadPartnerIncome(userAnswers, _: Map[String, String])

    userAnswers.doYouLiveWithPartner match {
      case Some(livesWithPartner) => {
        if (livesWithPartner) {
          userAnswers.whoIsInPaidEmployment match {
            case Some(whoInPaidEmployment) => {
              whoInPaidEmployment match {
                case ChildcareConstants.you => {
                  val partnerWorkedAtAnyPointThisYear = userAnswers.partnerPaidWorkCY.fold(false)(c => c)

                  if (partnerWorkedAtAnyPointThisYear) loadBothIncome(userAnswers, result) else parentIncome(result)
                }
                case ChildcareConstants.partner => {
                  val parentWorkedAtAnyPointThisYear = userAnswers.parentPaidWorkCY.fold(false)(c => c)

                  if (parentWorkedAtAnyPointThisYear) loadBothIncome(userAnswers, result) else partnerIncome(result)
                }
                case ChildcareConstants.both => loadBothIncome(userAnswers, result)
              }
            }
            case _ => result
          }
        }
        else{
          (parentIncome andThen parentPension andThen parentOtherIncome andThen parentBenefitsIncome) (result)
        }
      }
      case _ => result
    }
  }

  private def loadBothIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    userAnswers.employmentIncomeCY.foldLeft(result)((result, incomes) =>
      result + (Messages("incomeSummary.yourIncome") -> s"£${utils.valueFormatter(incomes.parentEmploymentIncomeCY)}",
        Messages("incomeSummary.partnersIncome") -> s"£${utils.valueFormatter(incomes.partnerEmploymentIncomeCY)}"))
  }

  private def loadPartnerIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    userAnswers.partnerEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.partnersIncome") -> s"£${utils.valueFormatter(income)}"))
  }

  private def loadParentIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.yourIncome") -> s"£${utils.valueFormatter(income)}"))
  }

  private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.YouPaidPensionCY,result,(Messages("incomeSummary.paidIntoPension") -> Messages("site.no")),Messages("incomeSummary.pensionPaymentsAmonth"),userAnswers.howMuchYouPayPension)
  }

  private def loadYourOtherIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.yourOtherIncomeThisYear,result,(Messages("incomeSummary.otherIncome") -> Messages("site.no")),Messages("incomeSummary.yourOtherIncome"),userAnswers.yourOtherIncomeAmountCY)
  }

  private def loadYourBenefitsIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.youAnyTheseBenefits,result,(Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")),Messages("incomeSummary.yourBenefitsIncome"),userAnswers.youBenefitsIncomeCY)
  }

  private def loadSectionAmount(conditionToCheckAmount: Option[Boolean], result: Map[String,String], conditionNotMet: (String,String), textForIncome: String, incomeSection: Option[BigDecimal])(implicit messages: Messages) = {
    conditionToCheckAmount match {
      case Some(conditionMet) => {
        if (conditionMet) {
          incomeSection.foldLeft(result)((result, income) => result + (textForIncome -> s"£${utils.valueFormatter(income)}"))
        }
        else {
          result + conditionNotMet
        }
      }
      case _ => result + conditionNotMet
    }
  }
}