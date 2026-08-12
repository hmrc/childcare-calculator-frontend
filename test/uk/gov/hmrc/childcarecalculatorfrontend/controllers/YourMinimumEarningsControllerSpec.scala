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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.config.NmwConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.*
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{LocationId, YourAgeId, YourMinimumEarningsId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Age, Location}
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings

class YourMinimumEarningsControllerSpec extends ControllerSpecBase with MockitoSugar {

  val view: yourMinimumEarnings = inject[yourMinimumEarnings]

  override lazy val nmwConfig: NmwConfig = mock[NmwConfig]

  val location: Location             = Location.England
  val locationMap: (String, JsValue) = LocationId.withValue(location)
  val cacheMapWithLocation: CacheMap = CacheMap.of(LocationId.withValue(location))
  val getDataWithLocationSet         = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new YourMinimumEarningsController(
      nmwConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(form: Form[Boolean] = BooleanForm()): String =
    view(form, 0, location)(using fakeRequest, messages).toString

  def mockEarnings(amount: Int = 0): Unit =
    Mockito.when(nmwConfig.getEarningsForAgeRange(any(), any())).thenReturn(amount)

  "YourMinimumEarnings Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(
        YourAgeId.withValue(Age.UnderEighteen),
        LocationId.withValue(Location.England)
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      mockEarnings()

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        YourAgeId.withValue(Age.UnderEighteen),
        YourMinimumEarningsId.withValue(true),
        LocationId.withValue(Location.England)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      mockEarnings()

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(BooleanForm("yourMinimumEarnings.error.notCompleted", 0).fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      mockEarnings()

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = BooleanForm("yourMinimumEarnings.error.notCompleted", 0).bind(Map("value" -> "invalid value"))

      mockEarnings()

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      mockEarnings()

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to the 'your age' view when session data does not hold this value" in {
      mockEarnings()

      val result = controller(getDataWithLocationSet).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.YourAgeController.onPageLoad().url)
    }

  }

}
