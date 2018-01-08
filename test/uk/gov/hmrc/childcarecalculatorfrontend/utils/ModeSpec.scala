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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.scalatestplus.play.FakeApplicationFactory
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase


class ModeSpec extends SpecBase with FakeApplicationFactory {

  "Mode" should {

    "return 400 if mode isn't supported" in {

      val requestR = FakeRequest("GET", "/childcare-calc/location?mode=CheckMode")
      val request = route(app, requestR).get
      status(request) mustBe BAD_REQUEST
      contentAsString(request) must include(messages("global.error.InternalServerError500.heading"))
    }

    "return 200 if mode is valid" in {

      val requestR = FakeRequest("GET", "/childcare-calc/location")
      val request = route(app, requestR).get
      status(request) mustBe OK
    }

  }

}
