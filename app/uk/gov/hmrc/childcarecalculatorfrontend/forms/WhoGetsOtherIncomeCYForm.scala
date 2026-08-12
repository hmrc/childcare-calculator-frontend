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
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object WhoGetsOtherIncomeCYForm extends FormErrorHelper {

  private val WhoGetsOtherIncomeCYFormatter: Formatter[YouPartnerBoth] =
    EnumFormatter[YouPartnerBoth](
      missingErrorKey = whoGetsOtherIncomeCYErrorKey,
      unknownValueErrorKey = unknownErrorKey
    )

  def apply(): Form[YouPartnerBoth] =
    Form(single("value" -> of(WhoGetsOtherIncomeCYFormatter)))

  val options: Seq[InputOption] = InputOption.indexedFromEnumValues(
    messagePrefix = "whoGetsOtherIncomeCY",
    values = Seq(
      YouPartnerBoth.You,
      YouPartnerBoth.Partner,
      YouPartnerBoth.Both
    )
  )

}
