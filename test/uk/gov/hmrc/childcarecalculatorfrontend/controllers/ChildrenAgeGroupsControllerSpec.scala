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

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildrenAgeGroupsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildrenAgeGroupsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildAgeGroup, TwoYears}
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childrenAgeGroups

class ChildrenAgeGroupsControllerSpec extends ControllerSpecBase {

  val view: childrenAgeGroups = application.injector.instanceOf[childrenAgeGroups]

  def onwardRoute: Call = routes.ChildcareCostsController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ChildrenAgeGroupsController =
    new ChildrenAgeGroupsController(
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(form: Form[Set[ChildAgeGroup]] = ChildrenAgeGroupsForm()): String =
    view(form)(fakeRequest, messages).toString

  "ChildrenAgeGroupsController" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        ChildrenAgeGroupsId.toString -> Json.toJson(Set[ChildAgeGroup](TwoYears))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(ChildrenAgeGroupsForm().fill(Set[ChildAgeGroup](TwoYears)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody((s"${ChildrenAgeGroupsForm.formId}[0]", ChildAgeGroup.twoYears))
        .withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody((s"${ChildrenAgeGroupsForm.formId}[0]", "invalid value"))
        .withMethod("POST")
      val boundForm = ChildrenAgeGroupsForm()
        .bind(Map(s"${ChildrenAgeGroupsForm.formId}[0]" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody((s"${ChildrenAgeGroupsForm.formId}[0]", ChildAgeGroup.twoYears))
        .withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
