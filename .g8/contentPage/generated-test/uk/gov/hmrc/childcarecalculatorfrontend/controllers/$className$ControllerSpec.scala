package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

class $className$ControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new $className$Controller(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl)

  "$className$ Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe $className;format="decap"$(frontendAppConfig)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
