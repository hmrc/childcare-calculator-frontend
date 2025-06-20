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
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{
  AboutYourChildId,
  ChildrenDisabilityBenefitsId,
  NoOfChildrenId
}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{childDisabilityBenefits, childrenDisabilityBenefits}

import java.time.LocalDate

class ChildrenDisabilityBenefitsControllerSpec extends ControllerSpecBase {

  val view1       = application.injector.instanceOf[childDisabilityBenefits]
  val view2       = application.injector.instanceOf[childrenDisabilityBenefits]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ChildrenDisabilityBenefitsController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view1,
      view2
    )

  def singleViewAsString(form: Form[Boolean] = BooleanForm()): String =
    view1(frontendAppConfig, form, "Foo")(fakeRequest, messages).toString

  def viewAsString(form: Form[Boolean] = BooleanForm()): String =
    view2(frontendAppConfig, form)(fakeRequest, messages).toString

  def requiredData(number: Int): Map[String, JsValue] = Map(
    NoOfChildrenId.toString -> JsNumber(number),
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
    )
  )

  def getRequiredData(number: Int = 1) =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(number))))

  "ChildrenDisabilityBenefits Controller" must {

    "return OK and the correct view for a GET when the user has a single child" in {
      val result = controller(getRequiredData()).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe singleViewAsString()
    }

    "return OK and the correct view for a GET when the user has multiple children" in {
      val result = controller(getRequiredData(2)).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET for a user with a single child when the question has previously been answered" in {
      val validData       = requiredData(1) + (ChildrenDisabilityBenefitsId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result          = controller(getRelevantData).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe singleViewAsString(BooleanForm().fill(true))
    }

    "populate the view correctly on a GET for a user with multiple children when the question has previously been answered" in {
      val validData       = requiredData(2) + (ChildrenDisabilityBenefitsId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result          = controller(getRelevantData).onPageLoad()(fakeRequest)
      contentAsString(result) mustBe viewAsString(BooleanForm().fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val result = controller(getRequiredData()).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted for a user with a single child" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = BooleanForm("childrenDisabilityBenefits.error.notCompleted").bind(Map("value" -> "invalid value"))
      val result    = controller(getRequiredData()).onSubmit()(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe singleViewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for a user with multiple children" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = BooleanForm("childrenDisabilityBenefits.error.notCompleted").bind(Map("value" -> "invalid value"))
      val result    = controller(getRequiredData(2)).onSubmit()(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      val result      = controller(dontGetAnyData).onSubmit()(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a GET if there is no answer for `number of children`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
        )
      )
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val result  = controller(getData).onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if there is no answer for `number of children`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
        )
      )
      val getData     = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      val result      = controller(getData).onSubmit()(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a GET if there is no answer for `about your child`" in {
      val data    = Map(NoOfChildrenId.toString -> JsNumber(1))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val result  = controller(getData).onPageLoad()(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if there is no answer for `about your child`" in {
      val data        = Map(NoOfChildrenId.toString -> JsNumber(1))
      val getData     = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      val result      = controller(getData).onSubmit()(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
