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
import play.api.libs.json.{JsBoolean, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.EmploymentIncomeCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, TaxYearInfo}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.employmentIncomeCY

class EmploymentIncomeCYControllerSpec extends ControllerSpecBase {

  val view        = application.injector.instanceOf[employmentIncomeCY]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  val taxYearInfo = new TaxYearInfo

  val form = new EmploymentIncomeCYForm(frontendAppConfig).apply()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new EmploymentIncomeCYController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      new EmploymentIncomeCYForm(frontendAppConfig),
      taxYearInfo,
      view
    )

  def viewAsString(form: Form[EmploymentIncomeCY] = form) =
    view(frontendAppConfig, form, taxYearInfo)(fakeRequest, messages).toString

  "EmploymentIncomeCY Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = Map(EmploymentIncomeCYId.toString -> Json.toJson(EmploymentIncomeCY(1, 2)))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(EmploymentIncomeCY(1, 2)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody(("parentEmploymentIncomeCY", "1"), ("partnerEmploymentIncomeCY", "2"))
        .withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody(("parentEmploymentIncomeCY", "value 1"), ("partnerEmploymentIncomeCY", "value 2"))
        .withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "return a Bad Request and errors when user answered max earnings question under 100000 but input was above 100000" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody(("parentEmploymentIncomeCY", "100000"), ("partnerEmploymentIncomeCY", "100000"))
        .withMethod("POST")

      val validData = Map(
        EitherOfYouMaximumEarningsId.toString -> JsBoolean(false),
        ParentEmploymentIncomeCYId.toString   -> Json.toJson("100000"),
        PartnerEmploymentIncomeCYId.toString  -> Json.toJson("100000"),
        WhoIsInPaidEmploymentId.toString      -> Json.toJson("both")
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages(parentEmploymentIncomeInvalidMaxEarningsErrorKey)
      contentAsString(result) contains messages(partnerEmploymentIncomeInvalidMaxEarningsErrorKey)
    }

    "return a Bad Request and errors when user answered max earnings question under 1000000 but input was above 1000000" in {
      val postRequest = fakeRequest
        .withFormUrlEncodedBody(("parentEmploymentIncomeCY", "1000000"), ("partnerEmploymentIncomeCY", "1000000"))
        .withMethod("POST")

      val validData = Map(
        EitherOfYouMaximumEarningsId.toString -> JsBoolean(true),
        ParentEmploymentIncomeCYId.toString   -> Json.toJson("1000000"),
        PartnerEmploymentIncomeCYId.toString  -> Json.toJson("1000000"),
        WhoIsInPaidEmploymentId.toString      -> Json.toJson("both")
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages(parentEmploymentIncomeInvalidErrorKey)
      contentAsString(result) contains messages(partnerEmploymentIncomeInvalidErrorKey)
    }
  }

}
