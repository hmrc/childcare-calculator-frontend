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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxFreeChildcareSpec extends SchemeSpec with MockitoSugar {

  val answers: UserAnswers = mock[UserAnswers]
  val modelFactory: ModelFactory = mock[ModelFactory]
  val scheme = new TaxFreeChildcare(modelFactory)

  ".eligibility" must {

    "return `NotDetermined` when `hasApprovedCosts` is undefined" in {
      when(answers.hasApprovedCosts) thenReturn None
      when(modelFactory(any())) thenReturn Some(SingleHousehold(Parent(true, false, false, false, Set.empty)))
      scheme.eligibility(answers) mustEqual NotDetermined
    }

    "return `NotDetermined` when `household` is undefined" in {
      when(answers.hasApprovedCosts) thenReturn Some(true)
      when(modelFactory(any())) thenReturn None
      scheme.eligibility(answers) mustEqual NotDetermined
    }

    "return `NotEligible` when `hasApprovedCosts` is false" in {
      when(answers.hasApprovedCosts) thenReturn Some(false)
      when(modelFactory(any())) thenReturn Some(SingleHousehold(Parent(true, false, false, false, Set.empty)))
      scheme.eligibility(answers) mustEqual NotEligible
    }

    "single household" when {

      "return `Eligible` when they earn over the minimum threshold" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(modelFactory(any())) thenReturn Some(SingleHousehold(Parent(true, false, false, false, Set.empty)))
        scheme.eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when they earn under the minimum threshold but are an apprentice" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(modelFactory(any())) thenReturn Some(SingleHousehold(Parent(false, false, false, true, Set.empty)))
        scheme.eligibility(answers) mustEqual Eligible
      }

      "return `Eligible` when they earn under the minimum threshold but are self employed" in {
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(modelFactory(any())) thenReturn Some(SingleHousehold(Parent(false, true, false, false, Set.empty)))
        scheme.eligibility(answers) mustEqual Eligible
      }

      "return `NotEligible` when they earn over the maximum threshold" in {

      }

      "return `NotEligible` when they earn under the minimum threshold and are not an apprentice or self employed" in {

      }
    }

    "joint household" when {

      "return `Eligible` when both parents are eligible" in {

      }

      "return `Eligible` when the user is eligible, their partner is not but claims carers allowance" in {

      }

      "return `Eligible` when the user is ineligible, but claims carers allowance and their partner is eligible" in {

      }

      "return `NotEligible` when the user is eligible but their partner is not, and doesn't claim carers allowance" in {

      }

      "return `NotEligible` when the user is ineligible and doesn't claim carers allowance but their partner is eligible" in {

      }

      "return `NotEligible` when neither parent is eligible" in {

      }

      "return `NotEligible` when neither parent is eligible, and the user claims carers allowance" in {

      }

      "return `NotEligible` when neither parent is eligible, and the user's partner claims carers allowance" in {

      }
    }
  }
}
