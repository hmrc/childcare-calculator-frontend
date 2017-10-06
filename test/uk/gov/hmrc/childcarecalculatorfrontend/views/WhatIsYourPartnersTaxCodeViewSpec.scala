package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhatIsYourPartnersTaxCodeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.IntViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatIsYourPartnersTaxCode

class WhatIsYourPartnersTaxCodeViewSpec extends IntViewBehaviours {

  val messageKeyPrefix = "whatIsYourPartnersTaxCode"

  def createView = () => whatIsYourPartnersTaxCode(frontendAppConfig, WhatIsYourPartnersTaxCodeForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Int]) => whatIsYourPartnersTaxCode(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  val form = WhatIsYourPartnersTaxCodeForm()

  "WhatIsYourPartnersTaxCode view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like intPage(createViewUsingForm, messageKeyPrefix, routes.WhatIsYourPartnersTaxCodeController.onSubmit(NormalMode).url)
  }
}
