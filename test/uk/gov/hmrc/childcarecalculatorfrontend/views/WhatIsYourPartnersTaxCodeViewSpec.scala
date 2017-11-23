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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhatIsYourPartnersTaxCodeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.StringViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatIsYourPartnersTaxCode

class WhatIsYourPartnersTaxCodeViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "whatIsYourPartnersTaxCode"
  val whatIsYourPartnersTaxCodeForm = new WhatIsYourPartnersTaxCodeForm(frontendAppConfig).apply()

  def createView = () => whatIsYourPartnersTaxCode(frontendAppConfig, whatIsYourPartnersTaxCodeForm, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => whatIsYourPartnersTaxCode(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  val form = whatIsYourPartnersTaxCodeForm

  "WhatIsYourPartnersTaxCode view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like textPage(createViewUsingForm, messageKeyPrefix, routes.WhatIsYourPartnersTaxCodeController.onSubmit(NormalMode).url)
  }
}
