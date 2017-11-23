package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.childcarecalculatorfrontend.models.$className$

object $className$Form {

  def apply(): Form[$className$] = Form(
    mapping(
      "field1" -> nonEmptyText,
      "field2" -> nonEmptyText
    )($className$.apply)($className$.unapply)
  )
}
