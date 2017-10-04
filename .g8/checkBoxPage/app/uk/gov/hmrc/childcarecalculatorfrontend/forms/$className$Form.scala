package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object $className$Form extends FormErrorHelper {

  def $className$Formatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, "error.required")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[Set[String]] =
    Form(set("value" -> of($className$Formatter)))

  def options = Seq(
    InputOption("$className;format="decap"$", "option1"),
    InputOption("$className;format="decap"$", "option2")
  )

  def optionIsValid(value: String) = options.exists(o => o.value == value)
}
