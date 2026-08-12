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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.universalCredit

class UniversalCreditViewSpec extends NewYesNoViewBehaviours {

  val view: universalCredit = inject[universalCredit]

  override val form: Form[Boolean] = BooleanForm()

  val messageKeyPrefix        = "universalCredit"
  val messageKeyPartnerPrefix = "universalCreditPartner"

  def render(form: Form[Boolean] = this.form, isPartner: Option[Boolean]): Html =
    view(form, isPartner)(using fakeRequest, messages)

  "UniversalCredit view when there is partner" must {
    val isPartner = Some(true)

    behave.like(normalPage(() => render(isPartner = isPartner), messageKeyPartnerPrefix))

    behave.like(pageWithBackLink(() => render(isPartner = isPartner)))

    behave.like(
      yesNoPage(
        form => render(form = form, isPartner = isPartner),
        messageKeyPartnerPrefix,
        routes.UniversalCreditController.onSubmit().url
      )
    )
  }

  "UniversalCredit view when there is no partner" must {
    val isPartner = Some(false)

    behave.like(normalPage(() => render(isPartner = isPartner), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render(isPartner = isPartner)))

    behave.like(
      yesNoPage(
        form => render(form = form, isPartner = isPartner),
        messageKeyPrefix,
        routes.UniversalCreditController.onSubmit().url
      )
    )
  }

}
