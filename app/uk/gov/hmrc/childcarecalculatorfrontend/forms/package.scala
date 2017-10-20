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
import play.api.data.Mapping

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
}
