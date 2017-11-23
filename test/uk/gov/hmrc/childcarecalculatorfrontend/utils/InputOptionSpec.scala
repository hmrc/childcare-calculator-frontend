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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

class InputOptionSpec extends SpecBase {

  "Input Option" must {
    "build correctly from a key prefix and option" in {
      val inputOption = InputOption("prefix", "option")
      inputOption.id mustBe "prefix.option"
      inputOption.value mustBe "option"
      inputOption.messageKey mustBe "prefix.option"
    }
  }
}
