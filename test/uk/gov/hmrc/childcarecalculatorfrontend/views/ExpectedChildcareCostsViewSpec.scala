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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectedChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency.WEEKLY
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectedChildcareCosts

class ExpectedChildcareCostsViewSpec extends NewBigDecimalViewBehaviours {

  val messageKeyPrefix  = "expectedChildcareCosts"
  val messageKeyPostfix = ".notYet"
  val view              = application.injector.instanceOf[expectedChildcareCosts]

  def createView = () =>
    view(frontendAppConfig, ExpectedChildcareCostsForm(WEEKLY, "Foo"), YES, 0, WEEKLY, "Foo", NormalMode)(
      fakeRequest,
      messages
    )

  def createViewNotYet = () =>
    view(frontendAppConfig, ExpectedChildcareCostsForm(WEEKLY, "Foo"), NOTYET, 0, WEEKLY, "Foo", NormalMode)(
      fakeRequest,
      messages
    )

  def createViewUsingForm = (form: Form[BigDecimal]) =>
    view(frontendAppConfig, form, YES, 0, WEEKLY, "Foo", NormalMode)(fakeRequest, messages)

  val form     = ExpectedChildcareCostsForm(WEEKLY, "Foo")
  val cardinal = messages("nth.0")

  "ExpectedChildcareCosts view" must {

    "user has costs" when
      behave.like(
        normalPageWithTitleParameters(
          createView,
          messageKeyPrefix,
          messageKeyPostfix = "",
          Seq("info"),
          args = Seq(WEEKLY.toString, "Foo"),
          titleArgs = Seq(WEEKLY.toString, cardinal)
        )
      )

    "user may have costs in the future" when
      behave.like(
        normalPageWithTitleParameters(
          createViewNotYet,
          messageKeyPrefix,
          messageKeyPostfix,
          Seq(s"info$messageKeyPostfix"),
          args = Seq(WEEKLY.toString, "Foo"),
          titleArgs = Seq(WEEKLY.toString, cardinal)
        )
      )

    behave.like(pageWithBackLink(createView))

    behave.like(
      bigDecimalPage(
        createViewUsingForm,
        messageKeyPrefix,
        routes.ExpectedChildcareCostsController.onSubmit(NormalMode, 0).url,
        Some(messages(s"$messageKeyPrefix.heading", WEEKLY, "Foo"))
      )
    )
  }

}
