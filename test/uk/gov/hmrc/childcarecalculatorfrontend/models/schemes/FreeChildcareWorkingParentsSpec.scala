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
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc.ModelFactory
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class FreeChildcareWorkingParentsSpec extends SchemeSpec with MockitoSugar {

  def freeChildcareWorkingParents(tfc: TaxFreeChildcare = new TaxFreeChildcare(new ModelFactory), appConfig: FrontendAppConfig) =
    new FreeChildcareWorkingParents(tfc, appConfig)

  ".eligibility" must {
    val answers: UserAnswers = mock[UserAnswers]
    val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
    val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

    "return `NotEligible`" when {
      "user does not have a 2 year old or a 3 or 4 year old" in {
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.isChildAgedTwo) thenReturn Some(false)
        when(answers.isChildAgedThreeOrFour) thenReturn Some(false)
        when(answers.location) thenReturn Some(ENGLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual NotEligible
      }

      "user does not live in England" in {
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.isChildAgedTwo) thenReturn Some(true)
        when(answers.isChildAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some(SCOTLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual NotEligible
      }

      "tax free childcare is not eligible" in {
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(answers.isChildAgedTwo) thenReturn Some(true)
        when(answers.isChildAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some(ENGLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual NotEligible
      }
    }

    "return `Eligible`" when {
      "user has a 2 year old in England" in {
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.isChildAgedTwo) thenReturn Some(true)
        when(answers.location) thenReturn Some(ENGLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual Eligible
      }

      "user has a 3 or 4 year old in England" in {
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.isChildAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some(ENGLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual Eligible
      }

      "user has a 2 year old or a 3 or 4 year old" in {
        when(tfc.eligibility(any())) thenReturn Eligible
        when(answers.isChildAgedTwo) thenReturn Some(true)
        when(answers.isChildAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some(ENGLAND)
        freeChildcareWorkingParents(tfc, appConfig).eligibility(answers) mustEqual Eligible
      }
    }
  }
}
