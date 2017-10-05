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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildAgedThreeOrFourId, LocationId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MaxFreeHoursSpec extends SchemeSpec {

  ".eligibility" must {

    "return `NotDetermined`" when {

      "free hours eligibility is undetermined" in {
        val answers: UserAnswers = helper(
          LocationId.toString -> JsString("england")
        )
        MaxFreeHours.eligibility(answers) mustEqual NotDetermined
      }

      "user has not told the calculator where they live" in {
        val answers: UserAnswers = helper(
          ChildAgedThreeOrFourId.toString -> JsBoolean(true)
        )
        MaxFreeHours.eligibility(answers) mustEqual NotDetermined
      }
    }

    "return `NotEligible`" when {

      "user is not eligible for free hours" in {
        val answers: UserAnswers = helper(
          ChildAgedThreeOrFourId.toString -> JsBoolean(false),
          LocationId.toString -> JsString("england")
        )
        MaxFreeHours.eligibility(answers) mustEqual NotEligible
      }

      "user is not from England" in {
        val answers: UserAnswers = helper(
          ChildAgedThreeOrFourId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("northernIreland")
        )
      }
    }
  }
}
