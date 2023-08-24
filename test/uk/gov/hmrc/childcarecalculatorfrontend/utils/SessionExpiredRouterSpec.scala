/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.Mockito.spy
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes

class SessionExpiredRouterSpec extends SpecBase {
  "Session Expired Router" should {
    "Route to session expired controller" in {
      val answers = spy(userAnswers())
      val result = SessionExpiredRouter.route("test","test",Some(answers))

      result mustBe routes.SessionExpiredController.onPageLoad
    }

    "Be able to cope with no UserAnswers" in {
      val result = SessionExpiredRouter.route("test","test",None)

      result mustBe routes.SessionExpiredController.onPageLoad
    }

    "Be able to cope with UserAnswers with populated cache map" in {
      val answers = spy(userAnswers())
      val result = SessionExpiredRouter.route("test","test",Some(answers))

      result mustBe routes.SessionExpiredController.onPageLoad
    }

    "Be able to cope with UserAnswers with no cache map" in {
      val result = SessionExpiredRouter.route("test","test",Some(new UserAnswers(null)))

      result mustBe routes.SessionExpiredController.onPageLoad
    }
  }

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))
}
