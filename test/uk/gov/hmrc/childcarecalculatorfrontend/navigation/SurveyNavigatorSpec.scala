/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class SurveyNavigatorSpec extends SpecBase with MockitoSugar {
  val navigator = new SurveyNavigator(new Utils(), appConfig =frontendAppConfig)

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Survey Navigator" must {
    "Redirect to Not Understand Survey Page" when {
      "User selects that I don't understand childcare support options" in {
        val answers = spy(userAnswers())
        when(answers.surveyChildcareSupport) thenReturn Some(false)

        navigator.nextPage(SurveyChildcareSupportId, NormalMode).value(answers) mustBe routes.SurveyDoNotUnderstandController.onPageLoad()
      }
    }

    "Redirect to thank you page" when {
      "User selects that they understand their childcare support options" in {
        val answers = spy(userAnswers())
        when(answers.surveyChildcareSupport) thenReturn Some(true)

        val result = navigator.nextPage(SurveyChildcareSupportId, NormalMode).value(answers)

        result.url mustBe frontendAppConfig.surveyThankYouUrl
      }

      "User submits why they don't understand their childcare support options" in {
        val answers = spy(userAnswers())
        when(answers.surveyDoNotUnderstand) thenReturn Some("I don't understand.")

        val result = navigator.nextPage(SurveyDoNotUnderstandId, NormalMode).value(answers)

        result.url mustBe frontendAppConfig.surveyThankYouUrl
      }
    }
  }
}
