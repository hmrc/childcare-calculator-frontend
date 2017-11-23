package uk.gov.hmrc.childcarecalculatorfrontend.forms

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.$className$

class $className$FormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "field1" -> "1",
    "field2" -> "2"
  )

  val form = $className$Form()

  "$className$ form" must {
    behave like questionForm($className$("1", "2"))

    behave like formWithMandatoryTextFields("field1", "field2")
  }
}
