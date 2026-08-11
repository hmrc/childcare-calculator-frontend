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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

case class InputOption(id: String, value: String, messageKey: String) {

  def toRadioItem(using messages: Messages): RadioItem = RadioItem(
    id = Some(id),
    value = Some(value),
    content = HtmlContent(messages(messageKey))
  )

}

object InputOption {

  private[utils] def namedFromEnumValue[E](namePrefix: String, enumValue: E): InputOption = InputOption(
    id = s"$namePrefix.$enumValue",
    value = enumValue.toString,
    messageKey = s"$namePrefix.$enumValue"
  )

  def namedFromEnumValues[E](namePrefix: String, values: Seq[E]): Seq[InputOption] =
    values.map(enumValue => namedFromEnumValue(namePrefix, enumValue))

  private[utils] def indexedFromEnumValue[E](index: Int, enumValue: E, messagePrefix: String): InputOption =
    InputOption(
      id = if (index == 1) "value" else s"value-$index",
      value = enumValue.toString,
      messageKey = s"$messagePrefix.$enumValue"
    )

  def indexedFromEnumValues[E](messagePrefix: String, values: Seq[E]): Seq[InputOption] =
    values.zip(Iterator.from(1)).map { case (enumValue, index) =>
      indexedFromEnumValue(index, enumValue, messagePrefix)
    }

}
