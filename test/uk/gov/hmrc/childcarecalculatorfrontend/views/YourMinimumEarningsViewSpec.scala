/*
 * Copyright 2020 HM Revenue & Customs
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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMinimumEarnings

class YourMinimumEarningsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "yourMinimumEarnings"

  def createView = () => yourMinimumEarnings(frontendAppConfig, BooleanForm(), NormalMode, 0)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => yourMinimumEarnings(frontendAppConfig, form, NormalMode, 0)(fakeRequest, messages)

  def createViewWithAmount = (amount: BigDecimal) => yourMinimumEarnings(frontendAppConfig, form, NormalMode, amount)(fakeRequest, messages)

  "YourMinimumEarnings view" must {

    behave like normalPageWithTitleAsString(createView, messageKeyPrefix, messages("yourMinimumEarnings.heading", 0))

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YourMinimumEarningsController.onSubmit(NormalMode).url,
      legend = Some(messages(s"$messageKeyPrefix.heading", 0))
    )

    "show correct hint text and value of minimum earnings" in {

      val amount = BigDecimal(40)
      val doc = asDocument(createViewWithAmount(amount))

      assertContainsText(doc, messages("yourMinimumEarnings.hint"))
      assertContainsText(doc, messages("yourMinimumEarnings.heading", amount))
    }
  }
}
