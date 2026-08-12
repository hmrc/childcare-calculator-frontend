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

class InputOptionSpec extends SpecBase {

  enum Enum(override val toString: String) {
    case Value1 extends Enum("Value1")
    case Value2 extends Enum("Value2")
    case Value3 extends Enum("Value3CustomToString")
  }

  "namedFromEnumValue" must {
    "build correctly from a key prefix and enum value" in {
      InputOption.namedFromEnumValue(namePrefix = "prefix", enumValue = Enum.Value1) mustBe InputOption(
        id = "prefix.Value1",
        value = "Value1",
        messageKey = "prefix.Value1"
      )
    }
  }

  "namedFromEnumValues" must {
    "build multiple values with the correct prefix and ids" in {
      InputOption.namedFromEnumValues(namePrefix = "prefix", values = Enum.values.toSeq) mustBe Seq(
        InputOption(
          id = "prefix.Value1",
          value = "Value1",
          messageKey = "prefix.Value1"
        ),
        InputOption(
          id = "prefix.Value2",
          value = "Value2",
          messageKey = "prefix.Value2"
        ),
        InputOption(
          id = "prefix.Value3CustomToString",
          value = "Value3CustomToString",
          messageKey = "prefix.Value3CustomToString"
        )
      )
    }
  }

  "indexedFromEnumValue" when {
    "index is 1" must {
      "return value as 'value'" in {
        InputOption.indexedFromEnumValue(
          index = 1,
          enumValue = Enum.Value1,
          messagePrefix = "messagePrefix"
        ) mustBe InputOption(
          id = "value",
          value = "Value1",
          messageKey = "messagePrefix.Value1"
        )
      }
    }

    "index is 2" must {
      "return value as 'value-2" in {
        InputOption.indexedFromEnumValue(
          index = 2,
          enumValue = Enum.Value2,
          messagePrefix = "messagePrefix"
        ) mustBe InputOption(
          id = "value-2",
          value = "Value2",
          messageKey = "messagePrefix.Value2"
        )
      }
    }
  }

  "indexedFromEnumValues" must {
    "build multiple input options with the correct prefix and ids" in {
      InputOption.indexedFromEnumValues(
        messagePrefix = "messagePrefix",
        values = Enum.values.toSeq
      ) mustBe Seq(
        InputOption(
          id = "value",
          value = "Value1",
          messageKey = "messagePrefix.Value1"
        ),
        InputOption(
          id = "value-2",
          value = "Value2",
          messageKey = "messagePrefix.Value2"
        ),
        InputOption(
          id = "value-3",
          value = "Value3CustomToString",
          messageKey = "messagePrefix.Value3CustomToString"
        )
      )
    }
  }

}
