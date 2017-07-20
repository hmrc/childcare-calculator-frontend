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

import akka.stream.Materializer
import org.scalatest.Suite
import org.scalatestplus.play.OneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{SessionKeys, HeaderCarrier}
import play.api.inject._
import org.scalatest.mock.MockitoSugar

trait FakeCCApplication extends OneAppPerSuite with MockitoSugar {
  this: Suite =>

  val config: Map[String, _] = Map()

  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config)
    .overrides(bind[com.kenshoo.play.metrics.PlayModule].to(mock[com.kenshoo.play.metrics.PlayModule]))
    .build()

  implicit lazy val materializer: Materializer = app.materializer
  implicit val request = FakeRequest()
  implicit val hc = HeaderCarrier()
  val validSession = SessionKeys.sessionId -> "session-id"
}
