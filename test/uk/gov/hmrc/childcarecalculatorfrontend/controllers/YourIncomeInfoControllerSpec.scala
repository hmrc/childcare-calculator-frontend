/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourIncomeInfo

import scala.concurrent.ExecutionContext.Implicits.global

class YourIncomeInfoControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[yourIncomeInfo]
  val taxYearInfo = new TaxYearInfo

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new YourIncomeInfoController(frontendAppConfig, mcc, dataRetrievalAction, new DataRequiredAction, taxYearInfo, view)

  "YourIncomeInfo Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, taxYearInfo)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
