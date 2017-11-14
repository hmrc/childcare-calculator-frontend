/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{unknownErrorKey, whichBenefitsPartnerGetErrorKey}

object WhichBenefitsPartnerGetForm extends FormErrorHelper {

  def WhichBenefitsPartnerGetFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, whichBenefitsPartnerGetErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  private def constraint(): Constraint[Set[String]] = Constraint {
    case set if set.nonEmpty =>
      Valid
    case _ =>
      Invalid(whichBenefitsPartnerGetErrorKey)
  }

  def apply(): Form[Set[String]] =
    Form(
      "value" -> set(of(WhichBenefitsPartnerGetFormatter))
        .verifying(constraint())
    )

  lazy val options: Map[String, String] = WhichBenefitsEnum.values.map {
    value =>
      s"whichBenefitsPartnerGet.$value" -> value.toString
  }.toMap

  def optionIsValid(value: String): Boolean = options.values.toSeq.contains(value)
}
