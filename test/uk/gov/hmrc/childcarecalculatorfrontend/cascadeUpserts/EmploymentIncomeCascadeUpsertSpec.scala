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

import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomePY, OtherIncomeAmountCY, OtherIncomeAmountPY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

class EmploymentIncomeCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Employment Income PY" when {

    "Save bothPaidWorkPY data" must {
      "remove whoWasInPaidWorkPY, employmentIncomePY, parentEmploymentIncomePY, partnerEmploymentIncomePY," +
        "youPaidPensionPY, partnerPaidPensionPY, bothPaidPensionPY, whoPaidIntoPensionPY," +
        "howMuchYouPayPensionPY, howMuchPartnerPayPensionPY, howMuchBothPayPensionPY pages data when user selects no option " in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoWasInPaidWorkPYId.toString -> JsString(Both),
          EmploymentIncomePYId.toString -> Json.toJson(EmploymentIncomePY(20, 25)),
          ParentEmploymentIncomePYId.toString -> JsNumber(BigDecimal(20)),
          PartnerEmploymentIncomePYId.toString -> JsNumber(BigDecimal(20)),
          YouPaidPensionPYId.toString -> JsBoolean(true),
          PartnerPaidPensionPYId.toString -> JsBoolean(true),
          BothPaidPensionPYId.toString -> JsBoolean(true),
          WhoPaidIntoPensionPYId.toString -> JsString(Both),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchPartnerPayPensionPYId.toString -> JsNumber(BigDecimal(20)),
          HowMuchBothPayPensionPYId.toString -> JsNumber(BigDecimal(20, 20))
        ))

        val result = cascadeUpsert(BothPaidWorkPYId.toString, false, originalCacheMap)

        result.data mustBe Map(BothPaidWorkPYId.toString -> JsBoolean(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = new CacheMap("id", Map(
          WhoWasInPaidWorkPYId.toString -> JsString(You),
          ParentEmploymentIncomePYId.toString -> JsNumber(BigDecimal(20)),
          YouPaidPensionPYId.toString -> JsBoolean(true),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20))
        ))

        val result = cascadeUpsert(BothPaidWorkPYId.toString, true, originalCacheMap)

        result.data mustBe Map(BothPaidWorkPYId.toString -> JsBoolean(true),
          WhoWasInPaidWorkPYId.toString -> JsString(You),
          ParentEmploymentIncomePYId.toString -> JsNumber(BigDecimal(20)),
          YouPaidPensionPYId.toString -> JsBoolean(true),
          HowMuchYouPayPensionPYId.toString -> JsNumber(BigDecimal(20)))


      }
    }

  }
}
