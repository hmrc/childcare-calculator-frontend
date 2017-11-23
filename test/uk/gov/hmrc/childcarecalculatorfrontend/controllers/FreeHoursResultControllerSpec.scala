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
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.FreeHours
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.http.cache.client.CacheMap

class FreeHoursResultControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new FreeHoursResultController(frontendAppConfig, messagesApi, dataRetrievalAction, new DataRequiredActionImpl, new Utils, new FreeHours)

 "FreeHoursResult Controller" must {
   "return 200 and the correct view for a GET when data exists in UserAnswers" in {
      val validData = Map(
        LocationId.toString -> JsString("england"),
        ChildAgedTwoId.toString -> JsBoolean(false),
        ChildAgedThreeOrFourId.toString -> JsBoolean(true),
        ChildcareCostsId.toString -> JsString("no"))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

   "throw exception if location is not found in UserAnswers" in {
     val validData = Map(ChildAgedTwoId.toString -> JsBoolean(false))
     val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

     intercept[RuntimeException] {
       await(controller(getRelevantData).onPageLoad()(fakeRequest))
     }
   }
  }

}
