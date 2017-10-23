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

package uk.gov.hmrc.childcarecalculatorfrontend

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Scheme
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

class NavigatorSpec extends SpecBase with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator()

  case object UnknownIdentifier extends Identifier

  "Navigator" when {

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        navigator.nextPage(UnknownIdentifier, CheckMode)(userAnswers()) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    ".nextPage" must {

      def scheme(e: Eligibility): Scheme = new Scheme {
        override def eligibility(answers: UserAnswers): Eligibility = e
      }

      val eligible: Scheme = scheme(Eligible)
      val notEligible: Scheme = scheme(NotEligible)

      "return a redirect to the Results page" when {

        "all schemes are determined" in {
          val navigator = new Navigator()
          navigator.nextPage(UnknownIdentifier, NormalMode)(spy(userAnswers())) mustEqual routes.WhatToTellTheCalculatorController.onPageLoad()
        }
      }
    }

  }
}
