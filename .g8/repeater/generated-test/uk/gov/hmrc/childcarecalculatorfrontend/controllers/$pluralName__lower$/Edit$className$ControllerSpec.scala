
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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.edit$className$

class Edit$className$ControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.$className$OverviewController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new Edit$className$Controller(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction)

  def viewAsString(index: Int, form: Form[$className$] = $className$Form()) = edit$className$(index, frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "Edit $className$ Controller" must {

    "return OK and the correct view on a GET when the requested $className;format="decap"$ exists" in {
      val $className;format="decap"$ = $className$("value 1", "value 2")
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className;format="decap"$)))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val validIndex = 0

      val result = controller(getRelevantData).onPageLoad(validIndex, NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(validIndex, $className$Form().fill($className;format="decap"$))
    }

    "return Not Found for a GET when no $pluralName;format="decap"$ have been added yet" in {
      val result = controller().onPageLoad(0, NormalMode)(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

    "return Not Found for a GET when the requested $className;format="decap"$ does not exist" in {
      val $className;format="decap"$ = $className$("value 1", "value 2")
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className;format="decap"$)))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val invalidIndex = 1

      val result = controller(getRelevantData).onPageLoad(invalidIndex, NormalMode)(fakeRequest)

      status(result) mustBe NOT_FOUND
    }

    "redirect to the next page when valid data is submitted" in {
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className$("value 1", "value 2"))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))
      val validIndex = 0

      val result = controller(getRelevantData).onSubmit(validIndex, NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return Not Found when data is submitted for a thing that does not exist" in {
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className$("value 1", "value 2"))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))
      val invalidIndex = 1

      val result = controller(getRelevantData).onSubmit(invalidIndex, NormalMode)(postRequest)

      status(result) mustBe NOT_FOUND
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val validData = Map($pluralName$Id.toString -> Json.toJson(Seq($className$("value 1", "value 2"))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = $className$Form().bind(Map("value" -> "invalid value"))
      val validIndex = 0

      val result = controller(getRelevantData).onSubmit(validIndex, NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(validIndex, boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(0, NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(baseRoutes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))
      val result = controller(dontGetAnyData).onSubmit(0, NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(baseRoutes.SessionExpiredController.onPageLoad().url)
    }
  }
}
