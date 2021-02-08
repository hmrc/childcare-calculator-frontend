/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{JsBoolean, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildAgedThreeOrFourId, LocationId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedThreeOrFour

import scala.concurrent.ExecutionContext.Implicits.global

class ChildAgedThreeOrFourControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  val location = Location.ENGLAND

  val cacheMapWithLocation = new CacheMap("id", Map(LocationId.toString -> JsString(location.toString)))

  def getDataWithLocationSet = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new ChildAgedThreeOrFourController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction)

  def viewAsString(form: Form[Boolean] = BooleanForm()) = childAgedThreeOrFour(frontendAppConfig, form, NormalMode, location)(fakeRequest, messages).toString

  "ChildAgedThreeOrFour Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        LocationId.toString -> JsString(location.toString),
        ChildAgedThreeOrFourId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(BooleanForm().fill(true))
    }

    "redirect to Location on a GET when previous data exists but the location hasn't been answered" in {
      val result = controller(getEmptyCacheMap).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad(NormalMode).url)
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Location when valid data is submitted and previous data exists, but the location hasn't been answered" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val result = controller(getEmptyCacheMap).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad(NormalMode).url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = BooleanForm("childAgedThreeOrFour.error.notCompleted").bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

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
  }
}




