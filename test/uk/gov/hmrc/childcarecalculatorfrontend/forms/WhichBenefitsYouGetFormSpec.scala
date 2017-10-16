package uk.gov.hmrc.childcarecalculatorfrontend.forms

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours

class WhichBenefitsYouGetFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> WhichBenefitsYouGetForm.options.head.value
  )

  val form = WhichBenefitsYouGetForm()

  "WhichBenefitsYouGet form" must {
    behave like questionForm[String](WhichBenefitsYouGetForm.options.head.value)

    behave like formWithOptionField("value", WhichBenefitsYouGetForm.options.map{x => x.value}:_*)
  }
}
