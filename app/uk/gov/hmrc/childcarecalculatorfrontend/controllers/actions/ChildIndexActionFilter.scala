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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions

import com.google.inject.Inject
import play.api.http.{HttpErrorHandler, Status}
import play.api.mvc.{ActionFilter, Result}
import play.api.mvc.Results._
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes.SessionExpiredController

import scala.concurrent.{ExecutionContext, Future}

class ChildIndexActionFilter(childIndex: Int, errorHandler: HttpErrorHandler, implicit val ec: ExecutionContext) extends ActionFilter[DataRequest] {

  private def badRequest[A](request: DataRequest[A]): Future[Option[Result]] = {
    // TODO logging?
    errorHandler.onClientError(request, Status.BAD_REQUEST, "error.childIndex").map(Some.apply)
  }

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    request.userAnswers.noOfChildren.map {
      numberOfChildren =>
        if (childIndex > numberOfChildren - 1 || childIndex < 0) {
          badRequest(request)
        } else {
          Future.successful(None)
        }
    }.getOrElse(Future.successful(Some(Redirect(SessionExpiredController.onPageLoad()))))
}

class ChildIndexActionFilterFactory @Inject() (
                                                errorHandler: HttpErrorHandler,
                                                ec: ExecutionContext
                                              ) {
  def apply(childIndex: Int): ChildIndexActionFilter =
    new ChildIndexActionFilter(childIndex, errorHandler, ec)
}