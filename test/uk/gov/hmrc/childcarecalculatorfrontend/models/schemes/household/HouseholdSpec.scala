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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.household

import org.joda.time.LocalDate
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{EitherValues, OptionValues}
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.household._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{SchemeSpec, TaxCredits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

class HouseholdSpec extends SchemeSpec with MockitoSugar with OptionValues with EitherValues {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val cy: Int = LocalDate.now.getYear

  "HouseHold" must{

    "Create the Household model with statutory income for CY and PY when single user is eligible for TC and " +
      "statutory income weeks span over current and previous year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(32)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(13, 2600)))),
        lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
      ))

    }

    "Create the Household model with statutory income for CY and PY when single user is eligible for TC and " +
      "statutory income weeks are only in previous year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(18)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = None)),
        lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
      ))

    }

    "Create the Household model with statutory income for CY when single user is eligible for TC and " +
      "statutory income weeks in only current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(18)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
      ))

    }


    "Create the Household model with statutory income for CY when single user is eligible for TC and " +
      "statutory income weeks go beyond current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(35)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
      ))

    }


    "Create the Household model with statutory income for CY when user is eligible for TC, with partner," +
      "only parent got statutory and stat weeks are in only current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(18)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        )))

    }

    "Create the Household model with statutory income for CY when user is eligible for TC, with partner," +
      "only parent got statutory and stat weeks go beyond current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(35)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        )))

    }


    "Create the Household model with statutory income for CY when user is eligible for TC, with partner and " +
      "only partner got statutory and stat weeks are in only current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)
      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(18)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
        )))

    }

    "Create the Household model with statutory income for CY when user is eligible for TC, with partner and " +
      "only partner got statutory and stat weeks go beyond current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)
      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(35)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        )))

    }


    "Create the Household model with statutory income for CY when user is eligible for TC, with partner and " +
      "both got statutory and stat weeks are in only current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(18)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(18)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(18, 3600))))
        )))

    }

    "Create the Household model with statutory income for CY when user is eligible for TC, with partner and " +
      "both got statutory and stat weeks go beyond current year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(35)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(35)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        )))

    }

    "Create the Household model with statutory income for CY and PY when user is eligible for TC, with partner and " +
      "only parent got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(32)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(13, 2600)))),
          lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        )))

    }


    "Create the Household model with statutory income for CY and PY when user is eligible for TC, with partner and " +
      "only partner got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)
      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(32)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = None))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(13, 2600)))),
          lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        )))

    }

    "Create the Household model with statutory income for CY and PY when user is eligible for TC, with partner and " +
      "both got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(32)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.partnerStatutoryWeeks) thenReturn Some(9)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(13, 2600)))),
          lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(19, 3800))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None)),
          lastYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(9, 1800))))
        )))

    }

    ////////////////////////////////////////
    "Create the Household model with statutory income for CY and PY when single user is eligible for TC and " +
      "statutory income weeks span over current and previous year but stat pay start date is before previous tax year" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 2, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(32)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = None)),
        lastYearlyIncome = Some(Income(statutoryIncome = None))
      ))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC and single user " in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(8)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(8, 1600))))
      ))

    }

    "Create the Household model with statutory income for CY when single user is not eligible for TC and no of weeks" +
      "go past current year " in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(39)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(33, 6600))))
      ))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner and " +
      "only parent got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(10)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(10, 2000))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None))
        )))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner and " +
      "only partner got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)
      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.partnerStatutoryWeeks) thenReturn Some(10)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(10, 2000))))
        )))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner and " +
      "both got statutory" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(10)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.partnerStatutoryWeeks) thenReturn Some(29)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(10, 2000))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(29, 5800))))
        )))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner, " +
      "only parent got statutory and stats weeks are more than remaining tax year weeks" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(39)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(33, 6600))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None))
        )))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner, " +
      "only partner got statutory and stats weeks are more than remaining tax year weeks" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)
      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.partnerStatutoryWeeks) thenReturn Some(39)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = None))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(33, 6600))))
        )))

    }

    "Create the Household model with statutory income for CY when user is not eligible for TC, with partner, " +
      "both got statutory and stats weeks are more than remaining tax year weeks" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.yourStatutoryWeeks) thenReturn Some(39)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(cy, 8, 12))
      when(answers.partnerStatutoryWeeks) thenReturn Some(39)
      when(answers.partnerStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(
        parent = Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(33, 6600))))
        ),
        partner = Some(Claimant(
          currentYearlyIncome = Some(Income(statutoryIncome = Some(StatutoryIncome(33, 6600))))
        )))

    }

    "Create the Household model with statutory income for CY and PY as None when TC eligibility is NotDetermined" in  {
      val answers = spy(userAnswers())
      val taxCredits = mock[TaxCredits]

      when(taxCredits.eligibility(any())) thenReturn NotDetermined
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(cy-1, 11, 20))
      when(answers.yourStatutoryWeeks) thenReturn Some(32)
      when(answers.yourStatutoryPayPerWeek) thenReturn Some(200)

      Household(answers, taxCredits) mustBe Household(parent = new Claimant(
        currentYearlyIncome = Some(Income(statutoryIncome = None)),
        lastYearlyIncome = Some(Income(statutoryIncome = None))))

    }

  }

}
