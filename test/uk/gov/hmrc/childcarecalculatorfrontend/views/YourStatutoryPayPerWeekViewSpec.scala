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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryPayPerWeekForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryPayPerWeek

class YourStatutoryPayPerWeekViewSpec extends NewBigDecimalViewBehaviours {

  val view = application.injector.instanceOf[yourStatutoryPayPerWeek]
  val messageKeyPrefix = "yourStatutoryPayPerWeek"

  val statutoryType = "maternity"

  val form = YourStatutoryPayPerWeekForm(statutoryType)

  def createView = () => view(frontendAppConfig, YourStatutoryPayPerWeekForm(statutoryType), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  def createViewWithStatutoryType = (statutoryType: String) => view(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)


  "YourStatutoryPayPerWeek view" must {
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

    behave like bigDecimalPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YourStatutoryPayPerWeekController.onSubmit(NormalMode).url,
      Some(messages(s"$messageKeyPrefix.heading", statutoryType) + messages(""))
    )
  }

  "show correct statutory pay type" in {
    val doc = asDocument(createViewWithStatutoryType(statutoryType))
    assertContainsText(doc, messages(s"$messageKeyPrefix.heading", statutoryType))
  }
}
