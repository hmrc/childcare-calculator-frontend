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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import java.time.LocalDate
import play.api.libs.json

import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class StatutoryCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Statutory for single user" when {

    "Save Your Statutory Pay Type" must {
      "remove data for YourStatutoryStartDate, YourStatutoryWeeks, YourStatutoryPayBeforeTax and YourStatutoryPayBeforeTax pages when" +
        "Statutory Pay Type is changed" in {

        val originalCacheMap = new CacheMap("id", Map(
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          YourStatutoryPayTypeId.toString -> json.JsString("maternity")))

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "paternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("paternity"))

      }

      "retain the data for YourStatutoryStartDate and YourStatutoryPayBeforeTax pages when" +
        "Statutory Pay Type is not changed" in {

        val originalCacheMap = new CacheMap("id", Map(
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          YourStatutoryPayTypeId.toString -> json.JsString("maternity")))

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "maternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("maternity"),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)))

      }

      "save the data for first time" in {
        val originalCacheMap = new CacheMap("id", Map())

        val result = cascadeUpsert(YourStatutoryPayTypeId.toString, "maternity", originalCacheMap)

        result.data mustBe Map(YourStatutoryPayTypeId.toString -> JsString("maternity"))

      }

    }

    "Save You Statutory Pay data" must {
      "remove YourStatutoryPayPerWeek page data when user selects no option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourStatutoryPayTypeId.toString -> JsString("maternity"),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1))))


        val result = cascadeUpsert(YouStatutoryPayId.toString, false, originalCacheMap)

        result.data mustBe Map(YouStatutoryPayId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          YourStatutoryPayTypeId.toString -> JsString("maternity"),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1))))

        val result = cascadeUpsert(YouStatutoryPayId.toString, true, originalCacheMap)

        result.data mustBe Map(YouStatutoryPayId.toString.toString -> JsBoolean(true),
          YourStatutoryPayTypeId.toString -> JsString("maternity"),
          YourStatutoryWeeksId.toString -> JsNumber(200),
          YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)))
      }
    }
  }

  "Statutory for partner" when {
    "Save Partner Statutory Pay Before Tax data " must {

      "Save Both Statutory Pay Type" must {
        "remove data for PartnerStatutoryStartDate, PartnerStatutoryPayBeforeTax and PartnerStatutoryPayBeforeTax " +
          "pages when Statutory Pay Type is changed" in {

          val originalCacheMap = new CacheMap("id", Map(
            PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
            PartnerStatutoryPayTypeId.toString -> json.JsString("maternity")))

          val result = cascadeUpsert(PartnerStatutoryPayTypeId.toString, "paternity", originalCacheMap)

          result.data mustBe Map(PartnerStatutoryPayTypeId.toString -> JsString("paternity"))

        }

      }

      "retain the data for PartnerStatutoryStartDate, PartnerStatutoryWeeks and PartnerStatutoryPayBeforeTax pages when" +
        "Statutory Pay Type is not changed" in {

        val originalCacheMap = new CacheMap("id", Map(
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
          PartnerStatutoryPayTypeId.toString -> json.JsString("maternity")))

        val result = cascadeUpsert(PartnerStatutoryPayTypeId.toString, "maternity", originalCacheMap)

        result.data mustBe Map(PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
          PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)))

      }

      "who got statutory pay" must {
        "remove data for YourStatutoryStartDate, YourStatutoryWeeks, YourStatutoryPayBeforeTax and YourStatutoryPayType pages" +
          "when partner selected for who got statutory pay" in {

          val originalCacheMap = new CacheMap("id", Map(
            YourStatutoryWeeksId.toString -> JsNumber(200),
            YourStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
            YourStatutoryPayTypeId.toString -> json.JsString("maternity")))

          val result = cascadeUpsert(WhoGotStatutoryPayId.toString, "partner", originalCacheMap)

          result.data mustBe Map(WhoGotStatutoryPayId.toString -> JsString("partner"))
        }

        "remove data for PartnerStatutoryStartDate, PartnerStatutoryPayBeforeTax and PartnerStatutoryPayType pages" +
          "when You selected for who got statutory pay" in {

          val originalCacheMap = new CacheMap("id", Map(

            PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1)),
            PartnerStatutoryPayTypeId.toString -> json.JsString("maternity")))

          val result = cascadeUpsert(WhoGotStatutoryPayId.toString, "you", originalCacheMap)

          result.data mustBe Map(WhoGotStatutoryPayId.toString -> JsString("you"))
        }


        "save the data for first time" in {
          val originalCacheMap = new CacheMap("id", Map())

          val result = cascadeUpsert(PartnerStatutoryPayTypeId.toString, "maternity", originalCacheMap)

          result.data mustBe Map(PartnerStatutoryPayTypeId.toString -> JsString("maternity"))

        }

      }
      "Save Partner Statutory Pay data" must {
        "remove PartnerStatutoryPayPerWeek page data when user selects no option" in {
          val originalCacheMap = new CacheMap("id", Map(
            PartnerStatutoryPayTypeId.toString -> JsString("maternity"),
            PartnerStatutoryStartDateId.toString -> Json.toJson(LocalDate.of(2017, 2, 1))))

          val result = cascadeUpsert(BothStatutoryPayId.toString, false, originalCacheMap)

          result.data mustBe Map(BothStatutoryPayId.toString -> JsBoolean(false))
        }
      }

    }
  }
}
