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

case class IntFormatter(
    missingErrorKey: String,
    invalidValueErrorKey: String,
    args: Any*
) extends Formatter[Int] {

  private val baseFormatter = StringFormatter(missingErrorKey, args*)

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] =
    baseFormatter
      .bind(key, data)
      .flatMap { s =>
        s.toIntOption
          .filter(_ >= 0)
          .toRight(Seq(FormError(key, invalidValueErrorKey, args)))
      }

  override def unbind(key: String, value: Int): Map[String, String] =
    baseFormatter.unbind(key, value.toString)

  def withRange(minValue: Int, maxValue: Int, tooLowErrorKey: String, tooHighErrorKey: String): Formatter[Int] =
    new Formatter[Int] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] =
        IntFormatter.this.bind(key, data) match {
          case Right(int) if int < minValue => Left(Seq(FormError(key, tooLowErrorKey)))
          case Right(int) if int > maxValue => Left(Seq(FormError(key, tooHighErrorKey)))
          case Right(int)                   => Right(int)
          case left: Left[?, ?]             => left
        }

      override def unbind(key: String, value: Int): Map[String, String] = IntFormatter.this.unbind(key, value)
    }

}
