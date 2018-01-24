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

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.time.TaxYearResolver

class YourStatutoryStartDateFormSpec extends FormSpec with MockitoSugar {

  val statutoryType = "maternity"

  val validData: Map[String, String] = Map(
    "date.day"   -> "1",
    "date.month" -> "2",
    "date.year"  -> "2017"
  )

  val form = YourStatutoryStartDateForm(statutoryType)

  "YourStatutoryStartDate Form" must {

    "successfully bind when the date is valid" in {
      form.bind(validData).get shouldEqual new LocalDate(2017: Int, 2: Int, 1: Int)
    }

    "fail to bind when the date is omitted" in {
      val data = Map.empty[String, String]
      val expectedError = error("date", "yourStatutoryStartDate.error.required", statutoryType)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "date.day"   -> "",
        "date.month" -> "",
        "date.year"  -> ""
      )
      val expectedError = error("date", "yourStatutoryStartDate.error.required", statutoryType)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is invalid" in {
      val data = Map(
        "date.day"   -> "31",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )
      val expectedError = error("date", "yourStatutoryStartDate.error.invalid", statutoryType)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is in the future" in {
      val futureDate = LocalDate.now.plusDays(1)
      val data = Map(
        "date.day"   -> futureDate.getDayOfMonth.toString,
        "date.month" -> futureDate.getMonthOfYear.toString,
        "date.year"  -> futureDate.getYear.toString
      )
      val expectedError = error("date", "yourStatutoryStartDate.error.past", statutoryType)
      checkForError(form, data, expectedError)
    }


    "fail to bind when the date is more than 2 years and 1 day before 6th April 2017 " in {
      val mockTaxYearInfo = mock[TaxYearResolver]
      val taxYearStart = new LocalDate(2017: Int, 4: Int, 5: Int)

      when(mockTaxYearInfo.startOfCurrentTaxYear) thenReturn taxYearStart

      val data = Map(
        "date.day" -> "5",
        "date.month" -> "4",
        "date.year" -> "2015"
      )

      val expectedError = error("date", "yourStatutoryStartDate.error.past-over-2-years", statutoryType, "2017")
      checkForError(form, data, expectedError)
    }

    "successfully bind when the date is exactly 2 years before 6th April 2017" in {
      val mockTaxYearInfo = mock[TaxYearResolver]
      val taxYearStart = new LocalDate(2017: Int, 4: Int, 6: Int)

      when(mockTaxYearInfo.startOfCurrentTaxYear) thenReturn taxYearStart

      val data: Map[String, String] = Map(
        "date.day" -> "6",
        "date.month" -> "4",
        "date.year" -> "2015"
      )

      form.bind(data).get shouldEqual new LocalDate(2015: Int, 4: Int, 6: Int)
    }

  }

}
