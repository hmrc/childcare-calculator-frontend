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
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, EmploymentIncomePY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.http.cache.client.CacheMap


class EmploymentIncomeNavigationSpec extends SpecBase with MockitoSugar with OptionValues {

  val navigator = new EmploymentIncomeNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Income Route Navigation" when {

    "in Normal mode" must {
      "Partner Paid Work CY Route" must {
        "redirects to parent employment income CY when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY) thenReturn Some(false)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to both employment income CY when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY) thenReturn Some(true)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY) thenReturn None

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }

      }

      "Parent Paid Work CY Route" must {
        "redirects to partner employment income CY when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY) thenReturn Some(false)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to both employment income CY when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY) thenReturn Some(true)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY) thenReturn None

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Parent Employment Income CY Route" must {
        "redirects to parent paid pension CY when user provides valid value and is single" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.YouPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to parent paid pension CY when user lives with partner and partner does not work" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.YouPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to both paid pension CY when user provides valid value and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomeCY) thenReturn None

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Partner Employment Income CY Route" must {
        "redirects to both paid pension CY when when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)

          navigator.nextPage(PartnerEmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to partner paid pension CY when only partner works" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)

          navigator.nextPage(PartnerEmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.PartnerPaidPensionCYController.onPageLoad(NormalMode)
        }
      }

      "Parent and Partner Employment Income CY Route" must {
        "redirects to both paid pension CY when when user provides valid values" in {
          val answers = spy(userAnswers())
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY(12, 20))

          navigator.nextPage(EmploymentIncomeCYId, NormalMode).value(answers) mustBe routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }
      }

    }
  }

  "Previous Year Income Route Navigation" when {

    "in Normal mode" must {

      "Parent Paid Work PY route" must {
        "redirect to ParentEmploymentIncomePY page when user select yes" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkPY) thenReturn Some(true)

          navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)
        }

        "redirect to YouAnyTheseBenefitsPY page when user select no" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkPY) thenReturn Some(false)

          navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.YouAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        }

        "redirect to session expired page when there is no selection" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkPY) thenReturn None

          navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }

      "Partner Paid Work PY Route" must {
        "redirects to parent employment income PY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkPY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerPaidWorkPYId, NormalMode).value(answers) mustBe routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerPaidWorkPYId, NormalMode).value(answers) mustBe routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)
        }
      }

      "Both Paid Work PY" must {
        "redirect to WhoWasInPaidWorkPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidWorkPY) thenReturn Some(true)

          navigator.nextPage(BothPaidWorkPYId, NormalMode).value(answers) mustBe routes.WhoWasInPaidWorkPYController.onPageLoad(NormalMode)
        }

        "redirect to bothAnyTheseBenefitsPY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidWorkPY) thenReturn Some(false)

          navigator.nextPage(BothPaidWorkPYId, NormalMode).value(answers) mustBe routes.BothAnyTheseBenefitsPYController.onPageLoad(NormalMode)
        }

        "redirects to SessionExpired page when there is no value for selection" in {
          val answers = spy(userAnswers())
          when(answers.bothPaidWorkPY) thenReturn None

          navigator.nextPage(BothPaidWorkPYId, NormalMode).value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }

      "Who Was In Paid Work PY" must {
        "redirects to parentEmploymentIncomePY page when user selects You" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(you)

          navigator.nextPage(WhoWasInPaidWorkPYId, NormalMode).value(answers) mustBe routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to partnerEmploymentIncomePY page when user selects Partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(partner)

          navigator.nextPage(WhoWasInPaidWorkPYId, NormalMode).value(answers) mustBe routes.PartnerEmploymentIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to employmentIncomePY page when user selects Both" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(both)

          navigator.nextPage(WhoWasInPaidWorkPYId, NormalMode).value(answers) mustBe routes.EmploymentIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to SessionExpired page when there is no value for the selection" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn None

          navigator.nextPage(WhoWasInPaidWorkPYId, NormalMode).value(answers) mustBe routes.SessionExpiredController.onPageLoad
        }

      }

      "Parent Employment Income PY Route" must {
        "redirects to parent paid pension PY when user is single and provides valid value and lives is single" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.YouPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to parent paid pension PY when user lives with partner and only parent works" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(You)

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.YouPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to partner paid pension PY when user lives with partner and only partner works" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(Partner)

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.PartnerPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to both paid pension PY when user lives with partner and both work" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(Both)

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.BothPaidPensionPYController.onPageLoad(NormalMode)
        }
      }

      "Partner Employment Income PY Route" must {
        "redirects to Both Paid Pension PY Controller when parent was in paid work and partner works too" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(Both)

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.BothPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to Partner Paid Pension PY when when user provides valid value and only partner works" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(Partner)

          navigator.nextPage(PartnerEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.PartnerPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to Parent Paid Pension PY when when user provides valid value and only parent works" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoWasInPaidWorkPY) thenReturn Some(You)

          navigator.nextPage(PartnerEmploymentIncomePYId, NormalMode).value(answers) mustBe routes.YouPaidPensionPYController.onPageLoad(NormalMode)
        }
      }

      "Parent and Partner Employment Income PY Route" must {
        "redirects to both paid pension PY when when user provides valid values" in {
          val answers = spy(userAnswers())
          when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY(12, 20))

          navigator.nextPage(EmploymentIncomePYId, NormalMode).value(answers) mustBe routes.BothPaidPensionPYController.onPageLoad(NormalMode)
        }
      }

      "You get the same Income PY Route" must {
        "redirects to have you had statutory pay when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youGetSameIncomePreviousYear) thenReturn Some(true)

          navigator.nextPage(YouGetSameIncomePreviousYearId, NormalMode).value(answers) mustBe routes.YouStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to parent paid work py controller" in {
          val answers = spy(userAnswers())
          when(answers.youGetSameIncomePreviousYear) thenReturn Some(false)

          navigator.nextPage(YouGetSameIncomePreviousYearId, NormalMode).value(answers) mustBe routes.ParentPaidWorkPYController.onPageLoad()
        }
      }

      "Both get the same Income PY Route" must {
        "redirects to have you had statutory pay when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothGetSameIncomePreviousYear) thenReturn Some(true)

          navigator.nextPage(BothGetSameIncomePreviousYearId, NormalMode).value(answers) mustBe routes.BothStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to have you had statutory pay when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothGetSameIncomePreviousYear) thenReturn Some(false)

          navigator.nextPage(BothGetSameIncomePreviousYearId, NormalMode).value(answers) mustBe routes.BothPaidWorkPYController.onPageLoad()
        }
      }
    }
  }
}
