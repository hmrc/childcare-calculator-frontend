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

package uk.gov.hmrc.childcarecalculatorfrontend.connectors

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.childcarecalculatorfrontend.config.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeResults
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.claimant.Claimant
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import scala.concurrent.{ExecutionContext, Future}

class EligiblityConnectorSpec extends PlaySpec with MockitoSugar with ScalaFutures {
  val mockHttp: HttpClientV2               = mock[HttpClientV2]
  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  given request: Request[AnyContent]       = FakeRequest()
  given hc: HeaderCarrier                  = HeaderCarrier()
  given ec: ExecutionContext               = ExecutionContext.global

  def mockConnector: EligibilityConnector = new EligibilityConnector(frontendAppConfig, mockHttp)

  "Eligibility Connector" must {

    "get eligibility result" in {
      val schemesResult      = SchemeResults(schemes = Nil)
      val testRequestBuilder = mock[RequestBuilder]

      when(
        frontendAppConfig.eligibilityUrl
      ).thenReturn("http://localhost:9000/test")

      when(
        mockHttp.post(any())(using any())
      ).thenReturn(testRequestBuilder)

      when(
        testRequestBuilder
          .withBody[Household](any())(using any(), any(), any())
      ).thenReturn(testRequestBuilder)

      when(
        testRequestBuilder
          .execute[SchemeResults](any(), any())
      ).thenReturn(Future.successful(schemesResult))

      val res = mockConnector.getEligibility(
        Household(
          credits = None,
          location = Location.England,
          children = List.empty,
          parent = Claimant(),
          partner = None
        )
      )

      whenReady(res)(value => value mustBe schemesResult)

    }
  }

}
