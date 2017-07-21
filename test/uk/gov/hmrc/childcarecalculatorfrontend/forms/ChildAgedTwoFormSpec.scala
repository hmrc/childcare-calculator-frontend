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

import uk.gov.hmrc.play.test.UnitSpec
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication

class ChildAgedTwoFormSpec  extends UnitSpec with FakeCCApplication {

  "ChildAgedTwoForm" should {

    "accept value true" in {
      val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
        Map(
          "childAgedTwo" -> "true"
        )
      )
      form.value.get.get shouldBe true
      form.hasErrors shouldBe false
      form.errors shouldBe empty
    }

    "accept value false" in {
      val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
        Map(
          "childAgedTwo" -> "false"
        )
      )
      form.value.get.get shouldBe false
      form.hasErrors shouldBe false
      form.errors shouldBe empty
    }

    "throw error if no value supplied" in {
      val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
        Map(
          "childAgedTwo" -> ""
        )
      )
      form.value shouldBe None
      form.hasErrors shouldBe true
      form.errors.length shouldBe 1
      form.errors.head.message shouldBe applicationMessages.messages("child.aged.two.yes.no.not.selected.error")
    }
  }
}
