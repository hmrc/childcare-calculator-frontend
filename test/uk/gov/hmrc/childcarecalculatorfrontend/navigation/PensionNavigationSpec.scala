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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap


class PensionNavigationSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator(new Schemes())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Pension Route Navigation" when {

    "in Normal mode" must {
      "Parent Paid Pension CY Route" must {
        "redirects to howMuchYouPayPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn Some(true)

          navigator.nextPage(YouPaidPensionCYId, NormalMode)(answers) mustBe
            routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to yourOtherIncomeThisYear page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          navigator.nextPage(YouPaidPensionCYId, NormalMode)(answers) mustBe
            routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn None

          navigator.nextPage(YouPaidPensionCYId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()

        }

      }

    }
  }

}
