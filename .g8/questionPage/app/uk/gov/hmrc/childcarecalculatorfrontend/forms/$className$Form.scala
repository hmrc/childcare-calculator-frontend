package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.models.$className$

object $className$Form extends FormErrorHelper {

  def apply(): Form[$className$] = Form(
    mapping(
      "field1" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("field1.required"))),
      "field2" -> text.verifying(returnOnFirstFailure(
        valueNonEmpty("field2.required")))
    )($className$.apply)($className$.unapply)
  )
}
