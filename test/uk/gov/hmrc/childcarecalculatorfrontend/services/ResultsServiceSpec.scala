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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{ChildcareConstants, FirstParagraphBuilder, TCSchemeInEligibilityMsgBuilder, UserAnswers}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ResultsServiceSpec extends PlaySpec with MockitoSugar with SpecBase {

  "Result Service" must {
    "Return View Model with eligible schemes" when {
      "containing if you live with partner" when {
        "you live with partner" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.doYouLiveWithPartner) thenReturn Some(true)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.livesWithPartner mustBe true
        }

        "you don't live with partner" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.livesWithPartner mustBe false
        }
      }

      "cotaining childcare costs" when {
        "you have childcare costs" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.childcareCosts) thenReturn Some(ChildcareConstants.yes)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.hasChildcareCosts mustBe true
        }

        "you don't have childcare costs" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.childcareCosts) thenReturn Some(ChildcareConstants.no)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.hasChildcareCosts mustBe false
        }
      }

      "containing if your costs are with an approved provider" when {
        "your costs are with an approved provider" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.approvedProvider) thenReturn Some(ChildcareConstants.YES)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.hasCostsWithApprovedProvider mustBe true
        }

        "your costs are not with an approved provider" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.approvedProvider) thenReturn Some(ChildcareConstants.NO)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.hasCostsWithApprovedProvider mustBe false
        }
      }

      "contaning if you are in paid employment" when {
        "you are in paid work" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.areYouInPaidWork) thenReturn Some(true)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.isAnyoneInPaidEmployment mustBe true
        }

        "you are not paid work" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.areYouInPaidWork) thenReturn Some(false)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.isAnyoneInPaidEmployment mustBe false
        }

        "you live with your partner and one of you works" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.whoIsInPaidEmployment) thenReturn Some(ChildcareConstants.you)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.isAnyoneInPaidEmployment mustBe true
        }

        "none of you work" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.whoIsInPaidEmployment) thenReturn Some(ChildcareConstants.neither)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder, tcSchemeIneligibilityMsgBuilder)

          val values: ResultsViewModel = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.isAnyoneInPaidEmployment mustBe false
        }
      }

      "It is eligible for TC" when {
        "We are eligible for TC and dont' have UC" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
          val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)
          val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.taxOrUniversalCredits) thenReturn Some(taxCredits)


          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                                  tcSchemeIneligibilityMsgBuilder)

          val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.tc mustBe Some(500)
        }

        "It is eligible for TC scheme and don't have UC or TC" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                   tcSchemeIneligibilityMsgBuilder)
          val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.tc mustBe Some(500)
        }
      }

      "It is eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)

        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.tfc mustBe Some(500)
      }

      "It is eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.esc mustBe Some(600)
      }
    }

    "Return View Model with not eligible schemes" when {
      "It is not eligible for TC scheme" when {
        "Calculator returns NOT eligible for TC" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 0, None, Some(TaxCreditsEligibility(true, true)))
          val schemeResults = SchemeResults(List(tcScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                                 tcSchemeIneligibilityMsgBuilder)
          val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.tc mustBe None
        }

        "Calculator says we are eligible but we have Universal Credits" in {
          val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
          val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
          val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)
          val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
          when(answers.taxOrUniversalCredits) thenReturn Some("uc")

          val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                                 tcSchemeIneligibilityMsgBuilder)
          val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

          values.tc mustBe None
        }
      }

      "It is not eligible for TFC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.tfc mustBe None
      }

      "It is not eligible for ESC scheme" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 500, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 0, Some(EscClaimantEligibility(true, true)), None)

        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                               tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                               tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                               tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService, freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

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

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.freeHours mustBe None
      }
    }

    "Return View Model with no TFC warning message" when {
      "They are eligible for TFC and not TC" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 800, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.taxOrUniversalCredits) thenReturn Some("uc")

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.showTFCWarning mustBe false
      }
    }

    "Return View Model with TFC warning message" when {
      "They are eligible for TFC and have TC" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 0, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.taxOrUniversalCredits) thenReturn Some("tc")

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.showTFCWarning mustBe true
        values.tfcWarningMessage mustBe messages("result.schemes.tfc.tc.warning")
      }

      "They are eligible for TFC and have UC" in {
        val tcScheme = Scheme(name = SchemeEnum.TCELIGIBILITY, 0, None, Some(TaxCreditsEligibility(true, true)))
        val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 500, None, None)
        val escScheme = Scheme(name = SchemeEnum.ESCELIGIBILITY, 600, Some(EscClaimantEligibility(true, true)), None)
        val schemeResults = SchemeResults(List(tcScheme, tfcScheme, escScheme))
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())) thenReturn Future.successful(schemeResults)
        when(answers.taxOrUniversalCredits) thenReturn Some("uc")

        val resultService = new ResultsService(eligibilityService,freeHours, maxFreeHours,firstParagraphBuilder,
                                              tcSchemeIneligibilityMsgBuilder)
        val values = Await.result(resultService.getResultsViewModel(answers,Location.ENGLAND), Duration.Inf)

        values.showTFCWarning mustBe true
        values.tfcWarningMessage mustBe messages("result.schemes.tfc.uc.warning")
      }
    }
  }

  val firstParagraphBuilder = mock[FirstParagraphBuilder]
  val tcSchemeIneligibilityMsgBuilder = mock[TCSchemeInEligibilityMsgBuilder]
  val answers = spy(userAnswers())
  val tfcScheme = Scheme(name = SchemeEnum.TFCELIGIBILITY, 0, None, None)
  val schemeResults = SchemeResults(List(tfcScheme))
  val eligibilityService: EligibilityService = mock[EligibilityService]
  val freeHours: FreeHours = mock[FreeHours]
  val maxFreeHours: MaxFreeHours = mock[MaxFreeHours]
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}
