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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.play.test.UnitSpec

class WhoGetsBenefitsFormSpec extends UnitSpec with FakeCCApplication {

  "WhoGetsBeneftsForm" should {

    "accept valid values" when {
      YouPartnerBothEnum.values.foreach { youOrPartner =>
        s"'${youOrPartner}' is given" in {
          val result = new WhoGetsBenefitsForm(applicationMessagesApi).form.bind(
            Map(
              whoGetsBenefitsKey -> youOrPartner.toString
            )
          )
          result.hasErrors shouldBe false
          result.value.get.get shouldBe youOrPartner.toString
        }
      }
    }

    s"display error '${applicationMessages.messages("who.gets.benefits.not.selected.error")}'" when {
      val invalidValues: List[String] = List("", "abcd", "123", "[*]")
      invalidValues.foreach { invalidValue =>
        s"invalid value '${invalidValue}' is given" in {
          val result = new WhoGetsBenefitsForm(applicationMessagesApi).form.bind(
            Map(
              whoGetsBenefitsKey -> invalidValue
            )
          )
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages("who.gets.benefits.not.selected.error")
          result.errors.head.message should not be "who.gets.benefits.not.selected.error"
          result.value shouldBe None
        }
      }
    }

  }

}
