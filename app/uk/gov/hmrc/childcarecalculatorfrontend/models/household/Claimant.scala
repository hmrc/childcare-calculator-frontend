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

package uk.gov.hmrc.childcarecalculatorfrontend.models.household

import org.joda.time.{LocalDate, Weeks}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

case class Claimant(lastYearlyIncome: Option[Income] = None,
                    currentYearlyIncome: Option[Income] = None)

object Claimant {
  def apply(answers: UserAnswers,
            tcScheme: TaxCredits,
            partnerMode: Boolean): Claimant =

    tcScheme.eligibility(answers) match {

      case Eligible => getClaimantForTCEligibility(answers, partnerMode)
      case NotEligible => Claimant(currentYearlyIncome = getCurrentYearlyIncome(answers, partnerMode))
      case _ => Claimant()
    }

  private def getClaimantForTCEligibility(answers: UserAnswers,
                                          partnerMode: Boolean) = {

    getStartDateOfStatPay(answers, partnerMode) match {
      case Some(date) => {

        val currentTaxYear = getTaxYear(LocalDate.now)

        val taxYearStartDateCY = getTaxYearStartDate(currentTaxYear - 1)
        val taxYearEndDateCY = getTaxYearEndDate(currentTaxYear)

        val taxYearStartDatePY = getTaxYearStartDate(currentTaxYear - 2)
        val taxYearEndDatePY = getTaxYearEndDate(currentTaxYear - 1)

        if (isDateBetweenInterval(date, taxYearStartDatePY, taxYearEndDatePY)) {

          val noOfStatWeeks = getNoOfStatWeeks(answers, partnerMode)
          val payPerWeek = getPayPerWeek(answers, partnerMode)

          val remainingWeeksInPY = Weeks.weeksBetween(date, taxYearEndDatePY).getWeeks

          if (remainingWeeksInPY >= noOfStatWeeks) {
            val statIncomeLY = Some(StatutoryIncome(noOfStatWeeks, noOfStatWeeks * payPerWeek))

            Claimant(lastYearlyIncome = getLastYearlyIncome(answers, statIncomeLY),
              currentYearlyIncome = Some(Income(answers, None)))
          } else {

            val statWeeksCY = noOfStatWeeks - remainingWeeksInPY
            val statIncomeLY = Some(StatutoryIncome(remainingWeeksInPY, remainingWeeksInPY * payPerWeek))
            val statIncomeCY = Some(StatutoryIncome(statWeeksCY, statWeeksCY * payPerWeek))

            Claimant(lastYearlyIncome = getLastYearlyIncome(answers, statIncomeLY),
              currentYearlyIncome = Some(Income(answers, statIncomeCY)))
          }

        } else if (isDateBetweenInterval(date, taxYearStartDateCY, taxYearEndDateCY)) {
          Claimant(currentYearlyIncome = getCurrentYearlyIncome(answers, partnerMode))

        } else {
          Claimant(lastYearlyIncome = Some(Income(statutoryIncome = None)),
            currentYearlyIncome = Some(Income(statutoryIncome = None)))
        }

      }
      case _ => Claimant(lastYearlyIncome = Some(Income(statutoryIncome = None)),
        currentYearlyIncome = Some(Income(statutoryIncome = None)))
    }
  }

  private def isDateBetweenInterval(date: LocalDate,
                                    startDate: LocalDate,
                                    endDate: LocalDate) =
    ((date.isEqual(startDate) || date.isAfter(startDate))
      && (date.isEqual(endDate) || date.isBefore(endDate)))

  private def getLastYearlyIncome(answers: UserAnswers,
                                  statIncome: Option[StatutoryIncome]): Option[Income] =
    Some(Income(answers, statIncome))

  private def getCurrentYearlyIncome(answers: UserAnswers,
                                     partnerMode: Boolean = false): Option[Income] = {

    val noOfStatWeeks = getNoOfStatWeeks(answers, partnerMode)
    val startDateOfStatPay = getStartDateOfStatPay(answers, partnerMode)
    val payPerWeek = getPayPerWeek(answers, partnerMode)

    startDateOfStatPay match {
      case Some(date) => {
        val taxYearEndDate = getTaxYearEndDate(getTaxYear(date))
        val totalWeeksInCY = Weeks.weeksBetween(date, taxYearEndDate).getWeeks

        val statsPayableWeeksInCY = if (noOfStatWeeks >= totalWeeksInCY) totalWeeksInCY else noOfStatWeeks
        val statsPayableAmount = statsPayableWeeksInCY * payPerWeek

        Some(Income(answers, Some(StatutoryIncome(statsPayableWeeksInCY, statsPayableAmount))))

      }
      case _ => Some(Income(answers, None))
    }
  }

  private def getNoOfStatWeeks(answers: UserAnswers,
                               partnerMode: Boolean = false) =
    if (partnerMode) {
      answers.partnerStatutoryWeeks.getOrElse(0)
    } else {
      answers.yourStatutoryWeeks.getOrElse(0)
    }

  private def getStartDateOfStatPay(answers: UserAnswers,
                                    partnerMode: Boolean = false) =
    if (partnerMode) {
      answers.partnerStatutoryStartDate
    } else {
      answers.yourStatutoryStartDate
    }

  private def getPayPerWeek(answers: UserAnswers,
                            partnerMode: Boolean = false) =
    if (partnerMode) {
      answers.partnerStatutoryPayPerWeek.getOrElse(0)
    } else {
      answers.yourStatutoryPayPerWeek.getOrElse(0)
    }

  private def getTaxYear(date: LocalDate) = {
    val monthOfTheYear = date.getMonthOfYear
    val year = date.getYear

    if (monthOfTheYear > lastMonthOfTaxYear) {
      year + 1
    } else {
      if (monthOfTheYear < lastMonthOfTaxYear) {
        year
      } else {
        val day = date.getDayOfMonth
        if (day > lastDayOfTaxYear) year + 1 else year
      }
    }
  }

  private def getTaxYearEndDate(year: Int) = new LocalDate(year, lastMonthOfTaxYear, lastDayOfTaxYear)

  private def getTaxYearStartDate(year: Int) = new LocalDate(year, firstMonthOfTaxYear, startDayOfTaxYear)

  implicit val formatClaimant: OFormat[Claimant] = Json.format[Claimant]
}
