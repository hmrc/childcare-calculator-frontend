/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.LocalDate
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.time.TaxYear
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.data.Form
import play.api.i18n.Messages

class YourDateFormSpec extends FormSpec with MockitoSugar {

  val statutoryType = "maternity"
  val currentTaxYear: Int =  TaxYear.current.startYear
  val previousTaxYear: Int = currentTaxYear - 1
  val ctyMinusTwo: String = (currentTaxYear - 2).toString

  val validData: Map[String, String] = Map(
    "yourStatutoryStartDate.day"   -> "1",
    "yourStatutoryStartDate.month" -> "2",
    "yourStatutoryStartDate.year"  -> previousTaxYear.toString
  )

  implicit val messages: Messages = mock[Messages]

  val form: Form[LocalDate] = YourStatutoryStartDateForm(statutoryType)

  "YourStatutoryStartDate Form" must {

    "successfully bind when the date is valid" in {
      form.bind(validData).get shouldEqual LocalDate.of(previousTaxYear: Int, 2: Int, 1: Int)
    }

    "fail to bind when the date is omitted" in {
      val data = Map.empty[String, String]
      val expectedError = error("yourStatutoryStartDate", "yourStatutoryStartDate.error.required", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "yourStatutoryStartDate.day"   -> "",
        "yourStatutoryStartDate.month" -> "",
        "yourStatutoryStartDate.year"  -> ""
      )
      val expectedError = error("yourStatutoryStartDate", "yourStatutoryStartDate.error.required", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is not real" in {
      val data = Map(
        "yourStatutoryStartDate.day"   -> "31",
        "yourStatutoryStartDate.month" -> "2",
        "yourStatutoryStartDate.year"  -> "2017"
      )
      val expectedError = error("yourStatutoryStartDate", "yourStatutoryStartDate.error.notReal", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is in the future" in {
      val futureDate = LocalDate.now.plusDays(1)
      val data = Map(
        "yourStatutoryStartDate.day"   -> futureDate.getDayOfMonth.toString,
        "yourStatutoryStartDate.month" -> futureDate.getMonthValue.toString,
        "yourStatutoryStartDate.year"  -> futureDate.getYear.toString
      )
      val expectedError = error("yourStatutoryStartDate", "yourStatutoryStartDate.error.range.max", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }


    "fail to bind when the date is more than 2 years and 1 day before 6th April of the current tax year" in {
      val mockTaxYearInfo = mock[TaxYear]
      val taxYearStart = LocalDate.of(currentTaxYear: Int, 4: Int, 5: Int)

      when(mockTaxYearInfo.starts) thenReturn taxYearStart

      val data = Map(
        "yourStatutoryStartDate.day" -> "5",
        "yourStatutoryStartDate.month" -> "4",
        "yourStatutoryStartDate.year" -> (previousTaxYear-1).toString
      )

      val expectedError = error("yourStatutoryStartDate", "yourStatutoryStartDate.error.range.min", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "successfully bind when the date is exactly 2 years before 6th April of the current tax year" in {
      val mockTaxYearInfo = mock[TaxYear]
      val taxYearStart = LocalDate.of(currentTaxYear: Int, 4: Int, 6: Int)

      when(mockTaxYearInfo.starts) thenReturn taxYearStart

      val data: Map[String, String] = Map(
        "yourStatutoryStartDate.day" -> "6",
        "yourStatutoryStartDate.month" -> "4",
        "yourStatutoryStartDate.year" -> (previousTaxYear-1).toString
      )

      form.bind(data).get shouldEqual LocalDate.of(previousTaxYear-1: Int, 4: Int, 6: Int)
    }

  }

}
