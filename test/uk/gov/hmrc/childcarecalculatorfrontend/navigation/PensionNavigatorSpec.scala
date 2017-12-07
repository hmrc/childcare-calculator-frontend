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
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{HowMuchBothPayPensionPY, HowMuchBothPayPension, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.http.cache.client.CacheMap


class PensionNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new PensionNavigator(new Utils)

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Pension Route Navigation" when {

    "in Normal mode" must {
      "Parent Paid Pension CY Route" must {
        "redirects to howMuchYouPayPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn Some(true)

          navigator.nextPage(YouPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to your benefits current year page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn Some(false)

          navigator.nextPage(YouPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.YouPaidPensionCY) thenReturn None

          navigator.nextPage(YouPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Partner Paid Pension CY Route" must {
        "redirects to HowMuchPartnerPayPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY) thenReturn Some(true)

          navigator.nextPage(PartnerPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to yourOtherIncomeThisYear page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY) thenReturn Some(false)

          navigator.nextPage(PartnerPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.PartnerPaidPensionCY) thenReturn None

          navigator.nextPage(PartnerPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Paid Pension CY Route" must {
        "redirects to WhoPaysIntoPension page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY) thenReturn Some(true)

          navigator.nextPage(BothPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.WhoPaysIntoPensionController.onPageLoad(NormalMode)
        }

        "redirects to BothOtherIncomeThisYear page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY) thenReturn Some(false)

          navigator.nextPage(BothPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionCY) thenReturn None

          navigator.nextPage(BothPaidPensionCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Pays Into Pension CY Route" must {
        "redirects to howMuchYouPayPension page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension) thenReturn Some("you")

          navigator.nextPage(WhoPaysIntoPensionId, NormalMode).value(answers) mustBe
            routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to HowMuchPartnerPayPension page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension) thenReturn Some("partner")

          navigator.nextPage(WhoPaysIntoPensionId, NormalMode).value(answers) mustBe
            routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to HowMuchBothPayPension page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension) thenReturn Some("both")

          navigator.nextPage(WhoPaysIntoPensionId, NormalMode).value(answers) mustBe
            routes.HowMuchBothPayPensionController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoPaysIntoPension) thenReturn None

          navigator.nextPage(WhoPaysIntoPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much You Pay Pension CY Route" must {
        "redirects to YourOtherIncomeThisYear page when user provides valid input, lives with partner and" +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
        }


        "redirects to benefits page when user provides valid input,does not lives with partner and" +
            "parent in paid employment" in {
            val answers = spy(userAnswers())
            when(answers.areYouInPaidWork) thenReturn Some(true)
            when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

            navigator.nextPage(HowMuchYouPayPensionId, NormalMode).value(answers) mustBe
              routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
          }

        "redirects to sessionExpired page when user provides valid input, lives with partner and" +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to BothOtherIncomeThisYear page when user provides valid input, lives with partner and" +
          "both in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchYouPayPension) thenReturn None

          navigator.nextPage(HowMuchYouPayPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Partner Pay Pension CY Route" must {
        "redirects to PartnerAnyOtherIncomeThisYear page when user provides valid input and" +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode).value(answers) mustBe
            routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to BothOtherIncomeThisYear page when user provides valid input and" +
          "both in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.howMuchPartnerPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when user provides valid input and" +
          "you in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.howMuchYouPayPension) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchPartnerPayPension) thenReturn None

          navigator.nextPage(HowMuchPartnerPayPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Both Pay Pension CY Route" must {
        "redirects to BothOtherIncomeThisYear page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPension) thenReturn Some(HowMuchBothPayPension("23", "23"))

          navigator.nextPage(HowMuchBothPayPensionId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPension) thenReturn None

          navigator.nextPage(HowMuchBothPayPensionId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

    }
  }

  "Previous Year Pension Route Navigation" when {

    "in Normal mode" must {
      "Parent Paid Pension PY Route" must {
        "redirects to howMuchYouPayPensionPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youPaidPensionPY) thenReturn Some(true)

          navigator.nextPage(YouPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to yourOtherIncomeLY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youPaidPensionPY) thenReturn Some(false)

          navigator.nextPage(YouPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youPaidPensionPY) thenReturn None

          navigator.nextPage(YouPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Partner Paid Pension PY Route" must {
        "redirects to howMuchPartnerPayPensionPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidPensionPY) thenReturn Some(true)

          navigator.nextPage(PartnerPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to partnerAnyOtherIncomeLY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidPensionPY) thenReturn Some(false)

          navigator.nextPage(PartnerPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidPensionPY) thenReturn None

          navigator.nextPage(PartnerPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Paid Pension PY Route" must {
        "redirects to whoPaidIntoPensionPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionPY) thenReturn Some(true)

          navigator.nextPage(BothPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.WhoPaidIntoPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to bothOtherIncomeLY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionPY) thenReturn Some(false)

          navigator.nextPage(BothPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidPensionPY) thenReturn None

          navigator.nextPage(BothPaidPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Pays Into Pension PY Route" must {
        "redirects to howMuchYouPayPensionPY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaidIntoPensionPY) thenReturn Some(You)

          navigator.nextPage(WhoPaidIntoPensionPYId, NormalMode).value(answers) mustBe
            routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to HowMuchPartnerPayPensionPY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaidIntoPensionPY) thenReturn Some(Partner)

          navigator.nextPage(WhoPaidIntoPensionPYId, NormalMode).value(answers) mustBe
            routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to HowMuchBothPayPensionPY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoPaidIntoPensionPY) thenReturn Some(Both)

          navigator.nextPage(WhoPaidIntoPensionPYId, NormalMode).value(answers) mustBe
            routes.HowMuchBothPayPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoPaidIntoPensionPY) thenReturn None

          navigator.nextPage(WhoPaidIntoPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much You Pay Pension PY Route" must {
        "redirects to yourOtherIncomeLY page when user provides valid input,lives with partner and " +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.howMuchYouPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionPYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to YourOtherIncomeLY page when user provides valid input,does not lives with partner and" +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.areYouInPaidWork) thenReturn Some(true)
          when(answers.howMuchYouPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionPYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when user provides valid input,lives with partner  and" +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.howMuchYouPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to BothOtherIncomeLY page when user provides valid input,lives with partner  and" +
          "both in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.howMuchYouPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchYouPayPensionPYId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
        }


        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchYouPayPensionPY) thenReturn None

          navigator.nextPage(HowMuchYouPayPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Partner Pay Pension PY Route" must {
        "redirects to partnerAnyOtherIncomeLY page when user provides valid input and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.howMuchPartnerPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionPYId, NormalMode).value(answers) mustBe
            routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to BothOtherIncomeLY page when user provides valid input and" +
          "both in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.howMuchPartnerPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionPYId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when user provides valid input and" +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.howMuchPartnerPayPensionPY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(HowMuchPartnerPayPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchPartnerPayPensionPY) thenReturn None

          navigator.nextPage(HowMuchPartnerPayPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Both Pay Pension PY Route" must {
        "redirects to bothOtherIncomeLY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPensionPY) thenReturn Some(HowMuchBothPayPensionPY("23", "23"))

          navigator.nextPage(HowMuchBothPayPensionPYId, NormalMode).value(answers) mustBe
            routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPensionPY) thenReturn None

          navigator.nextPage(HowMuchBothPayPensionPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

    }
  }

}
