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

import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BenefitsIncomeCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class BenefitsIncomeNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new BenefitsIncomeNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Benefits Route Navigation" when {
    "in Normal mode" must {
      "Parent Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits).thenReturn(Some(true))

          navigator
            .nextPage(YouAnyTheseBenefitsIdCY, NormalMode)
            .value(answers) mustBe routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to your other income cy page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          navigator
            .nextPage(YouAnyTheseBenefitsIdCY, NormalMode)
            .value(answers) mustBe routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to both other income cy page when user selects no and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          navigator
            .nextPage(YouAnyTheseBenefitsIdCY, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits).thenReturn(None)

          navigator
            .nextPage(YouAnyTheseBenefitsIdCY, NormalMode)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Both Benefits CY Route" must {
        "redirects to whosHadBenefits page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY).thenReturn(Some(true))

          navigator
            .nextPage(BothAnyTheseBenefitsCYId, NormalMode)
            .value(answers) mustBe routes.WhosHadBenefitsController.onPageLoad(NormalMode)
        }

        "redirects to other income page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY).thenReturn(Some(false))

          navigator
            .nextPage(BothAnyTheseBenefitsCYId, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY).thenReturn(None)

          navigator
            .nextPage(BothAnyTheseBenefitsCYId, NormalMode)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }

      }

      "Whos Had Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits).thenReturn(Some(YOU))

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to partnerBenefitsIncomeCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits).thenReturn(Some(PARTNER))

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to benefitsIncomeCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits).thenReturn(Some(BOTH))

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits).thenReturn(None)

          navigator
            .nextPage(WhosHadBenefitsId, NormalMode)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "You Benefits Income CY Route" must {
        "redirects to other income page when user provides valid input,lives on own and in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          navigator
            .nextPage(YouBenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to other income page when user provides valid input,lives with partner and parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          navigator
            .nextPage(YouBenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to Your other income page when user provides valid input, does not live with partner and " +
          "parent in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))

            navigator
              .nextPage(YouBenefitsIncomeCYId, NormalMode)
              .value(answers) mustBe routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
          }

        "redirects to SessionExpired page when user provides valid input, lives with partner and " +
          "partner in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment).thenReturn(Some(partner))
            when(answers.youBenefitsIncomeCY).thenReturn(Some(BigDecimal(23)))

            navigator
              .nextPage(YouBenefitsIncomeCYId, NormalMode)
              .value(answers) mustBe routes.SessionExpiredController.onPageLoad
          }

        "redirects to other income page when user provides valid input, live with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          navigator
            .nextPage(YouBenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youBenefitsIncomeCY).thenReturn(None)

          navigator
            .nextPage(YouBenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Partner Benefits Income CY Route" must {
        "redirects to other income page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment).thenReturn(Some(both))
          when(answers.partnerBenefitsIncomeCY).thenReturn(Some(BigDecimal(23)))

          navigator
            .nextPage(PartnerBenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }
      }

      "Both Benefits Income CY Route" must {
        "redirects to BothOtherIncomeThisYear page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.benefitsIncomeCY).thenReturn(Some(BenefitsIncomeCY(23, 23)))

          navigator
            .nextPage(BenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.benefitsIncomeCY).thenReturn(None)

          navigator
            .nextPage(BenefitsIncomeCYId, NormalMode)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }
  }

}
