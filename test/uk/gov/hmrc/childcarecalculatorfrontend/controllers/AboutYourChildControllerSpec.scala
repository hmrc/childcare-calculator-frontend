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

import java.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{JsNumber, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AboutYourChildForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, NoOfChildrenId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild
import uk.gov.hmrc.http.cache.client.CacheMap

class AboutYourChildControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  val aboutYourChild = application.injector.instanceOf[aboutYourChild]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AboutYourChildController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, aboutYourChild)

  def viewAsString(form: Form[AboutYourChild] = AboutYourChildForm()) = aboutYourChild(frontendAppConfig, form, NormalMode, 0, 1)(fakeRequest, messages).toString

  val requiredData = Map(
    NoOfChildrenId.toString -> JsNumber(1)
  )

  "AboutYourChild Controller" must {

    "return OK and the correct view for a GET" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode, 0)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = requiredData + (AboutYourChildId.toString -> Json.obj("0" -> AboutYourChild("Foo", LocalDate.of(2016, 2, 1))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode, 0)(fakeRequest)

      contentAsString(result) mustBe viewAsString(AboutYourChildForm().fill(AboutYourChild("Foo", LocalDate.of(2016, 2, 1))))
    }

    "redirect to the next page when valid data is submitted" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))
      val date = LocalDate.now
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "name"      -> "Foo",
        "dob.day"   -> date.getDayOfMonth.toString,
        "dob.month" -> date.getMonthValue.toString,
        "dob.year"  -> date.getYear.toString
      ).withMethod("POST")

      val result = controller(getRelevantData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = AboutYourChildForm().bind(Map("value" -> "invalid value"))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))

      val result = controller(getRelevantData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, 0)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val date = LocalDate.now
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "name"      -> "Foo",
        "dob.day"   -> date.getDayOfMonth.toString,
        "dob.month" -> date.getMonthValue.toString,
        "dob.year"  -> date.getYear.toString
      ).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a GET if the user hasn't said how many children they have" in {
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map.empty)))
      val result = controller(getData).onPageLoad(NormalMode, 0)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if the user hasn't said how many children they have" in {
      val date = LocalDate.now
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "name"      -> "Foo",
        "dob.day"   -> date.getDayOfMonth.toString,
        "dob.month" -> date.getMonthValue.toString,
        "dob.year"  -> date.getYear.toString
      ).withMethod("POST")
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map.empty)))
      val result = controller(getData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
