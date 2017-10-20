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

import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqTo, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.http.HttpErrorHandler
import play.api.mvc.{AnyContent, Result, Results}
import play.api.test.FakeRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

import scala.concurrent.Future

class ChildIndexActionFilterSpec extends WordSpec with MustMatchers with ScalaFutures with OptionValues with MockitoSugar {

  import scala.concurrent.ExecutionContext.global

  class TestFilter(index: Int, errorHandler: HttpErrorHandler) extends ChildIndexActionFilter(index, errorHandler, global) {
    def callFilter[A](request: DataRequest[A]): Future[Option[Result]] =
      filter(request)
  }

  def instance(index: Int): TestFilter = {
    val errorHandler: HttpErrorHandler = mock[HttpErrorHandler]
    when(errorHandler.onClientError(any(), eqTo(400), eqTo("error.childIndex")))
      .thenReturn(Future.successful(Results.BadRequest))
    new TestFilter(index, errorHandler)
  }

  def request(numberOfChildren: Option[Int]): DataRequest[AnyContent] = {
    val answers = mock[UserAnswers]
    when(answers.noOfChildren).thenReturn(numberOfChildren)
    DataRequest(FakeRequest(), "", answers)
  }

  ".filter" must {

    "return `None` when index is valid" in {
      whenReady(instance(0).callFilter(request(Some(10)))) {
        _ mustNot be(defined)
      }
    }

    "return `Some` when index is negative" in {
      whenReady(instance(-1).callFilter(request(Some(10)))) {
        _ must be(defined)
      }
    }

    "return `Some` when index is out of bounds" in {
      whenReady(instance(10).callFilter(request(Some(10)))) {
        _ must be(defined)
      }
    }
  }
}
