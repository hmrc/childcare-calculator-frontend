/*
 * Copyright 2020 HM Revenue & Customs
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
  def load(userAnswers: UserAnswers)(implicit messages: Messages): ListMap[String, String] = {
    val result: ListMap[String, String] = ListMap()
    lazy val parentIncome = loadParentIncome(userAnswers, _: ListMap[String, String])
    lazy val parentPension = loadHowMuchYouPayPension(userAnswers, _: ListMap[String, String])
    lazy val parentOtherIncome = loadYourOtherIncome(userAnswers, _: ListMap[String, String])
    lazy val parentBenefitsIncome = loadYourBenefitsIncome(userAnswers, _: ListMap[String, String])
    lazy val bothIncome = loadBothIncomeSection(userAnswers, _: ListMap[String, String])
    lazy val bothPension = loadBothPension(userAnswers, _: ListMap[String, String])
    lazy val bothBenefits = loadBothBenefits(userAnswers, _: ListMap[String, String])
    lazy val bothOtherIncome = loadBothOtherIncome(userAnswers, _: ListMap[String, String])

    userAnswers.doYouLiveWithPartner match {
      case Some(livesWithPartner) => {
        if (livesWithPartner) {
          (bothIncome andThen bothPension andThen bothBenefits andThen bothOtherIncome) (result)
        }
        else {
          (parentIncome andThen parentPension andThen parentBenefitsIncome andThen parentOtherIncome) (result)
        }
      }
      case _ => result
    }
  }

  private def loadBothOtherIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit message: Messages) = {
    userAnswers.bothOtherIncomeThisYear match {
      case Some(anyoneGetsOtherIncome) => {
        if (anyoneGetsOtherIncome) {
          userAnswers.whoGetsOtherIncomeCY match {
            case Some(whoGetsOtherIncome) => {
              whoGetsOtherIncome match {
                case ChildcareConstants.you => userAnswers.yourOtherIncomeAmountCY.foldLeft(result)((result, otherIncome) => result + (Messages("incomeSummary.yourOtherIncome") -> s"£${utils.valueFormatter(otherIncome)}"))
                case ChildcareConstants.partner => userAnswers.partnerOtherIncomeAmountCY.foldLeft(result)((result, otherIncome) => result + (Messages("incomeSummary.partnerOtherIncome") -> s"£${utils.valueFormatter(otherIncome)}"))
                case ChildcareConstants.both => {
                  userAnswers.otherIncomeAmountCY.foldLeft(result)((result, otherIncomes) =>

                    result ++: ListMap(Messages("incomeSummary.yourOtherIncome") -> s"£${utils.valueFormatter(otherIncomes.parentOtherIncome)}",
                      Messages("incomeSummary.partnerOtherIncome") -> s"£${utils.valueFormatter(otherIncomes.partnerOtherIncome)}"))
                }
              }
            }
            case _ => result
          }
        }
        else {
          result + (Messages("incomeSummary.otherIncome") -> Messages("site.no"))
        }
      }
      case _ => result
    }
  }

  private def loadBothBenefits(userAnswers: UserAnswers, result: ListMap[String, String])(implicit message: Messages) = {
    userAnswers.bothAnyTheseBenefitsCY match {
      case Some(anyGotBenefits) => {
        if (anyGotBenefits) {
          userAnswers.whosHadBenefits match {
            case Some(whoGetsBenefits) => {
              whoGetsBenefits match {
                case YouPartnerBothEnum.YOU => userAnswers.youBenefitsIncomeCY.foldLeft(result)((result, benefitAmount) => result + (Messages("incomeSummary.yourBenefitsIncome") -> s"£${utils.valueFormatter(benefitAmount)}"))
                case YouPartnerBothEnum.PARTNER => userAnswers.partnerBenefitsIncomeCY.foldLeft(result)((result, benefitAmount) => result + (Messages("incomeSummary.partnerBenefitsIncome") -> s"£${utils.valueFormatter(benefitAmount)}"))
                case YouPartnerBothEnum.BOTH => {
                  userAnswers.benefitsIncomeCY.foldLeft(result)((result, benefits) =>
                    result ++: ListMap(Messages("incomeSummary.yourBenefitsIncome") -> s"£${utils.valueFormatter(benefits.parentBenefitsIncome)}",
                      Messages("incomeSummary.partnerBenefitsIncome") -> s"£${utils.valueFormatter(benefits.partnerBenefitsIncome)}"))
                }
              }
            }
            case _ => result
          }
        }
        else {
          result + (Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"))
        }
      }
      case _ => result
    }
  }

  private def loadBothPension(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {

    userAnswers.whoIsInPaidEmployment match {
      case Some(whoIsInPaidEmployment) => {
        whoIsInPaidEmployment match {
          case ChildcareConstants.partner => {
            val paysIntoPension = userAnswers.PartnerPaidPensionCY.fold(false)(c => c)

            if (paysIntoPension) {
              userAnswers.howMuchPartnerPayPension.foldLeft(result)((result, pension) => result + (Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${utils.valueFormatter(pension)}"))
            }
            else{
              result + (Messages("incomeSummary.paidIntoPension") -> Messages("site.no"))
            }
          }

          case ChildcareConstants.you => {
            val paysIntoPension = userAnswers.YouPaidPensionCY.fold(false)(c => c)

            if (paysIntoPension) {
              userAnswers.howMuchYouPayPension.foldLeft(result)((result, pension) => result + (Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${utils.valueFormatter(pension)}"))
            }
            else{
              result + (Messages("incomeSummary.paidIntoPension") -> Messages("site.no"))
            }
          }

          case ChildcareConstants.both => {
            userAnswers.bothPaidPensionCY match {
              case Some(anyPaidPension) => {
                if (anyPaidPension) {
                  userAnswers.whoPaysIntoPension match {
                    case Some(whoPaysIntoPension) => {
                      whoPaysIntoPension match {
                        case ChildcareConstants.you => userAnswers.howMuchYouPayPension.foldLeft(result)((result, pension) => result + (Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${utils.valueFormatter(pension)}"))
                        case ChildcareConstants.partner => userAnswers.howMuchPartnerPayPension.foldLeft(result)((result, pension) => result + (Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${utils.valueFormatter(pension)}"))
                        case ChildcareConstants.both => {
                          userAnswers.howMuchBothPayPension.foldLeft(result)((result, pensions) =>
                            result ++: ListMap(Messages("incomeSummary.pensionPaymentsAmonth") -> s"£${utils.valueFormatter(pensions.howMuchYouPayPension)}",
                              Messages("incomeSummary.partnerPensionPaymentsAmonth") -> s"£${utils.valueFormatter(pensions.howMuchPartnerPayPension)}"))
                        }
                      }
                    }
                    case _ => result
                  }
                }
                else {
                  result + (Messages("incomeSummary.paidIntoPension") -> Messages("site.no"))
                }
              }
              case _ => result
            }
          }

          case _ => result
        }
      }
      case _ => result
    }
  }

  private def loadBothIncomeSection(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    userAnswers.whoIsInPaidEmployment match {
      case Some(whoInPaidEmployment) => {
        whoInPaidEmployment match {
          case ChildcareConstants.you => {
            val partnerWorkedAtAnyPointThisYear = userAnswers.partnerPaidWorkCY.fold(false)(c => c)

            if (partnerWorkedAtAnyPointThisYear) loadBothIncome(userAnswers, result) else loadParentIncome(userAnswers, result)
          }
          case ChildcareConstants.partner => {
            val parentWorkedAtAnyPointThisYear = userAnswers.parentPaidWorkCY.fold(false)(c => c)

            if (parentWorkedAtAnyPointThisYear) loadBothIncome(userAnswers, result) else loadPartnerIncome(userAnswers, result)
          }
          case ChildcareConstants.both => loadBothIncome(userAnswers, result)
        }
      }
      case _ => result
    }
  }

  private def loadBothIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    userAnswers.employmentIncomeCY.foldLeft(result)((result, incomes) =>
      result ++: ListMap(Messages("incomeSummary.yourIncome") -> s"£${utils.valueFormatter(incomes.parentEmploymentIncomeCY)}",
        Messages("incomeSummary.partnersIncome") -> s"£${utils.valueFormatter(incomes.partnerEmploymentIncomeCY)}"))
  }

  private def loadPartnerIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    userAnswers.partnerEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.partnersIncome") -> s"£${utils.valueFormatter(income)}"))
  }

  private def loadParentIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.yourIncome") -> s"£${utils.valueFormatter(income)}"))
  }

  private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.YouPaidPensionCY, result, (Messages("incomeSummary.paidIntoPension") -> Messages("site.no")), Messages("incomeSummary.pensionPaymentsAmonth"), userAnswers.howMuchYouPayPension)
  }

  private def loadYourOtherIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.yourOtherIncomeThisYear, result, (Messages("incomeSummary.otherIncome") -> Messages("site.no")), Messages("incomeSummary.yourOtherIncome"), userAnswers.yourOtherIncomeAmountCY)
  }

  private def loadYourBenefitsIncome(userAnswers: UserAnswers, result: ListMap[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.youAnyTheseBenefits, result, (Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")), Messages("incomeSummary.yourBenefitsIncome"), userAnswers.youBenefitsIncomeCY)
  }

  private def loadSectionAmount(conditionToCheckAmount: Option[Boolean], result: ListMap[String, String], conditionNotMet: (String, String), textForIncome: String, incomeSection: Option[BigDecimal]) = {
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