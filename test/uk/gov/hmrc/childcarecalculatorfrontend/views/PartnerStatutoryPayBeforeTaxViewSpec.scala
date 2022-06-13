/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryPayBeforeTax

class PartnerStatutoryPayBeforeTaxViewSpec extends NewYesNoViewBehaviours {

  override val form = BooleanForm()
  val messageKeyPrefix = "partnerStatutoryPayBeforeTax"
  val view = application.injector.instanceOf[partnerStatutoryPayBeforeTax]

  val statutoryType = "maternity"

  def createView = () => view(frontendAppConfig, BooleanForm("partnerStatutoryPayBeforeTax.error.notCompleted", statutoryType), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  "PartnerStatutoryPayBeforeTax view" must {
    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.title", statutoryType)),
      expectedGuidanceKeys = Seq(),
      args = (statutoryType)
    )

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YourStatutoryPayBeforeTaxController.onSubmit(NormalMode).url,
      Some(messages(s"$messageKeyPrefix.heading", statutoryType.toString))
    )
  }
}
