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

import play.api.libs.json.{JsBoolean, JsString}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

class FreeHoursInfoControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[freeHoursInfo]
  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new FreeHoursInfoController(frontendAppConfig, mcc, dataRetrievalAction, new DataRequiredAction, view)

  "FreeHoursInfo Controller" must {

    Seq(ENGLAND, WALES, SCOTLAND).foreach { location =>
      s"return OK with childAgedTwo as true and location $location and the correct view for a GET" in {
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(true), LocationId.toString -> JsString(location.toString))
        val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = true,false,false,false, location)(fakeRequest, messages).toString
      }
    }

    Seq(ENGLAND, WALES, SCOTLAND).foreach { location =>
      s"return OK with childAgedTwo as false, location $location and the correct view for a GET" in {
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(false), LocationId.toString -> JsString(location.toString))
        val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = false,false,false,false, location)(fakeRequest, messages).toString
      }
    }

    "return OK with childAgedTwo as false, location Northern Ireland and the correct view for a GET" in {
      val location = NORTHERN_IRELAND
      val validData = Map(LocationId.toString -> JsString(location.toString))
      val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = false,false,false,false, location)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }

    "return OK with no free hours for 2 year old" when {
      "we live in Northern Ireland" in {
        val location = NORTHERN_IRELAND
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(true), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = true,false,false,false, location)(fakeRequest, messages).toString
      }

      "we don't have a 2 year old" in {
        val location = SCOTLAND
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(false), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = false,false,false,false, location)(fakeRequest, messages).toString
      }
    }
    
    "return OK with free hours for 2 year old info if they have a 2 year old and live in England, Scotland or Wales" in {
      val location = SCOTLAND
      val validData = Map(ChildAgedTwoId.toString -> JsBoolean(true), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, isChildAgedTwo = true,false,false,false, location)(fakeRequest, messages).toString
    }

    "return OK with 30 hours" when {
      "They live in ENGLAND, don't have childcare costs and have 3 or 4 year old child" in {
        val location = ENGLAND
        val validData = Map(ChildAgedThreeOrFourId.toString -> JsBoolean(true), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig, false, true, false,false, location, true)(fakeRequest, messages).toString
      }

      "They have childcare costs and they are approved" in {
        val location = ENGLAND
        val validData = Map(ApprovedProviderId.toString-> JsString(YesNoUnsureEnum.YES.toString), ChildAgedThreeOrFourId.toString -> JsBoolean(true), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,true,true,true,location)(fakeRequest, messages).toString
      }

      "They are not sure about having childcare costs or them being by an approved provider" in {
        val location = ENGLAND
        val validData = Map(ApprovedProviderId.toString-> JsString(YesNoUnsureEnum.NOTSURE.toString), ChildAgedThreeOrFourId.toString -> JsBoolean(true), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,true,true,true,location)(fakeRequest, messages).toString
      }
    }

    "return OK with no 30 hours" when {
      "we don't live in ENGLAND" in {
        val location = NORTHERN_IRELAND
        val validData = Map(ChildAgedThreeOrFourId.toString -> JsBoolean(true),ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,true,false,false, location)(fakeRequest, messages).toString
      }

      "we don't have a 3 or 4 year old" in {
        val location = ENGLAND
        val validData = Map(ChildAgedThreeOrFourId.toString -> JsBoolean(false),ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,false,false,false, location)(fakeRequest, messages).toString
      }
    }

    "return OK with childcare vouchers, tfc and tc when we have childcare costs and they are approved" in {
      val location = ENGLAND
      val validData = Map(ApprovedProviderId.toString-> JsString(YesNoUnsureEnum.NOTSURE.toString),ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig,false,false,true,true, location)(fakeRequest, messages).toString
    }

    "return OK with no childcare vouchers, tfc and tc when we don't have childcare costs" when {
      "we don't have childcare costs" in {
        val location = ENGLAND
        val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,false,false,false, location)(fakeRequest, messages).toString
      }

      "we have childcare costs but they are not approved" in {
        val location = ENGLAND
        val validData = Map(ApprovedProviderId.toString-> JsString(YesNoUnsureEnum.NO.toString),ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(frontendAppConfig,false,false,true,false, location)(fakeRequest, messages).toString
      }
    }

    "return OK with no childcare costs paragraph if they have no childcare costs" in {
      val location = ENGLAND
      val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig,false,false,false,false, location)(fakeRequest, messages).toString
    }

    "return OK with no approved childcare paragraph when they have childcare costs but not approved" in {
      val location = ENGLAND
      val validData = Map(ApprovedProviderId.toString-> JsString(YesNoUnsureEnum.NO.toString), ChildAgedThreeOrFourId.toString -> JsBoolean(true), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, false, true, true, false,location, true)(fakeRequest, messages).toString
    }

    "redirect to Location on a GET when previous data exists but the location hasn't been answered" in {
      val result = controller(getEmptyCacheMap).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad(NormalMode).url)
    }
  }
}
