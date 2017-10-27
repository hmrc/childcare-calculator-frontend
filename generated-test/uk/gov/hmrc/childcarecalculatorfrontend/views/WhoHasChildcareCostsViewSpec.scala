package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts

class WhoHasChildcareCostsViewSpec extends ViewBehaviours with CheckboxViewBehaviours[String] {

  val messageKeyPrefix = "whoHasChildcareCosts"
  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Map[String, String] = WhoHasChildcareCostsForm.options

  def form: Form[Set[String]] = WhoHasChildcareCostsForm()

  def createView(form: Form[Set[String]] = form): Html =
    whoHasChildcareCosts(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhoHasChildcareCosts view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like checkboxPage()
  }
}
