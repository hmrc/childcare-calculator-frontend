/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.ModelFactory
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MaxFreeHoursSpec extends SchemeSpec with MockitoSugar {

  def maxFreeHours(freeHours: FreeHours = new FreeHours, tfc: TaxFreeChildcare = new TaxFreeChildcare(new ModelFactory)) =
    new MaxFreeHours(freeHours, tfc)

  ".eligibility" must {

    "return `NotDetermined`" when {

      "free hours eligibility is undetermined" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn NotDetermined
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotDetermined
      }

      "tfc eligibility is undetermined" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotDetermined
      }

      "there is no answer for location" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn None
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotDetermined
      }
    }

    "return `NotEligible`" when {

      "user is not eligible for free hours" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn NotEligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is not eligible for tfc" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Scotland" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(SCOTLAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Wales" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(WALES)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Northern Ireland" in {
        val answers: UserAnswers = mock[UserAnswers]
        val freeHours: FreeHours = mock[FreeHours]
        val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(NORTHERN_IRELAND)
        maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual NotEligible
      }
    }

    "return `Eligible` when user is eligible for Free Hours, TFC and lives in England" in {

      val answers: UserAnswers = mock[UserAnswers]
      val freeHours: FreeHours = mock[FreeHours]
      val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
      when(freeHours.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn Eligible
      when(answers.location) thenReturn Some(ENGLAND)
      maxFreeHours(freeHours, tfc).eligibility(answers) mustEqual Eligible
    }
  }
}
