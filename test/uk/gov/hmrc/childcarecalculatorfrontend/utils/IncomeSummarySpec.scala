/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.Mockito.{reset, spy, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.Messages
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models._

import scala.collection.immutable.ListMap

class IncomeSummarySpec extends SpecBase with MockitoSugar with BeforeAndAfterEach  {

  private val incomeSummary = new IncomeSummary(new Utils())
  private val answers = spy(userAnswers())

  private def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(answers)
  }

  "Your Income Summary" should {

    "Format values appropriately, adding £ symbol to numeric values" in {
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(200000))

      val result = incomeSummary.load(answers)

      result.get(Messages("incomeSummary.yourIncome")) mustBe Some("£200,000")
    }

    "Handle a single parent journey" when {

      "The income section has an income" in {
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

        val result = incomeSummary.load(answers)

        result mustBe ListMap(
          Messages("incomeSummary.yourIncome") -> "£30",
          Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
          Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
          Messages("incomeSummary.otherIncome") -> Messages("site.no")
        )
      }

      "The pension section" when {

        "has a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.pensionPaymentsAmonth") -> "£300",
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "does not have a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "there is no data about pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }
      }

      "Your other income section" when {

        "has another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.yourOtherIncome") -> "£300"
          )
        }

        "does not have another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "there is no data about another income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }
      }

      "Your benefits section" when {

        "has benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.youAnyTheseBenefits) thenReturn Some(true)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(500))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.yourBenefitsIncome") -> "£500",
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "does not have benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "there is no data about benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no"),
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no"),
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }
      }
    }

    "Handle a joint journey" when {

      "The income section" when {

        "Have an income for both" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350), BigDecimal(250)))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourIncome") -> "£350",
            Messages("incomeSummary.partnersIncome") -> "£250"
          )
        }

        "Only parent works" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(350))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourIncome") -> "£350",
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only parent works, but there is no data about their income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.parentEmploymentIncomeCY) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only partner works" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(350))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.partnersIncome") -> "£350",
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only partner works, but there is no data about their income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerEmploymentIncomeCY) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only parent works but partner has worked at some point in the same year" when {

          "there is data about their income" in {
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
            when(answers.partnerPaidWorkCY) thenReturn Some(true)
            when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350), BigDecimal(250)))

            val result = incomeSummary.load(answers)

            result mustBe ListMap(
              Messages("incomeSummary.yourIncome") -> "£350",
              Messages("incomeSummary.partnersIncome") -> "£250",
              Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
            )
          }

          "there is NO data about their income" in {
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
            when(answers.partnerPaidWorkCY) thenReturn Some(true)
            when(answers.employmentIncomeCY) thenReturn None

            val result = incomeSummary.load(answers)

            result mustBe ListMap(
              Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
            )
          }
        }

        "Only partner works but parent has worked at some point in the same year" when {

          "there is data about their income" in {
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
            when(answers.parentPaidWorkCY) thenReturn Some(true)
            when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(BigDecimal(350), BigDecimal(250)))

            val result = incomeSummary.load(answers)

            result mustBe ListMap(
              Messages("incomeSummary.yourIncome") -> "£350",
              Messages("incomeSummary.partnersIncome") -> "£250",
              Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
            )
          }

          "there is NO data about their income" in {
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
            when(answers.parentPaidWorkCY) thenReturn Some(true)
            when(answers.employmentIncomeCY) thenReturn None

            val result = incomeSummary.load(answers)

            result mustBe ListMap(
              Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
            )
          }
        }

        "there is no data about who is in paid employment" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }
      }

      "Pension section" when {

        "there is no data who is in paid employment" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "both are in paid employment, but there is no data if they pay into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "both are in paid employment, but there is no data about who pays into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "None of them pay into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only parent pays into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.pensionPaymentsAmonth") -> "£300"
          )
        }

        "Only partner pays into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.partnerPensionPaymentsAmonth") -> "£300"
          )
        }

        "Both pay into a pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.bothPaidPensionCY) thenReturn Some(true)
          when(answers.whoPaysIntoPension) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension(BigDecimal(300), BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.pensionPaymentsAmonth") -> "£300",
            Messages("incomeSummary.partnerPensionPaymentsAmonth") -> "£350"
          )
        }

        "Only parent works and pays pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.pensionPaymentsAmonth") -> "£300"
          )
        }

        "Only parent works and doesn't pay pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }

        "Only partner works and pays pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.PartnerPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.partnerPensionPaymentsAmonth") -> "£300"
          )
        }

        "Only partner works and doesn't pay pension" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.PartnerPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.paidIntoPension") -> Messages("site.no")
          )
        }
      }

      "Benefits section" when {

        "there is no data whether they get benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "there is no data who gets benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whosHadBenefits) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "None of them get benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")
          )
        }

        "Only parent gets benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.YOU)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourBenefitsIncome") -> "£300"
          )
        }

        "Only partner gets benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.PARTNER)
          when(answers.partnerBenefitsIncomeCY) thenReturn Some(BigDecimal(380))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.partnerBenefitsIncome") -> "£380"
          )
        }

        "Both get benefits" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)
          when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.BOTH)
          when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY(BigDecimal(300), BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourBenefitsIncome") -> "£300",
            Messages("incomeSummary.partnerBenefitsIncome") -> "£350"
          )
        }
      }

      "Other income section" when {

        "there is no data whether they have other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "there is no data who has other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn None

          val result = incomeSummary.load(answers)

          result mustBe ListMap.empty
        }

        "None of them have other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.otherIncome") -> Messages("site.no")
          )
        }

        "Only parent gets other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.YOU.toString)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourOtherIncome") -> "£300"
          )
        }

        "Only partner gets other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.PARTNER.toString)
          when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.partnerOtherIncome") -> "£300"
          )
        }

        "Both get other income" in {
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
          when(answers.whoGetsOtherIncomeCY) thenReturn Some(YouPartnerBothNeitherEnum.BOTH.toString)
          when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(BigDecimal(300), BigDecimal(350)))

          val result = incomeSummary.load(answers)

          result mustBe ListMap(
            Messages("incomeSummary.yourOtherIncome") -> "£300",
            Messages("incomeSummary.partnerOtherIncome") -> "£350",
          )
        }
      }
    }
  }

}
