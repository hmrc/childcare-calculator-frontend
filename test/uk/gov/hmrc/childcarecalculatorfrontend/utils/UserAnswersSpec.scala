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
import org.mockito.Mockito._
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswersSpec extends WordSpec with MustMatchers with OptionValues {

  "return partner when user lives with partner and the answer to whoIsInPaidEmployment returns 'partner'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("partner"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("partner")) mustEqual "partner"
  }

  "return both when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("both"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("both")) mustEqual "both"
  }

  "return you when the answer to whoIsInPaidEmployment returns 'you'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("you"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
  }

  "return you when user does not live with partner" in {
    val answers: CacheMap = cacheMap(
      DoYouLiveWithPartnerId.toString -> JsBoolean(false)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
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
      result.value must contain(0 -> AboutYourChild("Foo", over16))
      result.value must contain(3 -> AboutYourChild("Baz", over16))
      result.value mustNot contain(1 -> AboutYourChild("Bar", under16))
      result.value mustNot contain(2 -> AboutYourChild("Quux", under16))
    }

    "return `None` when there are no children defined" in {
      val answers: CacheMap = cacheMap()
      helper(answers).childrenOver16 mustNot be(defined)
    }
  }

  ".childrenWithDisabilityBenefits" must {

    "return `Some` if `whichChildrenDisability` is defined" in {
      val answers = helper(cacheMap(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq("0", "2"))
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0, 2)
    }

    "return `Some` if there is a single child with disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `Some(Set())` if there are multiple children without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `None` if `noOfChildren` and `whichChildrenDisability` are both undefined" in {
      val answers = helper(cacheMap(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }

    "return `None` if there is a single child and `childrenDisabilityBenefits` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }
  }

  def cacheMap(answers: (String, JsValue)*): CacheMap =
    CacheMap("", Map(answers: _*))

  def helper(map: CacheMap = cacheMap()): UserAnswers =
    new UserAnswers(map)
}
