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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{PartnerMinimumEarningsId, YourMaximumEarningsId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap


class MaximumEarningsNavigationSpec extends SpecBase with MockitoSugar{

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator(new Schemes())

  "Your Maximum Earnings Navigation" when {

    "in Normal mode" must {

      "single user redirects to your Do you get tax credits or universal credit page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      }

      "user with partner redirects to partner maximum earnings when partner satisfies NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)


        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)

        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      }

      "user with partner redirects to Do you get tax credits or universal credit page when partner does not satisfy NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)
        when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

        navigator.nextPage(YourMaximumEarningsId, NormalMode)(answers) mustBe
          routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

      }






    }
  }

}
