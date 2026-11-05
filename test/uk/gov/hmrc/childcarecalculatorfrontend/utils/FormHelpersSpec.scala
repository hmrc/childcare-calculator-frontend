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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

class FormHelpersSpec extends SpecBase {

  "FormHelpers on removeSpaces" should {

    "remove all whitespace from the value" in {
      FormHelpers.removeSpaces(" 12 34 ") mustBe "1234"
      FormHelpers.removeSpaces("  123  456  ") mustBe "123456"
    }

    "return an empty string when the value is empty or null" in {
      FormHelpers.removeSpaces("") mustBe ""
    }

    "return the original value when there are no spaces" in {
      FormHelpers.removeSpaces("123456") mustBe "123456"
    }
  }

  "FormHelpers On removeSpacesOrCommas" should {

    "remove all whitespace and commas from the value" in {
      FormHelpers.removeSpacesOrCommas(" 1,234 ") mustBe "1234"
      FormHelpers.removeSpacesOrCommas(" 1, 234, 567 ") mustBe "1234567"
      FormHelpers.removeSpacesOrCommas("1,234,567") mustBe "1234567"
    }

    "return an empty string when the value is empty or null" in {
      FormHelpers.removeSpacesOrCommas("") mustBe ""
    }

    "return the original value when there are no spaces or commas" in {
      FormHelpers.removeSpacesOrCommas("123456") mustBe "123456"
    }
  }

}
