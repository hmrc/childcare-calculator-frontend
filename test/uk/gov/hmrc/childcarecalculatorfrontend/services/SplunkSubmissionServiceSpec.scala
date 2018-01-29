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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.mockito.{ArgumentCaptor, Matchers}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector

import scala.concurrent.{ExecutionContext, Future}

class SplunkSubmissionServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures {

  implicit val hc = new HeaderCarrier

  private val mockConnector = mock[DefaultAuditConnector]

  val data = Map("key 1" -> "value 1", "key 2" -> "value 2")

  implicit val hcCaptor = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val ecCaptor = ArgumentCaptor.forClass(classOf[ExecutionContext])

  "SplunkSubmissionService" must {

    "receive SubmissionSuccessful when audit event passes" in {

      val eventCaptor = ArgumentCaptor.forClass(classOf[DataEvent])

      when(mockConnector.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.successful(AuditResult.Success))

      val submissionService = new SplunkSubmissionService(mockConnector)

      val submission = submissionService.submit(data)

      verify(mockConnector).sendEvent(eventCaptor.capture)(hcCaptor.capture, ecCaptor.capture)

      eventCaptor.getValue.detail.get("key 1") mustBe Some("value 1")
      eventCaptor.getValue.detail.get("key 2") mustBe Some("value 2")
      eventCaptor.getValue.tags must contain {
        "transactionName" -> "Childcare Calculator Submission Service"
        "path" -> "/survey/childcare-support"
      }

      whenReady(submission) {
        x =>
          x mustBe SubmissionSuccessful
      }

    }

    "receive SubmissionFailed when audit event fails" in {

      when(mockConnector.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.successful(AuditResult.Failure("")))

      val submission = new SplunkSubmissionService(mockConnector)

      whenReady(submission.submit(data)) {
        x =>
          x mustBe SubmissionFailed
      }

    }

  }

}