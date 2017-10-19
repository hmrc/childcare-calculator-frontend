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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AboutYourChildForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, AboutYourChild}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild

class AboutYourChildViewSpec extends QuestionViewBehaviours[AboutYourChild] {

  val messageKeyPrefix = "aboutYourChild"

  def createView = () => aboutYourChild(frontendAppConfig, AboutYourChildForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[AboutYourChild]) => aboutYourChild(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = AboutYourChildForm()

  "AboutYourChild view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.AboutYourChildController.onSubmit(NormalMode).url, "field1", "field2")
  }
}
