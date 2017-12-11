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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsValue
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ResultsServiceSpec extends PlaySpec with MockitoSugar {

  "Result Service" must {
    "Return View Model with TC values" when {
      "It is eligible for TC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY,500,None,Some(TaxCreditsEligibility(true,true)))
        val schemeResults = SchemeResults(List(tcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(Some(500))
      }
    }

    "Return View Model with TC None" when {
      "It is not eligible for TC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY,0,None,Some(TaxCreditsEligibility(true,true)))
        val schemeResults = SchemeResults(List(tcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(None)
      }
    }

    "Return View Model with TFC values" when {
      "It is eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = Some(500))
      }
    }

    "Return View Model with TFC as None" when {
      "It is not eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None)
      }
    }

    "Return View Model with ESC values" when {
      "It is eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)) , None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = Some(500), esc = Some(600))
      }
    }

    "Return View Model with ESC as None" when {
      "It is not eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None)
      }
    }

    "Return View Model with FreeHours as 15" when {
      "User is eligible for free hours, lives in England and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHpurs.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(15))
      }
    }

    "Return View Model with FreeHours as 16" when {
      "User is eligible for free hours, lives in Scotland and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHpurs.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(16))
      }
    }

    "Return View Model with FreeHours as 10" when {
      "User is eligible for free hours, lives in Wales and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.WALES)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHpurs.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(10))
      }
    }

    "Return View Model with FreeHours as 12.5" when {
      "User is eligible for free hours, lives in NI and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHpurs.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(12.5))
      }
    }


    "Return View Model with FreeHours as 30" when {
      "User is eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(maxFreeHpurs.eligibility(any())) thenReturn Eligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(30))
      }
    }

    "Return View Model with FreeHours as 0" when {
      "User is not eligible for free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true,true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(freeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,answers, freeHours, maxFreeHpurs)
        val values = Await.result(resultService.getResultsViewModel(), Duration.Inf)

        values mustBe ResultsViewModel(tc = Some(500), tfc = None, esc = None, freeHours = Some(0))
      }
    }

  }

  val eligibilityService: EligibilityService = mock[EligibilityService]
  val freeHours: FreeHours = mock[FreeHours]
  val maxFreeHpurs: MaxFreeHours = mock[MaxFreeHours]
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

}
