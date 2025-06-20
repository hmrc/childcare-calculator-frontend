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
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildcarePayFrequencyForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{
  AboutYourChildId,
  ChildcarePayFrequencyId,
  WhoHasChildcareCostsId
}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency}
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency

import java.time.LocalDate

class ChildcarePayFrequencyControllerSpec extends ControllerSpecBase with OptionValues {

  val view        = application.injector.instanceOf[childcarePayFrequency]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ChildcarePayFrequencyController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(
      form: Form[ChildcarePayFrequency.Value] = ChildcarePayFrequencyForm("Foo"),
      id: Int = 0,
      name: String = "Foo"
  ) =
    view(frontendAppConfig, form, id, name)(fakeRequest, messages).toString

  val requiredData: Map[String, JsValue] = Map(
    WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 1)),
    AboutYourChildId.toString -> Json.obj(
      "0" -> AboutYourChild("Foo", LocalDate.now),
      "1" -> AboutYourChild("Bar", LocalDate.now)
    )
  )

  val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))

  "ChildcarePayFrequency Controller" must {

    val cases: Map[Int, String] = Map(
      0 -> "Foo",
      1 -> "Bar"
    )

    "redirect to session expired if we can't find name" in {
      val result = controller(getRequiredData).onPageLoad(4)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    cases.foreach { case (id, name) =>

      s"return OK and the correct view for a GET, for id: $id" in {
        val result = controller(getRequiredData).onPageLoad(id)(fakeRequest)
        status(result) mustEqual OK
        contentAsString(result) mustBe viewAsString(name = name, id = id)
      }

      s"populate the view correctly on a GET when the question has previously been answered, for id: $id" in {
        val validData = requiredData + (ChildcarePayFrequencyId.toString -> Json.obj(
          id.toString -> JsString(ChildcarePayFrequency(0).toString)
        ))
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result          = controller(getRelevantData).onPageLoad(id)(fakeRequest)
        contentAsString(result) mustEqual viewAsString(
          ChildcarePayFrequencyForm(name).fill(ChildcarePayFrequency(0)),
          id,
          name
        )
      }

      s"return a Bad Request and errors when invalid data is submitted, for id: $id" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
        val boundForm   = ChildcarePayFrequencyForm(name).bind(Map("value" -> "invalid value"))
        val result      = controller(getRequiredData).onSubmit(id)(postRequest)
        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe viewAsString(boundForm, id, name)
      }
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody(("value", ChildcarePayFrequencyForm.options.head.value)).withMethod("POST")
      val result = controller(getRequiredData).onSubmit(0)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "redirect to Session Expired if we can't find the name on submission" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody(("value", ChildcarePayFrequencyForm.options.head.value)).withMethod("POST")
      val result = controller(getRequiredData).onSubmit(4)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(0)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody(("value", ChildcarePayFrequencyForm.options.head.value)).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(0)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
