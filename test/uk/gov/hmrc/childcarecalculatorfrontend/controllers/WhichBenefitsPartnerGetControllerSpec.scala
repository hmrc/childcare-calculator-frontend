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
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsPartnerGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{LocationId, WhichBenefitsPartnerGetId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsPartnerGet



class WhichBenefitsPartnerGetControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[whichBenefitsPartnerGet]

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  val location = Location.SCOTLAND
  val cacheMapWithLocation = new CacheMap("id", Map(LocationId.toString -> JsString(location.toString)))
  def getDataWithLocationSet = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new WhichBenefitsPartnerGetController(frontendAppConfig, mcc, FakeDataCacheService, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  def viewAsString(form: Form[Set[String]] = WhichBenefitsPartnerGetForm(location)) = view(frontendAppConfig, form, NormalMode, location)(fakeRequest, messages).toString

  "WhichBenefitsPartnerGet Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(LocationId.toString -> JsString(location.toString),
        WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq(WhichBenefitsPartnerGetForm.options.head._2)))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(WhichBenefitsPartnerGetForm(location).fill(Set(WhichBenefitsPartnerGetForm.options.head._2)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", WhichBenefitsPartnerGetForm.options.toSeq.head._2)).withMethod("POST")

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "invalid value")).withMethod("POST")
      val boundForm = WhichBenefitsPartnerGetForm(location).bind(Map("value[0]" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", WhichBenefitsPartnerGetForm.options.toSeq.head._2)).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
