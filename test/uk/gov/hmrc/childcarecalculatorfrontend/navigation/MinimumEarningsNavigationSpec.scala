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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap


class MinimumEarningsNavigationSpec extends SpecBase with MockitoSugar{

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator(new Schemes())

  "Partner Minimum Earnings Navigation" when {

    "in Normal mode" must {

      "redirect to your maximum earnings page if parent and partner earns more than NMW" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode)(answers) mustBe
          routes.YourMaximumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page if partner earns more than NMW but parent doesn't" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode)(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page if parent and partner do not earn more than NMW" in {

        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode)(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)

      }

      "redirect to partner self employed or apprentice page if parent earns more than NMW but partner doesn't" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode)(answers) mustBe
          routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode) //TODO: To be replaced by partner self employed
      }


    }
  }

}
