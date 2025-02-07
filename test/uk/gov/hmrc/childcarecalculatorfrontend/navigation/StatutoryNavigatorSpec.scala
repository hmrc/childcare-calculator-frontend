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

import java.time.LocalDate
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, StatutoryPayTypeEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc.ModelFactory
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap


class StatutoryNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new StatutoryNavigator(new Utils, new TaxCredits(new ModelFactory))

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Statutory Route Navigation" when {

    "in Normal mode" must {

      "Partner Statutory Pay Type route" must {

        "redirects to partnerStatutoryPayType page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPay) thenReturn Some(true)

          navigator.nextPage(PartnerStatutoryPayId, NormalMode).value(answers) mustBe
            routes.PartnerStatutoryPayTypeController.onPageLoad(NormalMode)
        }

      }

    }
  }
}
