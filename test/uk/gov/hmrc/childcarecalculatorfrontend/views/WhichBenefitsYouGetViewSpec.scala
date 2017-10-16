package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsYouGet

class WhichBenefitsYouGetViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichBenefitsYouGet"

  def createView = () => whichBenefitsYouGet(frontendAppConfig, WhichBenefitsYouGetForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[String]) => whichBenefitsYouGet(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhichBenefitsYouGet view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "WhichBenefitsYouGet view" when {
    "rendered" must {
      "contain check boxes for the value" in {
        val doc = asDocument(createViewUsingForm(WhichBenefitsYouGetForm()))
        for (option <- WhichBenefitsYouGetForm.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- WhichBenefitsYouGetForm.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(WhichBenefitsYouGetForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- WhichBenefitsYouGetForm.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
