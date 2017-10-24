package uk.gov.hmrc.childcarecalculatorfrontend.forms

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.BothNoWeeksStatPayPY

class BothNoWeeksStatPayPYFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "field1" -> "1",
    "field2" -> "2"
  )

  val form = BothNoWeeksStatPayPYForm()

  "BothNoWeeksStatPayPY form" must {
    behave like questionForm(BothNoWeeksStatPayPY("1", "2"))

    behave like formWithMandatoryTextFields("field1", "field2")
  }
}
