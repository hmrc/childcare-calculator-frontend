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

import org.joda.time.LocalDate
import play.api.libs.json
import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BenefitsIncomeCY, BothBenefitsIncomePY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class StatutoryCascadeUpsertSpec extends SpecBase with CascadeUpsertBase{

  "Statutory for single user" when {
    "Save Your Statutory Pay Before Tax data " must {
      "remove YourStatutoryPayPerWeek page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))))

        val result = cascadeUpsert(YourStatutoryPayBeforeTaxId.toString, false, originalCacheMap)

        result.data mustBe Map(YourStatutoryPayBeforeTaxId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(300))))

        val result = cascadeUpsert(YourStatutoryPayBeforeTaxId.toString, true, originalCacheMap)

        result.data mustBe Map(YourStatutoryPayBeforeTaxId.toString.toString -> JsBoolean(true),
          YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(300)))
      }
    }

    "Save Your Statutory Pay Type" must {
      "remove data for YourStatutoryStartDate, YourStatutoryWeeks and YourStatutoryPayBeforeTax pages when" +
        "Statutory Pay Type is changed" in {

        val originalCacheMap = new CacheMap("id", Map(YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200)),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(new LocalDate(2017, 2, 1)),
          YourStatutoryPayTypeId.toString -> json.JsString("maternity")))

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "paternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("paternity"))

      }

      "retain the data for YourStatutoryStartDate, YourStatutoryWeeks and YourStatutoryPayBeforeTax pages when" +
        "Statutory Pay Type is not changed" in {

        val originalCacheMap = new CacheMap("id", Map(YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200)),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(new LocalDate(2017, 2, 1)),
          YourStatutoryPayTypeId.toString -> json.JsString("maternity")))

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "maternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("maternity"),
          YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200)),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(new LocalDate(2017, 2, 1)))

      }

      "save the data for first time" in {
        val originalCacheMap = new CacheMap("id", Map())

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "maternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("maternity"))

      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = new CacheMap("id", Map(YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200))))

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "invalidvalue", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("invalidvalue"),
          YourStatutoryPayPerWeekId.toString -> JsNumber(BigDecimal(200)))
      }
    }
  }
}
