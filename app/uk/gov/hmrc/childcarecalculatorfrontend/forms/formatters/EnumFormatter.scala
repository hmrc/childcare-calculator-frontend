/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters

import play.api.data.FormError
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.EnumFormat

case class EnumFormatter[E](
    missingErrorKey: String,
    unknownValueErrorKey: String,
    args: Any*
)(using enumFormat: EnumFormat[E])
    extends Formatter[E] {

  def bind(key: String, data: Map[String, String]): Either[Seq[FormError], E] =
    data.get(key).map(enumFormat.withName) match {
      case Some(Some(value)) => Right(value)
      case Some(None)        => Left(Seq(FormError(key, unknownValueErrorKey)))
      case None              => Left(Seq(FormError(key, missingErrorKey, args)))
    }

  def unbind(key: String, value: E): Map[String, String] = Map(key -> value.toString)
}
