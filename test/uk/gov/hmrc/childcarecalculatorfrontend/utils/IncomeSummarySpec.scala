package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.mockito.Mockito.{spy, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeSummarySpec extends PlaySpec with MockitoSugar with SpecBase {
  "Your Income Summary" should {
    "Handle a single parent journey" when {
      "The income section" when {
        "has an income" in {
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.yourIncome")) mustBe Some("30")
        }
      }

      "The pension section" when {
        "has a pension" in {
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get(Messages("incomeSummary.pensionPaymentsAmonth")) mustBe Some("300")
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

          result.get(Messages("incomeSummary.yourOtherIncome")) mustBe Some("300")
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

          result.get(Messages("incomeSummary.yourBenefitsIncome")) mustBe Some("500")
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


  val incomeSummary = new IncomeSummary()
  val answers = spy(userAnswers())
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  class IncomeSummary {
    def load(userAnswers: UserAnswers)(implicit messages: Messages): Map[String, String] = {
      val result: Map[String, String] = Map()
      val parentIncome = loadParentIncome(userAnswers, _: Map[String, String])
      val parentPension = loadHowMuchYouPayPension(userAnswers, _: Map[String, String])
      val parentOtherIncome = loadYourOtherIncome(userAnswers, _: Map[String, String])
      val parentBenefitsIncome = loadYourBenefitsIncome(userAnswers, _: Map[String, String])
      (parentIncome andThen parentPension andThen parentOtherIncome andThen parentBenefitsIncome) (result)
    }

    private def loadParentIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.yourIncome") -> income.toString()))
    }

    private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.YouPaidPensionCY,result,(Messages("incomeSummary.paidIntoPension") -> Messages("site.no")),Messages("incomeSummary.pensionPaymentsAmonth"),userAnswers.howMuchYouPayPension)
    }

    private def loadYourOtherIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.yourOtherIncomeThisYear,result,(Messages("incomeSummary.otherIncome") -> Messages("site.no")),Messages("incomeSummary.yourOtherIncome"),userAnswers.yourOtherIncomeAmountCY)
    }

    private def loadYourBenefitsIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.youAnyTheseBenefits,result,(Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")),Messages("incomeSummary.yourBenefitsIncome"),userAnswers.youBenefitsIncomeCY)
    }

    private def loadSectionAmount(conditionToCheckAmount: Option[Boolean], result: Map[String,String], conditionNotMet: (String,String), textForIncome: String, incomeSection: Option[BigDecimal]) = {
      conditionToCheckAmount match {
        case Some(conditionMet) => {
          if (conditionMet) {
            incomeSection.foldLeft(result)((result, income) => result + (textForIncome -> income.toString()))
          }
          else {
            result + conditionNotMet
          }
        }
        case _ => result + conditionNotMet
      }
    }
  }
}
