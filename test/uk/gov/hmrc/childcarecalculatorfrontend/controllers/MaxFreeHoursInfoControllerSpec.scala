/*
 * Copyright 2017 HM Revenue & Customs
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

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsBoolean, JsString}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxFreeChildcare
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.http.cache.client.CacheMap

class MaxFreeHoursInfoControllerSpec extends ControllerSpecBase with MockitoSugar{

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad()

  val tfc = mock[TaxFreeChildcare]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new MaxFreeHoursInfoController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl, tfc)



  "MaxFreeHoursInfo Controller" must {

    "return OK and tax free childcare in the correct view for a GET  " in {

      when(tfc.eligibility(any())) thenReturn Eligible
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, Eligible, false, false)(fakeRequest, messages).toString()
    }

    "return OK when single claim and show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString),
                          DoYouLiveWithPartnerId.toString -> JsBoolean(false),
                          PaidEmploymentId.toString -> JsBoolean(true),
                          YourChildcareVouchersId.toString -> JsString(YesNoUnsureEnum.YES.toString))

      val childCareVouchersInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childCareVouchersInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, NotEligible, true, false)(fakeRequest, messages).toString

    }

    "return OK when joint claim and partner works and can get vouchers show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString),

        PartnerChildcareVouchersId.toString -> JsString(YesNoUnsureEnum.YES.toString))

      val childCareVouchersInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childCareVouchersInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, NotEligible, true, false)(fakeRequest, messages).toString

    }

    "return OK when joint claim and both work and both get vouchers show childcare vouchers in the correct view for a GET " in {

      when(tfc.eligibility(any())) thenReturn NotEligible
      val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString),
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString),
        WhoGetsVouchersId.toString -> JsString(YouPartnerBothEnum.BOTH.toString))

      val childCareVouchersInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childCareVouchersInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, NotEligible, true, false)(fakeRequest, messages).toString

    }

    "return OK and tax credits in the correct view for a GET " ignore {
      when(tfc.eligibility(any())) thenReturn NotEligible
      val validData = Map(PaidEmploymentId.toString -> JsBoolean(true),
        DoYouGetAnyBenefitsId.toString -> JsBoolean(true),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString))
      val taxCreditsInfo = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(taxCreditsInfo).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, NotEligible, false, true)(fakeRequest, messages).toString
    }



    "return OK and the correct view for a GET" ignore {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe maxFreeHoursInfo(frontendAppConfig, NotEligible, false, false)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
