/*
 * Copyright 2021 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{TaxCredits, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class OtherIncomeNavigatorSpec extends SpecBase with MockitoSugar {

  val taxCredits: TaxCredits = mock[TaxCredits]
  val tfc: TaxFreeChildcare = mock[TaxFreeChildcare]
  def navigator(tc:TaxCredits = taxCredits, tfcScheme: TaxFreeChildcare = tfc) = new OtherIncomeNavigator(new Utils(), tc, tfcScheme)

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Other Income Route Navigation" when {

    "in Normal mode" must {
      "Parent Other Income CY Route" must {
        "redirects to YourOtherIncomeAmountCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to results page when single user selects no, is in receipt of UC, eligible for TFC and TC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
          when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

          when(tfc.eligibility(any())) thenReturn Eligible
          when(taxCredits.eligibility(any())) thenReturn Eligible

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }

        "redirects to Your Other Income Amount CY page when single user selects no, is not in receipt of UC, eligible for TC but not eligible for TFC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
          when(answers.taxOrUniversalCredits) thenReturn Some("tc")

          when(tfc.eligibility(any())) thenReturn NotEligible
          when(taxCredits.eligibility(any())) thenReturn Eligible

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.YourIncomeInfoPYController.onPageLoad()
        }

        "redirects to Your Other Income Amount CY page when single user selects yes, is not in receipt of UC, eligible for TC but not eligible for TFC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
          when(answers.taxOrUniversalCredits) thenReturn Some("tc")

          when(tfc.eligibility(any())) thenReturn NotEligible
          when(taxCredits.eligibility(any())) thenReturn Eligible

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }


        "redirects to results page when user with partner selects no, is in receipt of UC, eligible for TFC and TC " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
          when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

          when(tfc.eligibility(any())) thenReturn Eligible
          when(taxCredits.eligibility(any())) thenReturn Eligible

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }


        "redirects to the right page when single user selects no to will you get any other income this year" when {
          "is not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "is eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.YourIncomeInfoPYController.onPageLoad()
          }

          "is not determined" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }
        }

        "redirects to the correct page when user with partner selects no to will you get any other income this year" when {
          "is not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "is eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

          "is not determined" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear) thenReturn None

          navigator().nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Partner Other Income CY Route" must {
        "redirects to PartnerOtherIncomeAmountCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(true)

          navigator().nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }


        "redirects to right page when user selects false" when {
          "they are not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(taxCredits.eligibility(any())) thenReturn NotEligible
            when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(false)

            navigator().nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "they are eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

          "is not determined" in {
            val answers = spy(userAnswers())
            when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(false)
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeThisYear) thenReturn None

          navigator().nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Other Income CY Route" must {
        "redirects to WhoGetsOtherIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)

          navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to right page when user selects false" when {
          {
            "they are not eligible for tax credits" in {
              val answers = spy(userAnswers())
              when(answers.bothOtherIncomeThisYear) thenReturn Some(false)
              when(taxCredits.eligibility(any())) thenReturn NotEligible

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.ResultController.onPageLoad()
            }

            "they are eligible for tax credits" in {
              val answers = spy(userAnswers())
              when(answers.bothOtherIncomeThisYear) thenReturn Some(false)
              when(taxCredits.eligibility(any())) thenReturn Eligible

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.BothIncomeInfoPYController.onPageLoad()
            }

            "it is not determined if they are eligible for tax credits" in {
              val answers = spy(userAnswers())
              when(answers.bothOtherIncomeThisYear) thenReturn Some(false)
              when(taxCredits.eligibility(any())) thenReturn NotDetermined

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.ResultController.onPageLoad()
            }

            "redirects to results page when user with partner  selects no, is in receipt of UC, eligible for TFC and TC " in {
              val answers = spy(userAnswers())
              when(answers.doYouLiveWithPartner) thenReturn Some(true)
              when(answers.bothOtherIncomeThisYear) thenReturn Some(false)
              when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

              when(tfc.eligibility(any())) thenReturn Eligible
              when(taxCredits.eligibility(any())) thenReturn Eligible

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.ResultController.onPageLoad()
            }

            "redirects to Both Income Info PY page when user with partner selects no, is not receipt of UC," +
              " eligible for TC and but not eligible for TC " in {
              val answers = spy(userAnswers())
              when(answers.doYouLiveWithPartner) thenReturn Some(true)
              when(answers.bothOtherIncomeThisYear) thenReturn Some(false)
              when(answers.taxOrUniversalCredits) thenReturn Some("tc")

              when(tfc.eligibility(any())) thenReturn NotEligible
              when(taxCredits.eligibility(any())) thenReturn Eligible

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.BothIncomeInfoPYController.onPageLoad()
            }

            "redirects to Who Gets Other Income CY page when user with partner selects yes, is not receipt of UC," +
              " eligible for TC and but not eligible for TC " in {
              val answers = spy(userAnswers())
              when(answers.doYouLiveWithPartner) thenReturn Some(true)
              when(answers.bothOtherIncomeThisYear) thenReturn Some(true)
              when(answers.taxOrUniversalCredits) thenReturn Some("tc")

              when(tfc.eligibility(any())) thenReturn NotEligible
              when(taxCredits.eligibility(any())) thenReturn Eligible

              navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
                routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
            }


          }
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear) thenReturn None

          navigator().nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Other Income CY RoutePartnerAnyTheseBenefitsCY" must {
        "redirects to YourOtherIncomeAmountCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("you")

          navigator().nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerOtherIncomeAmountCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("partner")

          navigator().nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to OtherIncomeAmountCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("both")

          navigator().nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn None

          navigator().nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Your Other Income CY Route" must {
        "redirect to right page when single user" when {
          "is not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "is eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.YourIncomeInfoPYController.onPageLoad()
          }

          "tax credits eligibility is not determined" in {
            val answers = spy(userAnswers())
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to results page when user is in receipt of UC, eligible for TFC and TC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(false)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

            when(tfc.eligibility(any())) thenReturn Eligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to YourIncomeInfoPY page when user is not in receipt of UC, eligible for TC but not eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(false)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some("tc")

            when(tfc.eligibility(any())) thenReturn NotEligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.YourIncomeInfoPYController.onPageLoad()
          }
        }

        "redirect to correct page when user has partner" when {
          "is not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "is eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

          "tax credits eligibility is not determined" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to results page when user is in receipt of UC, eligible for TFC and TC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

            when(tfc.eligibility(any())) thenReturn Eligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to BothIncomeInfoPY page when user is not in receipt of UC, eligible for TC but not eligible for TFC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourOtherIncomeThisYear) thenReturn Some(true)
            when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some("tc")

            when(tfc.eligibility(any())) thenReturn NotEligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

        }
      }
    }
  }

  "Previous Year Other Income Route Navigation" when {

    "in Normal mode" must {
      "Parent Other Income PY Route" must {
        "redirects to YourOtherIncomeAmountLY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeLY) thenReturn Some(true)

          navigator().nextPage(YourOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
        }

        "redirects to YouStatutoryPay page when user selects no and in single parent journey" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeLY) thenReturn Some(false)
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          navigator().nextPage(YourOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.YouStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to BothStatutoryPay page when user selects no and there is partner in the system" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeLY) thenReturn Some(false)
          when(answers.doYouLiveWithPartner) thenReturn Some(true)

          navigator().nextPage(YourOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeLY) thenReturn None

          navigator().nextPage(YourOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when user selects no and doYouLiveWithPartner is None" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeLY) thenReturn Some(false)
          when(answers.doYouLiveWithPartner) thenReturn None

          navigator().nextPage(YourOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Other Income PY Route" must {
        "redirects to PartnerOtherIncomeAmountPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeLY) thenReturn Some(true)

          navigator().nextPage(PartnerAnyOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
        }

        "redirects to BothStatutoryPay page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeLY) thenReturn Some(false)

          navigator().nextPage(PartnerAnyOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeLY) thenReturn None

          navigator().nextPage(PartnerAnyOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Other Income PY Route" must {
        "redirects to WhoGetsOtherIncomePY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeLY) thenReturn Some(true)

          navigator().nextPage(BothOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.WhoOtherIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to BothStatutoryPay page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeLY) thenReturn Some(false)

          navigator().nextPage(BothOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeLY) thenReturn None

          navigator().nextPage(BothOtherIncomeLYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Other Income PY Route" must {
        "redirects to YourOtherIncomeAmountPY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoOtherIncomePY) thenReturn Some("you")

          navigator().nextPage(WhoOtherIncomePYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountPYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerOtherIncomeAmountPY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoOtherIncomePY) thenReturn Some("partner")

          navigator().nextPage(WhoOtherIncomePYId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountPYController.onPageLoad(NormalMode)
        }

        "redirects to OtherIncomeAmountPY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoOtherIncomePY) thenReturn Some("both")

          navigator().nextPage(WhoOtherIncomePYId, NormalMode).value(answers) mustBe
            routes.OtherIncomeAmountPYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoOtherIncomePY) thenReturn None

          navigator().nextPage(WhoOtherIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Your Other Income PY Route" must {
        "redirects to StatutoryPay page when user provides valid input, lives with partner and " +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourOtherIncomeAmountPY) thenReturn Some(BigDecimal(23))

          navigator().nextPage(YourOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to StatutoryPay page when user provides valid input,does not lives with partner and " +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourOtherIncomeAmountPY) thenReturn Some(BigDecimal(23))

          navigator().nextPage(YourOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
            routes.YouStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeAmountPY) thenReturn None

          navigator().nextPage(YourOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Both other income CY route" must {
        "redirect to right page when user provides valid input" when {
          "both eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(both)
            when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(5, 5))
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

          "both not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(both)
            when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(5, 5))
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "cannot determine if they can have tax credits" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(both)
            when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(5, 5))
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to results page when user is in receipt of UC, eligible for TFC and TC " in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(both)
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY(5, 5))
            when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

            when(tfc.eligibility(any())) thenReturn Eligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

        }
      }

      "How Much Partner Other Income CY Route" must {
        "redirect to right page when user provides valid input" when {
          "partner is eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
            when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

          "partner is not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
            when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotEligible

            navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "partners eligibility for tax credits is not determined" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
            when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(taxCredits.eligibility(any())) thenReturn NotDetermined

            navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to results page when user is in receipt of UC, eligible for TFC and TC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(true)
            when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some(universalCredits)

            when(tfc.eligibility(any())) thenReturn Eligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.ResultController.onPageLoad()
          }

          "redirects to BothIncomeInfoPY page when user is not in receipt of UC, eligible for TFC and TC " in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(true)
            when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))
            when(answers.taxOrUniversalCredits) thenReturn Some("tc")

            when(tfc.eligibility(any())) thenReturn Eligible
            when(taxCredits.eligibility(any())) thenReturn Eligible

            navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
              routes.BothIncomeInfoPYController.onPageLoad()
          }

        }

        "redirects to SessionExpired page when user provides valid input and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(you)
          when(answers.partnerOtherIncomeAmountPY) thenReturn Some(BigDecimal(23))

          navigator().nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerOtherIncomeAmountPY) thenReturn None

          navigator().nextPage(PartnerOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Partner Other Income Amount PY Route" must {
        "redirect to BothStatutoryPay page " when {
          "user provides valid input" in {
            val answers = spy(userAnswers())
            when(answers.partnerOtherIncomeAmountPY) thenReturn Some(BigDecimal(23))

            navigator().nextPage(PartnerOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
              routes.BothStatutoryPayController.onPageLoad(NormalMode)
          }
        }

        "redirects to sessionExpired page" when {
          "there is no value for user selection" in {
            val answers = spy(userAnswers())
            when(answers.partnerOtherIncomeAmountPY) thenReturn None

            navigator().nextPage(PartnerOtherIncomeAmountPYId, NormalMode).value(answers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }
      }
    }
  }
}
