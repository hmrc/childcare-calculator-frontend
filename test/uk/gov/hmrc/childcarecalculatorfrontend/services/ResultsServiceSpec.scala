package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsValue
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Scheme, SchemeEnum, SchemeResults, TaxCreditsEligibility}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ResultsServiceSpec extends PlaySpec with MockitoSugar {

  implicit val hc = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  "Result Service" must {
    "Return View Model with TC values" when {
      "It is eligible" in {
        val scheme = Scheme(name = SchemeEnum.TCELIGIBILITY,500,None,Some(TaxCreditsEligibility(true,true)))
        val schemeResults = SchemeResults(List(scheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, answers)
        val values = Await.result(resultService.getResultsViewModel(),Duration.Inf)

        values mustBe Some(ResultsViewModel(Some(500)))
      }
    }

    "Return View Model with TC None" when {
      "It is not eligible for TC scheme" in {
        val scheme = Scheme(name = SchemeEnum.TCELIGIBILITY,0,None,Some(TaxCreditsEligibility(true,true)))
        val schemeResults = SchemeResults(List(scheme))
        val answers = spy(userAnswers())


        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,answers)
        val values = Await.result(resultService.getResultsViewModel(),Duration.Inf)

        values mustBe Some(ResultsViewModel(None))
      }
    }
  }

  val eligibilityService = mock[EligibilityService]

  class ResultsService(eligibilityService: EligibilityService, answers: UserAnswers) {
    def getResultsViewModel()(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier) : Future[Option[ResultsViewModel]] = {

      val result = eligibilityService.eligibility(answers)

      result.map(results => {
        val scheme: Option[Scheme] = results.schemes.find(scheme=> scheme.name == SchemeEnum.TCELIGIBILITY)
        scheme match {
          case Some(scheme) => {
            if (scheme.amount > 0) {
              Some(ResultsViewModel(Some(scheme.amount)))
            }
            else{
              Some(ResultsViewModel())
            }
          }
          case _ => None
        }
      })
    }
  }

  case class ResultsViewModel(tc: Option[BigDecimal] = None)
}


