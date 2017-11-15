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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import play.api.libs.json.{JsBoolean, JsString}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class MinimumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {


  "MinimumHoursCascadeUpsert" when {

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(LocationId.toString, "northernIreland", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("northernIreland"))
      }
    }

    "saving a location other than northernIreland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))

        val result = cascadeUpsert(LocationId.toString, "england", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("england" + "")
        )
      }
    }

    "clear the selection of approved childcare provider if there is change from yes to no in childcare costs" in {
      val originalCacheMap = new CacheMap("id", Map(ApprovedProviderId.toString -> JsString("yes")))

      val result = cascadeUpsert(ChildcareCostsId.toString, "no", originalCacheMap)
      result.data mustBe Map(ChildcareCostsId.toString -> JsString("no"))
    }

    "keep the selection of approved childcare provider if there is change from yes to not yet in childcare costs" in {
      val originalCacheMap = new CacheMap("id", Map(ApprovedProviderId.toString -> JsString("yes")))

      val result = cascadeUpsert(ChildcareCostsId.toString, "notYet", originalCacheMap)
      result.data mustBe Map(ChildcareCostsId.toString -> JsString("notYet"),
        ApprovedProviderId.toString -> JsString("yes"))
    }

  }

}
