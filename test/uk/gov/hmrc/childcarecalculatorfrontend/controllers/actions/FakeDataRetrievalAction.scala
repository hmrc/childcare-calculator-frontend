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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions

import org.joda.time.LocalDate
import play.api.Application
import play.api.mvc.{Request, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(cacheMapToReturn: Option[CacheMap], timeReplacement: Option[LocalDate] = None)
                             (implicit app: Application) extends DataRetrievalAction {

  override def executionContext: ExecutionContext = global
  override def parser: BodyParser[AnyContent]     = app.injector.instanceOf[MessagesControllerComponents].parsers.defaultBodyParser

  override protected def transform[A](request: Request[A]): Future[OptionalDataRequest[A]] = {
    val userAnswers: Option[UserAnswers] = cacheMapToReturn map {
      new UserAnswers(_) {
        override def now: LocalDate = timeReplacement.getOrElse(LocalDate.now())
      }
    }

    Future(OptionalDataRequest(request, "id", userAnswers))
  }
}