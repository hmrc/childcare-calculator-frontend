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

package uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours

import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.FormSpec

trait CheckboxBehaviours[A] extends FormSpec {

  def validOptions: Set[A]
  def invalidValue: String = "invalid value"
  def fieldName: String
  def form: Form[Set[A]]

  def aCheckboxForm(invalid: String = "error.invalid"): Unit = {
    for {
      (value, i) <- validOptions.zipWithIndex
    } yield s"binds `$value` successfully" in {
      val data = Map(
        s"$fieldName[$i]" -> value.toString
      )
      form.bind(data).get shouldEqual Set(value)
    }

    "fail to bind when the answer is invalid" in {
      val data = Map(
        "value[0]" -> invalidValue
      )
      form.bind(data).errors should contain(FormError("value[0]", invalid))
    }
  }

  def aMandatoryCheckboxForm(required: String, args: Any*): Unit = {

    "fail to bind when no answers are selected" in {
      val data = Map.empty[String, String]
      form.bind(data).errors should contain(FormError("value", required, args))
    }
  }
}
