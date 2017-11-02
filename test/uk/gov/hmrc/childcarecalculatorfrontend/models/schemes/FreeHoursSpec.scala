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

import play.api.libs.json.{JsBoolean, JsString}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildAgedThreeOrFourId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class FreeHoursSpec extends SchemeSpec {

  val freeHours = new FreeHours

  ".eligibility" must {

    "return `NotDetermined` if the user hasn't answered whether they have a child aged 3 or 4" in {
      val answers: UserAnswers = helper()
      freeHours.eligibility(answers) mustEqual NotDetermined
    }

    "return `Eligible` if the user has a child aged 3 or 4" in {
      val answers: UserAnswers = helper(
        ChildAgedThreeOrFourId.toString -> JsBoolean(true)
      )
      freeHours.eligibility(answers) mustEqual Eligible
    }

    "return `NotEligible` is the user does not have a child aged 3 or 4" in {
      val answers: UserAnswers = helper(
        ChildAgedThreeOrFourId.toString -> JsBoolean(false)
      )
      freeHours.eligibility(answers) mustEqual NotEligible
    }
  }
}
