/*
 * Copyright 2019 HM Revenue & Customs
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
import play.Logger
import play.api.mvc.{ActionBuilder, ActionTransformer, AnyContent, BodyParser, MessagesControllerComponents, Request}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject()(val dataCacheConnector: DataCacheConnector,
                                        val mcc: MessagesControllerComponents,
                                        val appConfig: FrontendAppConfig)
                                       (implicit ec: ExecutionContext) extends DataRetrievalAction {

  override protected def executionContext: ExecutionContext = mcc.executionContext
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    if (appConfig.navigationAudit) {
      Logger.warn(s"ChildcareCalculatorNavigationAudit - sessionId : ${hc.sessionId.getOrElse("missing").toString}, request : ${request.uri}")
    }

    hc.sessionId match {
      case None => Future.failed(new IllegalStateException())
      case Some(sessionId) =>
        dataCacheConnector.fetch(sessionId.toString).map {
          case None       => OptionalDataRequest(request, sessionId.toString, None)
          case Some(data) => OptionalDataRequest(request, sessionId.toString, Some(new UserAnswers(data)))
        }
    }
  }
}

@ImplementedBy(classOf[DataRetrievalActionImpl])
trait DataRetrievalAction extends ActionTransformer[Request, OptionalDataRequest] with ActionBuilder[OptionalDataRequest, AnyContent]