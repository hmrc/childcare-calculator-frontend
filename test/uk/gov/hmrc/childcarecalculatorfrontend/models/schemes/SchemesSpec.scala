/*
 * Copyright 2019 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class SchemesSpec extends SchemeSpec {

  def scheme(e: Eligibility): Scheme = new Scheme {
    override def eligibility(answers: UserAnswers): Eligibility = e
  }

  val eligible: Scheme = scheme(Eligible)
  val notEligible: Scheme = scheme(NotEligible)
  val notDetermined: Scheme = scheme(NotDetermined)

  ".allSchemesDetermined" must {

    "return `true`" when {

      "all schemes are `Eligible`" in {
        new Schemes(eligible).allSchemesDetermined(helper()) mustEqual true
      }

      "all schemes are `NotEligible" in {
        new Schemes(notEligible).allSchemesDetermined(helper()) mustEqual true
      }

      "all schemes are either `Eligible` or `NotEligible`" in {
        new Schemes(eligible, notEligible).allSchemesDetermined(helper()) mustEqual true
      }
    }

    "return `false`" when {

      "any tfcScheme is `NotDetermined`" in {
        new Schemes(notDetermined, eligible, notEligible).allSchemesDetermined(helper()) mustEqual false
      }
    }
  }
}
