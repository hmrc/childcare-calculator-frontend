package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import play.api.data.Form
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.ControllerSpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.{routes => baseRoutes}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.$pluralName$Id
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.add$className$

class Add$className$ControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.$className$OverviewController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new Add$className$Controller(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString(form: Form[$className$] = $className$Form()) = add$className$(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "Add $className$ Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "not populate the view on a GET when $pluralName;format="decap"$ have already been added" in {
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className$("value 1", "value 2"))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString($className$Form())
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = $className$Form().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(baseRoutes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(baseRoutes.SessionExpiredController.onPageLoad().url)
    }
  }
}
