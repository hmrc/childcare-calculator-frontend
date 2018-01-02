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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.control.Exception.nonFatalCatch

trait Formatters {

  def stringFormatter(errorKey: String, args: Any*): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey, args)))
        case Some(s) => Right(s)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  def decimalFormatter(requiredKey: String, invalidKey: String, args: Any*): Formatter[BigDecimal] = new Formatter[BigDecimal] {

    private val baseFormatter = stringFormatter(requiredKey, args:_*)
    private val decimalRegex = """\d+(\.\d{1,2})?"""

    override def bind(key: String, data: Map[String, String]) =
      baseFormatter
        .bind(key, data)
        .right.flatMap {
        case s if !s.matches(decimalRegex) =>
          Left(Seq(FormError(key, invalidKey, args)))
        case s =>
          nonFatalCatch
            .either(BigDecimal(s))
            .left.map(_ => Seq(FormError(key, invalidKey, args)))
      }

    override def unbind(key: String, value: BigDecimal) =
      Map(key -> value.toString)
  }

  def intFormatter(requiredKey: String, invalidKey: String, args: Any*): Formatter[Int] =
    new Formatter[Int] {

      private val baseFormatter = stringFormatter(requiredKey, args:_*)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .right.flatMap {
          s =>
            nonFatalCatch
              .either(s.toInt)
              .left.map(_ => Seq(FormError(key, invalidKey, args)))
        }

      override def unbind(key: String, value: Int) =
        baseFormatter.unbind(key, value.toString)
    }
}
