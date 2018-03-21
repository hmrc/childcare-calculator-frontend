package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.mockito.Mockito.{spy, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeSummarySpec extends PlaySpec with MockitoSugar with SpecBase {
  "Your Income Summary" should {
    "Handle a single parent journey" when {
      "Include your income" in {
        when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

        val result = incomeSummary.load(answers)

        result.get("Your income") mustBe Some("30")
      }

      "Include your pension payments a month" in {
        when(answers.YouPaidPensionCY) thenReturn Some(true)
        when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

        val result = incomeSummary.load(answers)

        result.get("Your pension payments a month") mustBe Some("300")
      }

      "Include that you don't have pension" in {
        when(answers.YouPaidPensionCY) thenReturn Some(false)

        val result = incomeSummary.load(answers)

        result.get("Paid into a pension") mustBe Some("No")
      }

      "Include your other income" in {
        when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

        val result = incomeSummary.load(answers)

        result.get("Your other income") mustBe Some("300")
      }
    }
  }


  val incomeSummary = new IncomeSummary()
  val answers = spy(userAnswers())
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  class IncomeSummary {
    def load(userAnswers: UserAnswers) : Map[String,String] = {
      val result : Map[String,String] = Map()
      val parentIncome = loadParentIncome(userAnswers, _ : Map[String,String])
      val parentPension = loadHowMuchYouPayPension(userAnswers, _ : Map[String, String])
      val parentOtherIncome = loadYourOtherIncome(userAnswers, _ : Map[String,String])
      (parentIncome andThen parentPension andThen parentOtherIncome)(result)
    }

    private def loadParentIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + ("Your income" -> income.toString()))
    }

    private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: Map[String,String]) = {
      userAnswers.YouPaidPensionCY match {
        case Some(paysPension) => {
          if (paysPension) {
            userAnswers.howMuchYouPayPension.foldLeft(result)((result, income) => result + ("Your pension payments a month" -> income.toString()))
          }
          else {
            result + ("Paid into a pension" -> "No")
          }
        }
        case _ => result
      }
    }

    private def loadYourOtherIncome(userAnswers: UserAnswers, result: Map[String,String]) = {
      userAnswers.yourOtherIncomeAmountCY.foldLeft(result)((result, income) => result + ("Your other income" -> income.toString()))
    }
  }
}
