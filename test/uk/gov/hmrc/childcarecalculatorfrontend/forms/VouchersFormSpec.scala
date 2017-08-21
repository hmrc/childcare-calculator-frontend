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

import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.play.test.UnitSpec

class VouchersFormSpec extends UnitSpec with FakeCCApplication {

  val testCases = Table(
    ("In paid employment", "Error message key"),
    (YouPartnerBothEnum.YOU, "vouchers.not.selected.error.you"),
    (YouPartnerBothEnum.PARTNER, "vouchers.not.selected.error.partner"),
    (YouPartnerBothEnum.BOTH, "vouchers.not.selected.error.both")
  )

  "VouchersForm" when {
    forAll(testCases) { case (inPaidEmployment, errorMessageKey) =>
      s"for in employment is selected '${inPaidEmployment}'" should {

        "accept valid value" when {
          YesNoUnsureEnum.values.foreach { yesNoUnsure => {
            val yesNoUnsureValue = yesNoUnsure.toString
            s"${yesNoUnsureValue} is selected" in {
              val result = new VouchersForm(inPaidEmployment, applicationMessagesApi).form.bind(Map(
                vouchersKey -> yesNoUnsureValue
              ))
              result.hasErrors shouldBe false
              result.value.get.get shouldBe yesNoUnsureValue
            }
          }}
        }

        s"return error (${applicationMessages.messages(errorMessageKey)})" when {
          val invalidValues = List("", "abcd", "1234", "[*]")
          invalidValues.foreach { invalidValue =>
            s"invalid value '${invalidValue}' is supplied" in {
              val form = new VouchersForm(inPaidEmployment, applicationMessagesApi).form.bind(
                Map(
                  vouchersKey -> invalidValue
                )
              )
              form.value shouldBe None
              form.hasErrors shouldBe true
              form.errors.length shouldBe 1
              form.errors.head.message shouldBe applicationMessages.messages(errorMessageKey)
              form.errors.head.message should not be errorMessageKey
            }
          }
        }
      }
    }
  }

}
