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

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, NoOfChildrenId, RegisteredBlindId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.registeredBlind
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childRegisteredBlind

class RegisteredBlindControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new RegisteredBlindController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def singleViewAsString(form: Form[Boolean] = BooleanForm()) =
    childRegisteredBlind(frontendAppConfig, form, "Foo", NormalMode)(fakeRequest, messages).toString

  def viewAsString(form: Form[Boolean] = BooleanForm()) =
    registeredBlind(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  def requiredData(number: Int): Map[String, JsValue] = Map(
    NoOfChildrenId.toString -> JsNumber(number),
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
    )
  )

  def getRequiredData(number: Int = 1) =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(number))))

  "RegisteredBlind Controller" must {

    "return OK and the correct view for a GET when the user has a single child" in {
      val result = controller(getRequiredData(1)).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe singleViewAsString()
    }

    "return OK and the correct view for a GET when the user has multiple children" in {
      val result = controller(getRequiredData(2)).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET with a single child when the question has previously been answered" in {
      val validData = requiredData(1) + (RegisteredBlindId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
      contentAsString(result) mustBe singleViewAsString(BooleanForm().fill(true))
    }

    "populate the view correctly on a GET with multiple children when the question has previously been answered" in {
      val validData = requiredData(2) + (RegisteredBlindId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
      contentAsString(result) mustBe viewAsString(BooleanForm().fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(getRequiredData()).onSubmit(NormalMode)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted for a user with a single child" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = BooleanForm("registeredBlind.error").bind(Map("value" -> "invalid value"))
      val result = controller(getRequiredData(1)).onSubmit(NormalMode)(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe singleViewAsString(boundForm)
    }

    "return a Bad Request and errors when invalid data is submitted for a user with multiple children" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = BooleanForm("registeredBlind.error").bind(Map("value" -> "invalid value"))
      val result = controller(getRequiredData(2)).onSubmit(NormalMode)(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if there is no answer for `number of children`" in {
      val data = Map(AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
      ))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val result = controller(getData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if there is no answer for `number of children`" in {
      val data = Map(AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now))
      ))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(getData).onSubmit(NormalMode)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if there is no answer for `about your child`" in {
      val data = Map(NoOfChildrenId.toString -> JsNumber(1))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val result = controller(getData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if there is no answer for `about your child`" in {
      val data = Map(NoOfChildrenId.toString -> JsNumber(1))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(getData).onSubmit(NormalMode)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}




