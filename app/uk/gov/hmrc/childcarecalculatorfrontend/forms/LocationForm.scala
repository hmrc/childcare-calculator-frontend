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

import play.api.data.Form
import play.api.data.Forms.*
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters.EnumFormatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object LocationForm extends FormErrorHelper {

  def apply(): Form[Location] =
    Form(single("value" -> of(LocationFormatter)))

  val options: Seq[InputOption] = InputOption.indexedFromEnumValues(
    messagePrefix = "location",
    values = Seq(
      Location.England,
      Location.Scotland,
      Location.Wales,
      Location.NorthernIreland
    )
  )

  private def LocationFormatter: Formatter[Location] =
    EnumFormatter[Location](missingErrorKey = locationErrorKey, unknownValueErrorKey = unknownErrorKey)

}
