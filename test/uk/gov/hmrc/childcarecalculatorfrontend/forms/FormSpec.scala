/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import org.scalactic.source.Position
import org.scalatest.Assertion
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.{Form, FormError}
import play.api.inject.Injector
import org.scalatestplus.play.PlaySpec
import play.api.test.Injecting
import uk.gov.hmrc.childcarecalculatorfrontend.config.FrontendAppConfig

trait FormSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = inject[FrontendAppConfig]

  def checkForError(form: Form[?], data: Map[String, String], expectedErrors: Seq[FormError])(
      using Position
  ): Assertion = {
    val formWithErrors = form.bind(data)

    for (error <- expectedErrors)
      formWithErrors.errors must contain(error)

    formWithErrors.errors.size mustBe expectedErrors.size

  }

  def error(key: String, value: String, args: Any*): Seq[FormError] = Seq(FormError(key, value, args))

  lazy val emptyForm: Map[String, String] = Map.empty
}
