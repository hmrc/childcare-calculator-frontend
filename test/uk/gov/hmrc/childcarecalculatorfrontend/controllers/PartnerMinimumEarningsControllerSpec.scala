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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{PartnerMinimumEarningsId, YourPartnersAgeId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerMinimumEarnings

class PartnerMinimumEarningsControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view      = application.injector.instanceOf[partnerMinimumEarnings]
  val mockUtils = mock[Utils]

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PartnerMinimumEarningsController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      mockUtils,
      view
    )

  def viewAsString(form: Form[Boolean] = BooleanForm()) =
    view(frontendAppConfig, form, 0)(fakeRequest, messages).toString

  "PartnerMinimumEarnings Controller" must {

    "return OK and the correct view for a GET" in {
      val validData       = Map(YourPartnersAgeId.toString -> JsString(AgeEnum.UNDER18.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      setUpMock()

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val validData = Map(
        YourPartnersAgeId.toString        -> JsString(AgeEnum.UNDER18.toString),
        PartnerMinimumEarningsId.toString -> JsBoolean(true)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      setUpMock()

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(
        BooleanForm("partnerMinimumEarnings.error.notCompleted", 0).fill(true)
      )
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      setUpMock()

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = BooleanForm("partnerMinimumEarnings.error.notCompleted", 0).bind(Map("value" -> "invalid value"))
      setUpMock()

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)
      setUpMock()

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      setUpMock()

      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to 'your partners age' view when session data does not hold this value" in {
      val result = controller(getEmptyCacheMap).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.YourPartnersAgeController.onPageLoad().url)
    }

  }

  private def setUpMock() =
    when(mockUtils.getEarningsForAgeRange(any(), any(), any())).thenReturn(0)

}
