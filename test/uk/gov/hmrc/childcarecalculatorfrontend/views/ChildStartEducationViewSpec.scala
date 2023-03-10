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

import java.time.LocalDate
import play.api.data.{Form, Forms}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildStartEducationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewDateViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childStartEducation

class ChildStartEducationViewSpec extends NewDateViewBehaviours[LocalDate] {

  val messageKeyPrefix = "childStartEducation"
  val view = application.injector.instanceOf[childStartEducation]
  val validBirthday = LocalDate.of(LocalDate.now.minusYears(17).getYear, 2, 1)

  def createView = () => view(frontendAppConfig, ChildStartEducationForm(validBirthday), NormalMode, 0, "Foo")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LocalDate]) => view(frontendAppConfig, form, NormalMode, 0, "Foo")(fakeRequest, messages)

  val form = ChildStartEducationForm(validBirthday)

  "ChildStartEducation view" must {

    behave like normalPageWithTitleAsString(
      view = createView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyPostfix = "",
      title = messages(s"$messageKeyPrefix.title"),
      heading = Some(messages(s"$messageKeyPrefix.heading", "Foo")),
      expectedGuidanceKeys = Seq(),
      args = "Foo"
    )

    behave like pageWithBackLink(createView)

    behave like pageWithDateFields(createViewUsingForm, messageKeyPrefix, routes.AboutYourChildController.onSubmit(NormalMode, 0).url, "date")
  }
}
