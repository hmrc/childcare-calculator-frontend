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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap


class CurrentYearIncomeNavigationSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator(new Schemes())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Income Route Navigation" when {

    "in Normal mode" must {
      "Partner Paid Work CY Route" must {
        "redirects to parent employment income CY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode)(answers) mustBe
            routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode)(answers) mustBe
            routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)
        }
      }

      "Parent Paid Work CY Route" must {
        "redirects to partner employment income CY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode)(answers) mustBe
            routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode)(answers) mustBe
            routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)
        }
      }

      "Parent Employment Income CY Route" must {
        "redirects to parent paid pension CY when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode)(answers) mustBe
            routes.YouPaidPensionCYController.onPageLoad(NormalMode)
        }
      }

      "Partner Employment Income CY Route" must {
        "redirects to partner paid pension CY when when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(PartnerEmploymentIncomeCYId, NormalMode)(answers) mustBe
            routes.PartnerPaidPensionCYController.onPageLoad(NormalMode)
        }
      }

      "Parent and Partner Employment Income CY Route" must {
        "redirects to both paid pension CY when when user provides valid values" in {
          val answers = spy(userAnswers())
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY("12", "20"))

          navigator.nextPage(EmploymentIncomeCYId, NormalMode)(answers) mustBe
            routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }
      }

    }
  }

}
