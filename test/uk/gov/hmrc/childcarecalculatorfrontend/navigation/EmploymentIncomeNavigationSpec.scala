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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY,NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap


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
}
