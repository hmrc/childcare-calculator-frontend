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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeResults
import uk.gov.hmrc.childcarecalculatorfrontend.services.SubmissionService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result


class ResultControllerSpec extends ControllerSpecBase {


  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 submissionService: SubmissionService = FakeSuccessfulSubmissionService) =
    new ResultController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl,
      submissionService)

  "Result Controller" must {
    "return OK and the correct view for a GET" in {
      val resultPage = controller().onPageLoad()(fakeRequest)
      status(resultPage) mustBe OK
      contentAsString(resultPage) mustBe result(frontendAppConfig)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}


object FakeSuccessfulSubmissionService extends SubmissionService {
  override def eligibility(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[SchemeResults] = {
    Future(SchemeResults(Nil))
  }
}

