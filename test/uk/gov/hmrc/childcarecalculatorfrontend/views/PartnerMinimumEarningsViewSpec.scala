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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerMinimumEarnings

class PartnerMinimumEarningsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "partnerMinimumEarnings"

  def createView = () => partnerMinimumEarnings(frontendAppConfig, BooleanForm(), NormalMode, 0)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => partnerMinimumEarnings(frontendAppConfig, form, NormalMode, 0)(fakeRequest, messages)

  def createViewWithAmount = (amount: BigDecimal) => partnerMinimumEarnings(frontendAppConfig, form, NormalMode, amount)(fakeRequest, messages)

  "PartnerMinimumEarnings view" must {

    behave like normalPageWithTitleAsString(createView, messageKeyPrefix, messagesApi("partnerMinimumEarnings.heading", 0))

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.PartnerMinimumEarningsController.onSubmit(NormalMode).url)

    "show correct hint text and value of minimum earnings" in {

      val amount = BigDecimal(40)
      val doc = asDocument(createViewWithAmount(amount))

      assertContainsText(doc, messagesApi("partnerMinimumEarnings.hint"))
      assertContainsText(doc, messagesApi("partnerMinimumEarnings.heading", amount))
    }
  }
}
