/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.config

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Age

import java.time.LocalDate

class NmwConfigSpec extends SpecBase {

  "getEarningsForAgeRange" should {

    "return the 2026 earnings value for under 18 on day of tax year change" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-06"),
        Some(Age.UnderEighteen)
      ) mustBe 128
    }

    "return the 2026 earnings value for under 18 on 1st April 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-01"),
        Some(Age.UnderEighteen)
      ) mustBe 128
    }

    "return the 2025 earnings value for under 18 on 31 March 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-03-31"),
        Some(Age.UnderEighteen)
      ) mustBe 120
    }

    "return the 2026 earnings value for 18-20 year old on day of tax year change" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-06"),
        Some(Age.EighteenToTwenty)
      ) mustBe 173
    }

    "return the 2026 earnings value for 18-20 year old on 1st April 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-01"),
        Some(Age.EighteenToTwenty)
      ) mustBe 173
    }

    "return the 2025 earnings value for 18-20 year old on 31 March 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-03-31"),
        Some(Age.EighteenToTwenty)
      ) mustBe 160
    }

    "return the 2026 earnings value for 21+ year old on day of tax year change" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-06"),
        Some(Age.TwentyOneOrOver)
      ) mustBe 203
    }

    "return the 2026 earnings value for 21+ year old on 1st April 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-04-01"),
        Some(Age.TwentyOneOrOver)
      ) mustBe 203
    }

    "return the 2025 earnings value for 21-24 year old on 31 March 2026" in {
      nmwConfig.getEarningsForAgeRange(
        LocalDate.parse("2026-03-31"),
        Some(Age.TwentyOneOrOver)
      ) mustBe 195
    }
  }

}
