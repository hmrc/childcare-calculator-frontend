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

  "Statutory for partner" when {
    "Save Partner Statutory Pay Before Tax data " must {

      "Save Partner Statutory Pay data" must {
        "remove PartnerStatutoryPayPerWeek page data when user selects no option" in {
          val originalCacheMap = new CacheMap("id", Map(
          ))

          val result = cascadeUpsert(BothStatutoryPayId.toString, false, originalCacheMap)

          result.data mustBe Map(BothStatutoryPayId.toString -> JsBoolean(false))
        }
      }

    }
  }
}
