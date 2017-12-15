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
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.services.ResultsService
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ResultControllerSpec extends ControllerSpecBase with MockitoSugar{

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 resultService: ResultsService): ResultController =
    new ResultController(frontendAppConfig,
      messagesApi,
      dataRetrievalAction,
      new DataRequiredActionImpl,
      resultService)

  "Result Controller" must {
    "return OK and with ResultViewModel for a GET" in {
      when(resultService.getResultsViewModel(any())(any(),any(),any())) thenReturn Future.successful(
        ResultsViewModel(freeHours = Some(15), tc = Some(500), tfc = Some(600), esc = Some(1000)))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map())))
      val resultPage = controller(getRelevantData, resultService).onPageLoad()(fakeRequest)
      status(resultPage) mustBe OK
      contentAsString(resultPage) must include("15")
      contentAsString(resultPage) must include("500")
      contentAsString(resultPage) must include("600")
      contentAsString(resultPage) must include("1000")
    }

    "redirect to Session Expired" when {
      "we do a GET and no data is found" in {
        val result = controller(dontGetAnyData, resultService).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }

      "we try to get the view model and something goes wrong" in {
        when(resultService.getResultsViewModel(any())(any(),any(),any())) thenReturn Future.failed(new RuntimeException)

        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, Map())))
        val result = controller(getRelevantData, resultService).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }
    }
  }

  val resultService: ResultsService = mock[ResultsService]
}
