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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form

object YourStatutoryWeeksForm extends FormErrorHelper {

  def apply(statutoryType: String): Form[Int] =
    Form(
      "value" ->
        int("yourStatutoryWeeks.required", "yourStatutoryWeeks.invalid", statutoryType)
          .verifying(inRange[Int](1, 48, "yourStatutoryWeeks.invalid", statutoryType))
    )
}
