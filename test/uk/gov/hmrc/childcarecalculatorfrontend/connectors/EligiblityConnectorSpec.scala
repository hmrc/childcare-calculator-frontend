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

package uk.gov.hmrc.childcarecalculatorfrontend.connectors

import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.config.WSHttp
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, Household, SchemesResult}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class EligiblityConnectorSpec extends UnitSpec with MockitoSugar with FakeCCApplication {
  val mockHttp = mock[WSHttp]
  
  val mockConnector = new EligibilityConnector {
    override def httpPost: WSHttp = mockHttp
  }

  "Eligibility Connector" should {

    "have httpPost" in {
      EligibilityConnector.httpPost.isInstanceOf[WSHttp] shouldBe true
    }

    "get eligibility result" in {

      val schemesResult = mock[SchemesResult]
      when(
        mockConnector.httpPost.POST[Household, SchemesResult](anyString(), any(),any())(any(),any(), any())
      ).thenReturn(Future.successful(schemesResult))

      val res = await(mockConnector.getEligibility(Household(children = Nil, parent = Claimant(), partner = None)))
      res shouldBe schemesResult
    }
  }
}
