package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youGetSameIncomePreviousYear

class YouGetSameIncomePreviousYearViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "youGetSameIncomePreviousYear"

  def createView = () => youGetSameIncomePreviousYear(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => youGetSameIncomePreviousYear(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "YouGetSameIncomePreviousYear view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YouGetSameIncomePreviousYearController.onSubmit(NormalMode).url)
  }
}
