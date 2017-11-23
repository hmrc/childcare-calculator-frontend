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

import org.joda.time.LocalDate

class ChildStartEducationFormSpec extends FormSpec {

  val validData: Map[String, String] = Map(
    "date.day"   -> "1",
    "date.month" -> "2",
    "date.year"  -> "2017"
  )

  val form = ChildStartEducationForm()

  "ChildStartEducation Form" must {

    "successfully bind when the date is valid" in {
      form.bind(validData).get shouldEqual new LocalDate(2017, 2, 1)
    }

    "fail to bind when the date is omitted" in {
      val data = Map.empty[String, String]
      val expectedError = error("date", "childStartEducation.error")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "date.day"   -> "",
        "date.month" -> "",
        "date.year"  -> ""
      )
      val expectedError = error("date", "childStartEducation.error")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is invalid" in {
      val data = Map(
        "date.day"   -> "31",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )
      val expectedError = error("date", "childStartEducation.error.invalid")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is in the future" in {
      val futureDate = LocalDate.now.plusDays(1)
      val data = Map(
        "date.day"   -> futureDate.getDayOfMonth.toString,
        "date.month" -> futureDate.getMonthOfYear.toString,
        "date.year"  -> futureDate.getYear.toString
      )
      val expectedError = error("date", "childStartEducation.error.invalid")
      checkForError(form, data, expectedError)
    }
  }
}
