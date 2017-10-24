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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BothNoWeeksStatPayPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, BothNoWeeksStatPayPY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothNoWeeksStatPayPY

class BothNoWeeksStatPayPYViewSpec extends QuestionViewBehaviours[BothNoWeeksStatPayPY] {

  val messageKeyPrefix = "bothNoWeeksStatPayPY"

  override val form = new BothNoWeeksStatPayPYForm(frontendAppConfig).apply

  def createView = () => bothNoWeeksStatPayPY(frontendAppConfig, new BothNoWeeksStatPayPYForm(frontendAppConfig).apply, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BothNoWeeksStatPayPY]) => bothNoWeeksStatPayPY(frontendAppConfig,
    form,
    NormalMode)(fakeRequest, messages)

  "BothNoWeeksStatPayPY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm,
      messageKeyPrefix,
      routes.BothNoWeeksStatPayPYController.onSubmit(NormalMode).url,
      "youNoWeeksYouStatPayPY",
      "partnerWeeksYouStatPayPY")
  }
}