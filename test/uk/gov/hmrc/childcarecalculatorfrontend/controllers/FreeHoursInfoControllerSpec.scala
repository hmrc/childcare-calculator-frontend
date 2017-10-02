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

import play.api.libs.json.{JsBoolean, JsString}
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildAgedTwoId, LocationId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo

class FreeHoursInfoControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new FreeHoursInfoController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl)

  "FreeHoursInfo Controller" must {

    Seq("England", "Wales", "Scotland").foreach { location =>
      s"return OK with childAgedTwo as true and location $location and the correct view for a GET" in {
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(true), LocationId.toString -> JsString(location))
        val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe freeHoursInfo(frontendAppConfig, isChildAgedTwo = true, location)(fakeRequest, messages).toString
      }
    }

    Seq("England", "Wales", "Scotland").foreach { location =>
      s"return OK with childAgedTwo as false, location $location and the correct view for a GET" in {
        val validData = Map(ChildAgedTwoId.toString -> JsBoolean(false), LocationId.toString -> JsString(location))
        val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe freeHoursInfo(frontendAppConfig, isChildAgedTwo = false, location)(fakeRequest, messages).toString
      }
    }

    "return OK with childAgedTwo as false, location Northern Ireland and the correct view for a GET" in {
      val location = "Northern Ireland"
      val validData = Map(LocationId.toString -> JsString(location))
      val childAgedTwoData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedTwoData).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe freeHoursInfo(frontendAppConfig, isChildAgedTwo = false, location)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Location on a GET when previous data exists but the location hasn't been answered" in {
      val result = controller(getEmptyCacheMap).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad(NormalMode).url)
    }

  }
}




