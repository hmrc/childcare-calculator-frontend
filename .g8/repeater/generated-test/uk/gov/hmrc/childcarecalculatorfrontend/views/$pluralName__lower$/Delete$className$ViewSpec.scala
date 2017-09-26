package uk.gov.hmrc.childcarecalculatorfrontend.views.$pluralName;format="lower"$

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.delete$className$

class Delete$className$ViewSpec extends YesNoViewBehaviours {
  val messageKeyPrefix = "$pluralName;format="decap"$.delete"

  val index = 0

  val $className;format="decap"$ = $className$("value 1", "value 2")

  override val form = BooleanForm()

  def createView = () => delete$className$(index, $className;format="decap"$, frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => delete$className$(index, $className;format="decap"$, frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "Delete $className$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.Delete$className$Controller.onSubmit(index, NormalMode).url)
  }
}
