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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.data.Form
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}

class AboutYourChildFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "name"      -> "Foo",
    "aboutYourChild.dob.day"   -> "1",
    "aboutYourChild.dob.month" -> "2",
    "aboutYourChild.dob.year"  -> "2017"
  )
  implicit val messages = MessagesImpl(Lang("en"), app.injector.instanceOf[MessagesApi])

  val form: Form[AboutYourChild] = AboutYourChildForm()

  val duplicateChild: Option[Map[Int, AboutYourChild]] = Some(Map(0 -> AboutYourChild("Foo", LocalDate.of(2017, 2, 1))))

  val formDuplicateChildren: Form[AboutYourChild] = AboutYourChildForm(1, children = duplicateChild)

  "AboutYourChild form" must {

    behave like questionForm(AboutYourChild("Foo", LocalDate.of(2017, 2, 1)))

    "bind when name is 35 chars long" in {
      val data = validData + ("name" -> "a" * 35)
      form.bind(data).get shouldBe AboutYourChild("a" * 35, LocalDate.of(2017, 2, 1))
    }

    "fail to bind when name is omitted" in {
      val data = validData - "name"
      val expectedError = error("name", "aboutYourChild.name.error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when name is blank" in {
      val data = validData + ("name" -> "")
      val expectedError = error("name", "aboutYourChild.name.error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when name is more than 35 chars" in {
      val data = validData + ("name" -> "a" * 36)
      val expectedError = error("name", "aboutYourChild.name.error.maxLength")
      checkForError(form, data, expectedError)
    }

    "fail to bind when name is duplicate" in {
      val expectedError = error("name", "aboutYourChild.name.error.duplicate")
      checkForError(formDuplicateChildren, validData, expectedError)
    }

    "fail to bind when the date is omitted" in {
      val data = Map("name" -> "Foo")
      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is blank" in {
      val data = Map(
        "name"      -> "Foo",
        "aboutYourChild.dob.day"   -> "",
        "aboutYourChild.dob.month" -> "",
        "aboutYourChild.dob.year"  -> ""
      )
      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.required")
      checkForError(form, data, expectedError)
    }

    "fail to bind when non numerics supplied" in {
      val data = Map(
        "name"      -> "Foo",
        "aboutYourChild.dob.day"   -> "not a number",
        "aboutYourChild.dob.month" -> "not a number",
        "aboutYourChild.dob.year"  -> "not a number"
      )

      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.invalid")
      checkForError(form, data, expectedError)
    }

    "fail to bind when a fake date is supplied" in {
      val data = Map(
        "name"      -> "Foo",
        "aboutYourChild.dob.day"   -> "31",
        "aboutYourChild.dob.month" -> "2",
        "aboutYourChild.dob.year"  -> "2000"
      )

      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.notReal")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is more than 20 years in the past" in {
      val date = LocalDate.now.minusYears(20).minusDays(1)
      val data = Map(
        "name"      -> "Foo",
        "aboutYourChild.dob.day"   -> date.getDayOfMonth.toString,
        "aboutYourChild.dob.month" -> date.getMonthValue.toString,
        "aboutYourChild.dob.year"  -> date.getYear.toString
      )
      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.range.min")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the date is more than 1 day in the future" in {
      val date = LocalDate.now.plusDays(1)
      val data = Map(
        "name"      -> "Foo",
        "aboutYourChild.dob.day"   -> date.getDayOfMonth.toString,
        "aboutYourChild.dob.month" -> date.getMonthValue.toString,
        "aboutYourChild.dob.year"  -> date.getYear.toString
      )
      val expectedError = error("aboutYourChild.dob", "aboutYourChild.dob.error.range.max")
      checkForError(form, data, expectedError)
    }
  }
}
