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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers, Utils}

class PensionNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new PensionNavigator(new Utils)

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Pension Route Navigation" when {
    "in Normal mode" must {
      "Parent Paid Pension CY Route" must {
        "redirects to howMuchYouPayPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY).thenReturn(Some(true))

          navigator.nextPage(YouPaidPensionCYId).value(answers) mustBe routes.HowMuchYouPayPensionController
            .onPageLoad()
        }

        "redirects to your benefits current year page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          navigator
            .nextPage(YouPaidPensionCYId)
            .value(answers) mustBe routes.YouAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to both benefits current year when user lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          navigator
            .nextPage(YouPaidPensionCYId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY).thenReturn(None)

          navigator
            .nextPage(YouPaidPensionCYId)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Partner Paid Pension CY Route" must {
        "redirects to HowMuchPartnerPayPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY).thenReturn(Some(true))

          navigator
            .nextPage(PartnerPaidPensionCYId)
            .value(answers) mustBe routes.HowMuchPartnerPayPensionController.onPageLoad()
        }

        "redirects to partner benefits cy page  when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY).thenReturn(Some(false))

          navigator
            .nextPage(PartnerPaidPensionCYId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY).thenReturn(None)

          navigator
            .nextPage(PartnerPaidPensionCYId)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Both Paid Pension CY Route" must {
        "redirects to WhoPaysIntoPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY).thenReturn(Some(true))

          navigator.nextPage(BothPaidPensionCYId).value(answers) mustBe routes.WhoPaysIntoPensionController
            .onPageLoad()
        }

        "redirects to BothAnyTheseBenefitsCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY).thenReturn(Some(false))

          navigator
            .nextPage(BothPaidPensionCYId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY).thenReturn(None)

          navigator
            .nextPage(BothPaidPensionCYId)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Who Pays Into Pension CY Route" must {
        "redirects to howMuchYouPayPension page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension).thenReturn(Some("you"))

          navigator
            .nextPage(WhoPaysIntoPensionId)
            .value(answers) mustBe routes.HowMuchYouPayPensionController.onPageLoad()
        }

        "redirects to HowMuchPartnerPayPension page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension).thenReturn(Some("partner"))

          navigator
            .nextPage(WhoPaysIntoPensionId)
            .value(answers) mustBe routes.HowMuchPartnerPayPensionController.onPageLoad()
        }

        "redirects to HowMuchBothPayPension page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension).thenReturn(Some("both"))

          navigator
            .nextPage(WhoPaysIntoPensionId)
            .value(answers) mustBe routes.HowMuchBothPayPensionController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension).thenReturn(None)

          navigator
            .nextPage(WhoPaysIntoPensionId)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "How Much You Pay Pension CY Route" must {
        "redirects to benefits page when user provides valid input, lives with partner and parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          navigator
            .nextPage(HowMuchYouPayPensionId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to benefits page when user provides valid input,does not lives with partner and" +
          "parent in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))

            navigator
              .nextPage(HowMuchYouPayPensionId)
              .value(answers) mustBe routes.YouAnyTheseBenefitsCYController.onPageLoad()
          }

        "redirects to SessionExpired page when user provides valid input, lives with partner and" +
          "partner in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.whoIsInPaidEmployment).thenReturn(Some(partner))

            navigator
              .nextPage(HowMuchYouPayPensionId)
              .value(answers) mustBe routes.SessionExpiredController.onPageLoad
          }

        "redirects to benefits page when user provides valid input, lives with partner and" +
          "both in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))

            navigator
              .nextPage(HowMuchYouPayPensionId)
              .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
          }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchYouPayPension).thenReturn(None)

          navigator
            .nextPage(HowMuchYouPayPensionId)
            .value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "How Much Partner Pay Pension CY Route" must {
        "redirects to benefits page when user provides valid input and partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment).thenReturn(Some(partner))

          navigator
            .nextPage(HowMuchPartnerPayPensionId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }

        "redirects to BothAnyTheseBenefitsCY page when user provides valid input and both in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment).thenReturn(Some(both))

          navigator
            .nextPage(HowMuchPartnerPayPensionId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }
      }

      "How Much Both Pay Pension CY Route" must {
        "redirects to benefits page when user provides valid input" in {
          val answers = spy(userAnswers())

          navigator
            .nextPage(HowMuchBothPayPensionId)
            .value(answers) mustBe routes.BothAnyTheseBenefitsCYController.onPageLoad()
        }
      }
    }
  }

}
