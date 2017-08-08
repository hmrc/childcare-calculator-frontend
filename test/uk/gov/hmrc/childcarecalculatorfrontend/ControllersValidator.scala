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

package uk.gov.hmrc.childcarecalculatorfrontend

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec

trait ControllersValidator extends UnitSpec with FakeCCApplication {

  def validateUrl(path: String, requestTypes: List[String] = List(GET, POST)) = {

    s"${path}" should {
      "be available" when {
        requestTypes.foreach { requestType =>
          s"${requestType} request is made" in {
            val req = FakeRequest(requestType, path).withSession(validSession)
            val result = route(app, req)
            result.isDefined shouldBe true
            status(result.get) should not be NOT_FOUND
          }
        }
      }
    }

  }

}
