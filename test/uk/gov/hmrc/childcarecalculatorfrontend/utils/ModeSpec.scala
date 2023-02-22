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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, NormalMode}


class ModeSpec extends SpecBase  {

  "Mode" should {
    "error if the mode is not supported" in {
      binder.bind("mode",Map("mode" -> Seq("Test"))).get mustBe Left("error.invalidMode")
    }
    "succeed if mode is supported" in {
      binder.bind("mode",Map("mode" -> Seq("NormalMode"))).get mustBe Right(NormalMode)
    }
  }

  val binder = Mode.binder
}
