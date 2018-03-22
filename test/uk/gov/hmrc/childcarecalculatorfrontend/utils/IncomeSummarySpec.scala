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

import org.mockito.Mockito.{spy, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeSummarySpec extends PlaySpec with MockitoSugar with SpecBase {
  "Your Income Summary" should {
    "Format values appropriately" in {
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(200000))

      val result = incomeSummary.load(answers)

      result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£200,000")
    }
    "Add £ symbol to numeric values" in {
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(200000))

      val result = incomeSummary.load(answers)

      result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£200,000")
    }
    "Handle a single parent journey" when {
      "The income section" when {
        "has an income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£30")
        }
      }

      "The pension section" when {
        "has a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.pensionPaymentsAmonth")) mustBe Some("£300")
        }

        "does not have a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.paidIntoPension")) mustBe Some(Messages("site.no"))
        }

        "there is no data about pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.paidIntoPension")) mustBe Some(Messages("site.no"))
        }
      }

      "Your other income section" when {
        "has another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourOtherIncome")) mustBe Some("£300")
        }

        "does not have another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.otherIncome")) mustBe Some(Messages("site.no"))
        }
        "there is no data about another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.otherIncome")) mustBe Some(Messages("site.no"))
        }
      }

      "Your benefits section" when {
        "has benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.youAnyTheseBenefits) thenReturn Some(true)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(500))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourBenefitsIncome")) mustBe Some("£500")
        }

        "does not have benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.incomeFromBenefits")) mustBe Some(Messages("site.no"))
        }

        "there is no data about benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.incomeFromBenefits")) mustBe Some(Messages("site.no"))
        }
      }
    }

    "Handle a joint journey" when {
      "The income section" when {
        "Have an income for both" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350),BigDecimal(250)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£350")
          result.get(Messages("incomeSummary.partnersIncome")) mustBe Some("£250")
        }

        "Only parent works" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(350))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£350")
        }

        "Only partner works" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(350))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.partnersIncome")) mustBe Some("£350")
        }

        "Only parent works but partner has worked at some point in the same year" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.partnerPaidWorkCY) thenReturn Some(true)
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350),BigDecimal(250)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£350")
          result.get(Messages("incomeSummary.partnersIncome")) mustBe Some("£250")
        }

        "Only partner works but parent has worked at some point in the same yar" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.parentPaidWorkCY) thenReturn Some(true)
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350),BigDecimal(250)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£350")
          result.get(Messages("incomeSummary.partnersIncome")) mustBe Some("£250")
        }
      }

      "Pension section" when {
        "None of them pay into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.paidIntoPension")) mustBe Some("No")
        }

        "Only parent pays into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.pensionPaymentsAmonth")) mustBe Some("£300")
        }

        "Only partner pays into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.partnerPensionPaymentsAmonth")) mustBe Some("£300")
        }

        "Both pay into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension(BigDecimal(300),BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.pensionPaymentsAmonth")) mustBe Some("£300")
          result.get(Messages("incomeSummary.partnerPensionPaymentsAmonth")) mustBe Some("£350")
        }
      }

      "Benefits section" when {
        "None of them get benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.incomeFromBenefits")) mustBe Some("No")
        }

        "Only parent gets benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourBenefitsIncome")) mustBe Some("£300")
        }

        "Only partner gets benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerBenefitsIncomeCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.partnerBenefitsIncome")) mustBe Some("£300")
        }

        "Both get benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY(BigDecimal(300),BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourBenefitsIncome")) mustBe Some("£300")
          result.get(Messages("incomeSummary.partnerBenefitsIncome")) mustBe Some("£350")
        }
      }

      "Other income section" when {
        "None of them have other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.otherIncome")) mustBe Some("No")
        }

        "Only parent gets other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourOtherIncome")) mustBe Some("£300")
        }

        "Only partner gets other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.partnerOtherIncome")) mustBe Some("£300")
        }

        "Both get other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(BigDecimal(300),BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourOtherIncome")) mustBe Some("£300")
          result.get(Messages("incomeSummary.partnerOtherIncome")) mustBe Some("£350")
        }
      }
    }
  }


  val incomeSummary = new IncomeSummary(new Utils())
  val answers = spy(userAnswers())
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}
