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

import com.google.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.bootstrap.audit.DefaultAuditConnector
import uk.gov.hmrc.play.audit.AuditExtensions._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed
trait SubmissionStatus

object SubmissionSuccessful extends SubmissionStatus
object SubmissionFailed extends SubmissionStatus

sealed
class SplunkSubmissionEvent @Inject() (data: Map[String, String])(implicit hc: HeaderCarrier)
  extends DataEvent(
    auditSource = "childcare-calc",
    auditType = "feedback-survey",
    tags = hc.toAuditTags("Childcare Calculator Submission Service", "/survey/childcare-support"),
    detail = hc.toAuditDetails(data.toSeq: _*))

sealed
trait SplunkSubmissionServiceInterface {
  def submit(data: Map[String, String]): Future[SubmissionStatus]
}

class SplunkSubmissionService @Inject() (http: DefaultAuditConnector)(implicit hc: HeaderCarrier) extends SplunkSubmissionServiceInterface {

  def submit(data: Map[String, String]): Future[SubmissionStatus] = {
    
    val dataEvent = new SplunkSubmissionEvent(data)

    http.sendEvent(dataEvent).map {
      case AuditResult.Success => SubmissionSuccessful
      case _ => SubmissionFailed
    }

  }

}