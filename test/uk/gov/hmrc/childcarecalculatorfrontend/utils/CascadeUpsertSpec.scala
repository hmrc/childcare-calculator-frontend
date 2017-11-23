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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{DoYouKnowYourAdjustedTaxCodeId, HasYourTaxCodeBeenAdjustedId, WhatIsYourTaxCodeId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class CascadeUpsertSpec extends SpecBase with CascadeUpsertBase{

  lazy val no: String = YesNoUnsureEnum.NO.toString

  "using the apply method for a key that has no special function" when {
    "the key doesn't already exists" must {
      "add the key to the cache map" in {
        val originalCacheMap = new CacheMap("id", Map())

        val result = cascadeUpsert("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("value"))
      }
    }

    "data already exists for that key" must {
      "replace the value held against the key" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> JsString("original value")))

        val result = cascadeUpsert("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("new value"))
      }
    }
  }


  "apply method" must {
    "save the data for the existing key" in {
      val originalCacheMap = new CacheMap("id",
        Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsString(no),
        WhatIsYourTaxCodeId.toString -> JsString("1100L")))

      val result = cascadeUpsert(HasYourTaxCodeBeenAdjustedId.toString, no, originalCacheMap)
      result.data mustBe Map(HasYourTaxCodeBeenAdjustedId.toString -> JsString(no))
    }
  }

 "addRepeatedValue" when {
    "the key doesn't already exist" must {
      "add the key to the cache map and save the value in a sequence" in {
        val originalCacheMap = new CacheMap("id", Map())

        val result = cascadeUpsert.addRepeatedValue("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value")))
      }
    }

    "the key already exists" must {
      "add the new value to the existing sequence" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> Json.toJson(Seq("value"))))

        val result = cascadeUpsert.addRepeatedValue("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value", "new value")))
      }
    }

  }

}
