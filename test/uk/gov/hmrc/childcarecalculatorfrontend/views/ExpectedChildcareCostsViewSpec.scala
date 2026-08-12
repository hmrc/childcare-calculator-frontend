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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectedChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{ChildcarePayFrequency, YesNoNotYet}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectedChildcareCosts

class ExpectedChildcareCostsViewSpec extends NewBigDecimalViewBehaviours {

  val messageKeyPrefix             = "expectedChildcareCosts"
  val messageKeyPostfix            = ".notYet"
  val view: expectedChildcareCosts = inject[expectedChildcareCosts]

  override val form: Form[BigDecimal] = ExpectedChildcareCostsForm(ChildcarePayFrequency.Weekly, "Foo")
  val cardinal: String                = messages("nth.0")

  def render(
      form: Form[BigDecimal] = this.form,
      hasCosts: YesNoNotYet = YesNoNotYet.Yes,
      index: Int = 0,
      frequency: ChildcarePayFrequency = ChildcarePayFrequency.Weekly,
      name: String = "Foo"
  ): Html = view(form, hasCosts, index, frequency, name)(using fakeRequest, messages)

  "ExpectedChildcareCosts view" must {

    "user has costs" when
      behave.like(
        normalPageWithTitleParameters(
          () => render(),
          messageKeyPrefix,
          messageKeyPostfix = "",
          Seq("info"),
          args = Seq(ChildcarePayFrequency.Weekly.toString, "Foo"),
          titleArgs = Seq(ChildcarePayFrequency.Weekly.toString, cardinal)
        )
      )

    "user may have costs in the future" when
      behave.like(
        normalPageWithTitleParameters(
          () => render(hasCosts = YesNoNotYet.NotYet),
          messageKeyPrefix,
          messageKeyPostfix,
          Seq(s"info$messageKeyPostfix"),
          args = Seq(ChildcarePayFrequency.Weekly.toString, "Foo"),
          titleArgs = Seq(ChildcarePayFrequency.Weekly.toString, cardinal)
        )
      )

    behave.like(pageWithBackLink(() => render()))

    behave.like(
      bigDecimalPage(
        form => render(form = form),
        messageKeyPrefix,
        routes.ExpectedChildcareCostsController.onSubmit(0).url,
        Some(messages(s"$messageKeyPrefix.heading", ChildcarePayFrequency.Weekly, "Foo"))
      )
    )
  }

}
