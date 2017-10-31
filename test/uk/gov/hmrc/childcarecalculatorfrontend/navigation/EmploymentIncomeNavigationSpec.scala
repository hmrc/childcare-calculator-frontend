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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, EmploymentIncomePY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap


class EmploymentIncomeNavigationSpec extends SpecBase with MockitoSugar with OptionValues {

  val navigator = new EmploymentIncomeNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Income Route Navigation" when {

    "in Normal mode" must {
      "Partner Paid Work CY Route" must {
        "redirects to parent employment income CY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.ParentEmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkCY)  thenReturn None

          navigator.nextPage(PartnerPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Parent Paid Work CY Route" must {
        "redirects to partner employment income CY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.PartnerEmploymentIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkCY)  thenReturn None

          navigator.nextPage(ParentPaidWorkCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Parent Employment Income CY Route" must {
        "redirects to parent paid pension CY when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomeCY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.YouPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomeCY)  thenReturn None

          navigator.nextPage(ParentEmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Employment Income CY Route" must {
        "redirects to partner paid pension CY when when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerEmploymentIncomeCY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(PartnerEmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.PartnerPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.partnerEmploymentIncomeCY)  thenReturn None

          navigator.nextPage(PartnerEmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Parent and Partner Employment Income CY Route" must {
        "redirects to both paid pension CY when when user provides valid values" in {
          val answers = spy(userAnswers())
          when(answers.employmentIncomeCY) thenReturn Some(EmploymentIncomeCY("12", "20"))

          navigator.nextPage(EmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.BothPaidPensionCYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.employmentIncomeCY)  thenReturn None

          navigator.nextPage(EmploymentIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

    }
  }

  "Previous Year Income Route Navigation" when {

    "in Normal mode" must {
     "Partner Paid Work PY Route" must {
        "redirects to parent employment income PY when user selects yes or no" in {
          val answers = spy(userAnswers())
          when(answers.partnerPaidWorkPY) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.ParentEmploymentIncomePYController.onPageLoad(NormalMode)
        }

       "redirects to session expired route when relevant answers has no value" in {
         val answers = spy(userAnswers())
         when(answers.partnerPaidWorkPY)  thenReturn None

         navigator.nextPage(PartnerPaidWorkPYId, NormalMode).value(answers) mustBe
           routes.SessionExpiredController.onPageLoad()
       }
      }

      "Parent Paid Work PY Route" must {
       "redirects to partner employment income PY when user selects yes or no" in {
         val answers = spy(userAnswers())
         when(answers.parentPaidWorkPY) thenReturn Some(true) thenReturn Some(false)

         navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
           routes.PartnerEmploymentIncomePYController.onPageLoad(NormalMode)

         navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
           routes.PartnerEmploymentIncomePYController.onPageLoad(NormalMode)
       }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentPaidWorkPY)  thenReturn None

          navigator.nextPage(ParentPaidWorkPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
     }

      "Parent Employment Income PY Route" must {
        "redirects to parent paid pension PY when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomePY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe
            routes.YouPaidPensionPYController.onPageLoad(NormalMode)
        }

        "redirects to session expired route when relevant answers has no value" in {
          val answers = spy(userAnswers())
          when(answers.parentEmploymentIncomePY)  thenReturn None

          navigator.nextPage(ParentEmploymentIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

     "Partner Employment Income PY Route" must {
        "redirects to partner paid pension PY when when user provides valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerEmploymentIncomePY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(PartnerEmploymentIncomePYId, NormalMode).value(answers) mustBe
            routes.PartnerPaidPensionPYController.onPageLoad(NormalMode)
        }

       "redirects to session expired route when relevant answers has no value" in {
         val answers = spy(userAnswers())
         when(answers.partnerEmploymentIncomePY)  thenReturn None

         navigator.nextPage(PartnerEmploymentIncomePYId, NormalMode).value(answers) mustBe
           routes.SessionExpiredController.onPageLoad()
       }
      }

    "Parent and Partner Employment Income PY Route" must {
       "redirects to both paid pension PY when when user provides valid values" in {
         val answers = spy(userAnswers())
         when(answers.employmentIncomePY) thenReturn Some(EmploymentIncomePY("12", "20"))

         navigator.nextPage(EmploymentIncomePYId, NormalMode).value(answers) mustBe
           routes.BothPaidPensionPYController.onPageLoad(NormalMode)
       }

      "redirects to session expired route when relevant answers has no value" in {
        val answers = spy(userAnswers())
        when(answers.employmentIncomePY)  thenReturn None

        navigator.nextPage(EmploymentIncomePYId, NormalMode).value(answers) mustBe
          routes.SessionExpiredController.onPageLoad()
      }
     }

    }
  }


}
