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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerMinimumEarnings

class PartnerMinimumEarningsViewSpec extends NewYesNoViewBehaviours {

  override val form: Form[Boolean] = BooleanForm()
  val messageKeyPrefix = "partnerMinimumEarnings"
  val view = application.injector.instanceOf[partnerMinimumEarnings]

  def createView = () => view(frontendAppConfig, BooleanForm(), NormalMode, 0)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, 0)(fakeRequest, messages)

  def createViewWithAmount = (amount: BigDecimal) => view(frontendAppConfig, form, NormalMode, amount)(fakeRequest, messages)

  "PartnerMinimumEarnings view" must {

    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages("partnerMinimumEarnings.heading", 0),
      heading = Some(""),
      expectedGuidanceKeys= Seq(),
      args = 0
    )

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.PartnerMinimumEarningsController.onSubmit(NormalMode).url,
      legend = Some(messages(s"$messageKeyPrefix.heading", 0))
    )

    "show correct hint text and value of minimum earnings" in {

      val amount = BigDecimal(40)
      val doc = asDocument(createViewWithAmount(amount))

      assertContainsText(doc, messages("partnerMinimumEarnings.hint"))
      assertContainsText(doc, messages("partnerMinimumEarnings.heading", amount))
    }
  }
}
