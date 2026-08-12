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
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.ChildcarePayFrequency
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{childcarePayFrequencyErrorKey, unknownErrorKey}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object ChildcarePayFrequencyForm extends FormErrorHelper {

  def apply(name: String): Form[ChildcarePayFrequency] =
    Form(single("value" -> of(ChildcarePayFrequencyFormatter(name))))

  val options: Seq[InputOption] = InputOption.indexedFromEnumValues(
    messagePrefix = "childcarePayFrequency",
    values = Seq(
      ChildcarePayFrequency.Weekly,
      ChildcarePayFrequency.Monthly
    )
  )

  private def ChildcarePayFrequencyFormatter(name: String): Formatter[ChildcarePayFrequency] =
    EnumFormatter[ChildcarePayFrequency](
      missingErrorKey = childcarePayFrequencyErrorKey,
      unknownValueErrorKey = unknownErrorKey,
      name
    )

}
