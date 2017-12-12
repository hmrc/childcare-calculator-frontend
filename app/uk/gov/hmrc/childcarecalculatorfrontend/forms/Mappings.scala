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

import play.api.data.{FieldMapping, FormError}
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.util.control.Exception.nonFatalCatch

trait Mappings {

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

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String, errorArgs: Any*)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum) {
          Valid
        } else {
          Invalid(errorKey, errorArgs:_*)
        }
    }

  protected def maximumValue[A](maximum: A, errorKey: String, errorArgs: Any*)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, errorArgs:_*)
        }
    }

  protected def maxLength(maximum: Int, errorKey: String, errorArgs: Any*): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, errorArgs:_*)
    }

  protected def inRange[A : Ordering](minimum: A, maximum: A, errorKey: String, errorArgs: Any*): Constraint[A] =
    firstError(
      minimumValue[A](minimum, errorKey, errorArgs: _*),
      maximumValue[A](maximum, errorKey, errorArgs: _*)
    )

  protected def decimal(requiredKey: String,
                        invalidKey: String,
                        args: Any*): FieldMapping[BigDecimal] =
    of(decimalFormatter(requiredKey, invalidKey, args:_*))

  protected def string(requiredKey: String, args: Any*): FieldMapping[String] =
    of(stringFormatter(requiredKey, args:_*))
}
