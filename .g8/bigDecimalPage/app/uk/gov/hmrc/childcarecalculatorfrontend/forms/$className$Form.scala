package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

object $className$Form extends FormErrorHelper {

  def $className;format="decap"$Formatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[BigDecimal] {

    val decimalRegex = """\\d+(\\.\\d{1,2})?""".r.toString()

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) if(s.matches(decimalRegex)) => Right(BigDecimal(s))
        case _ => produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = "error.required", errorKeyInvalid: String = "error.bigDecimal"): Form[BigDecimal] =
    Form(single("value" -> of($className;format="decap"$Formatter(errorKeyBlank, errorKeyInvalid))))
}
