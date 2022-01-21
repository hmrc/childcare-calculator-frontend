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

import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}

import scala.concurrent.ExecutionContext.Implicits.global

class SessionManagementControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): SessionManagementController =
    new SessionManagementController(frontendAppConfig,
      mcc,
      FakeDataCacheConnector,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction)

  "SessionManagement Controller" must {
    "return 200 for a GET" in {
      val result = controller().extendSession()(fakeRequest)
      status(result) mustBe OK
    }
  }

  "redirect to the next page when valid data is submitted" in {
    val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "sessionData"))
    val result = controller().clearSessionData()(postRequest)

    status(result) mustBe SEE_OTHER
    redirectLocation(result) mustBe Some(onwardRoute.url)
  }
}
