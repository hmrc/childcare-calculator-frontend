/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.play.test.UnitSpec

class EnumUtilsSpec extends UnitSpec {

  "EnumerationUtil" should {

    "return a JsError when it cannot parse json object" in {
      val json = Json.parse(
        """
          |{
          | "enum" : "something"
          |}
        """.stripMargin)
      json.validate[Location.Value] match {
        case JsSuccess(v, _) =>
          !v.isInstanceOf[Location.Value]
        case JsError(errors) =>
          errors.head._2.head.message shouldBe "String value expected"
      }
    }

    "return a JsError when it cannot parse json string" in {
      val json = Json.parse(
        """
          |"something"
        """.stripMargin)
      json.validate[Location.Value] match {
        case JsSuccess(v, _) =>
          !v.isInstanceOf[Location.Value]
        case JsError(errors) =>
          errors.head._2.head.message shouldBe
            "Enumeration expected of type: 'class uk.gov.hmrc.childcarecalculatorfrontend.models.Location$', but it does not appear to contain the value: 'something'"
      }
    }

    "return success when the the input is part of the Enum" in {
      val json = Json.parse(
        """
          |"england"
        """.stripMargin)
      json.validate[Location.Value] match {
        case JsSuccess(v, _) =>
          v.isInstanceOf[Location.Value] shouldBe true
        case JsError(errors) =>
          errors.head._2.head.message shouldBe ""
      }
    }

    "return valid json when the object is written" in {
      val res: JsValue = Json.toJson(Location.ENGLAND)
      res.toString() shouldBe "\"england\""
    }

  }

}
