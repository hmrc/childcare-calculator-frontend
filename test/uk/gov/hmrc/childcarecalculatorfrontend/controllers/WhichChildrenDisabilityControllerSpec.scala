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

import org.scalatest.OptionValues
import play.api.data.Form
import play.api.libs.json._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenDisabilityForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, WhichChildrenDisabilityId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenDisability

import java.time.LocalDate

class WhichChildrenDisabilityControllerSpec extends ControllerSpecBase with OptionValues {

  val view = application.injector.instanceOf[whichChildrenDisability]

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhichChildrenDisabilityController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  val requiredData: Map[String, JsValue] = Map(
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", LocalDate.now)),
      "1" -> Json.toJson(AboutYourChild("Bar", LocalDate.now))
    )
  )

  val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))

  val options = Seq(
    "Foo" -> "0",
    "Bar" -> "1"
  )

  def viewAsString(form: Form[_] = WhichChildrenDisabilityForm()): String =
    view(frontendAppConfig, form, options)(fakeRequest, messages).toString

  "WhichChildrenDisability Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller(getRequiredData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustEqual viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData       = requiredData + (WhichChildrenDisabilityId.toString -> JsArray(Seq(JsNumber(0))))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustEqual viewAsString(WhichChildrenDisabilityForm(0, 1).fill(Set(0)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value[0]" -> "0").withMethod("POST")
      val result      = controller(getRequiredData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value[0]" -> "invalid value").withMethod("POST")
      val boundForm   = WhichChildrenDisabilityForm().bind(Map("value[0]" -> "invalid value"))

      val result = controller(getRequiredData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "0")).withMethod("POST")
      val result      = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if no required data is found" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if no required data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "0")).withMethod("POST")
      val result      = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }
  }

}
