package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowMuchYouPayPensionForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.BigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howMuchYouPayPension

class HowMuchYouPayPensionViewSpec extends BigDecimalViewBehaviours {

  val messageKeyPrefix = "howMuchYouPayPension"

  def createView = () => howMuchYouPayPension(frontendAppConfig, HowMuchYouPayPensionForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => howMuchYouPayPension(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  val form = HowMuchYouPayPensionForm()

  "HowMuchYouPayPension view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like intPage(createViewUsingForm, messageKeyPrefix, routes.HowMuchYouPayPensionController.onSubmit(NormalMode).url)
  }
}
