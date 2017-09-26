package uk.gov.hmrc.childcarecalculatorfrontend.views.$pluralName;format="lower"$

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.any$pluralName$

class Any$pluralName$ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "$pluralName;format="decap"$.any$pluralName$"

  def createView = () => any$pluralName$(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => any$pluralName$(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "Any$pluralName$ view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.Any$pluralName$Controller.onSubmit(NormalMode).url)
  }
}
