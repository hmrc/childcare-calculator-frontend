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
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildStartEducationForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, ChildApprovedEducationId, ChildStartEducationId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childStartEducation

import java.time.LocalDate

class ChildStartEducationControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[childStartEducation]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ChildStartEducationController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  val date = LocalDate.of(2017, 2, 1)
  val validBirthday = LocalDate.of(LocalDate.now.minusYears(17).getYear, 2, 1)
  val requiredData = Map(
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", validBirthday)),
      "1" -> Json.toJson(AboutYourChild("Bar", validBirthday))
    ),
    ChildApprovedEducationId.toString -> Json.obj(
      "0" -> true,
      "1" -> false
    )
  )
  val getRequiredData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData)))

  def viewAsString(form: Form[LocalDate] = ChildStartEducationForm(validBirthday)) =
    view(frontendAppConfig, form, NormalMode, 0, "Foo")(fakeRequest, messages).toString

  "ChildStartEducation Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller(getRequiredData).onPageLoad(NormalMode, 0)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = requiredData + (ChildStartEducationId.toString -> Json.obj(
        "0" -> date
      ))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode, 0)(fakeRequest)

      contentAsString(result) mustBe viewAsString(ChildStartEducationForm(validBirthday).fill(date))
    }

    "redirect to the next page when valid data is submitted" in {
      val startEducationDate = LocalDate.now

      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "date.day"   -> startEducationDate.getDayOfMonth.toString,
        "date.month" -> startEducationDate.getMonthValue.toString,
        "date.year"  -> startEducationDate.getYear.toString
      ).withMethod("POST")
      val result = controller(getRequiredData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody().withMethod("POST")
      val boundForm = ChildStartEducationForm(validBirthday).bind(Map.empty[String, String])

      val result = controller(getRequiredData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, 0)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> "2017"
      ).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(NormalMode, 0)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a GET if childIndex is not valid" in {
      val result = controller(getRequiredData).onPageLoad(NormalMode, 1)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if childIndex is not valid" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )
      val result = controller(getRequiredData).onSubmit(NormalMode, 1)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
