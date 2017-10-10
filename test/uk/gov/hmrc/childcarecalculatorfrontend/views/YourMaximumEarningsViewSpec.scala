package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourMaximumEarnings

class YourMaximumEarningsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "yourMaximumEarnings"

  def createView = () => yourMaximumEarnings(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => yourMaximumEarnings(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "YourMaximumEarnings view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YourMaximumEarningsController.onSubmit(NormalMode).url)
  }
}
