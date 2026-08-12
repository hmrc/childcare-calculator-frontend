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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourOtherIncomeAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourOtherIncomeAmountCY

class YourOtherIncomeAmountCYViewSpec extends NewBigDecimalViewBehaviours {

  val view: yourOtherIncomeAmountCY = inject[yourOtherIncomeAmountCY]
  val messageKeyPrefix              = "yourOtherIncomeAmountCY"

  val form: Form[BigDecimal] = new YourOtherIncomeAmountCYForm(frontendAppConfig).apply()

  def render(form: Form[BigDecimal] = this.form): Html = view(form)(using fakeRequest, messages)

  "YourOtherIncomeAmountCY view" must {
    behave.like(normalPage(() => render(form), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render(form)))

    behave.like(
      bigDecimalPage(
        form => render(form = form),
        messageKeyPrefix,
        routes.YourOtherIncomeAmountCYController.onSubmit().url,
        Some(messages(s"$messageKeyPrefix.heading"))
      )
    )
  }

}
