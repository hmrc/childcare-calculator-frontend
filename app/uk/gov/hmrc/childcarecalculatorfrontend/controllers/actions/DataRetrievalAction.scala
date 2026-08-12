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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions

import com.google.inject.{ImplementedBy, Inject}
import play.api.Logging
import play.api.mvc.*
import uk.gov.hmrc.childcarecalculatorfrontend.config.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.{OptionalDataRequest, SessionIdProvider}
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRetrievalActionImpl @Inject() (
    val dataCacheService: DataCacheService,
    val mcc: MessagesControllerComponents,
    val appConfig: FrontendAppConfig
)(using ec: ExecutionContext)
    extends DataRetrievalAction
    with Logging {

  override protected def executionContext: ExecutionContext = mcc.executionContext
  override def parser: BodyParser[AnyContent]               = mcc.parsers.defaultBodyParser

  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = {
    given hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    if (appConfig.navigationAudit) {
      logger.warn(
        s"ChildcareCalculatorNavigationAudit - sessionId : ${hc.sessionId.getOrElse("missing").toString}, request : ${request.uri}"
      )
    }

    hc.sessionId match {
      case None => Future.failed(new IllegalStateException())
      case Some(sessionId) =>
        dataCacheService.fetch()(using sessionIdProvider(sessionId)).map {
          case None => OptionalDataRequest(request, sessionId.toString, None)
          case Some(data) =>
            OptionalDataRequest(request, sessionId.toString, Some(new UserAnswers(data)))
        }
    }
  }

  private def sessionIdProvider(sessionIdValue: SessionId): SessionIdProvider = new SessionIdProvider {
    override val sessionId: String = sessionIdValue.toString
  }

}

@ImplementedBy(classOf[DataRetrievalActionImpl])
trait DataRetrievalAction
    extends ActionTransformer[Request, OptionalDataRequest]
    with ActionBuilder[OptionalDataRequest, AnyContent]
