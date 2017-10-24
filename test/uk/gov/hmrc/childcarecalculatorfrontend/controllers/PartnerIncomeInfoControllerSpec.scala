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
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerIncomeInfo
import uk.gov.hmrc.http.cache.client.CacheMap

class PartnerIncomeInfoControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PartnerIncomeInfoController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl)

  "PartnerIncomeInfo Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe partnerIncomeInfo(frontendAppConfig, Call("GET", "testurl"))(fakeRequest, messages).toString
    }

    "return OK for view and contains the link for partner any other paid work CY as next page when parent in paid employment and partner not working " in {

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString)
        )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe partnerIncomeInfo(frontendAppConfig, routes.PartnerPaidWorkCYController.onPageLoad(NormalMode))(fakeRequest, messages).toString
    }

    "return OK for view and contains the link for parent any other paid work CY as next page when partner in paid employment and parent not working " in {

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))


      val result = controller(getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe partnerIncomeInfo(frontendAppConfig, routes.ParentPaidWorkCYController.onPageLoad(NormalMode))(fakeRequest, messages).toString
    }

    "return OK for view and contains the link for Employment Income CY as next page when both are in paid work " in {

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))


      val result = controller(getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe partnerIncomeInfo(frontendAppConfig, routes.EmploymentIncomeCYController.onPageLoad(NormalMode))(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
