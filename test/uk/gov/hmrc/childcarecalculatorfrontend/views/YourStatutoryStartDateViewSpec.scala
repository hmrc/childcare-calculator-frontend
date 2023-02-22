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

import org.joda.time.LocalDate
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryStartDateForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, StatutoryPayTypeEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewDateViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryStartDate

class YourStatutoryStartDateViewSpec extends NewDateViewBehaviours[LocalDate] {

  val view = application.injector.instanceOf[yourStatutoryStartDate]
  val messageKeyPrefix = "yourStatutoryStartDate"

  val statutoryType = "maternity"

  def createView = () => view(frontendAppConfig, YourStatutoryStartDateForm(statutoryType), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LocalDate]) => view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  val form = YourStatutoryStartDateForm(statutoryType)

  "YourStatutoryStartDate view" must {

    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages(s"$messageKeyPrefix.title", "maternity"),
      heading = Some(messages(s"$messageKeyPrefix.heading", "maternity")),
      expectedGuidanceKeys = Seq(),
      args = "maternity"
    )

    behave like pageWithBackLink(createView)

    behave like pageWithDateFields(createViewUsingForm, messageKeyPrefix, routes.YourStatutoryStartDateController.onSubmit(NormalMode).url)
  }
}
