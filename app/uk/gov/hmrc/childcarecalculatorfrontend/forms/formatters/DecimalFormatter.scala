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

import scala.util.control.Exception.nonFatalCatch

case class DecimalFormatter(missingErrorKey: String, invalidValueErrorKey: String, args: Any*)
    extends Formatter[BigDecimal] {

  private val baseFormatter = StringFormatter(missingErrorKey, args*)
  private val decimalRegex  = """\d+(\.\d{1,2})?"""

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] =
    baseFormatter
      .bind(key, data)
      .flatMap {
        case s if !s.matches(decimalRegex) =>
          Left(Seq(FormError(key, invalidValueErrorKey, args)))
        case s =>
          nonFatalCatch
            .either(BigDecimal(s))
            .left
            .map(_ => Seq(FormError(key, invalidValueErrorKey, args)))
      }

  override def unbind(key: String, value: BigDecimal): Map[String, String] =
    Map(key -> value.toString)

  def withRange(
      minValue: BigDecimal,
      maxValue: BigDecimal,
      tooLowErrorKey: String,
      tooHighErrorKey: String
  ): Formatter[BigDecimal] =
    new Formatter[BigDecimal] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] =
        DecimalFormatter.this.bind(key, data) match {
          case Right(bigDecimal) if bigDecimal < minValue => Left(Seq(FormError(key, tooLowErrorKey)))
          case Right(bigDecimal) if bigDecimal > maxValue => Left(Seq(FormError(key, tooHighErrorKey)))
          case Right(bigDecimal)                          => Right(bigDecimal)
          case left: Left[?, ?]                           => left
        }

      override def unbind(key: String, value: BigDecimal): Map[String, String] =
        DecimalFormatter.this.unbind(key, value)
    }

}
