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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum

class CacheMapClonerSpec extends SpecBase {

  "Cache map cloner" should {
    "mirror a cachemap property as boolean" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data, Map("property1" -> "property2"))

      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property1")
    }

    "mirror two boolean property" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true), "property2" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data, Map("property1" -> "property3", "property2" -> "property4"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property3")
      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property4")
    }

    "mirror three properties with different types" in {
      val data = new CacheMap(
        "id",
        Map(
          "property1" -> JsBoolean(true),
          "property2" -> JsBoolean(true),
          "property3" -> JsNumber(2),
          "property4" -> JsString("Test")
        )
      )

      val result = CacheMapCloner.cloneSection(
        data,
        Map(
          "property1" -> "property5",
          "property2" -> "property6",
          "property3" -> "property7",
          "property4" -> "property8"
        )
      )

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property5")
      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property6")
      result.getEntry[Int]("property3") mustBe result.getEntry[Int]("property7")
      result.getEntry[String]("property4") mustBe result.getEntry[String]("property8")
    }

    "be able to handle complex objects" in {
      val data = new CacheMap("id", Map("property1" -> Json.obj("0" -> Json.toJson(4), "1" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data, Map("property1" -> "property2"))

      result.getEntry[JsValue]("property1") mustBe result.getEntry[JsValue]("property2")
    }

    "be able to handle not existing data" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data, Map("property2" -> "property3"))

      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property3")
    }

    "be able to overwrite already existing data" in {
      val data = new CacheMap(
        "id",
        Map("property1" -> JsBoolean(true), "property2" -> Json.obj("0" -> Json.toJson(4), "1" -> JsBoolean(true)))
      )

      val result = CacheMapCloner.cloneSection(data, Map("property1" -> "property2"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property2")
    }

    "be able to handle custom mappings" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true)))

      val result =
        CacheMapCloner.cloneSection(data, Map("property1" -> "property2"), Some(Map("property4" -> JsBoolean(true))))

      result.getEntry[Boolean]("property4").get mustBe true
    }

  }

}
