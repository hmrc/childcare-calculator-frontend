/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{unknownErrorKey, whichBenefitsYouGetErrorKey}

object WhichBenefitsYouGetForm extends FormErrorHelper {

  def whichBenefitsYouGetFormatter(validBenefitOptions: Set[String]): Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = data.get(key) match {
      case Some(s) if optionIsValid(s, validBenefitOptions) => Right(s)
      case None => produceError(key, whichBenefitsYouGetErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  private def constraint(): Constraint[Set[String]] = Constraint {
    case set if set.nonEmpty =>
      Valid
    case _ =>
      Invalid(whichBenefitsYouGetErrorKey)
  }

  def apply(location: Location.Value): Form[Set[String]] = {
    val validBenefitOptions = validBenefitOptionsForLocation(location)
    Form(
        "value" -> set(of(whichBenefitsYouGetFormatter(validBenefitOptions)))
          .verifying(constraint())
      )
  }

  lazy val options: Seq[(String, String)] = WhichBenefitsEnum.sortedWhichBenefits.map {
    value =>
      s"whichBenefitsYouGet.$value" -> value.toString
  }

  def optionIsValid(value: String, validWhichBenefits: Set[String]): Boolean = { validWhichBenefits.contains(value) }

  private def validBenefitOptionsForLocation(location: Location.Value): Set[String] = {
    location match {
      case Location.SCOTLAND => WhichBenefitsEnum.sortedScottishWhichBenefits.map(_.toString).toSet
      case _ => WhichBenefitsEnum.sortedWhichBenefits.map(_.toString).toSet
    }
  }
}
