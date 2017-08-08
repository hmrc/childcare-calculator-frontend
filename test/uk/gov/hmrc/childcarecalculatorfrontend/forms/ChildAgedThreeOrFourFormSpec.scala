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

class ChildAgedThreeOrFourFormSpec extends UnitSpec with FakeCCApplication {

  "ChildAgedThreeOrFourForm" should {

    "accept valid value" when {

      val validValues = List("true", "false")
      validValues.foreach { validValue =>
        s"'${validValue}' is given" in {
          val form = new ChildAgedThreeOrFourForm(applicationMessagesApi).form.bind(
            Map(
              childAgedThreeOrFourKey -> validValue
            )
          )
          form.value.get.get shouldBe validValue.toBoolean
          form.hasErrors shouldBe false
          form.errors shouldBe empty
        }
      }

    }


    "return error message for invalid value" when {

      s"nothing is selected" in {
        val form = new ChildAgedThreeOrFourForm(applicationMessagesApi).form.bind(
          Map(
            childAgedThreeOrFourKey -> ""
          )
        )
        form.value shouldBe None
        form.hasErrors shouldBe true
        form.errors.length shouldBe 1
        form.errors.head.message shouldBe applicationMessages.messages("child.aged.three.or.four.yes.no.not.selected.error")
        form.errors.head.message should not be "child.aged.three.or.four.yes.no.not.selected.error"
      }

      val invalidValues = List("abcd", "1234")
      invalidValues.foreach { invalidValue =>
        s"'${invalidValue}' is given" in {
          val form = new ChildAgedThreeOrFourForm(applicationMessagesApi).form.bind(
            Map(
              childAgedThreeOrFourKey -> invalidValue
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe "error.boolean"
        }
      }

    }
  }

}
