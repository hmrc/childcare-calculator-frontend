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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.household

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{EitherValues, OptionValues}
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.models.household.{Income, StatutoryIncome}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeSpec extends SchemeSpec with MockitoSugar with OptionValues with EitherValues {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))


  "Income" must{
    "Create the Income model with statutory income " in  {
      val answers = spy(userAnswers())
      val statsIncome = StatutoryIncome(12, 2400)

      Income(answers, Some(statsIncome)) mustBe Income(statutoryIncome = Some(statsIncome))
    }
  }

}