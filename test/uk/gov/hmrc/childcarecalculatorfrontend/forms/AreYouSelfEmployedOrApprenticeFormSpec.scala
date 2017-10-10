package uk.gov.hmrc.childcarecalculatorfrontend.forms

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours

class AreYouSelfEmployedOrApprenticeFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> AreYouSelfEmployedOrApprenticeForm.options.head.value
  )

  val form = AreYouSelfEmployedOrApprenticeForm()

  "AreYouSelfEmployedOrApprentice form" must {
    behave like questionForm[String](AreYouSelfEmployedOrApprenticeForm.options.head.value)

    behave like formWithOptionField("value", AreYouSelfEmployedOrApprenticeForm.options.map{x => x.value}:_*)
  }
}
