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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsBoolean, JsNumber, JsString}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{EmploymentSupportedChildcare, TaxCredits, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo



class MaxFreeHoursInfoControllerSpec extends ControllerSpecBase with MockitoSugar{

  val view = application.injector.instanceOf[maxFreeHoursInfo]
  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  val tfc = mock[TaxFreeChildcare]
  val tc = mock[TaxCredits]
  val esc = mock[EmploymentSupportedChildcare]
  val userAnswers = mock[UserAnswers]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new MaxFreeHoursInfoController(frontendAppConfig,
      mcc,
      dataRetrievalAction,
      new DataRequiredAction,
      tfc,
      tc,
      esc,
      view)

  "MaxFreeHoursInfo Controller" must {

    "return OK and tax free childcare in the correct view for a GET  " in {

      when(tfc.eligibility(any())) thenReturn Eligible
      when(tc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn NotEligible

      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK when single claim and show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn Eligible

      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe OK
    }

    "return OK when joint claim and partner works and can get vouchers show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn Eligible

      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK when joint claim and both work and both get vouchers show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn Eligible

      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK when single claim working 18hrs a week and not getting any benefits" +
      " show tax credits and the correct view for a GET " in {
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn Eligible
      when(esc.eligibility(any())) thenReturn NotEligible


      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(false),
        AreYouInPaidWorkId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false),
        ParentWorkHoursId.toString ->JsNumber(18),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString))

      val taxCreditsInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(taxCreditsInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK when joint claim and both working 16hrs a week and not getting any benefits" +
      " show tax credits and the correct view for a GET " in {
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn Eligible
      when(esc.eligibility(any())) thenReturn NotEligible


      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        AreYouInPaidWorkId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false),
        ParentWorkHoursId.toString ->JsNumber(16),
        PartnerWorkHoursId.toString -> JsNumber(16),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString))
      val taxCreditsInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(taxCreditsInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
    }


    "return OK when joint claim and parent working 16hrs a week and partner getting qualifying benefits" +
      " show tax credits and the correct view for a GET " in {
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn Eligible
      when(esc.eligibility(any())) thenReturn NotEligible

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        AreYouInPaidWorkId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false),
        ParentWorkHoursId.toString ->JsNumber(16),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString))
      val taxCreditsInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(taxCreditsInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK when joint claim and parent works less than 10hrs a week, partner is working 20hrs a week " +
      " show tax credits and the correct view for a GET " in {
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn Eligible
      when(esc.eligibility(any())) thenReturn NotEligible

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        AreYouInPaidWorkId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(false),
        ParentWorkHoursId.toString ->JsNumber(10),
        PartnerWorkHoursId.toString -> JsNumber(20),
        DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString))

      val taxCreditsInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(taxCreditsInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
    }

    "return OK and the correct view for a GET" in {
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(tc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn NotEligible

      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe OK
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
