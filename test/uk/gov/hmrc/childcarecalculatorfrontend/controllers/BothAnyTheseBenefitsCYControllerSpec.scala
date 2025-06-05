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
import play.api.libs.json.{JsBoolean, JsString, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{
  BothAnyTheseBenefitsCYId,
  DoYouGetAnyBenefitsId,
  DoesYourPartnerGetAnyBenefitsId,
  LocationId
}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, TaxYearInfo}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothAnyTheseBenefitsCY

class BothAnyTheseBenefitsCYControllerSpec extends ControllerSpecBase {

  val taxYearInfo       = new TaxYearInfo
  val view              = application.injector.instanceOf[bothAnyTheseBenefitsCY]
  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  val location               = Location.ENGLAND
  val cacheMapWithLocation   = new CacheMap("id", Map(LocationId.toString -> JsString(location.toString)))
  def getDataWithLocationSet = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new BothAnyTheseBenefitsCYController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      taxYearInfo,
      view
    )

  def viewAsString(form: Form[Boolean] = BooleanForm()): String =
    view(frontendAppConfig, form, taxYearInfo, location)(fakeRequest, messages).toString

  "BothAnyTheseBenefitsCY Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData =
        Map(LocationId.toString -> JsString(location.toString), BothAnyTheseBenefitsCYId.toString -> JsBoolean(true))
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

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = BooleanForm(bothAnyTheseBenefitsCYErrorKey).bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when parent answered they get carers allowance and on current page they select 'No' for non scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

      val location = Location.ENGLAND
      val carerAllowance = Map(
        LocationId.toString                      -> JsString(location.toString),
        DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
        DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
    }

    "return a Bad Request and errors when parent answered they get either carer’s allowance or carer support payment and " +
      "on current page they select 'No' for scottish users for scottish users" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

        val location = Location.SCOTLAND
        val scottishCarersAllowance = Map(
          LocationId.toString                      -> JsString(location.toString),
          DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
          DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
        )

        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, scottishCarersAllowance)))

        val result = controller(getRelevantData).onSubmit()(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.scottishCarers.allowance")
      }

    "return a Bad Request and errors when partner answered they get carers allowance and on current page they select 'No' for non scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

      val location = Location.ENGLAND
      val carerAllowance = Map(
        LocationId.toString                      -> JsString(location.toString),
        DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
        DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
    }

    "return a Bad Request and errors when partner answered they get either carer’s allowance or carer support payment " +
      "and on current page they select 'No' for scottish users for scottish users" in {

        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

        val location = Location.SCOTLAND
        val scottishCarersAllowance = Map(
          LocationId.toString                      -> JsString(location.toString),
          DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
          DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
        )

        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, scottishCarersAllowance)))

        val result = controller(getRelevantData).onSubmit()(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.scottishCarers.allowance")
      }

    "return a Bad Request and errors when parent and partner both answered they get carers allowance and on current " +
      "                                                               page they select 'No' for non scottish users" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

        val location = Location.ENGLAND
        val carerAllowance = Map(
          LocationId.toString                      -> JsString(location.toString),
          DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
          DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
        )

        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

        val result = controller(getRelevantData).onSubmit()(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
      }

    "return a Bad Request and errors when parent and partner both answered they get either carer's allowance or " +
      "carer support system and on current page they select 'No' for scottish users" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false")).withMethod("POST")

        val location = Location.SCOTLAND
        val scottishCarersAllowance = Map(
          LocationId.toString                      -> JsString(location.toString),
          DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
          DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
        )

        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, scottishCarersAllowance)))

        val result = controller(getRelevantData).onSubmit()(postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.scottishCarers.allowance")
      }

    "redirect to next page when parent or partner or both answered they get carers allowance and they select 'Yes' for non scottish users" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

      val location = Location.ENGLAND
      val carerAllowance = Map(
        LocationId.toString                      -> JsString(location.toString),
        DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
        DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to next page when parent or partner or both answered they get carer’s allowance or carer support payment " +
      "and they select 'Yes' for scottish users" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true")).withMethod("POST")

        val location = Location.SCOTLAND
        val scottishCarersAllowance = Map(
          LocationId.toString                      -> JsString(location.toString),
          DoYouGetAnyBenefitsId.toString           -> Json.toJson(Seq("CarersAllowance")),
          DoesYourPartnerGetAnyBenefitsId.toString -> Json.toJson(Seq("ContributionBasedEmploymentAndSupportAllowance"))
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
