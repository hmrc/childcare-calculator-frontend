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
      "The income section" when {
        "has an income" in {
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(30))

          val result = incomeSummary.load(answers)

          result.get("Your income") mustBe Some("30")
        }
      }

      "The pension section" when {
        "has a pension" in {
          when(answers.YouPaidPensionCY) thenReturn Some(true)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get("Your pension payments a month") mustBe Some("300")
        }

        "does not have a pension" in {
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get("Paid into a pension") mustBe Some("No")
        }

        "there is no data about pension" in {
          val result = incomeSummary.load(answers)

          result.get("Paid into a pension") mustBe Some("No")
        }
      }

      "Your other income section" when {
        "has another income" in {
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(300))

          val result = incomeSummary.load(answers)

          result.get("Your other income") mustBe Some("300")
        }

        "does not have another income" in {
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get("Other income") mustBe Some("No")
        }
        "there is no data about another income" in {
          val result = incomeSummary.load(answers)

          result.get("Other income") mustBe Some("No")
        }
      }

      "Your benefits section" when {
        "has benefits" in {
          when(answers.youAnyTheseBenefits) thenReturn Some(true)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(500))

          val result = incomeSummary.load(answers)

          result.get("Your benefits income") mustBe Some("500")
        }

        "does not have benefits" in {
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          val result = incomeSummary.load(answers)

          result.get("Income from benefits") mustBe Some("No")
        }

        "there is no data about benefits" in {
          val result = incomeSummary.load(answers)

          result.get("Income from benefits") mustBe Some("No")
        }
      }
    }
  }


  val incomeSummary = new IncomeSummary()
  val answers = spy(userAnswers())

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  class IncomeSummary {
    def load(userAnswers: UserAnswers): Map[String, String] = {
      val result: Map[String, String] = Map()
      val parentIncome = loadParentIncome(userAnswers, _: Map[String, String])
      val parentPension = loadHowMuchYouPayPension(userAnswers, _: Map[String, String])
      val parentOtherIncome = loadYourOtherIncome(userAnswers, _: Map[String, String])
      val parentBenefitsIncome = loadYourBenefitsIncome(userAnswers, _: Map[String, String])
      (parentIncome andThen parentPension andThen parentOtherIncome andThen parentBenefitsIncome) (result)
    }

    private def loadParentIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + ("Your income" -> income.toString()))
    }

    private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.YouPaidPensionCY,result,("Paid into a pension" -> "No"),"Your pension payments a month",userAnswers.howMuchYouPayPension)
    }

    private def loadYourOtherIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.yourOtherIncomeThisYear,result,("Other income" -> "No"),"Your other income",userAnswers.yourOtherIncomeAmountCY)
    }

    private def loadYourBenefitsIncome(userAnswers: UserAnswers, result: Map[String, String]) = {
      loadSectionAmount(userAnswers.youAnyTheseBenefits,result,("Income from benefits" -> "No"),"Your benefits income",userAnswers.youBenefitsIncomeCY)
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
