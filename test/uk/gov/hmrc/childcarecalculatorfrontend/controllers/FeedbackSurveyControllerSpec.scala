package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.test.Helpers.status
import play.api.test.Helpers._

class FeedbackSurveyControllerSpec extends ControllerSpecBase {
  "Feedback survey controller" must {
    "Redirect to feedback survey page when survey link is clicked" in {
      val result = new FeedbackSurveyController(frontendAppConfig, messagesApi).loadFeedbackSurvey()(fakeRequest)

      status(result) mustBe SEE_OTHER
    }
  }
}
