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

package uk.gov.hmrc.childcarecalculatorfrontend.viewmodels

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.utils.FormHelpers

case class InputViewModel[A](id: String, form: Form[A]) extends InputViewModelBase {
  override def args: Seq[Any] = form(id).errors.flatMap(_.args)
  def errorKey = FormHelpers.getErrorByKey(form, id)
  def value = Some(form.data.getOrElse(id, ""))
}
