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
import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoIsInPaidEmploymentId
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.doYouLiveWithPartner
import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswersSpec extends WordSpec with MustMatchers {

  def cacheMap(answers: (String, JsValue)*): CacheMap =
    CacheMap("", Map(answers: _*))

  def helper(cacheMap: CacheMap): UserAnswers =
    new UserAnswers(cacheMap)

  "hasPartnerInPaidWork" must {

    "return true when user lives with partner and the answer to whoIsInPaidEmployment returns 'partner'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("partner")
      )
      helper(answers).hasPartnerInPaidWork mustEqual true
    }

    "return true when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("both")
      )
      helper(answers).hasPartnerInPaidWork mustEqual true
    }

    "return false when the answer to whoIsInPaidEmployment returns 'you'" in {
      val answers: CacheMap = cacheMap(
        WhoIsInPaidEmploymentId.toString -> JsString("you")
      )
      helper(answers).hasPartnerInPaidWork mustEqual false
    }

    "return false when the answer to whoIsInPaidEmployment returns None" in {
      val answers: CacheMap = cacheMap()
      helper(answers).hasPartnerInPaidWork mustEqual false
    }
  }
}
