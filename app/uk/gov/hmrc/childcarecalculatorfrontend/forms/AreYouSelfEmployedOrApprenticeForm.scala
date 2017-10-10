package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.RadioOption

object AreYouSelfEmployedOrApprenticeForm extends FormErrorHelper {

  def AreYouSelfEmployedOrApprenticeFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, "error.required")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] = 
    Form(single("value" -> of(AreYouSelfEmployedOrApprenticeFormatter)))

  def options = Seq(
    RadioOption("areYouSelfEmployedOrApprentice", "option1"),
    RadioOption("areYouSelfEmployedOrApprentice", "option2")
  )

  def optionIsValid(value: String) = options.exists(o => o.value == value)
}
