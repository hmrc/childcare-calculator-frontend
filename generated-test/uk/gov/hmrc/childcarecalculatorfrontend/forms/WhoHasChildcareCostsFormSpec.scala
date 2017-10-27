package uk.gov.hmrc.childcarecalculatorfrontend.forms

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.CheckboxBehaviours

class WhoHasChildcareCostsFormSpec extends CheckboxBehaviours[String] {

  override val validOptions: Set[String] = Set("option1", "option2")

  override val fieldName = "value"

  val form = WhoHasChildcareCostsForm()

  "WhoHasChildcareCosts form" must {
    behave like aCheckboxForm(invalid = "error.unknown")
  }
}
