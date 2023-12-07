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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.ModelFactory
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MaxFreeHoursSpec extends SchemeSpec with MockitoSugar {

  def maxFreeHours(freeChildcareWorkingParents: FreeChildcareWorkingParents = new FreeChildcareWorkingParents, tfc: TaxFreeChildcare = new TaxFreeChildcare(new ModelFactory)) =
    new MaxFreeHours(freeChildcareWorkingParents, tfc)

  ".eligibility" must {

    val answers: UserAnswers = mock[UserAnswers]
    val freeChildcareWorkingParents: FreeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
    val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]

    "return `NotDetermined`" when {

      "free hours eligibility is undetermined" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotDetermined
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotDetermined
      }

      "tfc eligibility is undetermined" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotDetermined
      }

      "there is no answer for location" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn None
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotDetermined
      }
    }

    "return `NotEligible`" when {

      "user is not eligible for free hours" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is not eligible for tfc" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(answers.location) thenReturn Some(ENGLAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Scotland" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(SCOTLAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Wales" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(WALES)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotEligible
      }

      "user is from Northern Ireland" in {
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.location) thenReturn Some(NORTHERN_IRELAND)
        maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual NotEligible
      }
    }

    "return `Eligible` when user is eligible for Free Hours, TFC and lives in England" in {

      when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn Eligible
      when(answers.location) thenReturn Some(ENGLAND)
      maxFreeHours(freeChildcareWorkingParents, tfc).eligibility(answers) mustEqual Eligible
    }
  }
}
