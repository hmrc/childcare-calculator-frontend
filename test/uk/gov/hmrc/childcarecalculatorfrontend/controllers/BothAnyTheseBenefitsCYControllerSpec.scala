/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{BothAnyTheseBenefitsCYId, WhichBenefitsPartnerGetId, WhichBenefitsYouGetId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothAnyTheseBenefitsCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext.Implicits.global

class BothAnyTheseBenefitsCYControllerSpec extends ControllerSpecBase {

  val taxYearInfo = new TaxYearInfo

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new BothAnyTheseBenefitsCYController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, taxYearInfo)

  def viewAsString(form: Form[Boolean] = BooleanForm()): String = bothAnyTheseBenefitsCY(frontendAppConfig,
                                                                                  form,
                                                                                  NormalMode,
                                                                                  taxYearInfo)(fakeRequest, messages).toString

  "BothAnyTheseBenefitsCY Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(BothAnyTheseBenefitsCYId.toString -> JsBoolean(true))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(BooleanForm().fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = BooleanForm(bothAnyTheseBenefitsCYErrorKey).bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return a Bad Request and errors when parent answered they get carers allowance and on current page they select 'No'" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false"))

      val carerAllowance = Map(WhichBenefitsYouGetId.toString -> Json.toJson(Seq("carersAllowance")),
        WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq("incomeBenefits")))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
    }

    "return a Bad Request and errors when partner answered they get carers allowance and on current page they select 'No'" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false"))

      val carerAllowance = Map(WhichBenefitsYouGetId.toString -> Json.toJson(Seq("incomeBenefits")),
        WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq("carersAllowance")))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
    }

    "return a Bad Request and errors when parent and partner both answered they get carers allowance and on current page they select 'No'" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "false"))

      val carerAllowance = Map(WhichBenefitsYouGetId.toString -> Json.toJson(Seq("carersAllowance")),
        WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq("carersAllowance")))


      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) contains messages("bothAnyTheseBenefitsCY.error.carers.allowance")
    }

    "redirect to next page when parent or partner or both answered they get carers allowance and they select 'Yes'" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val carerAllowance = Map(WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq("carersAllowance")),
        WhichBenefitsPartnerGetId.toString -> Json.toJson(Seq("incomeBenefits")))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, carerAllowance)))

      val result = controller(getRelevantData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}




