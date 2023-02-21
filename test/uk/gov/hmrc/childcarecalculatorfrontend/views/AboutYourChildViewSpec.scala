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
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{AboutYourChildForm}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewDateViewBehaviours, NewQuestionViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild

class AboutYourChildViewSpec extends NewQuestionViewBehaviours[AboutYourChild] with NewDateViewBehaviours[AboutYourChild] {

  val messageKeyPrefix = "aboutYourChild"

  val view = application.injector.instanceOf[aboutYourChild]

  def createView: () => Html = () => createView(0, 1)

  def createViewUsingForm = (form: Form[AboutYourChild]) => view(frontendAppConfig, form, NormalMode, 0, 1)(fakeRequest, messages)

  def createView(index: Int, total: Int): Html =
    view(frontendAppConfig, AboutYourChildForm(), NormalMode, index, total)(fakeRequest, messages)

  override val form = AboutYourChildForm()

  "AboutYourChild view" must {

    behave like normalPage(createView, messageKeyPrefix, "title", "heading")

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.AboutYourChildController.onSubmit(NormalMode, 0).url, "name")

    behave like pageWithDateFields(createViewUsingForm, messageKeyPrefix, routes.AboutYourChildController.onSubmit(NormalMode, 0).url, "dob")

    "use the correct messages when there are multiple children" in {
      val doc = asDocument(createView(0, 2))
      assertContainsText(doc, messages(s"$messageKeyPrefix.title.nth", messages("nth.0")))
    }

    "contain lede guidance for the first child" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.lede"))
    }

    "not contain lede guidance for other children" in {
      val doc = asDocument(createView(1, 2))
      assertNotContainsText(doc, messages(s"$messageKeyPrefix.lede"))
    }
  }
}
