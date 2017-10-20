package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YouNoWeeksStatPayPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.IntViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youNoWeeksStatPayPY

class YouNoWeeksStatPayPYViewSpec extends IntViewBehaviours {

  val messageKeyPrefix = "youNoWeeksStatPayPY"

  def createView = () => youNoWeeksStatPayPY(frontendAppConfig, YouNoWeeksStatPayPYForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Int]) => youNoWeeksStatPayPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  val form = YouNoWeeksStatPayPYForm()

  "YouNoWeeksStatPayPY view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like intPage(createViewUsingForm, messageKeyPrefix, routes.YouNoWeeksStatPayPYController.onSubmit(NormalMode).url)
  }
}
