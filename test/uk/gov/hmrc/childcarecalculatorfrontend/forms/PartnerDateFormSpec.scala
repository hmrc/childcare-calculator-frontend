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
import play.api.i18n.Messages

class PartnerDateFormSpec extends FormSpec with MockitoSugar {

  val statutoryType = "maternity"
  val currentTaxYear: Int =  TaxYear.current.startYear
  val previousTaxYear: Int = currentTaxYear - 1
  val ctyMinusTwo: String = (currentTaxYear - 2).toString

  val validData: Map[String, String] = Map(
    "partnerStatutoryStartDate.day"   -> "1",
    "partnerStatutoryStartDate.month" -> "2",
    "partnerStatutoryStartDate.year"  -> previousTaxYear.toString
  )

  implicit val messages: Messages = mock[Messages]

  val form = PartnerStatutoryStartDateForm(statutoryType)

  "PartnerStatutoryStartDate Form" must {

    "successfully bind when the date is valid" in {
      form.bind(validData).get shouldEqual LocalDate.of(previousTaxYear, 2, 1)
    }

    "fail to bind when the date is omitted" in {
      val data = Map.empty[String, String]
      val expectedError = error("partnerStatutoryStartDate", "partnerStatutoryStartDate.error.required", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "partnerStatutoryStartDate.day"   -> "",
        "partnerStatutoryStartDate.month" -> "",
        "partnerStatutoryStartDate.year"  -> ""
      )
      val expectedError = error("partnerStatutoryStartDate", "partnerStatutoryStartDate.error.required", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is not real" in {
      val data = Map(
        "partnerStatutoryStartDate.day"   -> "31",
        "partnerStatutoryStartDate.month" -> "2",
        "partnerStatutoryStartDate.year"  -> "2023"
      )
      val expectedError = error("partnerStatutoryStartDate", "partnerStatutoryStartDate.error.notReal", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is in the future" in {
      val futureDate = LocalDate.now.plusDays(1)
      val data = Map(
        "partnerStatutoryStartDate.day"   -> futureDate.getDayOfMonth.toString,
        "partnerStatutoryStartDate.month" -> futureDate.getMonthValue.toString,
        "partnerStatutoryStartDate.year"  -> futureDate.getYear.toString
      )
      val expectedError = error("partnerStatutoryStartDate", "partnerStatutoryStartDate.error.range.max", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is more than 2 years and 1 day before 6th April of the current tax year " in {
      val mockTaxYearInfo = mock[TaxYear]
      val taxYearStart = LocalDate.of(currentTaxYear: Int, 4: Int, 5: Int)

      when(mockTaxYearInfo.starts) thenReturn taxYearStart

      val data = Map(
        "partnerStatutoryStartDate.day" -> "5",
        "partnerStatutoryStartDate.month" -> "4",
        "partnerStatutoryStartDate.year" -> (previousTaxYear-1).toString
      )

      val expectedError = error("partnerStatutoryStartDate", "partnerStatutoryStartDate.error.range.min", statutoryType, ctyMinusTwo)
      checkForError(form, data, expectedError)
    }

    "successfully bind when the date is exactly 2 years before 6th April of the current tax year" in {
      val mockTaxYearInfo = mock[TaxYear]
      val taxYearStart = LocalDate.of(currentTaxYear: Int, 4: Int, 6: Int)

      when(mockTaxYearInfo.starts) thenReturn taxYearStart

      val data: Map[String, String] = Map(
        "partnerStatutoryStartDate.day" -> "6",
        "partnerStatutoryStartDate.month" -> "4",
        "partnerStatutoryStartDate.year" -> (previousTaxYear-1).toString
      )

      form.bind(data).get shouldEqual LocalDate.of(previousTaxYear-1: Int, 4: Int, 6: Int)
    }

  }
}
