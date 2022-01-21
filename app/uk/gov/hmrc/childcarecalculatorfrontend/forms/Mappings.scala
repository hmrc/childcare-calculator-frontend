/*
 * Copyright 2022 HM Revenue & Customs
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

import org.joda.time.LocalDate
import play.api.data.FieldMapping
import play.api.data.Forms.of
import play.api.data.validation.{Constraint, Invalid, Valid}

trait Mappings extends Formatters {

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

  protected def before(date: LocalDate, errorKey: String, errorArgs: Any*): Constraint[LocalDate] =
    Constraint {
      case d if d.isBefore(date) =>
        Valid
      case _ =>
        Invalid(errorKey, errorArgs: _*)
    }

  protected def after(date: LocalDate, errorKey: String, errorArgs: Any*): Constraint[LocalDate] =
    Constraint {
      case d if d.isAfter(date) =>
        Valid
      case _ =>
        Invalid(errorKey, errorArgs: _*)
    }

  protected def decimal(requiredKey: String,
                        invalidKey: String,
                        args: Any*): FieldMapping[BigDecimal] =
    of(decimalFormatter(requiredKey, invalidKey, args:_*))

  protected def string(requiredKey: String, args: Any*): FieldMapping[String] =
    of(stringFormatter(requiredKey, args:_*))

  protected def int(requiredKey: String,
                    invalidKey: String,
                    args: Any*): FieldMapping[Int] =
    of(intFormatter(requiredKey, invalidKey, args:_*))
}
