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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.test.Helpers.status
import play.api.test.Helpers._

class FeedbackSurveyControllerSpec extends ControllerSpecBase {

  "Feedback survey controller" must {
    "Redirect to feedback survey page when survey link is clicked" in {
      val result = new FeedbackSurveyController(frontendAppConfig, mcc).loadFeedbackSurvey()(fakeRequest)

      status(result) mustBe SEE_OTHER
    }
  }

}
