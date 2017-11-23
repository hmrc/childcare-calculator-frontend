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

import play.api.data.Form
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BothNoWeeksStatPayCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.BothNoWeeksStatPayCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BothNoWeeksStatPayCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothNoWeeksStatPayCY
import uk.gov.hmrc.http.cache.client.CacheMap

class BothNoWeeksStatPayCYControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BothNoWeeksStatPayCYController(frontendAppConfig,
      messagesApi,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new BothNoWeeksStatPayCYForm(frontendAppConfig),
      new DataRequiredActionImpl())

  def viewAsString(form: Form[BothNoWeeksStatPayCY]) = bothNoWeeksStatPayCY(frontendAppConfig,
    form,
    NormalMode)(fakeRequest, messages).toString

  "BothNoWeeksStatPayCY Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(BothNoWeeksStatPayCYId.toString -> Json.toJson(BothNoWeeksStatPayCY(1, 2)))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(new BothNoWeeksStatPayCYForm(frontendAppConfig).apply.fill(BothNoWeeksStatPayCY(1, 2)))
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(BothNoWeeksStatPayCYId.toString -> Json.toJson(BothNoWeeksStatPayCY(1, 2)))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(new BothNoWeeksStatPayCYForm(frontendAppConfig).apply.fill(BothNoWeeksStatPayCY(1, 2)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("youNoWeeksYouStatPayCY", "1"), ("partnerWeeksYouStatPayCY", "2"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = new BothNoWeeksStatPayCYForm(frontendAppConfig).apply.bind(Map("value" -> "invalid value"))

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
      val postRequest = fakeRequest.withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
