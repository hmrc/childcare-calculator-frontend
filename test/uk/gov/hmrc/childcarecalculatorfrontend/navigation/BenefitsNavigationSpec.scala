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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._


class BenefitsNavigationSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator(new Schemes())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Benefits Route Navigation" when {

    "in Normal mode" must {
      "Parent Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn Some(true)

          navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode)(answers) mustBe
            routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to yourStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode)(answers) mustBe
            routes.YourStatutoryPayCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn None

          navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

     "Partner Benefits CY Route" must {
        "redirects to partnerBenefitsIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn Some(true)

          navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to partnerStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn Some(false)

          navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn None

          navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Benefits CY Route" must {
        "redirects to whosHadBenefits page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)

          navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.WhosHadBenefitsController.onPageLoad(NormalMode)
        }

        "redirects to bothStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(false)

          navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.BothStatutoryPayCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn None

          navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Whos Had Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(You)

          navigator.nextPage(WhosHadBenefitsId, NormalMode)(answers) mustBe
            routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to partnerBenefitsIncomeCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(Partner)

          navigator.nextPage(WhosHadBenefitsId, NormalMode)(answers) mustBe
            routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to benefitsIncomeCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(Both)

          navigator.nextPage(WhosHadBenefitsId, NormalMode)(answers) mustBe
            routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn None

          navigator.nextPage(WhosHadBenefitsId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      /*"How Much You Pay Pension CY Route" must {
        "redirects to YourOtherIncomeThisYear page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode)(answers) mustBe
            routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchYouPayPension) thenReturn None

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Partner Pay Pension CY Route" must {
        "redirects to PartnerAnyOtherIncomeThisYear page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode)(answers) mustBe
            routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchPartnerPayPension) thenReturn None

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Both Pay Pension CY Route" must {
        "redirects to BothOtherIncomeThisYear page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension("23", "23"))

          navigator.nextPage(HowMuchBothPayPensionId, NormalMode)(answers) mustBe
            routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPension) thenReturn None

          navigator.nextPage(HowMuchBothPayPensionId, NormalMode)(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }*/

    }
  }

}
