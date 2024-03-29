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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryStartDateForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewDateViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryStartDate

import java.time.LocalDate

class YourDateViewSpec extends NewDateViewBehaviours[LocalDate] {

  val view: yourStatutoryStartDate = application.injector.instanceOf[yourStatutoryStartDate]
  val messageKeyPrefix = "yourStatutoryStartDate"
  val statutoryType = "maternity"
  val form: Form[LocalDate] = YourStatutoryStartDateForm(statutoryType)

  def createView: () => HtmlFormat.Appendable = () => view(frontendAppConfig, YourStatutoryStartDateForm(statutoryType), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm: Form[LocalDate] => HtmlFormat.Appendable = (form: Form[LocalDate]) => view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)


  "YourStatutoryStartDate view" must {

    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.heading", statutoryType)),
      expectedGuidanceKeys = Seq(),
      args = statutoryType
    )

    behave like pageWithBackLink(createView)

    behave like pageWithDateFields(createViewUsingForm, messageKeyPrefix, routes.YourStatutoryStartDateController.onSubmit(NormalMode).url)
  }
}
