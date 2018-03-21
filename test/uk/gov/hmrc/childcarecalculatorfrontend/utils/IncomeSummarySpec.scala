/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import javax.inject.Inject

import org.mockito.Mockito.{spy, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeSummarySpec extends PlaySpec with MockitoSugar with SpecBase {
  "Your Income Summary" should {
    "Format values appropriately" in {
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(200000))

      val result = incomeSummary.load(answers)

      result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£200,000")
    }
    "Add £ symbol to numeric values" in {
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(200000))

      val result = incomeSummary.load(answers)

      result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£200,000")
    }
    "Handle a single parent journey" when {
      "The income section" when {
        "has an income" in {
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£30")
        }
      }

      "The pension section" when {
        "has a pension" in {
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.pensionPaymentsAmonth")) mustBe Some("£300")
        }

        "does not have a pension" in {
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.paidIntoPension")) mustBe Some(Messages("site.no"))
        }

        "there is no data about pension" in {
          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.paidIntoPension")) mustBe Some(Messages("site.no"))
        }
      }

      "Your other income section" when {
        "has another income" in {
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourOtherIncome")) mustBe Some("£300")
        }

        "does not have another income" in {
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.otherIncome")) mustBe Some(Messages("site.no"))
        }
        "there is no data about another income" in {
          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.otherIncome")) mustBe Some(Messages("site.no"))
        }
      }

      "Your benefits section" when {
        "has benefits" in {
          when(answers.youAnyTheseBenefits) thenReturn Some(true)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(500))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourBenefitsIncome")) mustBe Some("£500")
        }

        "does not have benefits" in {
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.incomeFromBenefits")) mustBe Some(Messages("site.no"))
        }

        "there is no data about benefits" in {
          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.incomeFromBenefits")) mustBe Some(Messages("site.no"))
        }
      }
    }
  }


  val incomeSummary = new IncomeSummary(new Utils())
  val answers = spy(userAnswers())
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}
