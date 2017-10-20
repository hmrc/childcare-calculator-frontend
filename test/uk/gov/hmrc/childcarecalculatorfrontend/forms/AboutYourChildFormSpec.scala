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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild

class AboutYourChildFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "name"      -> "Foo",
    "dob.day"   -> "1",
    "dob.month" -> "2",
    "dob.year"  -> "2017"
  )

  val form = AboutYourChildForm()

  "AboutYourChild form" must {

    behave like questionForm(AboutYourChild("Foo", new LocalDate(2017, 2, 1)))

    "fail to bind when name is omitted" in {
      val data = validData - "name"
      val expectedError = error("name", "aboutYourChild.error.name")
      checkForError(form, data, expectedError)
    }

    "fail to bind when name is blank" in {
      val data = validData + ("name" -> "")
      val expectedError = error("name", "aboutYourChild.error.name")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is omitted" in {
      val data = Map("name" -> "Foo")
      val expectedError = error("dob", "aboutYourChild.error.dob")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is more than 20 years in the past" in {
      val date = LocalDate.now.minusYears(20).minusDays(1)
      val data = Map(
        "name"      -> "Foo",
        "dob.day"   -> date.getDayOfMonth.toString,
        "dob.month" -> date.getMonthOfYear.toString,
        "dob.year"  -> date.getYear.toString
      )
      val expectedError = error("dob", "aboutYourChild.error.past")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is more than 1 year in the future" in {
      val date = LocalDate.now.plusYears(1).plusDays(1)
      val data = Map(
        "name"      -> "Foo",
        "dob.day"   -> date.getDayOfMonth.toString,
        "dob.month" -> date.getMonthOfYear.toString,
        "dob.year"  -> date.getYear.toString
      )
      val expectedError = error("dob", "aboutYourChild.error.future")
      checkForError(form, data, expectedError)
    }
  }
}
