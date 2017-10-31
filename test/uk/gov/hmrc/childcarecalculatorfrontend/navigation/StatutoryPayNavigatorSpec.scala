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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class StatutoryPayNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new StatutoryPayNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Previous Year Statutory Pay Route Navigation" when {

    "in Normal mode" must {
      "Parent Statutory Pay PY Route" must {
        "redirects to YouNoWeeksStatPayPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn Some(true)

          navigator.nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn Some(false)

          navigator.nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn None

          navigator.nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
    }

      "Partner Statutory Pay PY Route" must {
        "redirects to PartnerNoWeeksStatPayPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn Some(true)

          navigator.nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn Some(false)

          navigator.nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn None

          navigator.nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Statutory Pay PY Route" must {
        "redirects to WhoGetsStatutoryPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn Some(true)

          navigator.nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.WhoGetsStatutoryPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn Some(false)

          navigator.nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn None

          navigator.nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Statutory PY Route" must {
        "redirects to YouNoWeeksStatPayPY page when user selects you" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(You)

          navigator.nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerNoWeeksStatPayPY page when user selects partner" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(Partner)

          navigator.nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to BothNoWeeksStatPayPY page when user selects both" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(Both)

          navigator.nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.BothNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn None

          navigator.nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "You No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayPY) thenReturn Some(12)

          navigator.nextPage(YouNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayPY) thenReturn None

          navigator.nextPage(YouNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayPY) thenReturn Some(12)

          navigator.nextPage(PartnerNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayPY) thenReturn None

          navigator.nextPage(PartnerNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayPY) thenReturn Some(BothNoWeeksStatPayPY(12,12))

          navigator.nextPage(BothNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayPY) thenReturn None

          navigator.nextPage(BothNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Your Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayAmountPY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(YourStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayAmountPY) thenReturn None

          navigator.nextPage(YourStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Partner Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayAmountPY) thenReturn Some(BigDecimal(12))

          navigator.nextPage(PartnerStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayAmountPY) thenReturn None

          navigator.nextPage(PartnerStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.statutoryPayAmountPY) thenReturn Some(StatutoryPayAmountPY("12", "12"))

          navigator.nextPage(StatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.statutoryPayAmountPY) thenReturn None

          navigator.nextPage(StatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

  }

  }
}
