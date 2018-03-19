/*
 * Copyright 2018 HM Revenue & Customs
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

import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.{DataCacheConnector, FakeDataCacheConnector}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourResults
import org.mockito.Matchers.any
import org.mockito.Mockito._
import services.{MoreInfoService, MoreInfoServiceInterface}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AboutYourResultsControllerSpec extends ControllerSpecBase with MockitoSugar{

  val mockDataConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockMoreInfoService: MoreInfoServiceInterface = mock[MoreInfoService]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): AboutYourResultsController =
    new AboutYourResultsController(frontendAppConfig,
      messagesApi,
      mockDataConnector,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      mockMoreInfoService)

  "AboutYourResults Controller" must {
    "return OK and the correct view for a GET" in {
      val model = ResultsViewModel(location = Location.ENGLAND, hasChildcareCosts = true)
      when(mockDataConnector.getEntry[ResultsViewModel](any(), any())(any())) thenReturn Future(Some(model))
      when(mockMoreInfoService.getSchemeContent(any(), any())) thenReturn List.empty
      when(mockMoreInfoService.getSummary(any(), any())) thenReturn None

      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe aboutYourResults(frontendAppConfig, model, List.empty, None)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET" when {
      "no existing data is found" in {
        val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }

      "view model is returned as None" in {
        when(mockDataConnector.getEntry[ResultsViewModel](any(), any())(any())) thenReturn Future(None)
        val result = controller().onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }

      "something goes wrong while fetching view model" in {
        when(mockDataConnector.getEntry[ResultsViewModel](any(), any())(any())) thenReturn Future.failed(new RuntimeException)
        val result = controller().onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }
    }
  }
}
