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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childApprovedEducation

class ChildApprovedEducationViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "childApprovedEducation"

  def createView = () => childApprovedEducation(frontendAppConfig, BooleanForm(), NormalMode, 0, "Foo")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => childApprovedEducation(frontendAppConfig, form, NormalMode, 0, "Foo")(fakeRequest, messages)

  "ChildApprovedEducation view" must {

    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      title = messages(s"$messageKeyPrefix.title"),
      heading = Some(messages(s"$messageKeyPrefix.heading", "Foo"))
    )

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.ChildApprovedEducationController.onSubmit(NormalMode, 0).url,
      legend = Some(messages(s"$messageKeyPrefix.heading", "Foo"))
    )

    behave like pageWithBackLink(createView)
  }
}
