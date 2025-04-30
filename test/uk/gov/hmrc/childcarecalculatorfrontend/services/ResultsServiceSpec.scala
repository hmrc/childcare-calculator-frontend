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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, spy, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsValue
import play.api.mvc.Request
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeChildcareWorkingParents, FreeHours, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class ResultsServiceSpec extends PlaySpec with MockitoSugar with SpecBase with BeforeAndAfterEach {

  val firstParagraphBuilder: FirstParagraphBuilder             = mock[FirstParagraphBuilder]
  val eligibilityService: EligibilityService                   = mock[EligibilityService]
  val freeHours: FreeHours                                     = mock[FreeHours]
  val freeChildcareWorkingParents: FreeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
  val taxFreeChildcare: TaxFreeChildcare                       = mock[TaxFreeChildcare]
  val util: Utils                                              = mock[Utils]
  implicit val hc: HeaderCarrier                               = HeaderCarrier()
  implicit val req: Request[_]                                 = mock[Request[_]]

  override def beforeEach(): Unit = {
    reset(firstParagraphBuilder)
    reset(eligibilityService)
    reset(freeHours)
    reset(freeChildcareWorkingParents)
    reset(taxFreeChildcare)
    reset(util)
    super.beforeEach()
  }

  object TestService
      extends ResultsService(
        frontendAppConfig,
        eligibilityService,
        freeHours,
        freeChildcareWorkingParents,
        taxFreeChildcare,
        firstParagraphBuilder,
        util
      )

  val tfcScheme: SingleSchemeResult = SingleSchemeResult(name = SchemeEnum.TFCELIGIBILITY, 500, None)

  val escScheme: SingleSchemeResult =
    SingleSchemeResult(name = SchemeEnum.ESCELIGIBILITY, 500, Some(EscClaimantEligibility(true, true)))

  val fullSchemeResults: SchemeResults = SchemeResults(List(tfcScheme, escScheme))

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  "Result Service" must {
    "Return View Model with eligible schemes" when {
      "containing if you live with partner" when {
        "you live with partner" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.livesWithPartner mustBe true
        }

        "you don't live with partner" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.livesWithPartner mustBe false
        }
      }

      "cotaining childcare costs" when {
        "you have childcare costs" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.hasChildcareCosts mustBe true
        }

        "you don't have childcare costs" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.no))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.hasChildcareCosts mustBe false
        }
      }

      "containing if your costs are with an approved provider" when {
        "your costs are with an approved provider" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.hasCostsWithApprovedProvider mustBe true
        }

        "your costs are not with an approved provider" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.NO))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.hasCostsWithApprovedProvider mustBe false
        }
      }

      "contaning if you are in paid employment" when {
        "you are in paid work" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.areYouInPaidWork).thenReturn(Some(true))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.isAnyoneInPaidEmployment mustBe true
        }

        "you are not paid work" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.areYouInPaidWork).thenReturn(Some(false))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.isAnyoneInPaidEmployment mustBe false
        }

        "you live with your partner and one of you works" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.you))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.isAnyoneInPaidEmployment mustBe true
        }

        "none of you work" in {
          val schemeResults = SchemeResults(List(tfcScheme))
          val answers       = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.neither))

          val values: ResultsViewModel = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.isAnyoneInPaidEmployment mustBe false
        }
      }

      "It is eligible for TFC scheme" in {
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfc mustBe Some(500)
      }

      "It is eligible for ESC scheme" in {
        val schemeResults = SchemeResults(List(escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.esc mustBe Some(500)
      }
    }

    "Return View Model with not eligible schemes" when {

      "It is not eligible for TFC scheme" in {
        val schemeResults = SchemeResults(List(escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfc mustBe None
      }

      "It is not eligible for ESC scheme" in {
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.esc mustBe None
      }
    }

    "Return View Model with FreeHours" when {
      "User is eligible for 15 free hours, lives in England and not eligible for max free hours" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(NotEligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(15)
      }

      "User is eligible for 22 free hours, lives in Scotland and not eligible for max free hours" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.SCOTLAND))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(NotEligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(22)
      }

      "User is eligible for 10 free hours, lives in Wales and not eligible for max free hours" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.WALES))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(NotEligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(10)
      }

      "User is eligible for 12.5 free hours, lives in NI and not eligible for max free hours" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.NORTHERN_IRELAND))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(NotEligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(12.5)
      }

      "User is eligible for max free hours for three to four year olds" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(answers.isChildAgedThreeOrFour).thenReturn(Some(true))
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(Eligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(30)
      }

      "User is eligible for working parent free hours for non three to four year olds" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(freeHours.eligibility(any())).thenReturn(Eligible)
        when(answers.isChildAgedThreeOrFour).thenReturn(Some(false))
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(Eligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe Some(frontendAppConfig.maxFreeHoursAmount)
      }
    }

    "Return View Model with no Freehours" when {
      "User is not eligible for free hours" in {
        val answers       = spy(userAnswers())
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(freeHours.eligibility(any())).thenReturn(NotEligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeHours mustBe None
      }
    }

    "Return View Model with no TFC warning message" when {
      "They are eligible for TFC and not UC or ESC" in {
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.universalCredit).thenReturn(Some(false))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfcWarningMessage mustBe None
      }
    }

    "Return View Model with TFC warning message" when {
      "They are eligible for TFC, have ESC but not UC" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.universalCredit).thenReturn(Some(false))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfcWarningMessage mustBe Some(messages("result.tfc.warning.esc"))
      }

      "They are eligible for TFC and have ESC but not UC" in {
        val schemeResults = SchemeResults(List(tfcScheme, escScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.universalCredit).thenReturn(Some(false))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfcWarningMessage mustBe Some(messages("result.tfc.warning.esc"))
      }

      "They are eligible for TFC and have UC" in {
        val schemeResults = SchemeResults(List(tfcScheme))
        val answers       = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.universalCredit).thenReturn(Some(true))

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.tfcWarningMessage mustBe Some(messages("result.tfc.warning.uc"))
      }
    }

    "Return View Model with correct Free Hours and TFC ineligible messages" when {
      val schemeResults        = SchemeResults(List(tfcScheme, escScheme))
      lazy val msgKeyFreeHours = "result.free.childcare.working.parents.ineligible"
      lazy val msgKeyTFC       = "result.tfc.ineligible"

      "The user is eligible" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(freeChildcareWorkingParents.eligibility(any())).thenReturn(Eligible)
        when(taxFreeChildcare.eligibility(any())).thenReturn(Eligible)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          None
      }
      "The user passes eligibility criteria (catch all in case the first guard case fails somehow)" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
        when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(true))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.hasChildEligibleForTfc).thenReturn(true)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          None
      }
      "The user is ineligibile but isn't in England" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.SCOTLAND))
        when(answers.childrenAgeGroups).thenReturn(None)
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
        when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(false))

        val values = await(TestService.getResultsViewModel(answers, Location.SCOTLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          Some(messages(s"$msgKeyTFC.paidEmployment"))
      }
      "There is no eligible child for free hours but otherwise eligible" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(answers.childrenAgeGroups).thenReturn(Some(Set(NoneOfThese)))
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
        when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(true))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.hasChildEligibleForTfc).thenReturn(true)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          Some(messages(s"$msgKeyFreeHours.noChildrenInAgeRange"))
        values.taxFreeChildcareEligibilityMsg mustBe
          None
      }
      "There is no childcare costs" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.no))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(true))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.hasChildEligibleForTfc).thenReturn(false)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          Some(messages(s"$msgKeyTFC.noCostsWithApprovedProvider"))
      }
      "There is no approved provider" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
        when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.NO))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(true))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.hasChildEligibleForTfc).thenReturn(false)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          Some(messages(s"$msgKeyTFC.noCostsWithApprovedProvider"))
      }
      "There is no partner" when {
        "Parent is not in paid work" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.areYouInPaidWork).thenReturn(Some(false))

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.paidEmployment"))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.paidEmployment"))
        }
        "Parent is not earning enough" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.areYouInPaidWork).thenReturn(Some(true))
          when(answers.yourMinimumEarnings).thenReturn(Some(false))
          when(util.getEarningsForAgeRange(any(), any(), any())).thenReturn(150)

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.minimumEarning", 150))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.minimumEarning", 150))
        }
        "Parent is earning too much" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.areYouInPaidWork).thenReturn(Some(true))
          when(answers.yourMinimumEarnings).thenReturn(Some(true))
          when(answers.yourMaximumEarnings).thenReturn(Some(true))

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.maximumEarning"))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.maximumEarning"))
        }
      }
      "There is a partner" when {
        "At least one is not in paid work" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.partner))

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.partner.paidEmployment"))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.partner.paidEmployment"))
        }
        "At least one is not earning enough, same age" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.both))
          when(answers.yourMinimumEarnings).thenReturn(Some(true))
          when(answers.partnerMinimumEarnings).thenReturn(Some(false))
          when(util.getEarningsForAgeRange(any(), any(), any())).thenReturn(150)

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.partner.minimumEarning.sameAge", 150))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.partner.minimumEarning.sameAge", 150))
        }
        "At least one is not earning enough, different age" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.both))
          when(answers.yourMinimumEarnings).thenReturn(Some(false))
          when(answers.partnerMinimumEarnings).thenReturn(Some(true))
          when(util.getEarningsForAgeRange(any(), any(), any()))
            .thenReturn(150)
            .thenReturn(125)
            .thenReturn(150)
            .thenReturn(125)

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.partner.minimumEarning.differentAge", 150, 125))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.partner.minimumEarning.differentAge", 150, 125))
        }
        "At least one is earning too much" in {
          val answers = spy(userAnswers())

          when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
          when(answers.location).thenReturn(Some(Location.ENGLAND))
          when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
          when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
          when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.whoIsInPaidEmployment).thenReturn(Some(ChildcareConstants.both))
          when(answers.yourMinimumEarnings).thenReturn(Some(true))
          when(answers.partnerMinimumEarnings).thenReturn(Some(true))
          when(answers.eitherOfYouMaximumEarnings).thenReturn(Some(true))

          val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

          values.freeChildcareWorkingParentsEligibilityMsg mustBe
            Some(messages(s"$msgKeyFreeHours.partner.maximumEarning"))
          values.taxFreeChildcareEligibilityMsg mustBe
            Some(messages(s"$msgKeyTFC.partner.maximumEarning"))
        }
      }
      "There is no eligible child for TFC" in {
        val answers = spy(userAnswers())

        when(eligibilityService.eligibility(any())(any(), any())).thenReturn(Future.successful(schemeResults))
        when(answers.location).thenReturn(Some(Location.ENGLAND))
        when(answers.childrenAgeGroups).thenReturn(Some(Set(FourYears)))
        when(answers.childcareCosts).thenReturn(Some(ChildcareConstants.yes))
        when(answers.approvedProvider).thenReturn(Some(ChildcareConstants.YES))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.areYouInPaidWork).thenReturn(Some(true))
        when(answers.yourMinimumEarnings).thenReturn(Some(true))
        when(answers.yourMaximumEarnings).thenReturn(Some(false))
        when(answers.hasChildEligibleForTfc).thenReturn(false)

        val values = await(TestService.getResultsViewModel(answers, Location.ENGLAND))

        values.freeChildcareWorkingParentsEligibilityMsg mustBe
          None
        values.taxFreeChildcareEligibilityMsg mustBe
          Some(messages(s"$msgKeyTFC.noEligibleChild"))
      }
    }
  }

}
