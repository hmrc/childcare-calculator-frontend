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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.universalCredit

class UniversalCreditViewSpec extends NewYesNoViewBehaviours {

  val view = application.injector.instanceOf[universalCredit]

  override val form = BooleanForm()

  val messageKeyPrefix        = "universalCredit"
  val messageKeyPartnerPrefix = "universalCreditPartner"

  def createView(isPartner: Option[Boolean]) = () => view(frontendAppConfig, form, isPartner)(fakeRequest, messages)

  def createViewUsingForm(isPartner: Option[Boolean]) = (form: Form[Boolean]) =>
    view(frontendAppConfig, form, isPartner)(fakeRequest, messages)

  "UniversalCredit view when there is partner" must {

    behave.like(normalPage(createView(Some(true)), messageKeyPartnerPrefix))

    behave.like(pageWithBackLink(createView(Some(true))))

    behave.like(
      yesNoPage(
        createViewUsingForm(Some(true)),
        messageKeyPartnerPrefix,
        routes.UniversalCreditController.onSubmit().url
      )
    )
  }

  "UniversalCredit view when there is no partner" must {

    behave.like(normalPage(createView(Some(false)), messageKeyPrefix))

    behave.like(pageWithBackLink(createView(Some(false))))

    behave.like(
      yesNoPage(
        createViewUsingForm(Some(false)),
        messageKeyPrefix,
        routes.UniversalCreditController.onSubmit().url
      )
    )
  }

}
