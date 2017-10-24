package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BothNoWeeksStatPayPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, BothNoWeeksStatPayPY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothNoWeeksStatPayPY

class BothNoWeeksStatPayPYViewSpec extends QuestionViewBehaviours[BothNoWeeksStatPayPY] {

  val messageKeyPrefix = "bothNoWeeksStatPayPY"

  def createView = () => bothNoWeeksStatPayPY(frontendAppConfig, BothNoWeeksStatPayPYForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BothNoWeeksStatPayPY]) => bothNoWeeksStatPayPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  override val form = BothNoWeeksStatPayPYForm()

  "BothNoWeeksStatPayPY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, routes.BothNoWeeksStatPayPYController.onSubmit(NormalMode).url, "field1", "field2")
  }
}
