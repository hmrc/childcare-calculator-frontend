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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.scalatest.EitherValues
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, Json, JsonValidationError}

class MapFormatsSpec extends PlaySpec with EitherValues with MapFormats {

  "map reads" must {

    "successfully read json into a `Map[Int, String]`" in {

      val json = Json.obj(
        "0" -> "foo",
        "1" -> "bar"
      )

      val result = Json.fromJson[Map[Int, String]](json).asEither

      result.right.value must contain(0 -> "foo")
      result.right.value must contain(1 -> "bar")
    }

    "fail to read json into a `Map[Int, String]` when keys are not valid `Int`s" in {

      val json = Json.obj(
        "foo" -> "foo",
        "1" -> "bar"
      )

      val result = Json.fromJson[Map[Int, String]](json).asEither

      result.left.value must contain (JsPath -> Seq(JsonValidationError("Failed to convert map keys into ints")))
    }
  }

  "map writes" must {

    "write a `Map[Int, String]` into JSON" in {

      val json = Json.obj(
        "0" -> "foo",
        "1" -> "bar"
      )

      val model = Map(
        0 -> "foo",
        1 -> "bar"
      )

      Json.toJson(model) mustEqual json
    }
  }
}
