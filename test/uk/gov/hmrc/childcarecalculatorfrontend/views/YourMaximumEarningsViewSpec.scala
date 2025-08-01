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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMaximumEarnings

class YourMaximumEarningsViewSpec extends NewYesNoViewBehaviours {

  val view                         = application.injector.instanceOf[yourMaximumEarnings]
  val messageKeyPrefix             = "yourMaximumEarnings"
  override val form: Form[Boolean] = BooleanForm()

  def createView = () => view(frontendAppConfig, BooleanForm())(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form)(fakeRequest, messages)

  "YourMaximumEarnings view" must {
    behave.like(normalPage(createView, messageKeyPrefix))

    behave.like(pageWithBackLink(createView))

    behave.like(
      yesNoPage(
        createViewUsingForm,
        messageKeyPrefix,
        routes.YourMaximumEarningsController.onSubmit().url,
        legend = Some(messages(s"$messageKeyPrefix.form"))
      )
    )

    "show correct guidance" in {
      val doc = asDocument(createView())

      assertContainsText(doc, messages(s"$messageKeyPrefix.para"))
      assertContainsLinkWithHref(
        doc,
        messages(s"$messageKeyPrefix.linkText"),
        "https://www.gov.uk/guidance/adjusted-net-income"
      )
    }
  }

}
