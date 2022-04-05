/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import org.scalatestplus.play.PlaySpec

class SchemeResultsSpec extends PlaySpec {

  "SchemeResults" should {
    " throw an exception" when {
      "trying to build a scheme object and ESC claimant is missing" in {

        intercept[Exception] {
          Scheme(name = SchemeEnum.ESCELIGIBILITY,
            amount = BigDecimal(0.00),
            escClaimantEligibility = None,
            taxCreditsEligibility = None
          )
        }
      }
      "trying to build a scheme object and tax credits claimant is missing" in {

        intercept[Exception] {
          Scheme(name = SchemeEnum.TCELIGIBILITY,
            amount = BigDecimal(0.00),
            escClaimantEligibility = None,
            taxCreditsEligibility = None
          )
        }
      }
    }
  }
}
