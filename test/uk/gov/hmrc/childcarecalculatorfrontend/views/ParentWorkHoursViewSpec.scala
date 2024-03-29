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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ParentWorkHoursForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewBigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.parentWorkHours

class ParentWorkHoursViewSpec extends NewBigDecimalViewBehaviours {

  val messageKeyPrefix = "parentWorkHours"
  val view = application.injector.instanceOf[parentWorkHours]

  def createView = () => view(new ParentWorkHoursForm(frontendAppConfig).apply(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => view(form, NormalMode)(fakeRequest, messages)

  val form = new ParentWorkHoursForm(frontendAppConfig).apply()

  "ParentWorkHours view" must {
    behave like normalPage(createView, messageKeyPrefix, "para1")

    behave like pageWithBackLink(createView)

    behave like bigDecimalPage(createViewUsingForm, messageKeyPrefix, routes.ParentWorkHoursController.onSubmit(NormalMode).url)
  }
}
