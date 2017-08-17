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

import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.test.UnitSpec

class HoursFormSpec extends UnitSpec with FakeCCApplication {

  "HoursForm" should {

    "accept data" when {
      val validValues: List[BigDecimal] = List(1, 1.000, 15, 37.5, 99.5)
      validValues.foreach { hours =>
        s"valid value '${hours}' is given" in {
          val form = new HoursForm(applicationMessagesApi).form.bind(
            Map(
              hoursKey -> hours.toString
            )
          )
          form.value.get.get shouldBe hours
          form.hasErrors shouldBe false
          form.errors shouldBe empty
        }
      }
    }

    s"display '${applicationMessages.messages("hours.a.week.not.selected.error")}'" when {
      "user submits an empty form" in {
        val form = new HoursForm(applicationMessagesApi).form.bind(
          Map(
            hoursKey -> ""
          )
        )
        form.value shouldBe None
        form.hasErrors shouldBe true
        form.errors.length shouldBe 1
        form.errors.head.message shouldBe applicationMessages.messages("hours.a.week.not.selected.error")
        form.errors.head.message should not be "hours.a.week.not.selected.error"
      }
    }

    s"display '${applicationMessages.messages("hours.a.week.invalid.error")}'" when {
      val invalidValues: List[BigDecimal] = List(-1, 0, 0.9, 1.01, 15.55, 99.51, 99.6)
      invalidValues.foreach { hours =>
        s"invalid value '${hours}' is given" in {
          val form = new HoursForm(applicationMessagesApi).form.bind(
            Map(
              hoursKey -> hours.toString
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe applicationMessages.messages("hours.a.week.invalid.error")
          form.errors.head.message should not be "hours.a.week.invalid.error"
        }
      }
    }

    s"display 'error.real'" when {
      val invalidValues: List[String] = List("1,5", "abcd", "1%", "[*]")
      invalidValues.foreach { hours =>
        s"non-numeric value '${hours}' is given" in {
          val form = new HoursForm(applicationMessagesApi).form.bind(
            Map(
              hoursKey -> hours
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe "error.real"
        }
      }
    }

  }

}
