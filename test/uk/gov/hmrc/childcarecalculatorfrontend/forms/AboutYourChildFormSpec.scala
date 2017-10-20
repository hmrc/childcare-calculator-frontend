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

    behave like formWithMandatoryNumberFields("dob.day", "dob.month", "dob.year")

    "fail to bind when name is omitted" in {
      val data = validData - "name"
      val expectedError = error("name", "error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when name is blank" in {
      val data = validData + ("name" -> "")
      val expectedError = error("name", "error.required")
      checkForError(form, data, expectedError)
    }
  }
}
