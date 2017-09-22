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

import play.api.i18n.Messages.Implicits.{applicationMessages, applicationMessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.play.test.UnitSpec

class HowManyChildrenFormSpec extends UnitSpec with FakeCCApplication {

  "HowManyChildrenForm" should {

    "accept data" when {
      val validValues: List[Int] = List(1)
      validValues.foreach { children =>
        s"valid value '${children}' is given" in {
          val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
            Map(
              howManyChildrenKey -> children.toString
            )
          )
         // form.value.get.get shouldBe children
         form.hasErrors shouldBe false
        //  form.errors shouldBe empty
        }
      }
    }

    s"display '${applicationMessages.messages("number.of.children.not.selected.error")}'" when {
      "user submits an empty form" in {
        val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
          Map(
            howManyChildrenKey -> ""
          )
        )
        form.value shouldBe None
        form.hasErrors shouldBe true
        form.errors.length shouldBe 1
        form.errors.head.message shouldBe applicationMessages.messages("number.of.children.not.selected.error")
        form.errors.head.message should not be "number.of.children.not.selected.error"
      }
    }

    s"display not in range error '${applicationMessages.messages("number.of.children.invalid.error")}'" when {
      val invalidValues: List[Int] = List(0, 21)
      invalidValues.foreach { children =>
        s"invalid value '${children}' is given" in {
          val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
            Map(
              howManyChildrenKey -> children.toString

            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe applicationMessages.messages("number.of.children.invalid.error")
          form.errors.head.message should not be "number.of.children.invalid.error"
        }
      }
    }

    s"display '${applicationMessages.messages("number.of.children.invalid.error")}'" when {
      val invalidValues: List[BigDecimal] = List(1.01, 15.55)
      invalidValues.foreach { children =>
        s"invalid value '${children}' is given" in {
          val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
            Map(
              howManyChildrenKey -> children.toString
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe applicationMessages.messages("number.of.children.invalid.error")
          form.errors.head.message should not be "number.of.children.invalid.error"
        }
      }
    }

    s"display '${applicationMessages.messages("number.of.children.invalid.error")}' & '${applicationMessages.messages("number.of.children.not.selected.error")}'" ignore {
      val invalidValues: List[BigDecimal] = List(-1, 99.55)
      invalidValues.foreach { children =>
        s"invalid value '${children}' is given" in {
          val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
            Map(
              howManyChildrenKey -> children.toString
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
        //  form.errors.length shouldBe 2
          form.errors.head.message shouldBe applicationMessages.messages("number.of.children.not.selected.error")
          form.errors.last.message shouldBe applicationMessages.messages("number.of.children.invalid.error")
          form.errors.head.message should not be "hnumber.of.children.not.selected.error"
          form.errors.last.message should not be "number.of.children.invalid.error"
        }
      }
    }

    s"display 'error.real'" ignore {
      val invalidValues: List[String] = List("1,5", "abcd", "1%", "[*]")
      invalidValues.foreach { children =>
        s"non-numeric value '${children}' is given" in {
          val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
            Map(
              howManyChildrenKey -> children
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





