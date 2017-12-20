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
import org.scalatest.OptionValues
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{PartnerIncomeInfoId, BothIncomeInfoPYId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeInfoNavigationSpec extends SpecBase with MockitoSugar with OptionValues {

  val navigator = new IncomeInfoNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Income Route Navigation" when {

    "in Normal mode" must {
      "NextPageUrlCY" must {
        "return  PartnerPaidWorkCY page when parent in paid work and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)

          navigator.nextPage(PartnerIncomeInfoId, NormalMode).value(answers) mustBe
            routes.PartnerPaidWorkCYController.onPageLoad(NormalMode)
        }

        "return ParentPaidWorkCY page when partner in paid work and lives with partneBothOtherIncomeThisYearIdr" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)

          navigator.nextPage(PartnerIncomeInfoId, NormalMode).value(answers) mustBe
            routes.ParentPaidWorkCYController.onPageLoad(NormalMode)
        }

        "return EmploymentIncomeCY page when both in paid work and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)

          navigator.nextPage(PartnerIncomeInfoId, NormalMode).value(answers) mustBe
            routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "return sessionExpired page when there is no value for paid work and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn None

          navigator.nextPage(PartnerIncomeInfoId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }

  }

  "Previous Year Income Route Navigation" when {

    "in Normal mode" must {
      "NextPageUrlPY" must {
        "return BothPaidWorkPY page when parent/partner/both in paid work and lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(You) thenReturn Some(Partner) thenReturn Some(Both)

          navigator.nextPage(BothIncomeInfoPYId, NormalMode).value(answers) mustBe
            routes.BothPaidWorkPYController.onPageLoad(NormalMode)
          navigator.nextPage(BothIncomeInfoPYId, NormalMode).value(answers) mustBe
            routes.BothPaidWorkPYController.onPageLoad(NormalMode)
          navigator.nextPage(BothIncomeInfoPYId, NormalMode).value(answers) mustBe
            routes.BothPaidWorkPYController.onPageLoad(NormalMode)
        }

        "return sessionExpired page when user lives with partner and value is not present" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn None

          navigator.nextPage(BothIncomeInfoPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }
    }

  }
}
