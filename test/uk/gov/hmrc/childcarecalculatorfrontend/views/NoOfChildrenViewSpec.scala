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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.NoOfChildrenForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewIntViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.noOfChildren

class NoOfChildrenViewSpec extends NewIntViewBehaviours {

  val view             = application.injector.instanceOf[noOfChildren]
  val messageKeyPrefix = "noOfChildren"

  val NoOfChildrenForm = new NoOfChildrenForm(frontendAppConfig).apply()

  def createView = () => view(frontendAppConfig, NoOfChildrenForm)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Int]) => view(frontendAppConfig, form)(fakeRequest, messages)

  val form = NoOfChildrenForm

  "NoOfChildren view" must {
    behave.like(normalPage(createView, messageKeyPrefix))

    behave.like(pageWithBackLink(createView))

    behave.like(intPage(createViewUsingForm, messageKeyPrefix, routes.NoOfChildrenController.onSubmit().url))
  }

}
