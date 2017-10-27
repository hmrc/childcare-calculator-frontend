package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter

object WhoHasChildcareCostsForm extends FormErrorHelper {

  def whoHasChildcareCostsFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, "error.required")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[Set[String]] =
    Form(single("value" -> set(of(whoHasChildcareCostsFormatter))))

  def options = Map(
    "whoHasChildcareCosts.option1" -> "option1",
    "whoHasChildcareCosts.option2" -> "option2"
  )

  def optionIsValid(value: String): Boolean = options.values.toSeq.contains(value)
}
