package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerMaximumEarnings

class PartnerMaximumEarningsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "partnerMaximumEarnings"

  def createView = () => partnerMaximumEarnings(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => partnerMaximumEarnings(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "PartnerMaximumEarnings view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.PartnerMaximumEarningsController.onSubmit(NormalMode).url)
  }
}
