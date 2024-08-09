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
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{PartnerStatutoryPayBeforeTaxId, PartnerStatutoryPayTypeId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryPayBeforeTax



class PartnerStatutoryPayBeforeTaxControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[partnerStatutoryPayBeforeTax]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  private val statutoryTypeNameValuePair = Map(PartnerStatutoryPayTypeId.toString -> JsString(statutoryType.toString))

  private val retrievalAction = new FakeDataRetrievalAction(
    Some(CacheMap("id", statutoryTypeNameValuePair))
  )

  val myForm = BooleanForm("partnerStatutoryPayBeforeTax.error.notCompleted", statutoryType.toString)

  def controller(dataRetrievalAction: DataRetrievalAction = retrievalAction) =
    new PartnerStatutoryPayBeforeTaxController(frontendAppConfig, mcc, FakeDataCacheService, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  def viewAsString(form: Form[Boolean] = myForm) = view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages).toString

  "PartnerStatutoryPayBeforeTax Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller(buildFakeRequest(statutoryTypeNameValuePair)).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(PartnerStatutoryPayBeforeTaxId.toString -> JsBoolean(true)) ++ statutoryTypeNameValuePair
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(myForm.fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = myForm.bind(Map("value" -> "invalid value"))

      val result = controller(buildFakeRequest(statutoryTypeNameValuePair)).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
