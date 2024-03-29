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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryWeeksForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum.MATERNITY
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.StatutoryPayWeeksViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewIntViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryWeeks

class YourStatutoryWeeksViewSpec extends NewIntViewBehaviours {

  val view = application.injector.instanceOf[yourStatutoryWeeks]
  val messageKeyPrefix = "yourStatutoryWeeks"

  val statutoryType = MATERNITY

  val form = new YourStatutoryWeeksForm(frontendAppConfig).apply(statutoryType, statutoryType.toString)

  val viewModel = new StatutoryPayWeeksViewModel(frontendAppConfig, statutoryType)

  def createView = () => view(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Int]) => view(frontendAppConfig, form, NormalMode, viewModel)(fakeRequest, messages)

  "YourStatutoryWeeks view" must {

    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.title", statutoryType)),
      expectedGuidanceKeys = Seq(),
      statutoryType
    )

    behave like pageWithBackLink(createView)

    behave like intPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YourStatutoryWeeksController.onSubmit(NormalMode).url,
      messageDynamicValue = Some("maternity")
     )
  }
}
