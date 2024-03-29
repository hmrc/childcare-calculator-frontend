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
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedTwo

class ChildAgedTwoViewSpec extends NewYesNoViewBehaviours {

  override val form = BooleanForm()
  val view = application.injector.instanceOf[childAgedTwo]
  val messageKeyPrefix = "childAgedTwo"
  val location = Location.ENGLAND

  def createView = () => view(frontendAppConfig, BooleanForm(), NormalMode, location)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, location)(fakeRequest, messages)

  "ChildAgedTwo view" must {

    behave like normalPage(createView, messageKeyPrefix, s"guidance.$location")

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.ChildAgedTwoController.onSubmit(NormalMode).url, None)
  }
}
