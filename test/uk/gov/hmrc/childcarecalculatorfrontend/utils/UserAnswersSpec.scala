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

import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, DoYouLiveWithPartnerId, WhoIsInPaidEmploymentId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswersSpec extends WordSpec with MustMatchers with OptionValues {

  def cacheMap(answers: (String, JsValue)*): CacheMap =
    CacheMap("", Map(answers: _*))

  def helper(cacheMap: CacheMap): UserAnswers =
    new UserAnswers(cacheMap)

  "hasPartnerInPaidWork" must {

    "return true when user lives with partner and the answer to whoIsInPaidEmployment returns 'partner'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("partner"),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true)
      )
      helper(answers).hasPartnerInPaidWork mustEqual true
    }

    "return false when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("both"),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true)
      )
      helper(answers).hasPartnerInPaidWork mustEqual false
    }

    "return false when the answer to whoIsInPaidEmployment returns 'you'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("you"),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true)
      )
      helper(answers).hasPartnerInPaidWork mustEqual false
    }

    "return false when user does not live with partner" in {
      val answers: CacheMap = cacheMap(
        DoYouLiveWithPartnerId.toString -> JsBoolean(false)
      )
      helper(answers).hasPartnerInPaidWork mustEqual false
    }

    "return false when the answer to whoIsInPaidEmployment returns None" in {
      val answers: CacheMap = cacheMap()
      helper(answers).hasPartnerInPaidWork mustEqual false
    }
  }

  "hasBothInPaidWork" must {
    "return true when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("both"),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true)
      )
      helper(answers).hasBothInPaidWork mustEqual true
    }

    "return false when the answer to whoIsInPaidEmployment returns 'you'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("you"),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true)
      )
      helper(answers).hasBothInPaidWork mustEqual false
    }

    "return false when the answer to whoIsInPaidEmployment returns 'partner'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("partner")
      )
      helper(answers).hasBothInPaidWork mustEqual false
    }

    "return false when user does not live with partner" in {
      val answers: CacheMap = cacheMap(
        DoYouLiveWithPartnerId.toString -> JsBoolean(false)
      )
      helper(answers).hasBothInPaidWork mustEqual false
    }

    "return false when the answer to whoIsInPaidEmployment returns None" in {
      val answers: CacheMap = cacheMap()
      helper(answers).hasBothInPaidWork mustEqual false
    }
  }

  ".childrenOver16" must {

    "return any children who are over 16" in {

      val over16 = LocalDate.now.minusYears(16).minusDays(1)
      val under16 = LocalDate.now

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over16)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", under16)),
          "3" -> Json.toJson(AboutYourChild("Baz", over16))
        )
      )

      val result = helper(answers).childrenOver16
      result.value must contain(0 -> "Foo")
      result.value must contain(3 -> "Baz")
    }

    "return `None` when there are no children defined" in {

      val answers: CacheMap = cacheMap()

      helper(answers).childrenOver16 mustNot be(defined)
    }
  }
}
