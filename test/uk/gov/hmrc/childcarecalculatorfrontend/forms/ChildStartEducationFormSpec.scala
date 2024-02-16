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
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.data.Form
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}

class ChildStartEducationFormSpec extends FormSpec {

  val validData: Map[String, String] = Map(
    "childStartEducation.day"   -> "1",
    "childStartEducation.month" -> "2",
    "childStartEducation.year"  -> "2017"
  )

  implicit val messages: MessagesImpl = MessagesImpl(Lang("en"), app.injector.instanceOf[MessagesApi])

  def form(dob: LocalDate): Form[LocalDate] = ChildStartEducationForm(dob, "Foo")
  val validDob: LocalDate = LocalDate.of(2000, 1, 1)

  "ChildStartEducation Form" must {

    "successfully bind when the date is valid" in {
      form(validDob).bind(validData).get shouldEqual LocalDate.of(2017, 2, 1)
    }

    "fail to bind when the date is omitted" in {
      val data = Map.empty[String, String]
      val expectedError = error("childStartEducation", "childStartEducation.error.required", "Foo")
      checkForError(form(validDob), data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "childStartEducation.day"   -> "",
        "childStartEducation.month" -> "",
        "childStartEducation.year"  -> ""
      )
      val expectedError = error("childStartEducation", "childStartEducation.error.required", "Foo")
      checkForError(form(validDob), data, expectedError)
    }

    "fail to bind when the date is not real" in {
      val data = Map(
        "childStartEducation.day"   -> "31",
        "childStartEducation.month" -> "2",
        "childStartEducation.year"  -> "2017"
      )
      val expectedError = error("childStartEducation", "childStartEducation.error.notReal", "Foo")
      checkForError(form(validDob), data, expectedError)
    }

    "fail to bind when the date is in the future" in {
      val futureDate = LocalDate.now.plusDays(1)
      val data = Map(
        "childStartEducation.day"   -> futureDate.getDayOfMonth.toString,
        "childStartEducation.month" -> futureDate.getMonthValue.toString,
        "childStartEducation.year"  -> futureDate.getYear.toString
      )
      val expectedError = error("childStartEducation", "childStartEducation.error.range.max", "Foo")
      checkForError(form(validDob), data, expectedError)
    }

    "fail to bind when the date is before the child's 16th birthday" in {
      val dob = LocalDate.of(2010, 1, 1)
      val expectedError = error("childStartEducation", "childStartEducation.error.range.min", "Foo")
      checkForError(form(dob), validData, expectedError)
    }
  }
}
