/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{YourStatutoryPayTypeForm, YourStatutoryStartDateForm}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{YourStatutoryPayTypeId, YourStatutoryStartDateId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryStartDate
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.ExecutionContext.Implicits.global

class YourStatutoryStartDateControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  private val statutoryTypeNameValuePair = Map(YourStatutoryPayTypeId.toString -> JsString(statutoryType.toString))

  private val retrievalAction = new FakeDataRetrievalAction(
    Some(CacheMap("id", statutoryTypeNameValuePair))
  )

  val previousTaxYear = new TaxYear(LocalDate.now().getYear).previous.currentYear

  val currentTaxYear =  new TaxYear(LocalDate.now().getYear).currentYear

  def controller(dataRetrievalAction: DataRetrievalAction = retrievalAction) =
    new YourStatutoryStartDateController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction)

  def viewAsString(form: Form[LocalDate] = YourStatutoryStartDateForm(statutoryType)) =
    yourStatutoryStartDate(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages).toString

  "YourStatutoryStartDate Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = Map(YourStatutoryPayTypeId.toString -> JsString(YourStatutoryPayTypeForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(YourStatutoryStartDateId.toString -> Json.toJson(new LocalDate(previousTaxYear, 2, 1)),
        YourStatutoryPayTypeId.toString -> JsString(YourStatutoryPayTypeForm.options.head.value)
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(YourStatutoryStartDateForm(statutoryType).fill(new LocalDate(previousTaxYear, 2, 1)))
    }

    "populate the view correctly on a GET request" in {
      val validData = Map(YourStatutoryPayTypeId.toString -> JsString(YourStatutoryPayTypeForm.options.head.value))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) must include("maternity")
    }

    "redirect to Session Expired for page load if there is no answer for statutory type" in {
      val validData = Map(LOCATION.toString -> JsString(Location.ENGLAND.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
  }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> previousTaxYear.toString
      )

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = YourStatutoryStartDateForm(statutoryType).bind(Map("value" -> "invalid value"))

      val result = controller(buildFakeRequest(statutoryTypeNameValuePair)).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "maternity"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
