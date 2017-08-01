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

import play.api.http.Status.{OK, SEE_OTHER}
import play.api.i18n.Messages.Implicits.applicationMessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.{CCRoutes, FakeCCApplication}
import uk.gov.hmrc.play.test.UnitSpec

class ChildCareBaseControllerSpec extends UnitSpec with FakeCCApplication with CCRoutes {

  val sut = new ChildCareBaseController(applicationMessagesApi)

  "ChildCareBaseController" should {

    "load home page" when {
      "request with session Id is received" in {
        val res: Result = await(sut.onPageLoad()(request.withSession(validSession)))
        status(res) shouldBe SEE_OTHER
        res.header.headers("Location") shouldBe whatYouNeedPath
      }
    }

    "load technical difficulties page" when {
      s"receive GET request on ${technicalDifficultiesPath}" in {
        val req = FakeRequest(GET, technicalDifficultiesPath)
        val result = route(app, req)
        result.isDefined shouldBe true
        status(result.get) shouldBe OK
      }
    }

  }
}
