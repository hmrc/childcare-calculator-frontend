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
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ResultsServiceSpec extends PlaySpec with MockitoSugar with SpecBase {

  "Result Service" must {
    "Return View Model with eligible schemes" when {
      "It is eligible for TC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val schemeResults = SchemeResults(List(tcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.tc mustBe Some(500)
      }

      "It is eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)


        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)

        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.tfc mustBe Some(500)
      }

      "It is eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.esc mustBe Some(600)
      }
    }

    "Return View Model with not eligible schemes" when {
      "It is not eligible for TC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 0, None, Some(TaxCreditsEligibility(true, true)))
        val schemeResults = SchemeResults(List(tcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.tc mustBe None
      }

      "It is not eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.tfc mustBe None
      }

      "It is not eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.esc mustBe None
      }
    }

    "Return View Model with FreeHours" when {
      "User is eligible for 15 free hours, lives in England and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe Some(15)
      }


      "User is eligible for 16 free hours, lives in Scotland and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.SCOTLAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe Some(16)
      }


      "User is eligible for 10 free hours, lives in Wales and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.WALES)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe Some(10)
      }


      "User is eligible for 12.5 free hours, lives in NI and not eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.location) thenReturn Some(Location.NORTHERN_IRELAND)
        when(freeHours.eligibility(any())) thenReturn Eligible
        when(maxFreeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe Some(12.5)
      }


      "User is eligible for max free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(maxFreeHours.eligibility(any())) thenReturn Eligible

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe Some(30)
      }
    }

    "Return View Model with no Freehours" when {
      "User is not eligible for free hours" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)
        val answers = spy(userAnswers())
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(freeHours.eligibility(any())) thenReturn NotEligible

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.freeHours mustBe None
      }
    }

    "Return View Model with first paragraph info" when {
      "You have children" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(1))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("you have children")
      }

      "You don't have children" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(0))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("you don't have children")
      }

      "The number of children field is empty" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map()))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("you don't have children")
      }

      "We have childcare costs at monthly aggregation" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString)),ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(25)))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("yearly childcare costs of around £300.")
      }

      "We have more than one childcare cost at monthly aggregation" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString),
          "2"->JsString(ChildcarePayFrequency.MONTHLY.toString),
          "3"->JsString(ChildcarePayFrequency.MONTHLY.toString)),
          ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(20),"2" -> JsNumber(10),"3"-> JsNumber(5)))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("yearly childcare costs of around £420.")
      }

      "We have one childcare cost at weekely aggregation" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.WEEKLY.toString)),
          ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(4)))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("yearly childcare costs of around £208.")
      }

      "We have one childcare cost at weekly aggregation and one childcare cost at monthly aggregation" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString),
          "2"->JsString(ChildcarePayFrequency.MONTHLY.toString),
          "3"->JsString(ChildcarePayFrequency.WEEKLY.toString)),
          ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(20),"2" -> JsNumber(10),"3"-> JsNumber(10)))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("yearly childcare costs of around £880.")
      }

      "We have children but no childcare costs" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map()))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("you don't have children.")
      }

      "You live on your own" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("on your own")
      }

      "You live with your partner" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("with your partner")
      }

      "We have no data to establish whether if they live on their own or with partner" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = new UserAnswers(new CacheMap("id", Map()))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph mustNot include("with your partner and")
        values.firstParagraph mustNot include("on your own and")
      }

      "Only user is in paid work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("and only you are")
      }

      "You live on your own and you are in paid work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("You live on your own and you are currently in paid work")
      }

      "You are in paid work but there is no data to know if you live with partner" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph mustNot include("You live on your own and you are currently in paid work")
      }

      "You live on your own and don't work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("You live on your own.")
      }

      "You live with your partner and no one works" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("You live with your partner.")
      }

      "Partner in paid work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("only your partner is")
      }

      "Both are in paid work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("both you and your partner are")
      }

      "No data about who is in paid work" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph mustNot include("your partner is")
        values.firstParagraph mustNot include("you are")
        values.firstParagraph mustNot include("you and your partner are")
      }

      "User works x hours a week" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(40))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("You work 40 hours a week")
      }


      "Your partner works x hours a week" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(40))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("Your partner works 40 hours a week")
      }

      "Your and your partner works x hours a week" in {
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers = spy(userAnswers())
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(40))
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(40))

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours)
        val values = Await.result(resultService.getResultsViewModel(answers), Duration.Inf)

        values.firstParagraph must include("You work 40 hours and your partner works 40 hours a week")
      }
    }
  }

  val eligibilityService: EligibilityService = mock[EligibilityService]
  val freeHours: FreeHours = mock[FreeHours]
  val maxFreeHours: MaxFreeHours = mock[MaxFreeHours]
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}
