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

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{HouseholdFactory, TaxCredits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap


class StatutoryNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new StatutoryNavigator(new Utils, new TaxCredits(new HouseholdFactory))

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Statutory Route Navigation" when {

    "in Normal mode" must {


      "Your Statutory Pay route" must {
        "redirects to result page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.youStatutoryPay) thenReturn Some(false)

          navigator.nextPage(YouStatutoryPayId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad() //TODO: to be replaced by Results page
        }

        "redirects to yourStatutoryPayType page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.youStatutoryPay) thenReturn Some(true)

          navigator.nextPage(YouStatutoryPayId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youStatutoryPay) thenReturn None

          navigator.nextPage(YouStatutoryPayId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }



      "Both Statutory Pay route" must {
        "redirects to result page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPay) thenReturn Some(false)

          navigator.nextPage(BothStatutoryPayId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad() //TODO: to be replaced by Results page
        }

        "redirects to whoGotStatutoryPay page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPay) thenReturn Some(true)

          navigator.nextPage(BothStatutoryPayId, NormalMode).value(answers) mustBe
            routes.WhoGotStatutoryPayController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPay) thenReturn None

          navigator.nextPage(BothStatutoryPayId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }


      "Who Got Statutory Pay route" must {
        "redirects to yourStatutoryPayType page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.YOU)

          navigator.nextPage(WhoGotStatutoryPayId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
        }

        "redirects to partnerStatutoryPayType page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.PARTNER)

          navigator.nextPage(WhoGotStatutoryPayId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
        }

        "redirects to yourStatutoryPayType page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoGotStatutoryPay) thenReturn Some(YouPartnerBothEnum.BOTH)

          navigator.nextPage(WhoGotStatutoryPayId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayTypeController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGotStatutoryPay) thenReturn None

          navigator.nextPage(WhoGotStatutoryPayId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Your Statutory Pay Type route" must {
        "redirects to yourStatutoryStartDate page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayType) thenReturn
            Some("maternity") thenReturn Some("paternity") thenReturn Some("adoption") thenReturn Some("shared parental")

          navigator.nextPage(YourStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.YourStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(YourStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.YourStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(YourStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.YourStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(YourStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.YourStatutoryStartDateController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayType) thenReturn None

          navigator.nextPage(YourStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Pay Type route" must {
        "redirects to partnerStatutoryStartDate page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayType) thenReturn
            Some("maternity") thenReturn Some("paternity") thenReturn Some("adoption") thenReturn Some("shared parental")

          navigator.nextPage(PartnerStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(PartnerStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(PartnerStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryStartDateController.onPageLoad(NormalMode)
          navigator.nextPage(PartnerStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryStartDateController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayType) thenReturn None

          navigator.nextPage(PartnerStatutoryPayTypeId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Your Statutory Start Date route" must {
        "redirects to yourStatutoryWeeks page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryStartDate) thenReturn Some(new LocalDate(2017, 2, 1))

          navigator.nextPage(YourStatutoryStartDateId, NormalMode).value(answers) mustBe
            routes.YourStatutoryWeeksController.onPageLoad(NormalMode)

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryStartDate) thenReturn None

          navigator.nextPage(YourStatutoryStartDateId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Start Date route" must {
        "redirects to partnerStatutoryWeeks page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryStartDate) thenReturn Some(new LocalDate(2017, 2, 1))

          navigator.nextPage(PartnerStatutoryStartDateId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryWeeksController.onPageLoad(NormalMode)

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryStartDate) thenReturn None

          navigator.nextPage(PartnerStatutoryStartDateId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Your Statutory Weeks route" must {
        "redirects to yourStatutoryPayBeforeTax page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryWeeks) thenReturn Some(12)

          navigator.nextPage(YourStatutoryWeeksId, NormalMode).value(answers) mustBe
            routes.YourStatutoryPayBeforeTaxController.onPageLoad(NormalMode)

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryWeeks) thenReturn None

          navigator.nextPage(YourStatutoryWeeksId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Weeks route" must {
        "redirects to partnerStatutoryPayBeforeTax page when user selects some value" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryWeeks) thenReturn Some(12)

          navigator.nextPage(PartnerStatutoryWeeksId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayBeforeTaxController.onPageLoad(NormalMode)

        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryWeeks) thenReturn None

          navigator.nextPage(PartnerStatutoryWeeksId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

    }
  }


}
