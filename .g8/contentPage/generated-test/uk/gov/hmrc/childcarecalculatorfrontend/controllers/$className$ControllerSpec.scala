package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

class $className$ControllerSpec extends ControllerSpecBase {

  "$className$ Controller" must {
    "return 200 for a GET" in {
      val result = new $className$Controller(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "return the correct view for a GET" in {
      val result = new $className$Controller(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe $className;format="decap"$(frontendAppConfig)(fakeRequest, messages).toString
    }
  }
}
