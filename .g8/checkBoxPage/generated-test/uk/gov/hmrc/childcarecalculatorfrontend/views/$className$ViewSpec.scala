package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

class $className$ViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "$className;format="decap"$"

  val answer = Some(Set("options1", "option2"))

  def createView = () => $className;format="decap"$(frontendAppConfig, answer, $className$Form(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Set[String]]) => $className;format="decap"$(frontendAppConfig, answer, form, NormalMode)(fakeRequest, messages)

  "$className$ view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

//  "$className$ view" when {
//    "rendered" must {
//      "contain check box for the value" in {
//        val doc = asDocument(createViewUsingForm($className$Form()))
//        for (option <- $className$Form.options) {
//          assertContainsRadioButton(doc, option.id, "value", option.value, false)
//        }
//      }
//    }
//
//    for(option <- $className$Form.options) {
//      s"rendered with a value of '\${option.value}'" must {
//        s"have the '\${option.value}' check box selected" in {
//          val doc = asDocument(createViewUsingForm($className$Form().bind(Map("value" -> s"\${option.value}"))))
//          assertContainsRadioButton(doc, option.id, "value", option.value, true)
//
//          for(unselectedOption <- $className$Form.options.filterNot(o => o == option)) {
//            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
//          }
//        }
//      }
//    }
//  }
}
