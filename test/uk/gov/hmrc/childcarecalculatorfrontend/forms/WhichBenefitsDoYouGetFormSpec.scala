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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Benefits, BenefitsEnum}
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

/**
 * Created by user on 24/08/17.
 */
class WhichBenefitsDoYouGetFormSpec extends UnitSpec with FakeCCApplication {

  "BenefitsForm" should {
    val isPartnerTestCase = Table(
      ("isPartner", "errorMessage"),
      (false, "which.benefits.do.you.get.not.selected.parent.error"),
      (true, "which.benefits.do.you.get.not.selected.partner.error")
    )
    forAll(isPartnerTestCase) { case (isPartner, errorMessage) =>
      s"accept valid value for isPartner = ${isPartner}" when {
        val validTestCase = Table(
          ("income", "disabilityBenefits", "higherRateBenefits", "carersAllowance"),
          (false, false, false, true),
          (false, false, true, false),
          (false, false, true, true),
          (false, true, false, false),
          (false, true, false, true),
          (false, true, true, false),
          (false, true, true, true),
          (true, false, false, false),
          (true, false, false, true),
          (true, false, true, false),
          (true, false, true, true),
          (true, true, false, false),
          (true, true, false, true),
          (true, true, true, false),
          (true, true, true, true)
        )
        forAll(validTestCase) { case(income, disabilityBenefits, higherRateBenefits, carersAllowance) =>
          s"income = ${income}, disability = ${disabilityBenefits}, higherRateBenefits = ${higherRateBenefits} and carersAllowance = ${carersAllowance} benefitValue is selected" in {
            val result = new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form.bind(Map(
              s"${WhichBenefitsDoYouGetKey}-income" -> income.toString,
              s"${WhichBenefitsDoYouGetKey}-disability" -> disabilityBenefits.toString,
              s"${WhichBenefitsDoYouGetKey}-higherRateDisability" -> higherRateBenefits.toString,
              s"${WhichBenefitsDoYouGetKey}-carersAllowance" -> carersAllowance.toString
            ))
            result.hasErrors shouldBe false
            result.value.get shouldBe Benefits(
              disabilityBenefits = disabilityBenefits,
              highRateDisabilityBenefits = higherRateBenefits,
              incomeBenefits = income,
              carersAllowance = carersAllowance
            )
          }
        }
      }

      s"display error ${applicationMessages.messages(errorMessage)}" when {
        "nothing is selected" in {
          val result = new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form.bind(Map(
            s"${WhichBenefitsDoYouGetKey}-disability" -> "false",
            s"${WhichBenefitsDoYouGetKey}-higherRateDisability" -> "false",
            s"${WhichBenefitsDoYouGetKey}-income" -> "false",
            s"${WhichBenefitsDoYouGetKey}-carersAllowance" -> "false"
          ))
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages(errorMessage)
          result.errors.head.message should not be errorMessage
          result.value shouldBe None
        }
      }
    }
  }
}
