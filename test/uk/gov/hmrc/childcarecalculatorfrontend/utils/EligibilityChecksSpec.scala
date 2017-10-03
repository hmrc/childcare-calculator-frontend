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

import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildAgedThreeOrFourId, ChildAgedTwoId, LocationId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.http.cache.client.CacheMap
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.libs.json

class EligibilityChecksSpec extends WordSpec with MustMatchers {

  def helper(cacheMap: CacheMap): EligibilityChecks =
    new UserAnswers(cacheMap)

  def cacheMap(value: (String, JsValue)*): CacheMap =
    CacheMap("", Map(value.toSeq: _*))

  "isEligibleForFreeHours" must {

    val child2TestCases = Table(
      ("Location", "Child Aged 2"),
      ("england", true),
      ("scotland", true),
      ("wales", true)
    )

    val child3Or4TestCases = Table(
      ("Location", "Child Aged 3 or 4"),
      ("england", true),
      ("scotland", true),
      ("northernIreland", true),
      ("wales", true)
    )


    forAll(child2TestCases) { case (location, childAged2) =>
      s"return Eligible when they have a child aged 2 and location is $location" in {
        val data: CacheMap = cacheMap(
          LocationId.toString -> JsString(location),
          ChildAgedTwoId.toString -> JsBoolean(childAged2)
        )
        helper(data).isEligibleForFreeHours mustEqual Eligible
      }
    }

    forAll(child3Or4TestCases) { case (location, childAged3Or4) =>
      s"return Eligible when they have a child ages 3 or 4 and location is $location" in {
        val data: CacheMap = cacheMap(
          LocationId.toString -> JsString(location),
          ChildAgedThreeOrFourId.toString -> JsBoolean(childAged3Or4)
        )
        helper(data).isEligibleForFreeHours mustEqual Eligible
      }
    }

    s"return not eligible when they don't have a child aged 3 or 4 and location is northern Ireland" in {
      val data: CacheMap = cacheMap(
        LocationId.toString -> JsString("northernIreland"),
        ChildAgedThreeOrFourId.toString -> JsBoolean(false)
      )
      helper(data).isEligibleForFreeHours mustEqual NotEligible
    }

    "return not eligible when they don't have child aged 2 or child aged 3 or 4" in {
      val data: CacheMap = cacheMap(
        ChildAgedThreeOrFourId.toString -> JsBoolean(false),
        ChildAgedTwoId.toString -> JsBoolean(false)
      )
      helper(data).isEligibleForFreeHours mustEqual NotEligible
    }

  }

  "isEligibleForMaxFreeHours" must {

    "return not eligible when they are not eligible for free hours" in {
      val data: CacheMap = cacheMap(
        ChildAgedTwoId.toString -> JsBoolean(false),
        ChildAgedThreeOrFourId.toString -> JsBoolean(false)
      )
      helper(data).isEligibleForMaxFreeHours mustEqual NotEligible
    }

    "return eligible when they are eligible for free hours, live in england and have a child aged 3 or 4" in {
      val data: CacheMap = cacheMap(
        ChildAgedThreeOrFourId.toString -> JsBoolean(true),
        LocationId.toString -> JsString("england")
      )
      helper(data).isEligibleForMaxFreeHours mustEqual Eligible
    }

    "return not eligible when they are eligible for free hours but are not in england" in {
      val data: CacheMap = cacheMap(
        ChildAgedThreeOrFourId.toString -> JsBoolean(true),
        LocationId.toString -> JsString("wales")
      )
      helper(data).isEligibleForMaxFreeHours mustEqual NotEligible
    }

    "return not eligible when they are eligible for free hours, live in england but do not have a child aged 3 or 4" in {
      val data: CacheMap = cacheMap(
        ChildAgedThreeOrFourId.toString -> JsBoolean(false),
        ChildAgedTwoId.toString -> JsBoolean(true),
        LocationId.toString -> JsString("england")
      )
      helper(data).isEligibleForMaxFreeHours mustEqual NotEligible
    }
  }
}
