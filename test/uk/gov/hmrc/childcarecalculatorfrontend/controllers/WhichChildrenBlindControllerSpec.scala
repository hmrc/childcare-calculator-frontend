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
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenBlindForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, WhichChildrenBlindId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenBlind

import java.time.LocalDate

class WhichChildrenBlindControllerSpec extends ControllerSpecBase with OptionValues {

  val view = application.injector.instanceOf[whichChildrenBlind]

  "WhichChildrenBlind Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller(getRequiredData).onPageLoad()(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual viewAsString()
    }

    Seq(
      Map("Foo"   -> "0", "Bar"    -> "1"),
      Map("Spoon" -> "2", "Womble" -> "3")
    ).zipWithIndex.foreach { case (values, i) =>

      val value = values.values.toSeq.head

      s"populate the view correctly on a GET when the question has previously been answered, $i" in {
        val validData = requiredData(values) + (
          WhichChildrenBlindId.toString -> Json.toJson(Seq(value.toInt))
        )
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

        val result = controller(getRelevantData).onPageLoad()(fakeRequest)

        contentAsString(result) mustEqual viewAsString(WhichChildrenBlindForm().fill(Set(value.toInt)), values)
      }

      s"redirect to the next page when valid data is submitted, $i" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody("value[0]" -> value).withMethod("POST")

        val result = controller(getRequiredData(values)).onSubmit()(postRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value[0]" -> "invalid value").withMethod("POST")
      val boundForm   = WhichChildrenBlindForm().bind(Map("value[0]" -> "invalid value"))

      val result = controller(getRequiredData).onSubmit()(postRequest)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "0").withMethod("POST")
      val result      = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if required data is missing" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if required data is missing" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "0").withMethod("POST")
      val result      = controller().onSubmit()(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }
  }

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhichChildrenBlindController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  val defaultValues = Map("Foo" -> "0", "Bar" -> "1")

  def viewAsString(
      form: Form[_] = WhichChildrenBlindForm(0, 1),
      values: Map[String, String] = defaultValues
  ) =
    view(frontendAppConfig, form, values.toSeq)(fakeRequest, messages).toString

  def requiredData(values: Map[String, String]): Map[String, JsValue] = Map(
    AboutYourChildId.toString -> Json.obj(
      values.map { case (name, v) =>
        v -> (Json.toJson(AboutYourChild(name, LocalDate.now)): JsValueWrapper)
      }.toSeq: _*
    )
  )

  def getRequiredData(values: Map[String, String]): DataRetrievalAction =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(values))))

  def getRequiredData: DataRetrievalAction = getRequiredData(defaultValues)

}
