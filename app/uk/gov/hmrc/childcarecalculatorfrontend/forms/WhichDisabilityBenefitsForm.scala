/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.hmrc.childcarecalculatorfrontend.models.DisabilityBenefits

object WhichDisabilityBenefitsForm extends FormErrorHelper {

  private def whichDisabilityBenefitsFormatter = new Formatter[DisabilityBenefits.Value] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(DisabilityBenefits.withName(s))
      case None => produceError(key, "whichDisabilityBenefits.error.notCompleted")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: DisabilityBenefits.Value) = Map(key -> value.toString)
  }

  private def optionIsValid(value: String): Boolean = options.values.toSeq.contains(value)

  private def constraint(name: String): Constraint[Set[DisabilityBenefits.Value]] = Constraint {
    case set if set.nonEmpty =>
      Valid
    case _ =>
      Invalid("whichDisabilityBenefits.error.notCompleted", name)
  }

  def apply(name: String): Form[Set[DisabilityBenefits.Value]] =
    Form(
      "value" -> set(of(whichDisabilityBenefitsFormatter))
        .verifying(constraint(name))
    )

  def options: Map[String, String] = DisabilityBenefits.values.map {
    value =>
      s"whichDisabilityBenefits.$value" -> value.toString
  }.toMap
}
