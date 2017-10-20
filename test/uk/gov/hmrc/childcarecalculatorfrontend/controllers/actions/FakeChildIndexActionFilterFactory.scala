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
import play.api.mvc.Result
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.global

case object FakeChildIndexActionFilterFactory extends ChildIndexActionFilterFactory(null, global) {
  override def apply(childIndex: Int): ChildIndexActionFilter = FakeChildIndexActionFilter
}

case object FakeChildIndexActionFilter extends ChildIndexActionFilter(0, null, global) {
  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    Future.successful(None)
}
