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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.StatutoryPayAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, StatutoryPayAmountCY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.statutoryPayAmountCY

class StatutoryPayAmountCYViewSpec extends QuestionViewBehaviours[StatutoryPayAmountCY] {

  override val form = new StatutoryPayAmountCYForm(frontendAppConfig).apply()
  val messageKeyPrefix = "statutoryPayAmountCY"

  def createView = () => statutoryPayAmountCY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[StatutoryPayAmountCY]) => statutoryPayAmountCY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "StatutoryPayAmountCY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.StatutoryPayAmountCYController.onSubmit(NormalMode).url, "parentStatutoryPayAmount", "partnerStatutoryPayAmount")
  }
}
