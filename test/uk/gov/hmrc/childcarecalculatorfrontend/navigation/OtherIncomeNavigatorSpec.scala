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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{YouPartnerBoth, YouPartnerBothNeither}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxFreeChildcare
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers, Utils}

class OtherIncomeNavigatorSpec extends SpecBase with MockitoSugar {

  val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
  def navigator()           = new OtherIncomeNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers*)))

  "Current Year Other Income Route Navigation" when {

    "in Normal mode" must {
      "Parent Other Income CY Route" must {
        "redirects to YourOtherIncomeAmountCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad()
        }

        "redirects to results page when single user selects no, is in receipt of UC, eligible for TFC" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.yourOtherIncomeThisYear).thenReturn(Some(false))
          when(answers.universalCredit).thenReturn(Some(true))

          when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }

        "redirects to Your Other Income Amount CY page when single user selects no, is not in receipt of UC and not eligible for TFC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.yourOtherIncomeThisYear).thenReturn(Some(false))
          when(answers.universalCredit).thenReturn(Some(false))

          when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }

        "redirects to Your Other Income Amount CY page when single user selects yes, is not in receipt of UC and not eligible for TFC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))
          when(answers.universalCredit).thenReturn(Some(false))

          when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad()
        }

        "redirects to results page when user with partner selects no, is in receipt of UC, eligible for TFC" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.yourOtherIncomeThisYear).thenReturn(Some(false))
          when(answers.universalCredit).thenReturn(Some(true))

          when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear).thenReturn(None)

          navigator().nextPage(YourOtherIncomeThisYearId).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

      }

      "Both Other Income CY Route" must {
        "redirects to WhoGetsOtherIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear).thenReturn(Some(true))

          navigator().nextPage(BothOtherIncomeThisYearId).value(answers) mustBe
            routes.WhoGetsOtherIncomeCYController.onPageLoad()
        }

        "redirects to right page when user selects false" when {

          "redirects to results page when user with partner  selects no, is in receipt of UC, eligible for TFC" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.bothOtherIncomeThisYear).thenReturn(Some(false))
            when(answers.universalCredit).thenReturn(Some(true))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(BothOtherIncomeThisYearId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to Both Income Info CY page when user with partner selects no, is not in receipt of UC," +
            " and not eligible for TFC " in {
              val answers = spy(userAnswers())
              when(answers.doYouLiveWithPartner).thenReturn(Some(true))
              when(answers.bothOtherIncomeThisYear).thenReturn(Some(false))
              when(answers.universalCredit).thenReturn(Some(false))

              when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

              navigator().nextPage(BothOtherIncomeThisYearId).value(answers) mustBe
                routes.ResultController.onPageLoad()
            }

          "redirects to Who Gets Other Income CY page when user with partner selects yes, is not in receipt of UC," +
            " and not eligible for TFC " in {
              val answers = spy(userAnswers())
              when(answers.doYouLiveWithPartner).thenReturn(Some(true))
              when(answers.bothOtherIncomeThisYear).thenReturn(Some(true))
              when(answers.universalCredit).thenReturn(Some(false))

              when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

              navigator().nextPage(BothOtherIncomeThisYearId).value(answers) mustBe
                routes.WhoGetsOtherIncomeCYController.onPageLoad()
            }

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear).thenReturn(None)

          navigator().nextPage(BothOtherIncomeThisYearId).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }

      "Who Gets Other Income CY Route" must {
        "redirects to YourOtherIncomeAmountCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY).thenReturn(Some(YouPartnerBoth.You))

          navigator().nextPage(WhoGetsOtherIncomeCYId).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad()
        }

        "redirects to PartnerOtherIncomeAmountCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY).thenReturn(Some(YouPartnerBoth.Partner))

          navigator().nextPage(WhoGetsOtherIncomeCYId).value(answers) mustBe
            routes.PartnerOtherIncomeAmountCYController.onPageLoad()
        }

        "redirects to OtherIncomeAmountCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY).thenReturn(Some(YouPartnerBoth.Both))

          navigator().nextPage(WhoGetsOtherIncomeCYId).value(answers) mustBe
            routes.OtherIncomeAmountCYController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY).thenReturn(None)

          navigator().nextPage(WhoGetsOtherIncomeCYId).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }

      "How Much Your Other Income CY Route" must {
        "redirect to right page when single user" when {

          "redirects to results page when user is in receipt of UC, eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))
            when(answers.yourOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(true))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(YourOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to YourIncomeInfoCY page when user is not in receipt of UC and not eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))
            when(answers.yourOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(false))

            when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

            navigator().nextPage(YourOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }
        }

        "redirect to correct page when user has partner" when {

          "redirects to results page when user is in receipt of UC, eligible for TFC" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))
            when(answers.yourOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(true))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(YourOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to BothIncomeInfoCY page when user is not in receipt of UC and not eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.yourOtherIncomeThisYear).thenReturn(Some(true))
            when(answers.yourOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(false))

            when(tfc.eligibility(any())).thenReturn(Eligibility.NotEligible)

            navigator().nextPage(YourOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

        }
      }
    }
  }

  "Current Year Other Income Route Navigation" when {

    "in Normal mode" must {

      "How Much Both other income CY route" must {
        "redirect to right page when user provides valid input" when {

          "redirects to results page when user is in receipt of UC, eligible for TFC" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.Both))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.otherIncomeAmountCY).thenReturn(Some(OtherIncomeAmountCY(5, 5)))
            when(answers.universalCredit).thenReturn(Some(true))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(OtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

        }
      }

      "How Much Partner Other Income CY Route" must {
        "redirect to right page when user provides valid input" when {

          "redirects to results page when user is in receipt of UC, eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.whoGetsOtherIncomeCY).thenReturn(Some(YouPartnerBoth.Partner))
            when(answers.partnerOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(true))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(PartnerOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to BothIncomeInfoCY page when user is not in receipt of UC, eligible for TFC and " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.whoGetsOtherIncomeCY).thenReturn(Some(YouPartnerBoth.Partner))
            when(answers.partnerOtherIncomeAmountCY).thenReturn(Some(BigDecimal(23)))
            when(answers.universalCredit).thenReturn(Some(false))

            when(tfc.eligibility(any())).thenReturn(Eligibility.Eligible)

            navigator().nextPage(PartnerOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

        }

        "redirects to SessionExpired page when user provides valid input and " +
          "partner in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment).thenReturn(Some(YouPartnerBothNeither.You))

            navigator().nextPage(PartnerOtherIncomeAmountCYId).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }
      }
    }
  }

}
