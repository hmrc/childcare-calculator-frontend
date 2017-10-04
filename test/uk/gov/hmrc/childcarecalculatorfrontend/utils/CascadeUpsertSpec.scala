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
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.http.cache.client.CacheMap

class CascadeUpsertSpec extends SpecBase {

  "using the apply method for a key that has no special function" when {
    "the key doesn't already exists" must {
      "add the key to the cache map" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("value"))
      }
    }

    "data already exists for that key" must {
      "replace the value held against the key" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> JsString("original value")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("new value"))
      }
    }

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "northernIreland", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("northernIreland"))
      }
    }

    "saving a location other than northernIreland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "england", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("england" +
            "")
        )
      }
    }

    "saving the doYouLiveWithPartner" must {
      "remove an existing paid employment and who is in paid employment when doYouLiveWithpartner is No" in {
        val originalCacheMap = new CacheMap("id", Map(PaidEmploymentId.toString -> JsBoolean(true),
          WhoIsInPaidEmploymentId.toString -> JsString("you"), PartnerWorkHoursId.toString -> JsString("12")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))
      }

      "remove an existing paid employment, who is in paid employment when doYouLiveWithpartner is Yes" in {
        val originalCacheMap = new CacheMap("id", Map(AreYouInPaidWorkId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))
      }
    }

    "saving the whoIsInPaidEmployment" must {
      "remove an existing partner work hours when whoIsInPaidEmployment is you" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerWorkHoursId.toString -> JsString("12")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, "you", originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString("you"))
      }

      "remove an existing parent work hours when whoIsInPaidEmployment is partner" in {
        val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, "partner", originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString("partner"))
      }
    }

  }

  "addRepeatedValue" when {
    "the key doesn't already exist" must {
      "add the key to the cache map and save the value in a sequence" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value")))
      }
    }

    "the key already exists" must {
      "add the new value to the existing sequence" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> Json.toJson(Seq("value"))))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value", "new value")))
      }
    }
  }
}
