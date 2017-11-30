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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import org.joda.time.LocalDate
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, Location}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap
import play.api.libs.json.JsValue

class UserAnswerToHouseholdSpec extends SchemeSpec with MockitoSugar with OptionValues with EitherValues {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
        new UserAnswers(CacheMap("", Map(answers: _*)))

       val cy: Int = LocalDate.now.getYear

  "UserAnswerToHousehold" must {

      "given a user answer with location" in {

        val household = Household(location = Location.ENGLAND)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)

        UserAnswerToHousehold.convert(answers) mustEqual household
      }

    "given a user answer with location and credits" in {

      val household = Household(location = Location.ENGLAND)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)

      UserAnswerToHousehold.convert(answers) mustEqual household
    }


  }
}
