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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location

class EnumFormatSpec extends PlaySpec {

  "reads" must {

    "return error" when {
      "the value is not a string" in {
        Json
          .obj(
            "enum" -> "something"
          )
          .validate[Location] mustBe JsError(
          JsonValidationError(
            "String value expected"
          )
        )
      }

      "the input is a string" when {
        "the value is not in the enum" in {
          JsString("something").validate[Location] mustBe JsError(
            JsonValidationError(
              "Enumeration expected of type: 'class uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location$', but it does not appear to contain the value: 'something'"
            )
          )
        }

        "the value differs from a known value by only case" in {
          JsString("ENGLAND").validate[Location] mustBe JsError(
            JsonValidationError(
              "Enumeration expected of type: 'class uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location$', but it does not appear to contain the value: 'ENGLAND'"
            )
          )
        }

        "the value differs from a known value with the wrong separators" in {
          JsString("northern_ireland").validate[Location] mustBe JsError(
            JsonValidationError(
              "Enumeration expected of type: 'class uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location$', but it does not appear to contain the value: 'northern_ireland'"
            )
          )
        }
      }

    }

    "return success" when {
      "the input matches an enum value's toString value" in {
        JsString("england").validate[Location] mustBe JsSuccess(Location.England, _: JsPath)
      }

      "the input matches an enum value's name" in {
        JsString("NorthernIreland").validate[Location] mustBe JsSuccess(Location.NorthernIreland, _: JsPath)
      }
    }

  }

  "writes" must {

    "return valid json when the object is written" in {
      Json.toJson(Location.England) mustBe JsString("england")
    }

  }

}
