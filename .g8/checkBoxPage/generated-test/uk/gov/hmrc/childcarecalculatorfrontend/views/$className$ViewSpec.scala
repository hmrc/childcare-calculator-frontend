package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

class $className$ViewSpec extends ViewBehaviours with CheckboxViewBehaviours[String] {

  val messageKeyPrefix = "$className;format="decap"$"
  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Map[String, String] = $className$Form.options

  def form: Form[Set[String]] = $className$Form()

  def createView(form: Form[Set[String]] = form): Html =
    $className;format="decap"$(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "$className$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like checkboxPage()
  }
}
