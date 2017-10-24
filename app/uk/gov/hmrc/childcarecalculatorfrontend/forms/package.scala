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

package uk.gov.hmrc.childcarecalculatorfrontend

import org.joda.time.LocalDate
import play.api.data.validation.Constraint
import play.api.data.{FormError, Mapping}

import scala.util.Try

package object forms {

  private val defaultLocalDateMapping: Mapping[(Int, Int, Int)] = {
    import play.api.data.Forms._
    tuple(
      "day"   -> number(min = 1, max = 31),
      "month" -> number(min = 1, max = 12),
      "year"  -> number
    )
  }

  def localDateMapping(
                      dayMapping: (String, Mapping[Int]),
                      monthMapping: (String, Mapping[Int]),
                      yearMapping: (String, Mapping[Int])
                      ): Mapping[LocalDate] = {
    import play.api.data.Forms.tuple
    localDateMapping(tuple(dayMapping, monthMapping, yearMapping))
  }

  def localDateMapping: Mapping[LocalDate] =
    localDateMapping(defaultLocalDateMapping)

  def localDateMapping(subMapping: Mapping[(Int, Int, Int)]): Mapping[LocalDate] = {

    def validate(t: (Int, Int, Int)): Boolean =
      Try(bind(t)).isSuccess

    def bind(t: (Int, Int, Int)): LocalDate = t match {
      case (day, month, year) =>
        new LocalDate(year, month, day)
    }

    def unbind(date: LocalDate): (Int, Int, Int) = {
      (date.getDayOfMonth, date.getMonthOfYear, date.getYear)
    }

    subMapping.verifying("error.invalidDate", validate _).transform(bind, unbind)
  }

  implicit class WithErrors[A](mapping: Mapping[A]) {

    def replaceError(error: FormError, newError: FormError): Mapping[A] = {
      new Mapping[A] {

        override val key: String = mapping.key
        override val mappings: Seq[Mapping[_]] = mapping.mappings
        override val constraints: Seq[Constraint[A]] = mapping.constraints
        override def unbind(value: A): Map[String, String] = mapping.unbind(value)
        override def withPrefix(prefix: String): Mapping[A] =
          mapping.withPrefix(prefix).replaceError(error.withPrefix(prefix), newError.withPrefix(prefix))
        override def verifying(constraints: Constraint[A]*): Mapping[A] =
          mapping.verifying(constraints: _*).replaceError(error, newError)

        private def mapErrors(errors: Seq[FormError]): Seq[FormError] = {
          val index = errors.indexWhere(e => e.key == error.key && e.message == error.message)
          if (index > -1) {
            errors.updated(index, newError).distinct
          } else {
            errors
          }
        }

        override def unbindAndValidate(value: A): (Map[String, String], Seq[FormError]) = {
          val (map, errors) = mapping.unbindAndValidate(value)
          (map, mapErrors(errors))
        }

        override def bind(data: Map[String, String]): Either[Seq[FormError], A] = {
          mapping.bind(data).left.map(mapErrors)
        }
      }
    }

    def replaceError(error: String, message: String): Mapping[A] =
      replaceError(FormError(mapping.key, error), FormError(mapping.key, message))
  }

  implicit class WithPrefix(formError: FormError) {
    def withPrefix(prefix: String): FormError = {
      val key = Seq(prefix, formError.key).filter(_.nonEmpty).mkString(".")
      formError.copy(key = key)
    }
  }
}
