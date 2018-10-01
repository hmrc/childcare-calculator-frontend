/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerChildcareVouchers
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{ViewBehaviours, YesNoViewBehaviours}



class PartnerChildcareVouchersViewSpec extends ViewBehaviours with YesNoViewBehaviours {

  val messageKeyPrefix = "partnerChildcareVouchers"

  def createView = () => partnerChildcareVouchers(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => partnerChildcareVouchers(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "PartnerChildcareVouchers view" must {
    behave like normalPage(createView, messageKeyPrefix, "heading")

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.PartnerChildcareVouchersController.onSubmit(NormalMode).url)

  }
}
