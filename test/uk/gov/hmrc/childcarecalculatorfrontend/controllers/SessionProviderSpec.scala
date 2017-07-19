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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import org.scalatest.mock.MockitoSugar
import play.api.http.HttpEntity
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class SessionProviderSpec extends UnitSpec with MockitoSugar with FakeCCApplication {

  class SUT extends SessionProvider

  val sut = new SUT

  "create session" when {
    "no session Id exist" in {

      def df(x: Request[AnyContent]) = mock[Future[Result]]

      val res: Result = await(sut.withSession(df)(request))
      res.session.get(SessionKeys.sessionId).isDefined shouldBe true
      status(res) shouldBe SEE_OTHER
    }
  }

  "do not create session" when {
    "session Id exist" in {

      def df(x: Request[AnyContent]) = Future.successful(Result.apply(ResponseHeader(200), HttpEntity.NoEntity).
        withSession(x.session))

      val res: Result = await(sut.withSession(df)(request.withSession(SessionKeys.sessionId -> "12345")))
      res.session.get(SessionKeys.sessionId).get shouldBe "12345"
      status(res) shouldBe OK
    }
  }

}
