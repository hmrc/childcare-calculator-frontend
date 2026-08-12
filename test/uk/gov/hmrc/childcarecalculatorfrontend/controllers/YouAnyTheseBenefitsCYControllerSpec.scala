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
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.*
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{DoYouGetAnyBenefitsId, LocationId, YouAnyTheseBenefitsCYId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsCY

class YouAnyTheseBenefitsCYControllerSpec extends ControllerSpecBase {

  val view: youAnyTheseBenefitsCY = inject[youAnyTheseBenefitsCY]
  def onwardRoute: Call           = routes.WhatToTellTheCalculatorController.onPageLoad

  val location: Location             = Location.England
  val cacheMapWithLocation: CacheMap = CacheMap.of(LocationId.withValue(location))
  def getDataWithLocationSet         = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new YouAnyTheseBenefitsCYController(
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(form: Form[Boolean] = BooleanForm()): String =
    view(form, location)(using fakeRequest, messages).toString

  "YouAnyTheseBenefits Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData =
        Map(LocationId.withValue(location), YouAnyTheseBenefitsCYId.withValue(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(BooleanForm().fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when parent answered they get carers allowance and on current page they select 'No' for non Scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

      val location = Location.England
      val carerAllowance = Map(
        LocationId.withValue(location),
        DoYouGetAnyBenefitsId.withValue(Set(ParentsBenefit.CarersAllowance))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("youAnyTheseBenefitsCY.error.carers.allowance")
    }

    "return a Bad Request and errors when parent answered they get either carer’s allowance or carer support payment and " +
      "on current page they select 'No' for Scottish users" in {

        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

        val location = Location.Scotland
        val scottishCarersAllowance = Map(
          LocationId.withValue(location),
          DoYouGetAnyBenefitsId.withValue(Set(ParentsBenefit.CarersAllowance))
        )
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, scottishCarersAllowance)))

        val result = controller(getRelevantData).onSubmit()(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) contains messages("youAnyTheseBenefitsCY.error.ScottishCarers.allowance")
      }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = BooleanForm(youAnyTheseBenefitsCYErrorKey).bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to next page when parent answered they get carers allowance and they select 'Yes' for non Scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val location = Location.England
      val carerAllowance = Map(
        LocationId.withValue(location),
        DoYouGetAnyBenefitsId.withValue(Set(ParentsBenefit.CarersAllowance))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to next page when parent answered they get either carer’s allowance or carer support payment and they select 'Yes' for Scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val location = Location.Scotland
      val scottishCarersAllowance = Map(
        LocationId.withValue(location),
        DoYouGetAnyBenefitsId.withValue(Set(ParentsBenefit.CarersAllowance))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, scottishCarersAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")
      val result      = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
