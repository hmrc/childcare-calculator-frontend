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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{BothBenefitsIncomePY, BenefitsIncomeCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class BenefitsNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new BenefitsNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Benefits Route Navigation" when {

    "in Normal mode" must {
      "Parent Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn Some(true)

          navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode).value(answers) mustBe
            routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to yourStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn Some(false)

          /*navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefits) thenReturn None

          navigator.nextPage(YouAnyTheseBenefitsIdCY, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Benefits CY Route" must {
        "redirects to partnerBenefitsIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn Some(true)

          navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to partnerStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn Some(false)

         /* navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsCY) thenReturn None

          navigator.nextPage(PartnerAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Benefits CY Route" must {
        "redirects to whosHadBenefits page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(true)

          navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.WhosHadBenefitsController.onPageLoad(NormalMode)
        }

        "redirects to bothStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn Some(false)

        /*  navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsCY) thenReturn None

          navigator.nextPage(BothAnyTheseBenefitsCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Whos Had Benefits CY Route" must {
        "redirects to youBenefitsIncomeCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(YOU)

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to partnerBenefitsIncomeCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(PARTNER)

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to benefitsIncomeCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn Some(BOTH)

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefits) thenReturn None

          navigator.nextPage(WhosHadBenefitsId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "You Benefits Income CY Route" must {
        "redirects to YourStatutoryPayCY page when user provides valid input,lives with partner and " +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

         /* navigator.nextPage(YouBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to YourStatutoryPayCY page when user provides valid input, does not live with partner and " +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.areYouInPaidWork) thenReturn Some(true)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

         /* navigator.nextPage(YouBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to SessionExpired page when user provides valid input, lives with partner and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(YouBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to BothStatutoryPayCY page when user provides valid input, live with partner" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.youBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

         /* navigator.nextPage(YouBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youBenefitsIncomeCY) thenReturn None

          navigator.nextPage(YouBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Benefits Income CY Route" must {
        "redirects to partnerStatutoryPayCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.partnerBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

         /* navigator.nextPage(PartnerBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to BothStatutoryPayCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.partnerBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

        /*  navigator.nextPage(PartnerBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to SessionExpired page when user provides valid input and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.partnerBenefitsIncomeCY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(PartnerBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerBenefitsIncomeCY) thenReturn None

          navigator.nextPage(PartnerBenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Benefits Income CY Route" must {
        "redirects to bothStatutoryPayCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.benefitsIncomeCY) thenReturn Some(BenefitsIncomeCY("23", "23"))

          /*navigator.nextPage(BenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayCYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.benefitsIncomeCY) thenReturn None

          navigator.nextPage(BenefitsIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }

  "Previous Year Benefits Route Navigation" when {

    "in Normal mode" must {
      "Parent Benefits PY Route" must {
        "redirects to youBenefitsIncomePY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefitsPY) thenReturn Some(true)

          navigator.nextPage(YouAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to yourStatutoryPayPY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefitsPY) thenReturn Some(false)

         /* navigator.nextPage(YouAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youAnyTheseBenefitsPY) thenReturn None

          navigator.nextPage(YouAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Benefits PY Route" must {
        "redirects to partnerBenefitsIncomePY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsPY) thenReturn Some(true)

          navigator.nextPage(PartnerAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
        }

       /* "redirects to partnerStatutoryPayCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsPY) thenReturn Some(false)

          navigator.nextPage(PartnerAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayPYController.onPageLoad(NormalMode)
        }*/

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyTheseBenefitsPY) thenReturn None

          navigator.nextPage(PartnerAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Benefits PY Route" must {
        "redirects to whosHadBenefitsPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsPY) thenReturn Some(true)

          navigator.nextPage(BothAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.WhosHadBenefitsPYController.onPageLoad(NormalMode)
        }

        "redirects to bothStatutoryPayPY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsPY) thenReturn Some(false)

          /*navigator.nextPage(BothAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothAnyTheseBenefitsPY) thenReturn None

          navigator.nextPage(BothAnyTheseBenefitsPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Whos Had Benefits PY Route" must {
        "redirects to youBenefitsIncomePY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefitsPY) thenReturn Some(YOU)

          navigator.nextPage(WhosHadBenefitsPYId, NormalMode).value(answers) mustBe
            routes.YouBenefitsIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to partnerBenefitsIncomePY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefitsPY) thenReturn Some(PARTNER)

          navigator.nextPage(WhosHadBenefitsPYId, NormalMode).value(answers) mustBe
            routes.PartnerBenefitsIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to bothBenefitsIncomePY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefitsPY) thenReturn Some(BOTH)

          navigator.nextPage(WhosHadBenefitsPYId, NormalMode).value(answers) mustBe
            routes.BothBenefitsIncomePYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whosHadBenefitsPY) thenReturn None

          navigator.nextPage(WhosHadBenefitsPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "You Benefits Income PY Route" must {
        "redirects to yourStatutoryPayPY page when user provides valid input, lives with partner and" +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.youBenefitsIncomePY) thenReturn Some(BigDecimal(23))

          /*navigator.nextPage(YouBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to yourStatutoryPayPY page when user provides valid input, does not lives with partner and" +
          "parent in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.areYouInPaidWork) thenReturn Some(true)
          when(answers.youBenefitsIncomePY) thenReturn Some(BigDecimal(23))

         /* navigator.nextPage(YouBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to SessionExpired page when user provides valid input, lives with partner and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.youBenefitsIncomePY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(YouBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to BothStatutoryPayPY page when user provides valid input, lives with partner" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.youBenefitsIncomePY) thenReturn Some(BigDecimal(23))

        /*  navigator.nextPage(YouBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youBenefitsIncomePY) thenReturn None

          navigator.nextPage(YouBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Benefits Income PY Route" must {
       /* "redirects to partnerStatutoryPayPY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
          when(answers.partnerBenefitsIncomePY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(PartnerBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayPYController.onPageLoad(NormalMode)
        }*/

        "redirects to BothStatutoryPayPY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
          when(answers.partnerBenefitsIncomePY) thenReturn Some(BigDecimal(23))

        /*  navigator.nextPage(PartnerBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to SessionExpired page when user provides valid input and " +
          "partner in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(You)
          when(answers.partnerBenefitsIncomePY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(PartnerBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerBenefitsIncomePY) thenReturn None

          navigator.nextPage(PartnerBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Benefits Income PY Route" must {
        "redirects to bothStatutoryPayPY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.bothBenefitsIncomePY) thenReturn Some(BothBenefitsIncomePY("23", "23"))

          /*navigator.nextPage(BothBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.BothStatutoryPayPYController.onPageLoad(NormalMode)*/
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothBenefitsIncomePY) thenReturn None

          navigator.nextPage(BothBenefitsIncomePYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
