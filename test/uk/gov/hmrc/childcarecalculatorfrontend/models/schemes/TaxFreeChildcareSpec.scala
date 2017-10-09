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

import play.api.libs.json.JsString
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxFreeChildcareSpec extends SchemeSpec {

  ".eligibility" must {

    "return `NotDetermined`" when {

      "the user has not told the calculator that they have costs" in {
        TaxFreeChildcare.eligibility(helper()) mustEqual NotDetermined
      }

      "the user has told the calculator that they have costs" in {
        val answers: UserAnswers = helper(
          ChildcareCostsId.toString -> JsString("yes")
        )
        TaxFreeChildcare.eligibility(answers) mustEqual NotDetermined
      }

      "the user has told the calculator that they may have costs in the future" in {
        val answers: UserAnswers = helper(
          ChildcareCostsId.toString -> JsString("notYet")
        )
        TaxFreeChildcare.eligibility(answers) mustEqual NotDetermined
      }
    }

    "return `NotEligible`" when {

      "the user has told the calculator that they have no costs" in {
        val answers: UserAnswers = helper(
          ChildcareCostsId.toString -> JsString("no")
        )
        TaxFreeChildcare.eligibility(answers) mustEqual NotEligible
      }
    }
  }
}
