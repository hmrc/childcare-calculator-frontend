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

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo

class FreeHoursInfoControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private val view = application.injector.instanceOf[freeHoursInfo]

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new FreeHoursInfoController(mcc, dataRetrievalAction, new DataRequiredAction, view)

  "FreeHoursInfo Controller" when {

    Seq(ENGLAND, WALES, SCOTLAND, NORTHERN_IRELAND).foreach { location =>
      s"location is $location" must {
        "return OK containing freeHoursInfo view" in {
          val cacheData = Map(LocationId.toString -> JsString(location.toString))
          val dataRetrievalAction = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, cacheData)))

          val result = controller(dataRetrievalAction).onPageLoad(fakeRequest)

          status(result) mustBe OK
          contentAsString(result) mustBe view(location)(fakeRequest, messages).toString
        }
      }
    }

    "location is empty" must {
      "redirect to LocationController" in {
        val result = controller().onPageLoad(fakeRequest)

        status(result) mustBe SEE_OTHER
      }
    }
    "return OK with childcare vouchers and tfc when we have childcare costs and they are approved" in {
      val location = ENGLAND
      val validData = Map(ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(false, false, true, true, location)(fakeRequest, messages).toString
    }

    "return OK with no childcare vouchers and tfc when we don't have childcare costs" when {
      "we don't have childcare costs" in {
        val location = ENGLAND
        val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(false, false, false, false, location)(fakeRequest, messages).toString
      }

      "we have childcare costs but they are not approved" in {
        val location = ENGLAND
        val validData = Map(ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NO.toString), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString), LocationId.toString -> JsString(location.toString))
        val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
        val result = controller(childAgedFour).onPageLoad(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(false, false, true, false, location)(fakeRequest, messages).toString
      }
    }

    "return OK with no childcare costs paragraph if they have no childcare costs" in {
      val location = ENGLAND
      val validData = Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(false, false, false, false, location)(fakeRequest, messages).toString
    }

    "return OK with no approved childcare paragraph when they have childcare costs but not approved" in {
      val location = ENGLAND
      val validData = Map(ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NO.toString), ChildAgedThreeOrFourId.toString -> JsBoolean(true), ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString), LocationId.toString -> JsString(location.toString))
      val childAgedFour = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))
      val result = controller(childAgedFour).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe view(false, true, true, false, location)(fakeRequest, messages).toString
    }

    "redirect to Location on a GET when previous data exists but the location hasn't been answered" in {
      val result = controller(getEmptyCacheMap).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad(NormalMode).url)
    }
  }

}
