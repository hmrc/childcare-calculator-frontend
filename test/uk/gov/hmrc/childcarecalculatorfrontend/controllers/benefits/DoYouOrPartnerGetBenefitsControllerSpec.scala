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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers.benefits

import play.api.data.Form
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.JsString
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.ControllerSpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.DoYouOrPartnerGetBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.benefits.DoYouOrPartnerGetBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.You
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits.doYouOrPartnerGetBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeNavigator, controllers}

class DoYouOrPartnerGetBenefitsControllerSpec extends ControllerSpecBase {

  val onwardRoute: Call = controllers.routes.YourAgeController.onPageLoad()
  val view: doYouOrPartnerGetBenefits = application.injector.instanceOf[doYouOrPartnerGetBenefits]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): DoYouOrPartnerGetBenefitsController =
    new DoYouOrPartnerGetBenefitsController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  def viewAsString(form: Form[String] = DoYouOrPartnerGetBenefitsForm()): String = view(form)(fakeRequest, messages).toString

  "GET /you-or-partner-get-benefits" must {
    "return OK and the correct view" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly when the question has previously been answered" in {
      val validData = Map(DoYouOrPartnerGetBenefitsId.toString -> JsString(You))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(DoYouOrPartnerGetBenefitsForm().fill(You))
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad.url)
    }
  }

  "POST /you-or-partner-get-benefits" must {
    "redirect to next page when answer is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", You)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = DoYouOrPartnerGetBenefitsForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", You)).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad.url)
    }
  }
}




