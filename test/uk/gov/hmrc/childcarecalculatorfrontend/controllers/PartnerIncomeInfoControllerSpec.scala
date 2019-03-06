/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerIncomeInfo
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

class PartnerIncomeInfoControllerSpec extends ControllerSpecBase {

  val taxYearInfo = new TaxYearInfo

  def onwardRoute = routes.PartnerPaidWorkCYController.onPageLoad(NormalMode)

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PartnerIncomeInfoController(frontendAppConfig,
      mcc,
      dataRetrievalAction,
      new FakeNavigator(onwardRoute),
      new DataRequiredAction,
      taxYearInfo)

  "PartnerIncomeInfo Controller" must {
    "return OK and the correct view for a GET" in {

      val validData = Map(
        DoYouLiveWithPartnerId.toString -> JsBoolean(true),
        WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString)
      )

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe
        partnerIncomeInfo(frontendAppConfig, routes.PartnerPaidWorkCYController.onPageLoad(NormalMode), taxYearInfo)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
