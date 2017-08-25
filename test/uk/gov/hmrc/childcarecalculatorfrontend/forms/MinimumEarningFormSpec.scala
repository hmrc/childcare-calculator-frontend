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
import uk.gov.hmrc.play.test.UnitSpec

/**
 * Created by user on 25/08/17.
 */
class MinimumEarningFormSpec extends UnitSpec with FakeCCApplication {

  "MinimumEarningForm" when {
    val isPartnerTestCase = Table(
      ("isPartner", "errorMessage"),
      (false, "on.average.how.much.will.you.earn.parent.error"),
      (true, "on.average.how.much.will.you.earn.partner.error")
    )

    forAll(isPartnerTestCase) { case (isPartner, errorMessage) =>

      s"is partner = ${isPartner}" should {
        "accept value true" in {
          val form = new MinimumEarningsForm(isPartner, 100, applicationMessagesApi).form.bind(
            Map(
              minimumEarningKey -> "true"
            )
          )
          form.value.get.get shouldBe true
          form.hasErrors shouldBe false
          form.errors shouldBe empty
        }

        "accept value false" in {
          val form = new MinimumEarningsForm(isPartner, 100, applicationMessagesApi).form.bind(
            Map(
              minimumEarningKey -> "false"
            )
          )
          form.value.get.get shouldBe false
          form.hasErrors shouldBe false
          form.errors shouldBe empty
        }

        "return error if no value supplied" in {
          val form = new MinimumEarningsForm(isPartner, 100, applicationMessagesApi).form.bind(
            Map(
              minimumEarningKey -> ""
            )
          )
          form.value shouldBe None
          form.hasErrors shouldBe true
          form.errors.length shouldBe 1
          form.errors.head.message shouldBe applicationMessages.messages(errorMessage, 100)
          form.errors.head.message should not be errorMessage
        }

        val invalidValues = List("abcd", "1234")
        invalidValues.foreach { invalidValue =>
          s"return error if invalid value '${invalidValue}' is supplied" in {
            val form = new MinimumEarningsForm(isPartner, 100, applicationMessagesApi).form.bind(
              Map(
                minimumEarningKey -> invalidValue
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
}